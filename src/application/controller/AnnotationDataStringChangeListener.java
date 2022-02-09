package application.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Die Klasse registiert �nderungen an dem Text in einem TextField. Dies dient
 * der Erkennung von �nderungen an einer Datenannotation, die noch nicht
 * gespeichert worden sind.
 * 
 * @author Oliver Karras
 *
 */
public class AnnotationDataStringChangeListener implements
		ChangeListener<String> {

	private AnnotationTabController controller;

	/**
	 * Konstruktor f�r ein AnnotationDataStringChangeListener
	 * 
	 * @param controller
	 *            des korrespondierenden AnnotationsTab, welcher sich
	 *            entsprechend aktualisieren muss.
	 */
	public AnnotationDataStringChangeListener(AnnotationTabController controller) {
		this.controller = controller;
	}

	@Override
	public void changed(ObservableValue<? extends String> observable,
			String oldValue, String newValue) {
		controller.setNotSaved(true);
	}

}
