package application.controller;

import application.model.ControlledScene;
import application.model.annotation.CustomAnnotation;
import application.model.annotation.DataAnnotation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Die Klasse dient als Controller eines CustomAnnotationTabs, um mit den
 * zugeh�rigen GUI-Elementen interagieren zu k�nnen.
 * 
 * @author Oliver Karras
 *
 */
public class CustomAnnotationTabController extends AnnotationTabController {

	@FXML
	private Label attribute1;

	@FXML
	private Label attribute2;

	@FXML
	private Label attribute3;

	@FXML
	private Label attribute4;

	@FXML
	private Label attribute5;

	@FXML
	private TextField attribute1Value;

	@FXML
	private TextField attribute2Value;

	@FXML
	private TextField attribute3Value;

	@FXML
	private TextField attribute4Value;

	@FXML
	private TextField attribute5Value;

	private CustomAnnotation customAnno;

	/**
	 * Konstruktor, um ein CustomAnnotationTabController-Objekt zu erzeugen.
	 * 
	 * @param controller
	 *            der �bergeordneten View, die den Tab enth�lt
	 * @param annotation
	 *            die Annotation, die in dem Tab angezeigt werden soll
	 */
	public CustomAnnotationTabController(ControlledScene controller,
			DataAnnotation annotation) {
		super(controller, annotation);
	}

	@Override
	public void init() {
		super.init();

		customAnno = (CustomAnnotation) getAnnotation();

		if (customAnno.getAttribute1() == null) {
			attribute1.setVisible(false);
			attribute1Value.setVisible(false);
		} else {
			attribute1.setText(customAnno.getAttribute1());
			attribute1Value.setText(customAnno.getAttribute1Value());
			attribute1Value.textProperty().addListener(
					new AnnotationDataStringChangeListener(this));
		}

		if (customAnno.getAttribute2() == null) {
			attribute2.setVisible(false);
			attribute2Value.setVisible(false);
		} else {
			attribute2.setText(customAnno.getAttribute2());
			attribute2Value.setText(customAnno.getAttribute2Value());
			attribute2Value.textProperty().addListener(
					new AnnotationDataStringChangeListener(this));
		}

		if (customAnno.getAttribute3() == null) {
			attribute3.setVisible(false);
			attribute3Value.setVisible(false);
		} else {
			attribute3.setText(customAnno.getAttribute3());
			attribute3Value.setText(customAnno.getAttribute3Value());
			attribute3Value.textProperty().addListener(
					new AnnotationDataStringChangeListener(this));
		}

		if (customAnno.getAttribute4() == null) {
			attribute4.setVisible(false);
			attribute4Value.setVisible(false);
		} else {
			attribute4.setText(customAnno.getAttribute4());
			attribute4Value.setText(customAnno.getAttribute4Value());
			attribute4Value.textProperty().addListener(
					new AnnotationDataStringChangeListener(this));
		}

		if (customAnno.getAttribute5() == null) {
			attribute5.setVisible(false);
			attribute5Value.setVisible(false);
		} else {
			attribute5.setText(customAnno.getAttribute5());
			attribute5Value.setText(customAnno.getAttribute5Value());
			attribute5Value.textProperty().addListener(
					new AnnotationDataStringChangeListener(this));
		}
	}

	@FXML
	protected void saveAnnotation() {
		if (attribute1.isVisible()) {
			String attribute1ValueInput = attribute1Value.getText().trim();
			if (attribute1ValueInput != null) {
				customAnno.setAttribute1Value(attribute1ValueInput);
			}
		}

		if (attribute2.isVisible()) {
			String attribute2ValueInput = attribute2Value.getText().trim();
			if (attribute2ValueInput != null) {
				customAnno.setAttribute2Value(attribute2ValueInput);
			}
		}

		if (attribute3.isVisible()) {
			String attribute3ValueInput = attribute3Value.getText().trim();
			if (attribute3ValueInput != null) {
				customAnno.setAttribute3Value(attribute3ValueInput);
			}
		}

		if (attribute4.isVisible()) {
			String attribute4ValueInput = attribute4Value.getText().trim();
			if (attribute4ValueInput != null) {
				customAnno.setAttribute4Value(attribute4ValueInput);
			}
		}

		if (attribute5.isVisible()) {
			String attribute5ValueInput = attribute5Value.getText().trim();
			if (attribute5ValueInput != null) {
				customAnno.setAttribute5Value(attribute5ValueInput);
			}
		}

		setAnnotation(customAnno);
		super.saveAnnotation();
	}
}
