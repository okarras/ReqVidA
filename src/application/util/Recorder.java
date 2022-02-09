package application.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.VideoInputFrameGrabber;
import org.bytedeco.videoinput.videoInput;

import application.controller.RecorderController;
import application.main.ReqVidAMain;
import application.model.enums.CameraMode;

/**
 * Die Klasse dient zum Aufzeichnen eines Videos inklusive Ton. Dabei bestimmt
 * die Klasse alle verf�gbaren Audio- und Videoquellen, um die ben�tigten Ger�te
 * ausw�hlen zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class Recorder {
	private RecorderController controller;

	private ArrayList<String> videoDevices = new ArrayList<String>();
	private int currentVideoDeviceID = 0;

	private ArrayList<Mixer.Info> mixerInfos = new ArrayList<Mixer.Info>();
	private int currentAudioDeviceID = 0;

	private VideoInputFrameGrabber grabber;
	private TargetDataLine currentLine;
	private Mixer currentMixer;
	private DataLine.Info dataLineInfo;
	private AudioFormat audioFormat;
	private FFmpegFrameRecorder recordWriter;
	private final static int FRAME_RATE = 30;
	private final static int GOP_LENGTH_IN_FRAMES = 60;
	private final static int FRAME_WIDTH = 1280;
	private final static int FRAME_HEIGHT = 720;

	private ScheduledThreadPoolExecutor exec;
	private GrabberRunnable runnable;

	private String videoName;
	private int fileID = 1;

	/**
	 * Konstruktor eines Recorder-Objektes
	 * 
	 * @param controller
	 *            Controller der RecorderScene
	 * @param videoImage
	 *            GUI-Komponente zum anzeigen des Videobildes
	 */
	public Recorder(RecorderController controller) {
		this.controller = controller;

		/*
		 * Bestimmung aller angeschlossenen VideoDevices
		 */
		int numberOfDevices = videoInput.listDevices();
		for (int i = 0; i < numberOfDevices; i++) {
			videoDevices.add(videoInput.getDeviceName(i).getString());
		}

		selectVideoDevice(0);

		/*
		 * Bestimmung aller angeschlossenen AudioDevices
		 */
		audioFormat = new AudioFormat(44100, 16, 2, true, false);
		dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfo) {
			String name = info.getName();
			if (name.startsWith("Mikrofon") || name.startsWith("Line") || name.startsWith("Digitale")) {
				mixerInfos.add(info);
			}
		}
		selectMixer(0);
	}

	/**
	 * Setter-Methode zum Setzen des Kamera-Modus
	 * 
	 * @param mode
	 *            Modus den die Kamera annehmen soll
	 */
	public void setVideoDisplayMode(CameraMode mode) {

		if (mode == CameraMode.ACTIVE) {
			setupRunnable();

			exec = new ScheduledThreadPoolExecutor(1);
			runnable = new GrabberRunnable(controller, grabber, currentLine, audioFormat);
			exec.scheduleAtFixedRate(runnable, 500, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);

		} else if (mode == CameraMode.INACTIVE) {
			stopRunnable();
		}
	}

	/**
	 * Getter-Methode f�r eine Liste aller Audioquellen
	 * 
	 * @return List aller Audioquellen
	 */
	public ArrayList<Mixer.Info> getMixerInfoList() {
		return mixerInfos;
	}

	/**
	 * Setter-Methode zur Auswahl einer Audioquelle
	 * 
	 * @param id
	 *            ID der Audioquelle in der vollts�ndigen Liste
	 */
	public void selectMixer(int id) {
		currentMixer = AudioSystem.getMixer(mixerInfos.get(id));
		System.out.println(mixerInfos.get(id).getName());
	}

	private void setupRunnable() {
		try {
			grabber = new VideoInputFrameGrabber(currentVideoDeviceID);
			grabber.setImageWidth(FRAME_WIDTH);
			grabber.setImageHeight(FRAME_HEIGHT);
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}

		audioFormat = new AudioFormat(44100, 16, 2, true, false);
		dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

		try {
			currentMixer = AudioSystem.getMixer(mixerInfos.get(currentAudioDeviceID));
			currentLine = (TargetDataLine) currentMixer.getLine(dataLineInfo);
			currentLine.open(audioFormat);
			currentLine.start();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
	}

	private void stopRunnable() {
		try {
			exec.shutdown();
			exec.awaitTermination(33, TimeUnit.MILLISECONDS);

			Timer timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					if (exec.isTerminated()) {
						try {
							grabber.stop();
							grabber.release();
						} catch (Exception e) {
							e.printStackTrace();
						}

						currentLine.stop();
						currentLine.close();

						controller.setImage(null);

						timer.cancel();
						timer.purge();
					}
				}
			};

			timer.schedule(task, 0, 33);

		} catch (InterruptedException e) {
			System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
		}
	}

	/**
	 * Getter-Methode f�r eine Liste aller Videoquellen
	 * 
	 * @return Liste aller Videoquellen
	 */
	public ArrayList<String> getVideoDeviceList() {
		return videoDevices;
	}

	/**
	 * Setter-Methode zur Auswahl einer Videoquelle
	 * 
	 * @param id
	 *            ID der Videoquelle in der vollst�ndigen Liste
	 */
	public void selectVideoDevice(int id) {
		currentVideoDeviceID = id;
	}

	/**
	 * Die Methode initialisiert den Writer zum Erstellen des Video-Files
	 * 
	 */
	public void startRecording() {
		long startTime = System.currentTimeMillis();
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		final String formattedStartTime = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(cal.getTime());

		videoName = ReqVidAMain.workshopName + " - Video" + fileID + " - " + formattedStartTime + ".mp4";

		File dir = new File(ReqVidAMain.absoluteFolderPath + "\\videos");
		if (dir.exists() == false) {
			dir.mkdir();
		}

		recordWriter = new FFmpegFrameRecorder(ReqVidAMain.absoluteFolderPath + "\\videos\\" + videoName, FRAME_WIDTH, FRAME_HEIGHT, 2);
		recordWriter.setFormat("mp4");

		// Video-Einstellungen
		recordWriter.setVideoOption("tune", "zerolatency");
		recordWriter.setVideoOption("preset", "ultrafast");
		recordWriter.setVideoOption("crf", "28");
		recordWriter.setVideoBitrate(2000000);
		recordWriter.setVideoCodec(avcodec.AV_CODEC_ID_H264);
		recordWriter.setInterleaved(true);
		recordWriter.setFrameRate(FRAME_RATE);
		recordWriter.setGopSize(GOP_LENGTH_IN_FRAMES);

		// Audio-Einstellungen
		recordWriter.setAudioOption("crf", "0");
		recordWriter.setAudioQuality(0);
		recordWriter.setAudioBitrate(192000);
		recordWriter.setAudioChannels(2);
		recordWriter.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
		recordWriter.setSampleRate(44100);

		runnable.setRecorder(recordWriter);

		try {
			recordWriter.start();
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter-Methode f�r den Namen des Videos
	 * 
	 * @return Name des Videos
	 */
	public String getVideoName() {
		return videoName;
	}

	/**
	 * Die Methode schlie�t den Writer zur vollst�ndigen Erstellung des
	 * Video-Files.
	 */
	public void stopRecording() {

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					recordWriter.stop();
					recordWriter.release();
					timer.cancel();
					timer.purge();
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
		};
		timer.schedule(task, 500);

		fileID = fileID + 1;
		controller.setRecordingTime(0);
	}
}