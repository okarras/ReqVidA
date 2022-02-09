package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
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
import application.model.enums.DataAnnotationType;
import application.util.DomParser;
import application.util.Helper;
import application.view.AnnotationListCell;
import application.view.AnnotationTab;
import application.view.CustomSlider;
import application.view.GlossaryEntryDialog;
import application.view.GlossaryListCell;
import application.view.StakeholderDialog;
import application.view.StakeholderListCell;
import application.view.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Die Klasse dient als Controller der AnalyzerScene, um mit den enthaltenen
 * GUI-Elementen aus dem FXML-File entsprechend interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class AnalyzerController implements Initializable, ControlledScene {

	private boolean repeat = false;
	private Media media;
	private Duration duration;
	private MediaPlayer mediaPlayer;
	private String videoName = "";

	private ScenesController controller;

	@FXML
	private MediaView mediaView;
	@FXML
	private Button playButton;
	@FXML
	private CustomSlider timeSlider;
	@FXML
	private CustomSlider volumeSlider;
	@FXML
	private Label playTime;
	@FXML
	private Label volume;
	@FXML
	private ToggleButton checkRepeat;

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
	private SplitPane splitPane;
	private HashMap<String, DataAnnotation> shortcuts;
	private final String shortcutSettings = "./settings/shortcuts.xml";
	private final String customAnnotationSettings = "./settings/customannotations.xml";

	@FXML
	private Accordion accordion;
	@FXML
	private TitledPane annotationPane;

	@FXML
	private Timeline timeline;

	@FXML
	private HTMLEditor editor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void setSceneParent(ScenesController sceneParent) {
		controller = sceneParent;
	}

	@Override
	public void setup() {
		DomParser.createCustomAnnotations(new File(customAnnotationSettings));

		shortcuts = DomParser.createShortcuts(new File(shortcutSettings));

		splitPane.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				KeyCode key = event.getCode();
				if (shortcuts.containsKey(key.getName())) {
					addDataAnnotation(shortcuts.get(key.getName()));
				}
			}
		});

		String content = loadProtocoll();
		editor.setHtmlText(content);

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
				return ValidationResult.fromMessageIf(control,
						"Name is required.", Severity.ERROR, condition);
			}
		};

		Validator<String> termValidator = new Validator<String>() {

			@Override
			public ValidationResult apply(Control control, String value) {
				boolean condition = false;
				if (value.trim().equalsIgnoreCase("")) {
					condition = true;
				}
				return ValidationResult.fromMessageIf(control,
						"Term is required.", Severity.ERROR, condition);
			}
		};

		validationSupport.registerValidator(nameField, true, nameValidator);
		validationSupport.registerValidator(termField, true, termValidator);

		selectGlossaryData();
		termField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {
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
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {
				if (newValue.trim().equalsIgnoreCase("")) {
					addStakeholderButton.disableProperty().set(true);
				} else {
					addStakeholderButton.disableProperty().set(false);
				}
			}

		});

		setupMediaPlayer();

		setupTimeSlider();

		setupVolumeSlider();

		DataAnnotationType[] types = DataAnnotationType.values();
		String[] annotationTypes = new String[types.length];

		for (int i = 0; i < types.length; i++) {
			annotationTypes[i] = types[i].toString();
		}

		timeline.setController(this);
		ArrayUtils.reverse(annotationTypes);
		timeline.setAnnotationTypes(annotationTypes);
		selectAnnotationData();
	}

	private String loadProtocoll() {
		StringBuilder stringBuffer = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {

			File dir = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\");
			if (dir.exists() == false) {
				dir.mkdir();
			}

			File file = new File(ReqVidAMain.absoluteFolderPath
					+ "\\exported\\" + ReqVidAMain.workshopName
					+ " - Protocoll.html");
			if (file.exists() == false) {
				file.createNewFile();
			}
			bufferedReader = new BufferedReader(new FileReader(file));

			String text;
			while ((text = bufferedReader.readLine()) != null) {
				stringBuffer.append(text);
			}

			bufferedReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return stringBuffer.toString();
	}

	private void setupMediaPlayer() {
		try {
			videoName = new File(ReqVidAMain.videoFile).getName();
			media = new Media(new File(ReqVidAMain.videoFile).toURI().toURL()
					.toExternalForm());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		mediaPlayer = new MediaPlayer(media);

		mediaPlayer.currentTimeProperty().addListener(
				new ChangeListener<Duration>() {

					@Override
					public void changed(
							ObservableValue<? extends Duration> observable,
							Duration oldValue, Duration newValue) {
						updateValues();
					}
				});

		mediaPlayer.volumeProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				updateValues();
			}
		});

		mediaPlayer.setOnPlaying(new Runnable() {
			public void run() {
				if (mediaPlayer.getCurrentTime().equals(
						mediaPlayer.getStopTime())) {
					mediaPlayer.seek(mediaPlayer.getStartTime());
				}
				playButton.setText("||");
			}
		});

		mediaPlayer.setOnPaused(new Runnable() {
			public void run() {
				playButton.setText(">");
			}
		});

		mediaPlayer.setOnStopped(new Runnable() {

			@Override
			public void run() {
				playButton.setText(">");
			}
		});

		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				duration = mediaPlayer.getMedia().getDuration();

				timeline.setxAxisUpperBound(duration.toMillis());
				timeline.setSliderHighValue(duration.toMillis());
				selectAnnotationData();
				timeline.setup();

				updateValues();
			}
		});

		mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				if (!repeat) {
					playButton.setText(">");
					mediaPlayer.seek(mediaPlayer.getStartTime());
					mediaPlayer.pause();
				}
			}
		});

		mediaPlayer.setAutoPlay(false);

		mediaView.setMediaPlayer(mediaPlayer);

		importantButton.setDisable(false);
		requirementButton.setDisable(false);
		questionButton.setDisable(false);
		conflictButton.setDisable(false);
	}

	private void setupTimeSlider() {

		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (timeSlider.isValueChanging()
						|| timeSlider.mouseWasPressedWithinLast(100)
						|| timeSlider.mouseWasDraggedWithonLast(100)) {
					mediaPlayer.seek(duration.multiply(newValue.doubleValue() / 100.0));
					updateValues();

					// Sonderfall, wenn der thumb des sliders bis zum Ende
					// gezogen wird
					if (newValue.doubleValue() == 100.0) {
						if (mediaPlayer.getStatus() == Status.PLAYING) {
							mediaPlayer.pause();
							mediaPlayer.seek(mediaPlayer.getStartTime());
							updateValues();
							mediaPlayer.play();
						} else {
							mediaPlayer.seek(mediaPlayer.getStartTime());
							updateValues();
						}
					}
				}
			}
		});
	}

	private void setupVolumeSlider() {

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (volumeSlider.isValueChanging()
						|| volumeSlider.mouseWasPressedWithinLast(100)
						|| volumeSlider.mouseWasDraggedWithonLast(100)) {
					mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
				}
			}
		});
	}

	protected void updateValues() {
		if (playTime != null && timeSlider != null && volumeSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mediaPlayer.getCurrentTime();

					timeSlider.setValue(currentTime.toMillis()
							/ duration.toMillis() * 100.0);
					playTime.setText(formatTime(currentTime, duration));

					volumeSlider.setValue((int) Math.round(mediaPlayer
							.getVolume() * 100));
					volume.setText(Integer.toString((int) Math
							.round(mediaPlayer.getVolume() * 100)) + " %");
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
				- elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60
					- durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d / %d:%02d:%02d",
						elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d / %02d:%02d", elapsedMinutes,
						elapsedSeconds, durationMinutes, durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes,
						elapsedSeconds);
			}
		}
	}

	@FXML
	public void togglePlayButton() {
		Status status = mediaPlayer.getStatus();

		if (status == Status.UNKNOWN || status == Status.HALTED) {
			// don't do anything in these states
			return;
		}

		if (status == Status.PAUSED || status == Status.READY
				|| status == Status.STOPPED) {
			if (mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) {
				mediaPlayer.seek(mediaPlayer.getStartTime());
			}
			mediaPlayer.play();
		} else {
			mediaPlayer.pause();
		}
	}

	@FXML
	public void clickOnTimeSlider() {
		mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
		updateValues();
	}

	@FXML
	public void clickOnVolumeSlider() {
		mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
		updateValues();
	}

	@FXML
	public void toggleRepeat() {
		repeat = !repeat;
		mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
	}

	@FXML
	void saveAsPng() {
		WritableImage image = mediaView
				.snapshot(new SnapshotParameters(), null);

		File dir = new File(ReqVidAMain.absoluteFolderPath + "\\snapshots\\");
		if (dir.exists() == false) {
			dir.mkdir();
		}

		File file = new File(ReqVidAMain.absoluteFolderPath
				+ "\\snapshots\\"
				+ "snapshot_"
				+ Helper.milliToTimeFormat2((long) mediaPlayer.getCurrentTime()
						.toMillis()) + ".png");

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void seekTo(long time) {
		mediaPlayer.seek(new Duration(time));
		updateValues();
	}

	@Override
	public void close() {
		// nothing to do
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

		SQLiteDataBase.getInstance().insertStakeholderData(name, function,
				contact, airtime);
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

		ObservableList<GlossaryEntry> entries = FXCollections
				.observableArrayList(glossaryEntries);

		glossaryListView.setItems(entries);
		glossaryListView
				.setCellFactory(new Callback<ListView<GlossaryEntry>, ListCell<GlossaryEntry>>() {

					@Override
					public ListCell<GlossaryEntry> call(
							ListView<GlossaryEntry> param) {
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

		ObservableList<Stakeholder> holders = FXCollections
				.observableArrayList(stakeholders);

		stakeholderListView.setItems(holders);
		stakeholderListView
				.setCellFactory(new Callback<ListView<Stakeholder>, ListCell<Stakeholder>>() {

					@Override
					public ListCell<Stakeholder> call(
							ListView<Stakeholder> param) {
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
		final StakeholderDialog dialog = new StakeholderDialog(controller,
				stakeholder);

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
		final GlossaryEntryDialog dialog = new GlossaryEntryDialog(controller,
				entry);

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

	@FXML
	private void clearStakeholderData() {
		nameField.clear();
		functionField.clear();
		contactField.clear();
		airtimeField.clear();
	}

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
		annotations = SQLiteDataBase.getInstance().selectAnnotationData(
				videoName);

		timeline.setAnnotationData(annotations);

		ObservableList<Annotation> annos = FXCollections
				.observableArrayList(annotations);

		annotationListView.setItems(annos);
		annotationListView
				.setCellFactory(new Callback<ListView<Annotation>, ListCell<Annotation>>() {

					@Override
					public ListCell<Annotation> call(ListView<Annotation> param) {
						ListCell<Annotation> cell = new AnnotationListCell();
						return cell;
					}
				});

		/*
		 * Eintr�ge in der ListView k�nnen durch Doppelklick editiert werden.
		 */
		annotationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						DataAnnotation annotation = (DataAnnotation) annotationListView
								.getSelectionModel().getSelectedItem();
						openAnnotationTab(annotation);
					}
				}
			}
		});
	}

	public void openAnnotationTab(DataAnnotation annotation) {

		AnnotationTab tab = new AnnotationTab(AnalyzerController.this,
				annotation);
		Tab tabInPane = containsTab(tabPane.getTabs(), annotation.getId());
		if (tabInPane != null) {
			selectionModelTabs.select(tabInPane);
		} else {
			tabPane.getTabs().add(tab);
			selectionModelTabs.selectLast();
		}
	}

	public void updateAnnotations(DataAnnotation annotation, boolean toDelete) {
		AnnotationTab tab = new AnnotationTab(AnalyzerController.this,
				annotation);
		int index = selectionModelTabs.getSelectedIndex();
		tabPane.getTabs().remove(index);
		if (toDelete == false) {
			tabPane.getTabs().add(index, tab);
			selectionModelTabs.select(index);
		}

		selectAnnotationData();
	}

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
		int id = SQLiteDataBase.getInstance().insertDataAnnotation(annotation,
				videoName, (long) mediaPlayer.getCurrentTime().toMillis());
		selectAnnotationData();
		updateTabPane(id);
	}

	private void updateTabPane(int id) {
		for (Annotation annotation : annotations) {
			if(annotation.getId() == id) {
				tabPane.getTabs().add(
						new AnnotationTab(this, (DataAnnotation) annotation));
				selectionModelTabs.selectLast();
			}
		}
	}
	
	@FXML
	private void addImportantAnnotation() {
		int id = SQLiteDataBase.getInstance().insertAnnotationWithButton(
				DataAnnotationType.IMPORTANT, videoName,
				(long) mediaPlayer.getCurrentTime().toMillis());
		selectAnnotationData();
		updateTabPane(id);
	}

	@FXML
	private void addConflictAnnotation() {
		int id = SQLiteDataBase.getInstance().insertAnnotationWithButton(
				DataAnnotationType.CONFLICT, videoName,
				(long) mediaPlayer.getCurrentTime().toMillis());
		selectAnnotationData();
		updateTabPane(id);
	}

	@FXML
	private void addRequirementAnnotation() {
		int id = SQLiteDataBase.getInstance().insertAnnotationWithButton(
				DataAnnotationType.REQUIREMENT, videoName,
				(long) mediaPlayer.getCurrentTime().toMillis());
		selectAnnotationData();
		updateTabPane(id);
	}

	@FXML
	private void addQuestionAnnotation() {
		int id = SQLiteDataBase.getInstance().insertAnnotationWithButton(
				DataAnnotationType.QUESTION, videoName,
				(long) mediaPlayer.getCurrentTime().toMillis());
		selectAnnotationData();
		updateTabPane(id);
	}

	@FXML
	private void saveProtocoll() {
		String content = editor.getHtmlText();

		File file = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\"
				+ ReqVidAMain.workshopName + " - Protocoll.html");
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
		ArrayList<Stakeholder> stakeholders = SQLiteDataBase.getInstance()
				.selectStakeholderData();

		String output = "";
		Iterator<Stakeholder> iterator = stakeholders.iterator();
		while (iterator.hasNext()) {
			Stakeholder stakeholder = iterator.next();

			output = output + stakeholder.toString() + "\n";
			if (iterator.hasNext()) {
				output = output + "\n";
			}
		}

		File file = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\"
				+ ReqVidAMain.workshopName + " - Stakeholders.txt");
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
		ArrayList<GlossaryEntry> glossary = SQLiteDataBase.getInstance()
				.selectGlossaryData();

		String output = "";
		Iterator<GlossaryEntry> iterator = glossary.iterator();
		while (iterator.hasNext()) {
			GlossaryEntry entry = iterator.next();

			output = output + entry.toString() + "\n";
			if (iterator.hasNext()) {
				output = output + "\n";
			}
		}

		File file = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\"
				+ ReqVidAMain.workshopName + " - Glossary.txt");
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
		ArrayList<Annotation> annos = SQLiteDataBase.getInstance()
				.selectAnnotationData(videoName);

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

		File file = new File(ReqVidAMain.absoluteFolderPath + "\\exported\\"
				+ ReqVidAMain.workshopName + " - Annotations.txt");
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(output);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Die Methode erlauben den Wechsel zur Hauptansicht oder dem Recorder.
	 * M�ssen aber noch bez�glich der Datenhaltung korrekt eingebaut werden.
	 */
	// @FXML
	// private void goToScreen1(ActionEvent event) {
	// controller.setNode(ReqVidAMain.mainSceneID);
	// }
	//
	// @FXML
	// private void goToScreen2(ActionEvent event) {
	// controller.setNode(ReqVidAMain.recorderSceneID);
	// }

	/**
	 * Die Methode erlaubt die Ansteuerung einer bestimmten Stelle im Video.
	 * 
	 * @param moment
	 *            Zeit (in ms) der im Video angesteuert werden soll
	 */
	public void showInVideo(long moment) {
		Duration annoDuration = new Duration(moment);
		mediaPlayer.seek(annoDuration);
		updateValues();
	}

}
