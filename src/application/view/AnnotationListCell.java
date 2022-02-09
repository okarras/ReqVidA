package application.view;

import application.model.annotation.Annotation;
import application.model.annotation.CustomAnnotation;
import application.model.annotation.DataAnnotation;
import application.model.enums.DataAnnotationType;
import application.util.Helper;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Die Klasse dient der Anzeige aller Annotationdaten eines einzelnen
 * Datensatzes in einer einzelnen Zelle einer ListView.
 * 
 * @author Oliver Karras
 *
 */
public class AnnotationListCell extends ListCell<Annotation> {

	@Override
	public void updateItem(Annotation annotation, boolean empty) {
		super.updateItem(annotation, empty);
		if (empty) {
			setText(null);
		} else {
			if (annotation instanceof DataAnnotation) {
				DataAnnotation anno = (DataAnnotation) annotation;

				GridPane grid = new GridPane();

				ColumnConstraints columnConstraint1 = new ColumnConstraints();
				columnConstraint1.setPrefWidth(55);
				columnConstraint1.setMinWidth(55);
				columnConstraint1.setMaxWidth(55);
				columnConstraint1.setHgrow(Priority.ALWAYS);
				grid.getColumnConstraints().add(columnConstraint1);
				ColumnConstraints columnConstraint2 = new ColumnConstraints();
				columnConstraint2.setPrefWidth(135);
				columnConstraint2.setMinWidth(135);
				columnConstraint2.setMaxWidth(135);
				columnConstraint2.setHgrow(Priority.ALWAYS);
				grid.getColumnConstraints().add(columnConstraint2);

				grid.setHgap(2);
				grid.setVgap(0);

				Label nameLabel = new Label("Name:");
				grid.add(nameLabel, 0, 0);
				Label nameValue = new Label(annotation.getName());
				grid.add(nameValue, 1, 0);

				Label momentLabel = new Label("Moment:");
				grid.add(momentLabel, 0, 1);
				Label momentValue = new Label(
						Helper.milliToTimeFormat1(annotation.getMoment()));
				grid.add(momentValue, 1, 1);

				Label typeLabel = new Label("Type:");
				grid.add(typeLabel, 0, 2);
				Label typeValue = new Label(anno.getType().toString());
				grid.add(typeValue, 1, 2);

				if (anno.getType() == DataAnnotationType.CUSTOM) {
					Label customTypeLabel = new Label("CustomID:");
					grid.add(customTypeLabel, 0, 3);
					Label customTypeValue = new Label(
							((CustomAnnotation) anno).getCustomAnnoID());
					grid.add(customTypeValue, 1, 3);
				}

				setGraphic(grid);

			} else {
				// Fall einer StrukturAnnotation
			}
		}
	}
}
