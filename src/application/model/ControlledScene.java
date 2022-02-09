package application.model;

import application.controller.ScenesController;
import javafx.stage.Stage;

/**
 * Das Interface dient zur Abstraktion der verschiedenen Ansichten Main,
 * Recorder und Analyzer, sodass �ber den ScenesController sichergestellt werden
 * kann, dass stets nur eine Scene geladen ist, um Ressourcen zu sparen.
 * 
 * @author Oliver Karras
 *
 */
public interface ControlledScene {

	/**
	 * Diese Methode setzt den ScenesController als Parent in den Kind-Scenes.
	 * 
	 * @param screenPage
	 *            Parent der Kind-Scenes
	 */
	public void setSceneParent(ScenesController scenePage);

	/**
	 * Die Methode dient der Vorbereitung des Controllers eines Kindes des
	 * ScenesControllers.
	 */
	public void setup();

	/**
	 * Diese Methode dient dem Schlie�en und Freigeben von Ressourcen, die durch
	 * Kinder des ScenesControllers noch belegt werden.
	 */
	public void close();

	/**
	 * Diese Methode dient der �bergabe der Stage, welche alle GUI-Ansichten
	 * enth�lt und als Hauprreferenz f�r jegliche FileChooser dient.
	 * 
	 * @param stage der Anwendung
	 */
	public void setStage(Stage stage);

}
