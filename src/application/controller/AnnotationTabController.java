package application.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.database.SQLiteDataBase;
import application.model.ControlledScene;
import application.model.annotation.ConflictAnnotation;
import application.model.annotation.CustomAnnotation;
import application.model.annotation.DataAnnotation;
import application.model.annotation.ImportantAnnotation;
import application.model.annotation.QuestionAnnotation;
import application.model.annotation.RequirementAnnotation;
import application.model.enums.DataAnnotationType;
import application.util.DomParser;
import application.util.Helper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Die Klasse dient als genereller AnnotationTabController, vom dem alle
 * verfeinertern AnnotationTab-Arten erben. Er enth�lt Methoden und Attribute
 * zur Interaktion mit GUI-Elementen �ber die eine jede Annotation bzw. ein
 * jeder AnnotationTab verf�gt.
 * 
 * @author Oliver Karras
 *
 */
public class AnnotationTabController implements Initializable {

	@FXML
	private Label id;

	@FXML
	private TextField name;

	@FXML
	private Label videoName;

	@FXML
	private Button momentButton;

	@FXML
	Label momentLabel;

	@FXML
	private ChoiceBox<DataAnnotationType> type;

	@FXML
	private ChoiceBox<String> customid;

	@FXML
	private TextArea comment;

	private DataAnnotation annotation;
	private ControlledScene controller;

	@FXML
	private Button saveButton;

	@FXML
	private Button restoreButton;
	private static boolean notSaved = false;

	/**
	 * Konstruktor f�r ein AnnotationTabController-Objekt
	 * 
	 * @param controller
	 *            des Parent Node, wird f�r bestimmte GUI-Aktualisierungen
	 *            ben�tigt
	 * @param annotation
	 *            die in dem Tab angezeigt wird
	 */
	public AnnotationTabController(ControlledScene controller,
			DataAnnotation annotation) {
		this.controller = controller;
		this.annotation = annotation;
	}

