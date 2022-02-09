package application.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.VideoInputFrameGrabber;

import application.controller.RecorderController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class GrabberRunnable implements Runnable {

	private RecorderController controller;
	private VideoInputFrameGrabber grabber = null;

	private TargetDataLine line;
	private int sampleRate;
	private int numChannels;
	private byte[] audioBytes;
	private int audioBufferSize;

	private FFmpegFrameRecorder recorder;
	private long startTime = 0;
	private long timeStamp = 0;

	/**
	 * Konstruktor f�r ein GrabberRunnable-Objekt, mit dem die ausgew�hlte
	 * Kamera und zugeh�rige Audiospur ausgelesen und in eine Videodatei
	 * geschrieben werden kann.
	 * 
	 * @param controller
	 * @param grabber
	 * @param recorder
	 */
	public GrabberRunnable(RecorderController controller, VideoInputFrameGrabber grabber, TargetDataLine line,
			AudioFormat format) {
		this.controller = controller;
		this.grabber = grabber;
		this.line = line;

		sampleRate = (int) format.getSampleRate();
		numChannels = format.getChannels();
		audioBufferSize = sampleRate * numChannels;
		audioBytes = new byte[audioBufferSize];
	}

	@Override
	public void run() {

		try {
			// get the current frame
			Frame frame = grabber.grab();
			if (frame != null) {
				// Show the current frame in the preview
				controller.setImage(convertFrameToImage(frame));
			}

			int nBytesRead = line.read(audioBytes, 0, line.available());
			int nSamplesRead = nBytesRead / 2;
			short[] samples = new short[nSamplesRead];
			ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
			ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

			if (recorder != null && controller.isRecording()) {

				/*
				 * Define the start time
				 * 
				 * This needs to be initialized as close when we'll use it as
				 * possible, as the delta from assignment to computed time could
				 * be too high
				 */
				if (startTime == 0) {
					startTime = System.currentTimeMillis();
				}

				// Create current timestamp
				timeStamp = 1000 * (System.currentTimeMillis() - startTime);

				// Check for AV drift
				if (timeStamp > recorder.getTimestamp()) {
					System.out.println("Lip-flap correction: " + timeStamp + " : " + recorder.getTimestamp() + " -> "
							+ (timeStamp - recorder.getTimestamp()));

					/*
					 * Tell recorder to write the current frame at this
					 * timestamp
					 */
					recorder.setTimestamp(timeStamp);
				}

				// Record the current into the video file
				recorder.record(frame);
				recorder.recordSamples(sampleRate, numChannels, sBuff);
				controller.setRecordingTime(timeStamp / 1000);
			} else {
				if(startTime != 0) {
					startTime = 0;
				}
			}

		} catch (org.bytedeco.javacv.FrameGrabber.Exception | Exception e) {
			e.printStackTrace();
		}
	}

	public void setRecorder(FFmpegFrameRecorder recorder) {
		this.recorder = recorder;
	}
	
	private Image convertFrameToImage(Frame frame) {
		try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
			BufferedImage bimg = converter.convert(frame);

			return SwingFXUtils.toFXImage(bimg, null);
		}
	}
}
