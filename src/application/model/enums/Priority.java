package application.model.enums;

/**
 * Der Enumerator enth�lt alle m�glichen Typen von Priorit�ten f�r eine
 * Anforderung.
 * 
 * @author Oliver Karras
 *
 */
public enum Priority {

	PRIORITY1(1), PRIORITY2(2), PRIORITY3(3), PRIORITY4(4);

	private final int value;

	private Priority(int value) {
		this.value = value;
	}

	/**
	 * Getter-Methode f�r den Wert der Priorit�t eines Enums
	 * 
	 * @return Wert der Enum-Priorit�t
	 */
	public int getValue() {
		return value;
	}
}
