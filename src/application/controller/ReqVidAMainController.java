package application.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import application.main.ReqVidAMain;
import application.model.ControlledScene;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Die Klasse dient als Controller der MainScene, um mit den enthaltenen
 * GUI-Elementen aus dem FXML-File entsprechend zu interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class ReqVidAMainController implements Initializable, ControlledScene {

	private String absolutePathToSave = "";
	private String absolutePathToLoad = "";

	private ScenesController controller;

	@FXML
	private TextField workshopNameToSave;

	@FXML
	private TextField folderToSave;

	@FXML
	private Button startRecorder;

	@FXML
	private Label workshopNameToLoad;

	@FXML
	private TextField folderToLoad;

	@FXML
	private TextField videoToLoad;

	@FXML
	private Button chooseVideoToLoad;

	@FXML
	private Button startAnalyzer;
	
	@FXML
	private ImageView logoView;

	private ValidationSupport validationSupport;
	private Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void setSceneParent(ScenesController sceneParent) {
		controller = sceneParent;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void setup() {

		validationSupport = new ValidationSupport();
		Validator<String> workshopNameValidator = new Validator<String>() {

			@Override
			public ValidationResult apply(Control control, String value) {
				boolean condition = false;
				if (value.trim().equalsIgnoreCase("")) {
					condition = true;
				}
				return ValidationResult
						.fromMessageIf(control, "Workshop name is required.",
								Severity.ERROR, condition);
			}
		};

		validationSupport.registerValidator(workshopNameToSave, true,
				workshopNameValidator);

		folderToSave.setText(absolutePathToSave);
		folderToLoad.setText(absolutePathToLoad);

		workshopNameToSave.textProperty().addListener(
				new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						if (newValue.trim().equalsIgnoreCase("") == false
								&& folderToSave.getText().trim()
										.equalsIgnoreCase("") == false) {
							startRecorder.setDisable(false);
						} else {
							startRecorder.setDisable(true);
						}
					}
				});

		folderToSave.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("") == false
						&& workshopNameToSave.getText().trim()
								.equalsIgnoreCase("") == false) {
					startRecorder.setDisable(false);
				} else {
					startRecorder.setDisable(true);
				}
			}
		});

		folderToLoad.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("") == false
						&& videoToLoad.getText().trim().equalsIgnoreCase("") == false) {
					startAnalyzer.setDisable(false);
				} else {
					startAnalyzer.setDisable(true);
				}
			}
		});

		videoToLoad.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("") == false
						&& folderToLoad.getText().trim().equalsIgnoreCase("") == false) {
					startAnalyzer.setDisable(false);
				} else {
					startAnalyzer.setDisable(true);
				}
			}
		});
		
		logoView.setSmooth(true);
		logoView.setImage(new Image(getClass().getResourceAsStream("/resources/icons/SE-Logo(big).png")));

	}

	@Override
	public void close() {
		// nothing to do
	}

	/*
	 * Die Methode dient zur Auswahl des Hauptverzeichnisses, in dem ein
	 * Workshop-Projekt gespeichert werden soll.
	 */
	@FXML
	private void chooseFolderToSave() {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("./"));
		final File selectedDirectory = directoryChooser.showDialog(stage);
		if (selectedDirectory != null) {
			absolutePathToSave = selectedDirectory.getAbsolutePath();
			folderToSave.setText(absolutePathToSave);
		} else {
			absolutePathToSave = "";
			folderToSave.clear();
		}
	}

	/*
	 * Die Methoe dient der Auswahl eines Workshop-Projektes, dass geladen
	 * werden soll.
	 */
	@FXML
	private void chooseFolderToLoad() {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("./"));
		final File selectedDirectory = directoryChooser.showDialog(stage);
		if (selectedDirectory != null) {
			absolutePathToLoad = selectedDirectory.getAbsolutePath();
			folderToLoad.setText(absolutePathToLoad);

			workshopNameToLoad.setText(new File(absolutePathToLoad).getName());

			File dir = new File(absolutePathToLoad + "\\videos\\");
			if (dir.exists() == false) {
				dir.mkdir();
			}

			File[] videoFiles = dir.listFiles();
			if (videoFiles.length > 0) {
				videoToLoad.setText(absolutePathToLoad + "\\videos\\"
						+ videoFiles[0].getName());
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("No Video Files");
				alert.setHeaderText("No video files could be found in the selected project.");
				alert.setContentText("The video files of your project could not be found in the selected project folder."
						+ "\nPlease insert your video files to \""
						+ absolutePathToLoad + "\\videos\\" + "\".");

				alert.showAndWait();
			}
			chooseVideoToLoad.setDisable(false);
		} else {
			absolutePathToLoad = "";
			folderToLoad.clear();
		}
	}

	/*
	 * Die Methode dient der Auswahl des Videos, das f�r eine Analyse geladen
	 * werden soll.
	 */
	@FXML
	private void chooseVideoToLoad() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(absolutePathToLoad
				+ "\\videos\\"));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(".mp4", "*.mp4"));
		final File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			videoToLoad.setText(selectedFile.getAbsolutePath());
		}
	}

	/*
	 * Die Methode �ffnet den Recorder.
	 */
	@FXML
	private void goToRecorder(ActionEvent event) {
		ReqVidAMain.workshopName = workshopNameToSave.getText();
		ReqVidAMain.absoluteFolderPath = absolutePathToSave + "\\"
				+ workshopNameToSave.getText();

		File dir = new File(ReqVidAMain.absoluteFolderPath);
		if (dir.exists() == false) {
			dir.mkdir();
		}

		controller.setNode(ReqVidAMain.recorderSceneID);
	}

	/*
	 * Die Methode �ffnet den Analyzer.
	 */
	@FXML
	private void goToAnalyzer(ActionEvent event) {
		ReqVidAMain.workshopName = workshopNameToLoad.getText();
		ReqVidAMain.absoluteFolderPath = absolutePathToLoad;
		ReqVidAMain.videoFile = videoToLoad.getText();

		controller.setNode(ReqVidAMain.analyzerSceneID);
	}
}
