package application.model.annotation;

import java.util.ArrayList;
import java.util.List;

import application.model.enums.DataAnnotationType;
import application.util.Helper;

/**
 * Die Klasse stellt eine ConflictAnnotation dar. Sie ist eine Auspr�gung einer
 * Datenannotation, wobei der Typ der Datenannotation auf CONFLICT gesetzt wird.
 * Zus�tzlich zu einer normalen Datenannotation verf�gt die ConflictAnnotation
 * �ber eine Beschreibung des Konflikte sowie einer Liste von Referenzen im
 * Video, um den Konflikt zu erl�utern.
 * 
 * @author Oliver Karras
 *
 */
public class ConflictAnnotation extends DataAnnotation {

	private String conflict;
	private List<String> referenceList;

	/**
	 * Konstruktor f�r ein ConflictAnnotation-Objekt
	 */
	public ConflictAnnotation() {
		setType(DataAnnotationType.CONFLICT);
		this.referenceList = new ArrayList<String>();
		this.conflict = "";
	}

	/**
	 * Getter-Methode f�r die Beschreibung des Konfliktes
	 * 
	 * @return Beschreibung des Konfliktes
	 */
	public String getConflict() {
		return conflict;
	}

	/**
	 * Setter-Methode f�r die Beschreibung des Konfliktes
	 * 
	 * @param conflict
	 *            Beschreibung des Konflitkes
	 */
	public void setConflict(String conflict) {
		this.conflict = conflict;
	}

	/**
	 * Getter-Methode f�r die Liste der Referenzen im Video, durch die der
	 * Konflikt erl�utert wird
	 * 
	 * @return Liste der Referenzen bez�glich des identifizierten Konfliktes
	 */
	public List<String> getReferenceList() {
		return referenceList;
	}

	/**
	 * Setter-Methode f�r die Liste von Referenzen im Video, die den Konflikt
	 * erl�utern
	 * 
	 * @param referenceList
	 *            Liste mit Referenzen aus dem Video, die den Konflikt erl�utern
	 */
	public void setReferenceList(List<String> referenceList) {
		this.referenceList = referenceList;
	}

	/**
	 * Die Methode gibt die in einer ConflictAnnotation enthaltenen
	 * Informationen als String aus.
	 */
	@Override
	public String toString() {
		return "Annotation " + getId() + ")" + "\n Name: " + getName()
				+ "\n Type: " + getType() + "\n Video-Name: " + getVideoName()
				+ "\n Moment: " + Helper.milliToTimeFormat1(getMoment())
				+ "\n Comment: " + getComment() + "\n Conflict: "
				+ getConflict() + "\n " + referenceListToString();

	}

	/*
	 * Die Methode wandelt die Liste von Referenzen in eine String-Darstellung
	 * um.
	 */
	private String referenceListToString() {
		String out = "";
		if (referenceList.size() > 0) {
			out = "References:\n";
			for (int i = 0; i < referenceList.size(); i++) {
				out = out + "  " + (i + 1) + ". " + referenceList.get(i) + "\n";
			}
		}
		return out;
	}
}
