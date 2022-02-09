package application.model.annotation;

import application.model.enums.DataAnnotationType;
import application.util.Helper;

/**
 * Die Klasse dient zur Verwaltung selbstdefinierter Annotationen, die momentan
 * bis zu 5 eigene Attribute halten k�nnen. Nicht definierte Attribute in der
 * entsprechenden XML-Datei, welche zur Definition von CustomAnnotations genutzt
 * wird werden nicht in der GUI angezeigt.
 * 
 * @author Oliver Karras
 *
 */
public class CustomAnnotation extends DataAnnotation {

	private String customAnnoID;

	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String attribute5;

	private String attribute1Value;
	private String attribute2Value;
	private String attribute3Value;
	private String attribute4Value;
	private String attribute5Value;

	/**
	 * Konstruktor f�r ein CustomAnnotation-Objekt
	 */
	public CustomAnnotation() {
		setType(DataAnnotationType.CUSTOM);
		this.attribute1Value = "";
		this.attribute2Value = "";
		this.attribute3Value = "";
		this.attribute4Value = "";
		this.attribute5Value = "";
	}

	/**
	 * Getter-Methode f�r den Namen des ersten Attributes
	 * 
	 * @return Name des ersten Attributes
	 */
	public String getAttribute1() {
		return attribute1;
	}

	/**
	 * Setter-Methode f�r den Namen des ersten Attributes
	 * 
	 * @param attribute1
	 *            Wert des ersten Attributes
	 */
	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	/**
	 * Getter-Methode f�r den Namen des zweiten Attributes
	 * 
	 * @return Name des zweiten Attributes
	 */
	public String getAttribute2() {
		return attribute2;
	}

	/**
	 * Setter-Methode f�r den Namen des zweiten Attributes
	 * 
	 * @param attribute2
	 *            Wert des zweiten Attributes
	 */
	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	/**
	 * Getter-Methode f�r den Namen des dritten Attributes
	 * 
	 * @return Name des dritten Attributes
	 */
	public String getAttribute3() {
		return attribute3;
	}

	/**
	 * Setter-Methode f�r den Namen des dritten Attributes
	 * 
	 * @param attribute3
	 *            Wert des dritten Attributes
	 */
	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	/**
	 * Getter-Methode f�r den Namen des vierten Attributes
	 * 
	 * @return Name des vierten Attributes
	 */
	public String getAttribute4() {
		return attribute4;
	}

	/**
	 * Setter-Methode f�r den Namen des vierten Attributes
	 * 
	 * @param attribute4
	 *            Wert des vierten Attributes
	 */
	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	/**
	 * Getter-Methode f�r den Namen des f�nften Attributes
	 * 
	 * @return Name des f�nften Attributes
	 */
	public String getAttribute5() {
		return attribute5;
	}

	/**
	 * Setter-Methode f�r den Namen des f�nften Attributes
	 * 
	 * @param attribute5
	 *            Wert des f�nften Attributes
	 */
	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}

	/**
	 * Getter-Methode f�r den Wert des ersten Attributes
	 * 
	 * @return Wert des ersten Attributes
	 */
	public String getAttribute1Value() {
		return attribute1Value;
	}

	/**
	 * Setter-Methode f�r den Wert des ersten Attributes
	 * 
	 * @param attribute1Value
	 *            Wert des ersten Attributes
	 */
	public void setAttribute1Value(String attribute1Value) {
		this.attribute1Value = attribute1Value;
	}

	/**
	 * Getter-Methode f�r den Wert des zweiten Attributes
	 * 
	 * @return Wert des zweiten Attributes
	 */
	public String getAttribute2Value() {
		return attribute2Value;
	}

	/**
	 * Setter-Methode f�r den Wert des zweiten Attributes
	 * 
	 * @param attribute2Value
	 *            Wert des zweiten Attributes
	 */
	public void setAttribute2Value(String attribute2Value) {
		this.attribute2Value = attribute2Value;
	}

	/**
	 * Getter-Methode f�r den Wert des dritten Attributes
	 * 
	 * @return Wert des dritten Attributes
	 */
	public String getAttribute3Value() {
		return attribute3Value;
	}

	/**
	 * Setter-Methode f�r den Wert des dritten Attributes
	 * 
	 * @param attribute3Value
	 *            Wert des dritten Attributes
	 */
	public void setAttribute3Value(String attribute3Value) {
		this.attribute3Value = attribute3Value;
	}

	/**
	 * Getter-Methode f�r den Wert des vierten Attributes
	 * 
	 * @return Wert des vierten Attributes
	 */
	public String getAttribute4Value() {
		return attribute4Value;
	}

	/**
	 * Setter-Methode f�r den Wert des vierten Attributes
	 * 
	 * @param attribute4Value
	 *            Wert des vierten Attributes
	 */
	public void setAttribute4Value(String attribute4Value) {
		this.attribute4Value = attribute4Value;
	}

	/**
	 * Getter-Methode f�r den Wert des f�nften Attributes
	 * 
	 * @return Wert des f�nften Attributes
	 */
	public String getAttribute5Value() {
		return attribute5Value;
	}

	/**
	 * Setter-Methode f�r den Wert des f�nften Attributes
	 * 
	 * @param attribute5Value
	 *            Wert des f�nften Attributes
	 */
	public void setAttribute5Value(String attribute5Value) {
		this.attribute5Value = attribute5Value;
	}

	/**
	 * Getter-Methode f�r die ID der CustomAnnotation
	 * 
	 * @return Wert des ersten Attributes
	 */
	public String getCustomAnnoID() {
		return customAnnoID;
	}

	/**
	 * Setter-Methode f�r die ID der CustomAnnotation
	 * 
	 * @param customAnnoID
	 *            ID der CustomAnnotation
	 */
	public void setCustomAnnoID(String customAnnoID) {
		this.customAnnoID = customAnnoID;
	}

	/**
	 * Die Methode gibt die in einer CustomAnnotation enthaltenen Informationen als
	 * String aus.
	 */
	@Override
	public String toString() {
		String out = "Annotation " + getId() + ")" + "\n Name: " + getName()
				+ "\n Type: " + getType() + "\n CustomType: " + customAnnoID
				+ "\n Video-Name: " + getVideoName() + "\n Moment: "
				+ Helper.milliToTimeFormat1(getMoment()) + "\n Comment: "
				+ getComment() + "\n ";

		if (attribute1 != null) {
			out = out + attribute1 + " " + attribute1Value + "\n ";
		}
		if (attribute2 != null) {
			out = out + attribute2 + " " + attribute2Value + "\n ";
		}
		if (attribute3 != null) {
			out = out + attribute3 + " " + attribute3Value + "\n ";
		}
		if (attribute4 != null) {
			out = out + attribute4 + " " + attribute4Value + "\n ";
		}
		if (attribute5 != null) {
			out = out + attribute5 + " " + attribute5Value + "\n ";
		}
		
		return out;
	}
}
