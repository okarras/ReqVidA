package application.view;

import java.util.Optional;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import application.controller.ScenesController;
import application.database.SQLiteDataBase;
import application.model.Stakeholder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Die Klasse dient der Erzeugung eines Dialogs zur Bearbeitung oder L�schung
 * von eingegeben Stakeholder-Informationen.Es erfolgt je nach Aktion eine
 * entsprechende Anweisung f�r die Datenbank, sodass alle �nderungen umgehend
 * gespeichert werden.
 * 
 * @author Oliver Karras
 *
 */
public class StakeholderDialog extends Dialog<ButtonType> {

	private final ButtonType saveType;
	private final ButtonType deleteType;

	private Label name = new Label("Name: ");
	private Label function = new Label("Function: ");
	private Label contact = new Label("Contact: ");
	private Label airtime = new Label("Airtime: ");
	private TextField nameField = new TextField();
	private TextField functionField = new TextField();
	private TextField contactField = new TextField();
	private TextField airtimeField = new TextField();

	private ValidationSupport validationSupport;
	private boolean error = false;

	public StakeholderDialog(final ScenesController controller,
			final Stakeholder stakeholder) {

		/*
		 * Erzeugung der GUI des Dialogs
		 */
		setTitle("Edit Stakeholder");
		setResizable(false);

		nameField.setText(stakeholder.getName());
		nameField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				final Button saveButton = (Button) StakeholderDialog.this
						.getDialogPane().lookupButton(saveType);

				if (newValue.trim().equalsIgnoreCase("")) {
					saveButton.disableProperty().set(true);
				} else {
					saveButton.disableProperty().set(false);
				}
			}
		});

		functionField.setText(stakeholder.getFunction());
		contactField.setText(stakeholder.getContact());
		airtimeField.setText(stakeholder.getAirtime());

		GridPane grid = new GridPane();

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(20);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(80);
		grid.getColumnConstraints().addAll(column1, column2);

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.add(name, 0, 0);
		grid.add(function, 0, 1);
		grid.add(contact, 0, 2);
		grid.add(airtime, 0, 3);

		grid.add(nameField, 1, 0);
		grid.add(functionField, 1, 1);
		grid.add(contactField, 1, 2);
		grid.add(airtimeField, 1, 3);

		getDialogPane().setContent(grid);

		saveType = new ButtonType("Save");
		deleteType = new ButtonType("Delete");

		getDialogPane().getButtonTypes().setAll(saveType, deleteType,
				ButtonType.CANCEL);

		validationSupport = new ValidationSupport();
		Validator<String> nameValidator = new Validator<String>() {

			@Override
			public ValidationResult apply(Control control, String value) {
				boolean condition = false;
				if (value.trim().equalsIgnoreCase("")) {
					condition = true;
				}
				return ValidationResult.fromMessageIf(control,
						"Name is required.", Severity.ERROR, condition);
			}
		};
		validationSupport.registerValidator(nameField, true, nameValidator);

		/*
		 * Legt die Reaktion bez�glich der jeweiligen Buttons fest.
		 */
		setResultConverter(new Callback<ButtonType, ButtonType>() {

			@Override
			public ButtonType call(ButtonType type) {
				/*
				 * Save-Button ist ausgew�hlt worden
				 */
				if (type == saveType) {
					String name = nameField.getText().trim();
					String function = functionField.getText().trim();
					String contact = contactField.getText().trim();
					String airtime = airtimeField.getText().trim();

					/*
					 * Pr�fung, ob ein Name eingegeben worden ist
					 */
					if (name.equalsIgnoreCase("") == false) {
						// alles korrekt, Daten speichern
						SQLiteDataBase.getInstance().updateStakeholderData(
								stakeholder.getId(), name, function, contact,
								airtime);
						StakeholderDialog.this.close();
					}
					/*
					 * Delete-Button ist ausgew�hlt worden
					 */
				} else if (type == deleteType) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText("Delete the stakeholder.");
					alert.setContentText("Do you really want to delete the stakeholder?");

					Optional<ButtonType> result = alert.showAndWait();
					/*
					 * L�schung best�tigt
					 */
					if (result.get() == ButtonType.OK) {
						SQLiteDataBase.getInstance().deleteStakeholderData(
								stakeholder.getId());
						StakeholderDialog.this.close();
						/*
						 * L�schung abgebrochen
						 */
					} else {
						error = true;
						alert.close();
					}
				}
				return type;
			}
		});

	}

	/**
	 * Methode zum Pr�fen, ob ein Fehler aufgetreten ist
	 * 
	 * @return true, falls ein Fehler aufgetreten ist, ansonsten false
	 */
	public boolean hasErrorOccured() {
		return error;
	}

	/**
	 * Die Methode setzt die Fehlererkennung zur�ck.
	 */
	public void resetError() {
		error = false;
	}

}
