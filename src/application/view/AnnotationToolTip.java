package application.view;

import application.model.annotation.DataAnnotation;
import application.util.Helper;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 * Die Klasse dient zur Anzeige des Namens und konkreten Zeitpunktes einer in
 * der Timeline dargestellten Annotation. Zur Darstellung wird ein allgemeiner
 * ToolTip verwendet, dessen Layout enstprechend der dargestellten Informationen
 * angepasst worden ist.
 * 
 * @author Oliver Karras
 *
 */
public class AnnotationToolTip extends Tooltip {

	/**
	 * Konstruktor f�r ein AnnotationToolTip-Objekt
	 * 
	 * @param annotation
	 *            deren Informationen in dem ToolTip darsgestellt werden sollen
	 */
	public AnnotationToolTip(DataAnnotation annotation) {
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(2);
		grid.setPadding(new Insets(0, 10, 0, 10));

		Label nameLabel = new Label("Name:");
		grid.add(nameLabel, 0, 0);
		Label nameValue = new Label(annotation.getName());
		grid.add(nameValue, 1, 0);

		Label momentLabel = new Label("Moment:");
		grid.add(momentLabel, 0, 1);
		Label momentValue = new Label(Helper.milliToTimeFormat1(annotation
				.getMoment()));
		grid.add(momentValue, 1, 1);

		setGraphic(grid);
	}
}
