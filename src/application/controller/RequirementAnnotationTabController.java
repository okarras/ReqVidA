package application.controller;

import java.util.Arrays;
import java.util.List;

import application.model.ControlledScene;
import application.model.annotation.DataAnnotation;
import application.model.annotation.RequirementAnnotation;
import application.model.enums.ObligationType;
import application.model.enums.Priority;
import application.model.enums.RequirementFormat;
import application.model.enums.RequirementType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Die Klasse dient als Controller eines RequirementAnnotationTabs, um mit den
 * zugeh�rigen GUI Elementen interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class RequirementAnnotationTabController extends AnnotationTabController {

	@FXML
	private ChoiceBox<RequirementType> granularity;

	@FXML
	private TextField requirement;

	@FXML
	private TextField source;

	@FXML
	private TextField rationale;

	@FXML
	private ChoiceBox<ObligationType> obligation;

	@FXML
	private ChoiceBox<Priority> priority;

	@FXML
	private ChoiceBox<RequirementFormat> displayFormat;

	@FXML
	private Button addArtefactButton;

	@FXML
	private TextField artefact;

	@FXML
	private ListView<String> artefactListView;

	private RequirementAnnotation requirementAnno;
	private ObservableList<String> artefacts;

	/**
	 * Konstruktor f�r ein RequirementAnnotationTabController-Objekt
	 * 
	 * @param controller
	 *            des Parent Node, wird f�r bestimmte GUI-Aktualisierungen
	 *            ben�tigt
	 * @param annotation
	 *            die in dem Tab angezeigt wird
	 */
	public RequirementAnnotationTabController(ControlledScene controller,
			DataAnnotation annotation) {
		super(controller, annotation);
	}

	@Override
	public void init() {
		super.init();

		requirementAnno = (RequirementAnnotation) getAnnotation();

		List<RequirementType> granularities = Arrays.asList(RequirementType
				.values());
		ObservableList<RequirementType> granularityList = FXCollections
				.observableArrayList(granularities);
		granularity.setItems(granularityList);

		granularity.getSelectionModel()
				.select(requirementAnno.getGranularity());

		granularity.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						setNotSaved(true);
					}
				});

		if (requirementAnno.getDisplayFormat() == RequirementFormat.TEXT) {
			requirement.setDisable(false);
			requirement.setPromptText("requirement");
			requirement.setText(requirementAnno.getRequirement());
		} else if (requirementAnno.getDisplayFormat() == RequirementFormat.USE_CASE) {
			requirement.setDisable(true);
			requirement
					.setPromptText("Use Case Editor needs to be integrated.");
			requirement.clear();
		}

		source.setText(requirementAnno.getSource());
		rationale.setText(requirementAnno.getRationale());

		List<ObligationType> obligations = Arrays.asList(ObligationType
				.values());
		ObservableList<ObligationType> obligationList = FXCollections
				.observableArrayList(obligations);
		obligation.setItems(obligationList);
		obligation.getSelectionModel().select(requirementAnno.getObligation());

		obligation.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						setNotSaved(true);
					}
				});

		List<Priority> priorities = Arrays.asList(Priority.values());
		ObservableList<Priority> priorityList = FXCollections
				.observableArrayList(priorities);
		priority.setItems(priorityList);
		priority.getSelectionModel().select(requirementAnno.getPriority());

		priority.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						setNotSaved(true);
					}
				});

		List<RequirementFormat> formats = Arrays.asList(RequirementFormat
				.values());
		ObservableList<RequirementFormat> formatList = FXCollections
				.observableArrayList(formats);
		displayFormat.setItems(formatList);

		displayFormat.getSelectionModel().select(
				requirementAnno.getDisplayFormat());

		displayFormat.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						if (oldValue != newValue) {
							setNotSaved(true);
							if (newValue.intValue() == RequirementFormat.USE_CASE
									.getValue()) {
								requirement.setDisable(true);
								requirement
										.setPromptText("Use Case Editor needs to be integrated.");
								requirement.clear();
							} else if (newValue.intValue() == RequirementFormat.TEXT
									.getValue()) {
								requirement.setDisable(false);
								requirement.setPromptText("requirement");
								requirement.setText(requirementAnno
										.getRequirement());
							}
						}
					}
				});

		artefact.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (newValue.trim().equalsIgnoreCase("") == false) {
					addArtefactButton.setDisable(false);
				} else {
					addArtefactButton.setDisable(true);
				}
			}
		});

		artefacts = FXCollections.observableArrayList(requirementAnno
				.getArtefactList());

		artefactListView.setItems(artefacts);

		source.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));
		rationale.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));
		requirement.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));

		artefacts.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends String> c) {
				setNotSaved(true);
			}
		});
	}

	@FXML
	private void addArtefact() {
		if (artefact.getText().trim().equalsIgnoreCase("") == false) {
			requirementAnno.getArtefactList().add(artefact.getText());
			artefacts.add(artefact.getText());
			artefact.clear();
		}
	}

	@Override
	protected void saveAnnotation() {
		requirementAnno.setGranularity(granularity.getValue());

		String requirementInput = requirement.getText().trim();
		if (requirementInput != null) {
			requirementAnno.setRequirement(requirementInput);
		}

		String sourceInput = source.getText().trim();
		if (sourceInput != null) {
			requirementAnno.setSource(sourceInput);
		}
		String rationaleInput = rationale.getText().trim();
		if (rationaleInput != null) {
			requirementAnno.setRationale(rationaleInput);
		}

		requirementAnno.setObligation(obligation.getValue());
		requirementAnno.setPriority(priority.getValue());
		requirementAnno.setDisplayFormat(displayFormat.getValue());

		setAnnotation(requirementAnno);
		super.saveAnnotation();
	}
}
