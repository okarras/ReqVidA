package application.model;

/**
 * Die Klasse stellt das Modell eines Glossareintrages dar. Und dient zur
 * Speicherung der Glossardaten aus der Datenbank.
 * 
 * @author Oliver Karras
 *
 */
public class GlossaryEntry {

	private int id;
	private String term;
	private String description;

	/**
	 * Default-Konstruktor f�r ein GlossarEntry-Objekt
	 */
	public GlossaryEntry() {
	}

	/**
	 * Konstruktor f�r ein GlossarEntry-Objekt
	 * 
	 * @param term
	 *            Name des Wortes des Glossareintrages
	 * @param description
	 *            Beschreibung des Wortes des Glossareintrages
	 */
	public GlossaryEntry(String term, String description) {
		this.term = term;
		this.description = description;
	}

	/**
	 * Getter-Methode des Namens des Glossareintrages
	 * 
	 * @return Name des Glossareintrages
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * Setter-Methode des Namen eines Glossareintrages
	 * 
	 * @param term
	 *            neue Name des Glossareintrages
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Getter-Methode der Beschreibung des Glossareintrages
	 * 
	 * @return Beschreibung des Glossareintrages
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter-Methode der Beschreibung eines Glossareintrages
	 * 
	 * @param description
	 *            neue Beschreibung des Glossareintrages
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter-Methode f�r die ID in der Datenbank
	 * 
	 * @return ID des Glossareintrages in der Datenbank
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter-Methode f�r die ID aus der Datenbank
	 * 
	 * @param id
	 *            ID des Glossareintrages in der Datenbank
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Die Methode gibt die in einem GlossaryEntry enthaltenen Informationen als
	 * String aus.
	 */
	@Override
	public String toString() {
		return "Term: " + term + "\nDescription: " + description;
	}

}
