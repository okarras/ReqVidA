package application.controller;

import application.model.ControlledScene;
import application.model.annotation.ConflictAnnotation;
import application.model.annotation.DataAnnotation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Die Klasse dient als Controller eines ConflictAnnotationTabs, um mit den
 * zugeh�rigen GUI-Elementen interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class ConflictAnnotationTabController extends AnnotationTabController {

	@FXML
	private TextField conflict;

	@FXML
	private Button addReferenceButton;

	@FXML
	private TextField reference;

	@FXML
	private ListView<String> referenceListView;

	private ConflictAnnotation conflictAnno;
	private ObservableList<String> references;

	/**
	 * Konstruktor f�r ein ConflictAnnotationTabController-Objekt
	 * 
	 * @param controller
	 *            des Parent Node, wird f�r bestimmte GUI-Aktualisierungen
	 *            ben�tigt
	 * @param annotation
	 *            die in dem Tab angezeigt wird
	 */
	public ConflictAnnotationTabController(ControlledScene controller,
			DataAnnotation annotation) {
		super(controller, annotation);
	}

	@Override
	public void init() {
		super.init();

		conflictAnno = (ConflictAnnotation) getAnnotation();
		conflict.setText(conflictAnno.getConflict());

		reference.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (newValue.trim().equalsIgnoreCase("") == false) {
					addReferenceButton.setDisable(false);
				} else {
					addReferenceButton.setDisable(true);
				}
			}
		});

		references = FXCollections.observableArrayList(conflictAnno
				.getReferenceList());

		referenceListView.setItems(references);

		conflict.textProperty().addListener(
				new AnnotationDataStringChangeListener(this));

		references.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends String> c) {
				setNotSaved(true);
			}
		});
	}

	@FXML
	private void addReference() {
		if (reference.getText().matches("([0-9]{2}):([0-5][0-9]):([0-5][0-9])")) {
			conflictAnno.getReferenceList().add(reference.getText());
			references.add(reference.getText());
			reference.clear();
		}
	}

	@Override
	protected void saveAnnotation() {
		String conflictInput = conflict.getText().trim();

		if (conflictInput != null) {
			conflictAnno.setConflict(conflictInput);
		}

		setAnnotation(conflictAnno);
		super.saveAnnotation();
	}
}
