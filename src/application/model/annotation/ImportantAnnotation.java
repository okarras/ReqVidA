package application.model.annotation;

import application.model.enums.DataAnnotationType;
import application.util.Helper;

/**
 * Die Klasse stellt eine ImportantAnnotation dar. Sie ist eine Auspr�gung einer
 * Datenannotation, wobei der Typ der Datenannotation auf IMPORTANT gesetzt
 * wird.
 * 
 * @author Oliver Karras
 *
 */
public class ImportantAnnotation extends DataAnnotation {

	/**
	 * Konstruktor f�r ein ImportantAnnotation-Objekt
	 */
	public ImportantAnnotation() {
		setType(DataAnnotationType.IMPORTANT);
	}

	/**
	 * Die Methode gibt die in einer ImportantAnnotation enthaltenen Informationen als
	 * String aus.
	 */
	@Override
	public String toString() {
		return "Annotation " + getId() + ")" + "\n Name: " + getName()
				+ "\n Type: " + getType() + "\n Video-Name: "
				+ getVideoName() + "\n Moment: " + Helper.milliToTimeFormat1(getMoment()) + "\n Comment: " + getComment() + "\n";
	}

}
