package application.main;

import java.awt.SplashScreen;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import application.controller.ScenesController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Dies ist die Hauptklasse der ReqVidA-Applikation. Sie initialisiert alle
 * möglichen Kind-Scenen und erstellt das Hauptfenster der Oberfläche.
 * 
 * @author Oliver Karras
 *
 */
public class ReqVidAMain extends Application {

	/**
	 * Name der Main-Scene
	 */
	public final static String mainSceneID = "ReqVidAMain";

	/**
	 * FXML-File der Main-Scene
	 */
	public final static String mainSceneFile = "/resources/ui/ReqVidAMainScene.fxml";

	/**
	 * Name der Recorder-Scene
	 */
	public final static String recorderSceneID = "Recorder";

	/**
	 * FXML-File der Recorder-Scene
	 */
	public final static String recorderSceneFile = "/resources/ui/RecorderScene.fxml";

	/**
	 * Name der Analyzer-Scene
	 */
	public final static String analyzerSceneID = "Analyzer";

	/**
	 * FXML-File der Analyzer-Scene
	 */
	public final static String analyzerSceneFile = "/resources/ui/AnalyzerScene.fxml";

	/**
	 * Name des Workshops, der aufgenommen bzw. analyisert wird
	 */
	public static String workshopName = "";

	/**
	 * Absolute Pfad zu dem Hauptordner der Workshop-Projektes
	 */
	public static String absoluteFolderPath = "";

	/**
	 * aktuelle Video-File, der im Analyzer angezeigt wird
	 */
	public static String videoFile = "";

	private SplashScreen splash;

	@Override
	public void start(Stage stage) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Thread t = new Thread() {
						public void run() {
							/*
							 * Unter dem Reiter (x)=Arguments muss unter VM
							 * Arguments -splash:<Pfad-zur-Imagedatei>
							 * eingetragen werden. In diesem Fall ist es der
							 * Pfad <resources/icons/splashscreen.gif>.
							 */
							splash = SplashScreen.getSplashScreen();
							if (splash == null) {
								System.out.println("Splash not found");
							}
							try {
								Thread.sleep(1200);
								if (splash != null) {
									splash.close();
								}
							} catch (InterruptedException e) {
								System.err.println("Thread interrupted");
							}
						}
					};
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		setUserAgentStylesheet(STYLESHEET_MODENA);
		startReqVida(stage);
	}

	private void startReqVida(Stage stage) {
		/*
		 * Initialisierung aller GUI-Ansichten
		 */
		final ScenesController mainContainer = new ScenesController(stage);
		mainContainer.loadNode(ReqVidAMain.mainSceneID, ReqVidAMain.mainSceneFile);
		mainContainer.loadNode(ReqVidAMain.recorderSceneID, ReqVidAMain.recorderSceneFile);
		mainContainer.loadNode(ReqVidAMain.analyzerSceneID, ReqVidAMain.analyzerSceneFile);

		mainContainer.setNode(ReqVidAMain.mainSceneID);

		Scene scene = new Scene(mainContainer);
		
		stage.setResizable(true);
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icons/logo.png")));
		stage.setTitle("ReqVidA - Requirements Video Analyzer");
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				mainContainer.close();
				Timer timer = new Timer();
				TimerTask task = new TimerTask() {
					
					@Override
					public void run() {
						System.exit(0);
						timer.cancel();
						timer.purge();
					}
				};
				timer.schedule(task, 1000);
			}
		});

		stage.show();
	}

	/**
	 * Main-Methode zum Start der Applikation.
	 * 
	 * @param args
	 *            �bergebene Parameter bei Start der Applikation
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
