package application.model.annotation;


/**
 * Die Klasse stellt das Modell einer Annotation dar.
 * 
 * @author Oliver Karras
 *
 */
public class Annotation {

	private int id;
	private String name;
	private String videoName;
	private long moment;

	/**
	 * Default-Konstruktor f�r ein Annotation-Objekt
	 */
	public Annotation() {
	}

	/**
	 * Getter-Methode f�r die Datenbank-ID der Annotation
	 * 
	 * @return ID der Annotation in der Datenbank
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter-Methode f�r die ID der Annotation
	 * 
	 * @param id
	 *            der Annotation in der Datenbank
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter-Methode f�r den Name Annotation
	 * 
	 * @return Name der Annotation
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-Methode f�r den Name der Annotation
	 * 
	 * @param name
	 *            Name der Annotation
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter-Methode f�r den Videonamen zu dem die Annotation geh�rt
	 * 
	 * @return Name des Videos
	 */
	public String getVideoName() {
		return videoName;
	}

	/**
	 * Setter-Methode f�r den Videonamen zu dem die Annotation geh�rt
	 * 
	 * @param videoName
	 *            neuer Videoname
	 */
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	/**
	 * Getter-Methode f�r die Moment einer Annotation
	 * 
	 * @return Moment der Annotation
	 */
	public long getMoment() {
		return moment;
	}

	/**
	 * Setter-Methode f�r die Moment einer Annotation
	 * 
	 * @param moment
	 *            die f�r die Annotation gesetzt werden soll
	 */
	public void setMoment(long moment) {
		this.moment = moment;
	}

}
