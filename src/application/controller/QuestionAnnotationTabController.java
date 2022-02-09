package application.controller;

import application.model.ControlledScene;
import application.model.annotation.DataAnnotation;
import application.model.annotation.QuestionAnnotation;
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
 * Die Klasse dient als Controller eins QuestionAnnotationTabs, um mit den
 * enthaltenen GUI-Elementen aus dem zugeh�rigen FXML-File entsprechend
 * interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class QuestionAnnotationTabController extends AnnotationTabController {

	@FXML
	private Button addQuestionButton;

	@FXML
	private TextField question;

	@FXML
	private ListView<String> questionListView;

	private QuestionAnnotation questionAnno;
	private ObservableList<String> questions;

	@FXML
	private Button saveButton;

	@FXML
	private Button restoreButton;

	/**
	 * Konstruktor f�r ein QuestionAnnotationTabController-Objekt
	 * 
	 * @param controller
	 *            des Parent Node, wird f�r bestimmte GUI-Aktualisierungen
	 *            ben�tigt
	 * @param annotation
	 *            die in dem Tab angezeigt wird
	 */
	public QuestionAnnotationTabController(ControlledScene controller,
			DataAnnotation annotation) {
		super(controller, annotation);
	}

	/*
	 * Die Methode initialisiert den den Tab, wenn er angezeigt werden soll und
	 * setzt die enthaltenen Elemente entsprechend mit Inhalt und Listenern auf.
	 */
	@Override
	public void init() {
		super.init();

		question.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (newValue.trim().equalsIgnoreCase("") == false) {
					addQuestionButton.setDisable(false);
				} else {
					addQuestionButton.setDisable(true);
				}
			}
		});

		questionAnno = (QuestionAnnotation) getAnnotation();
		questions = FXCollections.observableArrayList(questionAnno
				.getQuestionList());

		questionListView.setItems(questions);

		questions.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends String> c) {
				setNotSaved(true);
			}
		});
	}

	@FXML
	private void addQuestion() {
		if (question.getText().trim().equalsIgnoreCase("") == false) {
			questionAnno.getQuestionList().add(question.getText());
			questions.add(question.getText());
			question.clear();
		}
	}

	@Override
	protected void saveAnnotation() {
		setAnnotation(questionAnno);
		super.saveAnnotation();
	}
}
