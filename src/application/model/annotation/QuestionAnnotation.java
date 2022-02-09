package application.model.annotation;

import java.util.ArrayList;
import java.util.List;

import application.model.enums.DataAnnotationType;
import application.util.Helper;

/**
 * Die Klasse stellt eine QuestionAnnotation dar. Sie ist eine Auspr�gung einer
 * Datenannotation, wobei der Typ der Datenannotation auf QUESTION gesetzt wird.
 * Zus�tzlich zu einer normalen Datenannotation verf�gt die QuestionAnnotation
 * �ber eine Liste von Fragen.
 * 
 * @author Oliver Karras
 *
 */
public class QuestionAnnotation extends DataAnnotation {

	private List<String> questionList;

	/**
	 * Konstruktor f�r ein QuestionAnnotation-Objekt
	 */
	public QuestionAnnotation() {
		setType(DataAnnotationType.QUESTION);
		this.questionList = new ArrayList<String>();
	}

	/**
	 * Getter-Methode f�r die Liste von Fragen der QuestionAnnotation
	 * 
	 * @return Liste mit Fragen der QuestionAnnotation
	 */
	public List<String> getQuestionList() {
		return questionList;
	}

	/**
	 * Setter-Methode f�r die Liste von Fragen der QuestionAnnotation
	 * 
	 * @param questionList
	 *            Liste von Fragen f�r die QuestionAnnotation
	 */
	public void setQuestionList(List<String> questionList) {
		this.questionList = questionList;
	}

	/**
	 * Die Methode gibt die in einer QuestionAnnotation enthaltenen
	 * Informationen als String aus.
	 */
	@Override
	public String toString() {
		return "Annotation " + getId() + ")" + "\n Name: " + getName()
				+ "\n Type: " + getType() + "\n Video-Name: " + getVideoName()
				+ "\n Moment: " + Helper.milliToTimeFormat1(getMoment())
				+ "\n Comment: " + getComment() + "\n "
				+ questionListToString();

	}

	/*
	 * Die Methode wandelt die Liste von Frage in eine String-Darstellung um.
	 */
	private String questionListToString() {
		String out = "";
		if (questionList.size() > 0) {
			out = "Questions:\n";
			for (int i = 0; i < questionList.size(); i++) {
				out = out + "  " + (i + 1) + ". " + questionList.get(i) + "\n";
			}
		}
		return out;
	}
}
