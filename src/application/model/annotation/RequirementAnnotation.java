package application.model.annotation;

import java.util.ArrayList;
import java.util.List;

import application.model.enums.DataAnnotationType;
import application.model.enums.ObligationType;
import application.model.enums.Priority;
import application.model.enums.RequirementFormat;
import application.model.enums.RequirementType;
import application.util.Helper;

/**
 * Die Klasse stellt eine RequirementAnnotation dar. Sie ist eine Auspr�gung
 * einer Datenannotation, wobei der Typ der Datenannotation auf REQUIREMENT
 * gesetzt wird. Zus�tzlich zu einer normalen Datenannotation verf�gt die
 * RequirementAnnotation �ber die Granularit�t der Anforderung bez�glich ihre
 * Abstraktionslevels, die Darstellungsform der Anforderung, die Quelle der
 * Anforderung, das Rationale der Anforderung, die rechtlichen Verbindlichkeit
 * der Anforderung, die Priorit�t einer Anforderung sowie �ber eine Liste von
 * Artefaktnamen, die entstanden im Workshop sind und zur Ausarbeitung der
 * Anforderung beitragen.
 * 
 * @author Oliver Karras
 *
 */
public class RequirementAnnotation extends DataAnnotation {

	private RequirementType granularity;
	private RequirementFormat displayFormat;
	private String source;
	private String rationale;
	private String requirement;
	private ObligationType obligation;
	private Priority priority;
	private List<String> artefactList;

	/**
	 * Konstruktor f�r ein RequirementAnnotation-Objekt
	 */
	public RequirementAnnotation() {
		setType(DataAnnotationType.REQUIREMENT);
		this.granularity = RequirementType.FEATURE;
		this.displayFormat = RequirementFormat.TEXT;
		this.obligation = ObligationType.SHALL;
		this.priority = Priority.PRIORITY1;
		this.artefactList = new ArrayList<String>();
		this.requirement = "";
		this.source = "";
		this.rationale = "";
	}

	/**
	 * Getter-Methode f�r die Granularit�t der Anforderung
	 * 
	 * @return Granularit�t der Anforderung
	 */
	public RequirementType getGranularity() {
		return granularity;
	}

	/**
	 * Setter-Methode f�r die Granulait�t der Anforderung
	 * 
	 * @param granularity
	 *            neue Granularit�t der Anforderung
	 */
	public void setGranularity(RequirementType granularity) {
		this.granularity = granularity;
	}

	/**
	 * Getter-Methode f�r die Darstellungsform der Anforderung
	 * 
	 * @return Darstellungsform der Anforderung
	 */
	public RequirementFormat getDisplayFormat() {
		return displayFormat;
	}

	/**
	 * Setter-Methode f�r die Darstellungsform der Anforderung
	 * 
	 * @param displayFormat
	 *            neue Darstellungsform der Anforderung
	 */
	public void setDisplayFormat(RequirementFormat displayFormet) {
		this.displayFormat = displayFormet;
	}

	/**
	 * Getter-Methode f�r die Quelle der Anforderung
	 * 
	 * @return Quelle der Anforderung
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Setter-Methode f�r die Quelle der Anforderung
	 * 
	 * @param source
	 *            neue Quelle der Anforderung
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Getter-Methode f�r das Rationale der Anforderung
	 * 
	 * @return Rationale der Anforderung
	 */
	public String getRationale() {
		return rationale;
	}

	/**
	 * Setter-Methode f�r das Rationale der Anforderung
	 * 
	 * @param rationale
	 *            neues Rationale der Anforderung
	 */
	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	/**
	 * Getter-Methode f�r die rechtliche Verbindlichkeit der Anforderung
	 * 
	 * @return rechtliche Verbindlichkeit der Anforderung
	 */
	public ObligationType getObligation() {
		return obligation;
	}

	/**
	 * Setter-Methode f�r die rechtliche Verbindlichkeit der Anforderung
	 * 
	 * @param obligation
	 *            neue rechtliche Verbindlichkeit der Anforderung
	 */
	public void setObligation(ObligationType obligation) {
		this.obligation = obligation;
	}

	/**
	 * Getter-Methode f�r die Priorit�t der Anforderung
	 * 
	 * @return Priorit�t der Anforderung
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Setter-Methode f�r die Priorit�t der Anforderung
	 * 
	 * @param priority
	 *            neue Priorit�t der Anforderung
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Getter-Methode f�r die Liste von Artefaktnamen, welche f�r die
	 * Ausarbeitugn der Anforderung hilfreich sind
	 * 
	 * @return Liste von Artefaktnamen
	 */
	public List<String> getArtefactList() {
		return artefactList;
	}

	/**
	 * Setter-Methode f�r die Artefaktliste der Anforderung
	 * 
	 * @param artefactList
	 *            neue ArtefaktListe der Anforderung
	 */
	public void setArtefactList(List<String> artefactList) {
		this.artefactList = artefactList;
	}

	/**
	 * Getter-Methode f�r die Requirement der RequirementAnnotation
	 * 
	 * @return Requirement der RequirementAnnotation
	 */
	public String getRequirement() {
		return requirement;
	}

	/**
	 * Setter-Methode f�r die Requirement der RequirementAnnotation
	 * 
	 * @param neue
	 *            Requirement der RequirementAnnotation
	 */
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	/**
	 * Die Methode gibt die in einer RequirementsAnnotation enthaltenen
	 * Informationen als String aus.
	 */
	@Override
	public String toString() {
		return "Annotation " + getId() + ")" + "\n Name: " + getName()
				+ "\n Type: " + getType() + "\n Video-Name: " + getVideoName()
				+ "\n Moment: " + Helper.milliToTimeFormat1(getMoment())
				+ "\n Comment: " + getComment() + "\n Granularity: "
				+ granularity + "\n Requirement: " + requirement
				+ "\n Obligation: " + obligation + "\n Priority: " + priority
				+ "\n Source: " + source + "\n Rationale: " + rationale + "\n "
				+ artefactListToString();

	}

	/*
	 * Die Methode wandelt die Liste von Artefakten in eine String-Darstellung
	 * u.
	 */
	private String artefactListToString() {
		String out = "";
		if (artefactList.size() > 0) {
			out = "Artefacts:\n";
			for (int i = 0; i < artefactList.size(); i++) {
				out = out + "  " + (i + 1) + ". " + artefactList.get(i) + "\n";
			}
		}
		return out;
	}
}
