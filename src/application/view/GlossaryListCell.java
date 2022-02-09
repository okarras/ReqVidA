package application.view;

import application.model.GlossaryEntry;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Die Klasse dient der Anzeige aller Glossardaten eines einzelnen Datensatzes
 * in einer einzelnen Zelle einer ListView.
 * 
 * @author Oliver Karras
 *
 */
public class GlossaryListCell extends ListCell<GlossaryEntry> {

	@Override
	public void updateItem(GlossaryEntry entry, boolean empty) {
		super.updateItem(entry, empty);
		if (empty) {
			setText(null);
		} else {
			GridPane grid = new GridPane();

			ColumnConstraints columnConstraint1 = new ColumnConstraints();
			columnConstraint1.setPrefWidth(65);
			columnConstraint1.setMinWidth(65);
			columnConstraint1.setMaxWidth(65);
			columnConstraint1.setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().add(columnConstraint1);
			ColumnConstraints columnConstraint2 = new ColumnConstraints();
			columnConstraint2.setPrefWidth(125);
			columnConstraint2.setMinWidth(125);
			columnConstraint2.setMaxWidth(125);
			columnConstraint2.setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().add(columnConstraint2);

			grid.setHgap(2);
			grid.setVgap(0);

			Label termLabel = new Label("Term:");
			grid.add(termLabel, 0, 0);
			Label termValue = new Label(entry.getTerm());
			grid.add(termValue, 1, 0);

			Label descriptionLabel = new Label("Description:");
			grid.add(descriptionLabel, 0, 1);
			Label descriptionValue = new Label(entry.getDescription());
			grid.add(descriptionValue, 1, 1);

			setGraphic(grid);
		}
	}
}