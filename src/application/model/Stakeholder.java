package application.model;

/**
 * Die Klasse stellt das Modell eines Stakeholders dar. Und dient zur
 * Speicherung der Stakeholderdaten aus der Datenbank.
 * 
 * @author Oliver Karras
 *
 */
public class Stakeholder {

	private int id;
	private String name;
	private String function;
	private String contact;
	private String airtime;

	/**
	 * Default-Konstruktur f�r ein Stakeholder-Objekt
	 */
	public Stakeholder() {
	}

	/**
	 * Konstruktor f�r ein Stakeholder-Objekt
	 * 
	 * @param id
	 *            ist ein fester Wert, der von der Datenbank bestimmt wird
	 * @param name
	 *            ist der Name des Stakeholders
	 * @param function
	 *            ist die Funktion des Stakeholders
	 * @param contact
	 *            ist die Kontaktm�glichkeit mit dem Stakeholder
	 * @param airtime
	 *            ist die Verf�gbarkeit des Stakeholders
	 */
	public Stakeholder(int id, String name, String function, String contact,
			String airtime) {
		this.id = id;
		this.name = name;
		this.function = function;
		this.contact = contact;
		this.airtime = airtime;
	}

	/**
	 * Getter-Methode f�r den Namen
	 * 
	 * @return Name des Stakeholders
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-Methode f�r den Namen
	 * 
	 * @param name
	 *            Name des Stakeholders
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter-Methode f�r die Funktion
	 * 
	 * @return Funktion des Stakeholders
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * Setter-Methode f�r die Funktion
	 * 
	 * @param function
	 *            Funktion des Stakeholders
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * Getter-Methode f�r den Kontakt
	 * 
	 * @return Kontakt des Stakeholders
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Setter-Methode f�r den Kontakt
	 * 
	 * @param contact
	 *            Kontakt des Stakeholders
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * Getter-Methode f�r die Verf�gbarkeit
	 * 
	 * @return Verf�gbarkeit des Stakeholders
	 */
	public String getAirtime() {
		return airtime;
	}

	/**
	 * Setter-Methode f�r die Verf�gbarkeit
	 * 
	 * @param airtime
	 *            Verf�gbarkeit des Stakeholders
	 */
	public void setAirtime(String airtime) {
		this.airtime = airtime;
	}

	/**
	 * Getter-Methode f�r die ID in der Datenbank
	 * 
	 * @return ID des Stakeholders in der Datenbank
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter-Methode f�r die ID aus der Datenbank
	 * 
	 * @param id
	 *            ID des Stakeholders in der Datenbank
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Die Methode gibt die in einem Stakeholder-Objekt enthaltenen Daten als
	 * String aus.
	 */
	@Override
	public String toString() {
		return "Name: " + name + "\nFunction: " + function + "\nContact: "
				+ contact + "\nAirtime: " + airtime;
	}

}