	/**
	 * Die Methode dient zur Initialisierung des zugeh�rigen AnnotationTabs zur
	 * Anzeige der Daten einer Annotation.
	 */
	public void init() {
		setNotSaved(notSaved);

		id.setText(Integer.toString(annotation.getId()));
		name.setText(annotation.getName());
		videoName.setText(annotation.getVideoName());

		if (controller instanceof RecorderController) {
			momentLabel
					.setText(Helper.milliToTimeFormat1(annotation.getMoment()));
		} else {
			momentButton.setText(Helper.milliToTimeFormat1(annotation
					.getMoment()));
		}

		List<CustomAnnotation> customAnnotations = DomParser
				.getCustomAnnotationList();
		List<String> customIDs = new ArrayList<String>();
		for (CustomAnnotation customAnnotation : customAnnotations) {
			customIDs.add(customAnnotation.getCustomAnnoID());
		}
		if (customIDs.isEmpty()
				|| annotation.getType() != DataAnnotationType.CUSTOM) {
			customid.setVisible(false);
		} else {
			customid.setVisible(true);
			final ObservableList<String> customIDList = FXCollections
					.observableArrayList(customIDs);
			customid.setItems(customIDList);
			CustomAnnotation customAnno = (CustomAnnotation) annotation;
			customid.getSelectionModel().select(customAnno.getCustomAnnoID());

			customid.getSelectionModel().selectedIndexProperty()
					.addListener(new ChangeListener<Number>() {

						@Override
						public void changed(
								ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue) {

							if (oldValue != newValue) {
								setNotSaved(true);
								String selectedCustomID = customIDList
										.get(newValue.intValue());

								List<CustomAnnotation> customAnnotations = DomParser
										.getCustomAnnotationList();
								for (CustomAnnotation customAnno : customAnnotations) {
									if (customAnno.getCustomAnnoID()
											.equalsIgnoreCase(selectedCustomID)) {
										CustomAnnotation anno = new CustomAnnotation();

										anno.setId(annotation.getId());
										anno.setName(name.getText());
										anno.setVideoName(annotation
												.getVideoName());
										anno.setMoment(annotation.getMoment());
										anno.setType(annotation.getType());
										anno.setComment(comment.getText());

										anno.setCustomAnnoID(customAnno
												.getCustomAnnoID());
										anno.setAttribute1(customAnno
												.getAttribute1());
										anno.setAttribute2(customAnno
												.getAttribute2());
										anno.setAttribute3(customAnno
												.getAttribute3());
										anno.setAttribute4(customAnno
												.getAttribute4());
										anno.setAttribute5(customAnno
												.getAttribute5());

										if (controller instanceof RecorderController) {
											RecorderController recorderController = (RecorderController) controller;
											recorderController
													.updateAnnotation(anno,
															false);
										} else if (controller instanceof AnalyzerController) {
											AnalyzerController analyzerController = (AnalyzerController) controller;
											analyzerController
													.updateAnnotations(anno,
															false);
										}
									}
								}

							}

						}
					});
		}

		List<DataAnnotationType> types = Arrays.asList(DataAnnotationType
				.values());
		final ObservableList<DataAnnotationType> typeList = FXCollections
				.observableArrayList(types);
		type.setItems(typeList);

		type.getSelectionModel().select(annotation.getType());

		type.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						if (oldValue != newValue) {
							setNotSaved(true);
							DataAnnotationType selectedType = typeList
									.get(newValue.intValue());

							DataAnnotation anno = null;
							switch (selectedType) {
							case IMPORTANT:
								anno = new ImportantAnnotation();
								break;
							case QUESTION:
								anno = new QuestionAnnotation();
								break;
							case CONFLICT:
								anno = new ConflictAnnotation();
								break;
							case REQUIREMENT:
								anno = new RequirementAnnotation();
								break;
							case CUSTOM:
								anno = new CustomAnnotation();
								CustomAnnotation customAnno = new CustomAnnotation();
								CustomAnnotation customAnnotation = DomParser
										.getCustomAnnotationList().get(0);

								customAnno.setCustomAnnoID(customAnnotation
										.getCustomAnnoID());
								customAnno.setAttribute1(customAnnotation
										.getAttribute1());
								customAnno.setAttribute2(customAnnotation
										.getAttribute2());
								customAnno.setAttribute3(customAnnotation
										.getAttribute3());
								customAnno.setAttribute4(customAnnotation
										.getAttribute4());
								customAnno.setAttribute5(customAnnotation
										.getAttribute5());

								anno = customAnno;
								break;
							}

							anno.setId(annotation.getId());
							anno.setName(name.getText());
							anno.setVideoName(annotation.getVideoName());
							anno.setMoment(annotation.getMoment());
							anno.setType(selectedType);
							anno.setComment(comment.getText());

							if (controller instanceof RecorderController) {
								RecorderController recorderController = (RecorderController) controller;
								recorderController.updateAnnotation(anno,
										false);
							} else if (controller instanceof AnalyzerController) {
								AnalyzerController analyzerController = (AnalyzerController) controller;
								analyzerController.updateAnnotations(anno,
										false);
							}
						}
					}
				});

		comment.setText(annotation.getComment());

		name.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));
		comment.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));
	}

	/**
	 * Getter-Methode f�r die DataAnnotation, die im AnnotationTab angezeigt
	 * wird
	 * 
	 * @return DataAnnotation des Tabs
	 */
	public DataAnnotation getAnnotation() {
		return annotation;
	}

	/**
	 * Setter-Methode f�r die DataAnnotation, die im AnnotationTab angezeigt
	 * wird
	 * 
	 * @param annotation
	 *            die im AnnotationTab angezeigt werden soll
	 */
	public void setAnnotation(DataAnnotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Die Methode pr�ft ob eine �nderung an den Daten im AnnotationTab erfolgt
	 * ist, die noch nicht gespeichert worden ist.
	 * 
	 * @return TRUE, falls eine �nderung vorhanden ist, die noch nicht
	 *         gespeichert worden ist, ansonsten FALSE
	 */
	protected boolean isNotSaved() {
		return notSaved;
	}

	/**
	 * Setter-Methode zum Setzen des Booleans, der angibt, ob eine �nderung an
	 * den Daten einer Annotation erfolgt ist, die noch nicht gespeichert worden
	 * ist.
	 * 
	 * @param notSaved
	 *            gibt an, ob eine nicht gespeicherte �nderung erfolgt ist
	 */
	protected void setNotSaved(boolean notSaved) {
		AnnotationTabController.notSaved = notSaved;
		saveButton.setDisable(!notSaved);
		restoreButton.setDisable(!notSaved);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Die Methode dient zum Speichern ge�nderter Daten einer Annotation.
	 */
	@FXML
	protected void saveAnnotation() {
		setNotSaved(false);
		String nameInput = name.getText().trim();
		String commentInput = comment.getText().trim();
		DataAnnotationType typeInput = type.getValue();

		if (nameInput != null) {
			annotation.setName(nameInput);
		}
		if (commentInput != null) {
			annotation.setComment(commentInput);
		}
		if (typeInput != null) {
			annotation.setType(typeInput);
		}

		SQLiteDataBase.getInstance().updateAnnotationData(annotation);

		if (controller instanceof RecorderController) {
			RecorderController recorderController = (RecorderController) controller;
			recorderController.updateAnnotation(annotation, false);
		} else if (controller instanceof AnalyzerController) {
			AnalyzerController analyzerController = (AnalyzerController) controller;
			analyzerController.updateAnnotations(annotation, false);
		}
	}

	/**
	 * Die Methode dient zum L�schen einer Annotation in einem AnnotationTab.
	 */
	@FXML
	private void deleteAnnotation() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Delete the annotation.");
		alert.setContentText("Do you really want to delete the annotation?");

		Optional<ButtonType> result = alert.showAndWait();
		/*
		 * L�schung best�tigt
		 */
		if (result.get() == ButtonType.OK) {
			SQLiteDataBase.getInstance().deleteAnnotationData(
					annotation.getId());
			if (controller instanceof RecorderController) {
				RecorderController recorderController = (RecorderController) controller;
				recorderController.updateAnnotation(annotation, true);
			} else if (controller instanceof AnalyzerController) {
				AnalyzerController analyzerController = (AnalyzerController) controller;
				analyzerController.updateAnnotations(annotation, true);
			}
			/*
			 * L�schung abgebrochen
			 */
		} else {
			alert.close();
		}
	}

	/**
	 * Die Methode dient zum Wiederherstellen der urspr�nglichen Daten einer
	 * Annotation, falls die zur�ckgesetzt werden soll auf ihren zuletzt
	 * gespeicherteFn Zustand.
	 */
	@FXML
	private void restoreAnnotation() {
		setNotSaved(false);
		DataAnnotation anno = SQLiteDataBase.getInstance()
				.selectAnnotationByID(annotation.getId());

		if (controller instanceof RecorderController) {
			RecorderController recorderController = (RecorderController) controller;
			recorderController.updateAnnotation(anno, false);
		} else if (controller instanceof AnalyzerController) {
			AnalyzerController analyzerController = (AnalyzerController) controller;
			analyzerController.updateAnnotations(anno, false);
		}
	}

	@FXML
	private void showInVideo() {
		if (controller instanceof AnalyzerController) {
			AnalyzerController analyzerController = (AnalyzerController) controller;
			analyzerController.showInVideo(annotation.getMoment());
		}
	}
}
