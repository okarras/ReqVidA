package application.model.annotation;

import application.model.enums.DataAnnotationType;

/**
 * Die Klasse stellt eine Datenannotation dar, die zur Ausarbeitung von
 * relevanten Stellen im Video gedacht sind. Durch die Ausarbeitung der
 * Datenannotationen wird die Exportfunktion angestrebter Workshop-Ergebnisse
 * unters�tzt.
 * 
 * @author Oliver Karras
 *
 */
public class DataAnnotation extends Annotation {

	private DataAnnotationType type;
	private String comment = "";

	/**
	 * Getter-Methode f�r den Kommentar einer Datenannotation
	 * 
	 * @return Kommentar der Datenannotation
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Setter-Methode f�r den Kommentar einer Datenannotation
	 * 
	 * @param comment
	 *            der f�r die Datenannotation gesetzt werden soll
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Getter-Methode f�r den Typ einer Datenannotation
	 * 
	 * @return Typ der Datenannotation
	 */
	public DataAnnotationType getType() {
		return type;
	}

	/**
	 * Setter-Methode f�r den Typ einer Datenannotation
	 * 
	 * @param type
	 *            der f�r die Datenannotation gesetzt werden soll
	 */
	public void setType(DataAnnotationType type) {
		this.type = type;
	}
}
