package application.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import application.database.SQLiteDataBase;
import application.main.ReqVidAMain;
import application.model.ControlledScene;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Die Klasse dient als Parent f�r alle m�glichen Ansichten ReqVidAMain,
 * Recorder und Analyzer. Durch das Konstrukt wird sichergestellt, das stets nur
 * eine Kind-Oberfl�che in der StackPane enthalten ist, wodruch keine unn�tigen
 * Ressourcen belegt werden.
 * 
 * @author Oliver Karras
 *
 */
public class ScenesController extends StackPane {

	private static HashMap<String, Node> nodes = new HashMap<String, Node>();
	private static HashMap<String, ControlledScene> controlledScenes = new HashMap<String, ControlledScene>();

	private Stage stage;

	/**
	 * Konstruktor f�r eine ScenesController-Objekt
	 */
	public ScenesController(Stage stage) {
		super();
		this.stage = stage;
	}

	/**
	 * Die Methode f�gt eine neue Scene zur Sammlung hinzu.
	 * 
	 * @param name
	 *            Name der Scene
	 * @param scene
	 *            neue Scene
	 */
	public void addNode(String name, Node scene) {
		nodes.put(name, scene);
	}

	/**
	 * Die Methode f�gt einen neuen Controller einer korrespondierenden Scene
	 * zur Sammlung hinzu.
	 * 
	 * @param name
	 *            Name des Controllers
	 * @param controller
	 *            Controller-Objekt
	 */
	public void addControlledScene(String name, ControlledScene controller) {
		controlledScenes.put(name, controller);
	}

	/**
	 * Getter-Methode f�r eine bestimmte Scene
	 * 
	 * @param name
	 *            Name der Scene
	 * @return entsprechendes Scene-Objekt
	 */
	public Node getNode(String name) {
		return nodes.get(name);
	}

	/**
	 * Die Methode l�dt den FXML-File und f�gt die Scene zu der Sammlung hinzu,
	 * wobei der ScenesController als Parent gesetzt wird.
	 * 
	 * @param name
	 *            Name der Scene
	 * @param resource
	 *            FXML-File, der geladen wird
	 * @return TRUE, falls das Laden erfolgreich gewesen ist, ansonsten FALSE
	 */
	public boolean loadNode(String name, String resource) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
			Parent loadScene = (Parent) loader.load();

			ControlledScene sceneController = ((ControlledScene) loader
					.getController());
			sceneController.setSceneParent(this);
			sceneController.setStage(stage);

			addNode(name, loadScene);

			addControlledScene(name, sceneController);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Die Methode dient der Anzeige einer ausgew�hlten Scene bez�glich des
	 * �bergebenen Namen.
	 * 
	 * @param name
	 *            Name der Scene, die angezeigt werden soll.
	 * @return TRUE, falls die Anzeige erfolgreich gewesen ist, ansonsten FALSE
	 */
	public boolean setNode(final String name) {
		
		controlledScenes.get(name).setup();
		
		// Scene geladen
		if (nodes.get(name) != null) {
			final DoubleProperty opacity = opacityProperty();

			// Pr�fung, ob mehr als eine Scene bereits geladen ist
			if (!getChildren().isEmpty()) {
				Timeline fadeOut = new Timeline(new KeyFrame(Duration.ZERO,
						new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(
						1000), new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {

						// entfernt die angezeigte Scene
						getChildren().remove(0);
						
						// anzeige der neuen Scene
						getChildren().add(0, nodes.get(name));

						Timeline fadeIn = new Timeline(new KeyFrame(
								Duration.ZERO, new KeyValue(opacity, 0.0)),
								new KeyFrame(new Duration(800), new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent event) {
										if (name.equalsIgnoreCase(ReqVidAMain.mainSceneID) == false) {
											stage.sizeToScene();
											stage.centerOnScreen();
											stage.setMinHeight(stage.getHeight());
											stage.setMinWidth(stage.getWidth());
											stage.setMaximized(true);
										}
									}}, new KeyValue(
										opacity, 1.0)));
						fadeIn.play();
					}
				}, new KeyValue(opacity, 0.0)));
				fadeOut.play();

			} else {
				setOpacity(0.0);
				getChildren().add(nodes.get(name));
				Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO,
						new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(
						2500), new KeyValue(opacity, 1.0)));
				fadeIn.play();
			}

			return true;
		} else {
			System.out.println("screen hasn't been loaded!!! \n");
			return false;
		}
	}

	/**
	 * Diese Methode entfernt eine Scene bez�glich des �bergebenen Namen aus der
	 * Sammlung.
	 * 
	 * @param name
	 *            Name der zu entfernen Scene
	 * @return TRUE, falls Scene entfernt worden ist, ansonsten FALSE
	 */
	public boolean unloadNode(String name) {
		if (nodes.remove(name) == null) {
			System.out.println("Screen didn't exist");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Die Methode dient der Freigabe aller belegten Ressourcen beim Beenden der
	 * Applikation.
	 */
	public void close() {
		try {
			SQLiteDataBase.getInstance().close();
			Iterator<Entry<String, ControlledScene>> iterator = controlledScenes
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, ControlledScene> pair = iterator.next();
				ControlledScene controller = pair.getValue();
				controller.close();
				iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
