package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.sound.sampled.Mixer;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import application.database.SQLiteDataBase;
import application.main.ReqVidAMain;
import application.model.ControlledScene;
import application.model.GlossaryEntry;
import application.model.Stakeholder;
import application.model.annotation.Annotation;
import application.model.annotation.ConflictAnnotation;
import application.model.annotation.CustomAnnotation;
import application.model.annotation.DataAnnotation;
import application.model.annotation.ImportantAnnotation;
import application.model.annotation.QuestionAnnotation;
import application.model.annotation.RequirementAnnotation;
import application.model.enums.CameraMode;
import application.model.enums.DataAnnotationType;
import application.util.DomParser;
import application.util.Helper;
import application.util.Recorder;
import application.view.AnnotationListCell;
import application.view.AnnotationTab;
import application.view.GlossaryEntryDialog;
import application.view.GlossaryListCell;
import application.view.StakeholderDialog;
import application.view.StakeholderListCell;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Die Klasse dient als Controller der RecorderScene, um mit den enthaltenen
 * GUI-Elementen aus dem FXML-File entsprechend zu interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class RecorderController implements Initializable, ControlledScene {

	private ScenesController controller;

	@FXML
	private ToggleButton startButton;
	@FXML
	private ToggleButton recordButton;
	private boolean isRecording = false;
	@FXML
	private Label recordingTime;
	private long timeStamp;
	@FXML
	private ChoiceBox<String> videoSources;
	@FXML
	private ChoiceBox<String> audioSources;
	@FXML
	private ImageView frameView;
	private Recorder recorder;

	private ValidationSupport validationSupport;

	@SuppressWarnings("unused")
	private Stage stage;

	@FXML
	private TextField nameField;
	@FXML
	private TextField functionField;
	@FXML
	private TextField contactField;
	@FXML
	private TextField airtimeField;
	@FXML
	private Button addStakeholderButton;
	@FXML
	private ListView<Stakeholder> stakeholderListView;
	private ArrayList<Stakeholder> stakeholders;

	@FXML
	private TextField termField;
	@FXML
	private TextField descriptionField;
	@FXML
	private Button addGlossaryButton;
	@FXML
	private ListView<GlossaryEntry> glossaryListView;
	private ArrayList<GlossaryEntry> glossaryEntries;

	@FXML
	private Button importantButton;
	@FXML
	private Button requirementButton;
	@FXML
	private Button conflictButton;
	@FXML
	private Button questionButton;
	@FXML
	private ListView<Annotation> annotationListView;
	private ArrayList<Annotation> annotations;

	@FXML
	private TabPane tabPane;
	private SingleSelectionModel<Tab> selectionModelTabs;

	@FXML
	private GridPane gridPane;
	private HashMap<String, DataAnnotation> shortcuts;
	private final String shortcutSettings = "./settings/shortcuts.xml";
	private final String customAnnotationSettings = "./settings/customannotations.xml";

	@FXML
	private Accordion accordion;
	@FXML
	private TitledPane annotationPane;

	@FXML
	private HTMLEditor editor;

	/**
	 * Konstruktor f�r ein RecorderController-Objekt
	 */
	public RecorderController() {
	}

	@Override
	public void setup() {

		DomParser.createCustomAnnotations(new File(customAnnotationSettings));

		shortcuts = DomParser.createShortcuts(new File(shortcutSettings));

		gridPane.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				KeyCode key = event.getCode();
				if (shortcuts.containsKey(key.getName()) && isRecording) {
					addDataAnnotation(shortcuts.get(key.getName()));
				}
			}
		});

		accordion.setExpandedPane(annotationPane);

		selectionModelTabs = tabPane.getSelectionModel();

		/*
		 * Dient zur Validierung von Eingaben
		 */
		validationSupport = new ValidationSupport();
		Validator<String> nameValidator = new Validator<String>() {

			@Override
			public ValidationResult apply(Control control, String value) {
				boolean condition = false;
				if (value.trim().equalsIgnoreCase("")) {
					condition = true;
				}
				return ValidationResult.fromMessageIf(control, "Name is required.", Severity.ERROR, condition);
			}
		};

		Validator<String> termValidator = new Validator<String>() {

			@Override
			public ValidationResult apply(Control control, String value) {
				boolean condition = false;
				if (value.trim().equalsIgnoreCase("")) {
					condition = true;
				}
				return ValidationResult.fromMessageIf(control, "Term is required.", Severity.ERROR, condition);
			}
		};

		validationSupport.registerValidator(nameField, true, nameValidator);
		validationSupport.registerValidator(termField, true, termValidator);

		selectGlossaryData();
		termField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("")) {
					addGlossaryButton.disableProperty().set(true);
				} else {
					addGlossaryButton.disableProperty().set(false);
				}
			}

		});

		selectStakeholderData();
		nameField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("")) {
					addStakeholderButton.disableProperty().set(true);
				} else {
					addStakeholderButton.disableProperty().set(false);
				}
			}

		});

		recorder = new Recorder(this);

		selectAnnotationData();
		/*
		 * Abfrage aller Video-Devices, die am Rechner angeschlossen sind
		 */
		ArrayList<String> vidDevices = recorder.getVideoDeviceList();

		ObservableList<String> videoDeviceNames = FXCollections.observableArrayList(vidDevices);
		videoSources.setItems(videoDeviceNames);

		videoSources.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (oldValue != newValue) {
					recorder.selectVideoDevice(newValue.intValue());
				}
			}
		});

		videoSources.getSelectionModel().selectFirst();

		/*
		 * Abfrage aller Microphones, die am Rechner angeschlossen sind
		 */
		ArrayList<Mixer.Info> audDevices = recorder.getMixerInfoList();
		ArrayList<String> audioDevices = new ArrayList<String>();
		for (Mixer.Info info : audDevices) {
			audioDevices.add(info.getName());
		}

		ObservableList<String> audioDeviceNames = FXCollections.observableArrayList(audioDevices);
		audioSources.setItems(audioDeviceNames);

		audioSources.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (oldValue != newValue) {
					recorder.selectMixer(newValue.intValue());
				}
			}
		});

		audioSources.getSelectionModel().selectFirst();

		/*
		 * (De-) Aktiviert das ausgew�hlte Video-Device, um den Live Stream
		 * anzuzeigen
		 */
		startButton.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue == true) {
					recorder.setVideoDisplayMode(CameraMode.ACTIVE);
					startButton.setText("Stop Camera");

					videoSources.setDisable(true);
					audioSources.setDisable(true);

					recordButton.setDisable(false);
				} else {
					recorder.setVideoDisplayMode(CameraMode.INACTIVE);

					startButton.setText("Start Camera");

					videoSources.setDisable(false);
					audioSources.setDisable(false);

					recordButton.setDisable(true);
				}
			}
		});

		/*
		 * Startet / Stoppt die Aufzeichnung des Live Streams.
		 */
		recordButton.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue == true) {
					recorder.startRecording();

					// TODO Workaround
					/*
					 * Die folgenden 2 Befehle dienen dazu bei der Aufzeichnung
					 * mehrerer Videos hintereinander nur die Annotationen zu
					 * dem jeweiligen Video im Recorder anzuzeigen.
					 */
					selectAnnotationData();
					tabPane.getTabs().clear();
					
					isRecording = newValue;
					importantButton.disableProperty().set(false);
					requirementButton.disableProperty().set(false);
					conflictButton.disableProperty().set(false);
					questionButton.disableProperty().set(false);
				} else {
					isRecording = newValue;
					recorder.stopRecording();
					importantButton.disableProperty().set(true);
					requirementButton.disableProperty().set(true);
					conflictButton.disableProperty().set(true);
					questionButton.disableProperty().set(true);
				}

				startButton.setDisable(newValue);
			}
		});

	}

	public void setImage(Image image) {
		frameView.setImage(image);
	}

	/**
	 * Die Methode stellt sicher, dass beim Schlie�en der gesammten Applikation
	 * die verwendeten Ressourcen freigegeben werden, da sonst ggf. das
	 * verwendete Video-Device blockiert wird.
	 */
	@Override
	public void close() {
		if (isRecording == true && recorder != null) {
			recorder.stopRecording();
			isRecording = false;
		}
	}

	/**
	 * Die Methode dient der Abfrage, ob ein Video aufgezeichnet wird.
	 */
	public boolean isRecording() {
		return isRecording;
	}

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

	/*
	 * Die Methode f�gt die eingegebenen Stakeholder-Daten zur Datenbank hinzu.
	 */
	@FXML
	private void addStakeholderData() {
		String name = nameField.getText().trim();
		String function = functionField.getText().trim();
		String contact = contactField.getText().trim();
		String airtime = airtimeField.getText().trim();

		SQLiteDataBase.getInstance().insertStakeholderData(name, function, contact, airtime);
		clearStakeholderData();
		selectStakeholderData();
	}

	/*
	 * Die Methode f�gt die eingegebenen Glossar-Daten zur Datenbank hinzu.
	 */
	@FXML
	private void addGlossaryData() {
		String term = termField.getText().trim();
		String description = descriptionField.getText().trim();

		SQLiteDataBase.getInstance().insertGlossaryEntryData(term, description);
		clearGlossaryData();
		selectGlossaryData();
	}

	private void selectGlossaryData() {
		glossaryEntries = SQLiteDataBase.getInstance().selectGlossaryData();

		ObservableList<GlossaryEntry> entries = FXCollections.observableArrayList(glossaryEntries);

		glossaryListView.setItems(entries);
		glossaryListView.setCellFactory(new Callback<ListView<GlossaryEntry>, ListCell<GlossaryEntry>>() {

			@Override
			public ListCell<GlossaryEntry> call(ListView<GlossaryEntry> param) {
				ListCell<GlossaryEntry> cell = new GlossaryListCell();
				return cell;
			}
		});

		/*
		 * Eintr�ge in der ListView k�nnen durch Doppelklick editiert werden.
		 */
		glossaryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						createGlossaryEntryDialog();
					}
				}
			}
		});
	}

	/*
	 * Die Methode fragt alle Stakeholder-Daten aus der Dantebank ab, um sie in
	 * der zugeh�rigen ListView anzuzeigen.
	 */
	private void selectStakeholderData() {
		stakeholders = SQLiteDataBase.getInstance().selectStakeholderData();

		ObservableList<Stakeholder> holders = FXCollections.observableArrayList(stakeholders);

		stakeholderListView.setItems(holders);
		stakeholderListView.setCellFactory(new Callback<ListView<Stakeholder>, ListCell<Stakeholder>>() {

			@Override
			public ListCell<Stakeholder> call(ListView<Stakeholder> param) {
				ListCell<Stakeholder> cell = new StakeholderListCell();
				return cell;
			}
		});

		/*
		 * Eintr�ge in der ListView k�nnen durch Doppelklick editiert werden.
		 */
		stakeholderListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						createStakeholderDialog();
					}
				}
			}
		});
	}

	/*
	 * Die Methode erstellt einen StakeholderDialog zum Bearbeiten oder L�schen
	 * des ausgew�hlten Stakeholder-Datensatzes.
	 */
	private void createStakeholderDialog() {
		int number = stakeholderListView.getSelectionModel().getSelectedIndex();
		Stakeholder stakeholder = stakeholders.get(number);
		final StakeholderDialog dialog = new StakeholderDialog(controller, stakeholder);

		dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent event) {
				if (dialog.hasErrorOccured() == true) {
					event.consume();
					dialog.resetError();
				} else {
					selectStakeholderData();
				}
			}

		});

		dialog.showAndWait();
	}

	/*
	 * Die Methode erstellt einen GlossaryEntryDialog zum Bearbeiten oder
	 * L�schen des ausgew�hlten Glossary-Datensatzes.
	 */
	private void createGlossaryEntryDialog() {
		int number = glossaryListView.getSelectionModel().getSelectedIndex();
		GlossaryEntry entry = glossaryEntries.get(number);
		final GlossaryEntryDialog dialog = new GlossaryEntryDialog(controller, entry);

		dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent event) {
				if (dialog.hasErrorOccured() == true) {
					event.consume();
					dialog.resetError();
				} else {
					selectGlossaryData();
				}
			}

		});

		dialog.showAndWait();
	}

	/*
	 * Die Methode cleart die Daten in den Textfeldern f�r einen
	 * Stakeholder-Eintrag.
	 */
	@FXML
	private void clearStakeholderData() {
		nameField.clear();
		functionField.clear();
		contactField.clear();
		airtimeField.clear();
	}

	/*
	 * Die Methode cleart die Daten in den Textfeldern f�r einen
	 * Glossar-Eintrag.
	 */
	@FXML
	private void clearGlossaryData() {
		termField.clear();
		descriptionField.clear();
	}

	/*
	 * Die Methode fragt alleAnnotation-Daten aus der Dantebank ab, um sie in
	 * der zugeh�rigen ListView anzuzeigen.
	 */
	private void selectAnnotationData() {
		if (recorder.getVideoName() != null) {
			annotations = SQLiteDataBase.getInstance().selectAnnotationData(recorder.getVideoName());

			ObservableList<Annotation> annos = FXCollections.observableArrayList(annotations);

			annotationListView.setItems(annos);
			annotationListView.setCellFactory(new Callback<ListView<Annotation>, ListCell<Annotation>>() {

				@Override
				public ListCell<Annotation> call(ListView<Annotation> param) {
					ListCell<Annotation> cell = new AnnotationListCell();
					return cell;
				}
			});

			/*
			 * Eintr�ge in der ListView k�nnen durch Doppelklick editiert
			 * werden.
			 */
			annotationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton().equals(MouseButton.PRIMARY)) {
						if (event.getClickCount() == 2) {
							DataAnnotation annotation = (DataAnnotation) annotationListView.getSelectionModel()
									.getSelectedItem();
							AnnotationTab tab = new AnnotationTab(RecorderController.this, annotation);
							Tab tabInPane = containsTab(tabPane.getTabs(), annotation.getId());
							if (tabInPane != null) {
								selectionModelTabs.select(tabInPane);
							} else {
								tabPane.getTabs().add(tab);
								selectionModelTabs.selectLast();
							}
						}
					}
				}
			});
		}
	}

	/**
	 * Die Methode aktualsiert den Tab der Darstellung einer Annotation mit
	 * ihren enthaltenen Daten.
	 * 
	 * @param annotation
	 *            Annotation, die zu aktualisieren ist
	 * @param toDelete
	 *            TRUE, falls die Annotation gel�scht worden ist, ansonsten
	 *            FALSE
	 */
	public void updateAnnotation(DataAnnotation annotation, boolean toDelete) {
		AnnotationTab tab = new AnnotationTab(RecorderController.this, annotation);
		int index = selectionModelTabs.getSelectedIndex();
		tabPane.getTabs().remove(index);
		if (toDelete == false) {
			tabPane.getTabs().add(index, tab);
			selectionModelTabs.select(index);
		}

		selectAnnotationData();
	}

	/*
	 * Die Methode pr�ft, ob ein bestimmter AnnotationTab bereits ge�ffnet ist.F
	 */
	private Tab containsTab(ObservableList<Tab> tabs, int id) {
		for (Tab tab : tabs) {
			AnnotationTab annoTab = (AnnotationTab) tab;
			if (annoTab.getId().equalsIgnoreCase(Integer.toString(id))) {
				return tab;
			}
		}
		return null;
	}

	private void addDataAnnotation(DataAnnotation annotation) {
		SQLiteDataBase.getInstance().insertDataAnnotation(annotation, recorder.getVideoName(), timeStamp);
		selectAnnotationData();
		tabPane.getTabs().add(new AnnotationTab(this, (DataAnnotation) annotations.get(0)));
		selectionModelTabs.selectLast();
	}

	@FXML
	private void addImportantAnnotation() {
		SQLiteDataBase.getInstance().insertAnnotationWithButton(DataAnnotationType.IMPORTANT, recorder.getVideoName(),
				timeStamp);
		selectAnnotationData();
		tabPane.getTabs().add(new AnnotationTab(this, (DataAnnotation) annotations.get(0)));
		selectionModelTabs.selectLast();
	}

	@FXML
	private void addConflictAnnotation() {
		SQLiteDataBase.getInstance().insertAnnotationWithButton(DataAnnotationType.CONFLICT, recorder.getVideoName(),
				timeStamp);
		selectAnnotationData();
		tabPane.getTabs().add(new AnnotationTab(this, (DataAnnotation) annotations.get(0)));
		selectionModelTabs.selectLast();
	}

	@FXML
	private void addRequirementAnnotation() {
		SQLiteDataBase.getInstance().insertAnnotationWithButton(DataAnnotationType.REQUIREMENT, recorder.getVideoName(),
				timeStamp);
		selectAnnotationData();
		tabPane.getTabs().add(new AnnotationTab(this, (DataAnnotation) annotations.get(0)));
		selectionModelTabs.selectLast();
	}

	@FXML
	private void addQuestionAnnotation() {
		SQLiteDataBase.getInstance().insertAnnotationWithButton(DataAnnotationType.QUESTION, recorder.getVideoName(),
				timeStamp);
		selectAnnotationData();
		tabPane.getTabs().add(new AnnotationTab(this, (DataAnnotation) annotations.get(0)));
		selectionModelTabs.selectLast();
	}

	/*
	 * Die Methode erlauben den Wechsel zur Hauptansicht oder dem Analyzer.
	 * M�ssen aber noch bez�glich der Datenhaltung korrekt eingebaut werden.
	 */
	// @FXML
	// private void goToScreen1(ActionEvent event) {
	// controller.setNode(ReqVidAMain.mainSceneID);
	// }
	//
	// @FXML
	// private void goToScreen3(ActionEvent event) {
	// controller.setNode(ReqVidAMain.analyzerSceneID);
	// }

	@FXML
	private void saveProtocoll() {
		String content = editor.getHtmlText();

		File dir = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\");
		if (dir.exists() == false) {
			dir.mkdir();
		}

		File file = new File(
				ReqVidAMain.absoluteFolderPath + "\\exported\\" + ReqVidAMain.workshopName + " - Protocoll.html");
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	private void exportData() {

		File dir = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\");
		if (dir.exists() == false) {
			dir.mkdir();
		}

		exportStakeholderData();
		exportGlossaryData();
		exportAnnotationData();
	}

	private void exportStakeholderData() {
		ArrayList<Stakeholder> stakeholders = SQLiteDataBase.getInstance().selectStakeholderData();

		String output = "";
		Iterator<Stakeholder> iterator = stakeholders.iterator();
		while (iterator.hasNext()) {
			Stakeholder stakeholder = iterator.next();

			output = output + stakeholder.toString() + "\n";
			if (iterator.hasNext()) {
				output = output + "\n";
			}
		}

		File file = new File(
				ReqVidAMain.absoluteFolderPath + "\\exported\\" + ReqVidAMain.workshopName + " - Stakeholders.txt");
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(output);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void exportGlossaryData() {
		ArrayList<GlossaryEntry> glossary = SQLiteDataBase.getInstance().selectGlossaryData();

		String output = "";
		Iterator<GlossaryEntry> iterator = glossary.iterator();
		while (iterator.hasNext()) {
			GlossaryEntry entry = iterator.next();

			output = output + entry.toString() + "\n";
			if (iterator.hasNext()) {
				output = output + "\n";
			}
		}

		File file = new File(
				ReqVidAMain.absoluteFolderPath + "\\exported\\" + ReqVidAMain.workshopName + " - Glossary.txt");
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(output);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void exportAnnotationData() {
		if (recorder.getVideoName() != null) {
			ArrayList<Annotation> annos = SQLiteDataBase.getInstance().selectAnnotationData(recorder.getVideoName());

			Collections.reverse(annos);

			String output = "";
			Iterator<Annotation> iterator = annos.iterator();
			while (iterator.hasNext()) {
				Annotation annotation = iterator.next();

				DataAnnotation anno = (DataAnnotation) annotation;
				switch (anno.getType()) {
				case IMPORTANT:
					ImportantAnnotation importantAnno = (ImportantAnnotation) anno;
					output = output + importantAnno.toString() + "\n";
					break;
				case QUESTION:
					QuestionAnnotation questionAnno = (QuestionAnnotation) anno;
					output = output + questionAnno.toString() + "\n";
					break;
				case CONFLICT:
					ConflictAnnotation conflictAnno = (ConflictAnnotation) anno;
					output = output + conflictAnno.toString() + "\n";
					break;
				case REQUIREMENT:
					RequirementAnnotation requirementAnno = (RequirementAnnotation) anno;
					output = output + requirementAnno.toString() + "\n";
					break;
				case CUSTOM:
					CustomAnnotation customAnno = (CustomAnnotation) anno;
					output = output + customAnno.toString() + "\n";
					break;
				}
			}

			File file = new File(
					ReqVidAMain.absoluteFolderPath + "\\exported\\" + ReqVidAMain.workshopName + " - Annotations.txt");
			try {
				FileWriter fileWriter = null;

				fileWriter = new FileWriter(file);
				fileWriter.write(output);
				fileWriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Setter-Methode f�r die bisherige Aufnahmedauer des Videos
	 * 
	 * @param timeStamp
	 *            aktuelle Aufnahmezeit des Videos
	 */
	public void setRecordingTime(long timeStamp) {
		this.timeStamp = timeStamp;
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				recordingTime.setText(Helper.milliToTimeFormat1(timeStamp));
			}
		});
	}

	/**
	 * Setter-Methode f�r das aktuelle Videobild
	 * 
	 * @param frame
	 *            aktuelle Videobild
	 */
	public void setFrame(Image frame) {
		frameView.setImage(frame);
	}
}
