package application.model.enums;

/**
 * Der Enumerator enth�lt alle m�glichen Typen f�r die Darstellungsformen einer
 * Anforderung.
 * 
 * @author Oliver Karras
 *
 */
public enum RequirementFormat {

	TEXT(0), USE_CASE(1);

	private final int value;

	private RequirementFormat(int value) {
		this.value = value;
	}

	/**
	 * Getter-Methode f�r den Wert des RequirementFormat eines Enums
	 * 
	 * @return Wert der Enum-Priorit�t
	 */
	public int getValue() {
		return value;
	}
}
