package application.view;

import application.model.Stakeholder;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Die Klasse dient der Anzeige aller Stakeholderdaten eines einzelnen
 * Datensatzes in einer einzelnen Zelle einer ListView.
 * 
 * @author Oliver Karras
 *
 */
public class StakeholderListCell extends ListCell<Stakeholder> {

	@Override
	public void updateItem(Stakeholder stakeholder, boolean empty) {
		super.updateItem(stakeholder, empty);
		if (empty) {
			setText(null);
		} else {
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
			Label nameValue = new Label(stakeholder.getName());
			grid.add(nameValue, 1, 0);

			Label functionLabel = new Label("Function:");
			grid.add(functionLabel, 0, 1);
			Label functionValue = new Label(stakeholder.getFunction());
			grid.add(functionValue, 1, 1);

			Label contactLabel = new Label("Contact:");
			grid.add(contactLabel, 0, 2);
			Label contactValue = new Label(stakeholder.getContact());
			grid.add(contactValue, 1, 2);

			Label airtimeLabel = new Label("Airtime:");
			grid.add(airtimeLabel, 0, 3);
			Label airtimeValue = new Label(stakeholder.getAirtime());
			grid.add(airtimeValue, 1, 3);
			
			setGraphic(grid);
		}
	}
}