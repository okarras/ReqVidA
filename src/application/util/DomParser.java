package application.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.model.annotation.ConflictAnnotation;
import application.model.annotation.CustomAnnotation;
import application.model.annotation.DataAnnotation;
import application.model.annotation.ImportantAnnotation;
import application.model.annotation.QuestionAnnotation;
import application.model.annotation.RequirementAnnotation;
import application.model.enums.DataAnnotationType;
import application.model.enums.ObligationType;
import application.model.enums.Priority;
import application.model.enums.RequirementFormat;
import application.model.enums.RequirementType;

/**
 * Der DomParser dient dazu die XML-Datei der Shortcut-Definitionen zu parsen,
 * um diese f�r das System bereitzustellen.
 * 
 * @author Oliver Karras
 *
 */
public class DomParser {

	private static Document doc;
	private static DocumentBuilder dBuilder;

	private static List<CustomAnnotation> customAnnotationList;

	/**
	 * Konstruktor, um ein DomParser-Objekt zu erstellen, mit dem die XML-Datei
	 * f�r die Shortcuts geparst werden kann
	 */
	public DomParser() {
	}

	/**
	 * Die Methode parst die XML-Datei f�r die CustomAnnotation und erstellt
	 * daraus eine Liste der definierten CustomAnnotationss.
	 * 
	 * @param url
	 *            die zu parsende XML-Datei
	 * @return eine Liste der definierten CustomAnnotations
	 */
	public static List<CustomAnnotation> createCustomAnnotations(File url) {
		customAnnotationList = new ArrayList<CustomAnnotation>();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean isParsed = true;

		try {
			doc = dBuilder.parse(url);
		} catch (Exception e) {
			isParsed = false;
			e.printStackTrace();
		}
		if (isParsed == true) {
			doc.getDocumentElement().normalize();

			/*
			 * Auslesen der einzelnen CustomAnnotation-Knoten in der XML-Datei
			 */
			NodeList customAnnotationNodes = doc
					.getElementsByTagName("customannotation");

			// Iteration �ber alle CustomAnnotation-Knoten
			for (int i = 0; i < customAnnotationNodes.getLength(); i++) {
				Node customAnnotationNode = customAnnotationNodes.item(i);
				Element customAnnotationElement = (Element) customAnnotationNode;

				// liefert den definierte nCustomAnnotation-Knoten
				if (customAnnotationNode.getNodeType() == Node.ELEMENT_NODE) {

					String customAnnotatioName = customAnnotationElement
							.getAttribute("id");

					NodeList attributesNodes = customAnnotationNode
							.getChildNodes();
					CustomAnnotation customAnno = new CustomAnnotation();
					customAnno.setCustomAnnoID(customAnnotatioName);

					/*
					 * Iteration �ber die Attribut-Knoten zum Auslesen der
					 * einzelnen Attribute
					 */
					for (int j = 0; j < attributesNodes.getLength(); j++) {
						Node attributeNode = attributesNodes.item(j);

						if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

							Element attributeElement = (Element) attributeNode;

							String attribute1Name = attributeElement
									.getElementsByTagName("attribute1").item(0)
									.getTextContent();

							String attribute2Name = attributeElement
									.getElementsByTagName("attribute2").item(0)
									.getTextContent();

							String attribute3Name = attributeElement
									.getElementsByTagName("attribute3").item(0)
									.getTextContent();

							String attribute4Name = attributeElement
									.getElementsByTagName("attribute4").item(0)
									.getTextContent();

							String attribute5Name = attributeElement
									.getElementsByTagName("attribute5").item(0)
									.getTextContent();

							if (attribute1Name.equalsIgnoreCase("")) {
								attribute1Name = null;
							} else {
								customAnno.setAttribute1(attribute1Name);
							}

							if (attribute2Name.equalsIgnoreCase("")) {
								attribute2Name = null;
							} else {
								customAnno.setAttribute2(attribute2Name);
							}

							if (attribute3Name.equalsIgnoreCase("")) {
								attribute3Name = null;
							} else {
								customAnno.setAttribute3(attribute3Name);
							}

							if (attribute4Name.equalsIgnoreCase("")) {
								attribute4Name = null;
							} else {
								customAnno.setAttribute4(attribute4Name);
							}

							if (attribute5Name.equalsIgnoreCase("")) {
								attribute5Name = null;
							} else {
								customAnno.setAttribute5(attribute5Name);
							}
						}
					}
					customAnnotationList.add(customAnno);
				}
			}
		}
		return customAnnotationList;
	}

	/**
	 * Die Methode parst die XML-Datei f�r die Shortcuts und erstellt daraus
	 * eine HashMap mit den jeweiligen Shortcuts f�r das System.
	 * 
	 * @param url
	 *            die zu parsende XML-Datei
	 * @return eine HashMap mit den Shortcuts des Systems
	 */
	public static HashMap<String, DataAnnotation> createShortcuts(
			File url) {
		HashMap<String, DataAnnotation> shortcuts = new HashMap<String, DataAnnotation>();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean isParsed = true;

		try {
			doc = dBuilder.parse(url);
		} catch (Exception e) {
			isParsed = false;
			e.printStackTrace();
		}
		if (isParsed == true) {
			doc.getDocumentElement().normalize();

			/*
			 * Auslesen der einzelnen Shortcut-Knoten in der XMl-Datei
			 */
			NodeList shortcutNodes = doc.getElementsByTagName("shortcut");

			// Iteration �ber alle Shortcut-Knoten
			for (int i = 0; i < shortcutNodes.getLength(); i++) {
				Node shortcutNode = shortcutNodes.item(i);
				Element shortcutElement = (Element) shortcutNode;

				// liefert den definierten Shortcut
				if (shortcutNode.getNodeType() == Node.ELEMENT_NODE) {
					String shortcutKey = shortcutElement.getAttribute("id");

					NodeList annotationNodes = shortcutNode.getChildNodes();
					DataAnnotation annotation = new DataAnnotation();

					for (int j = 0; j < annotationNodes.getLength(); j++) {
						Node annotationNode = annotationNodes.item(j);

						if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
							Element annotationElement = (Element) annotationNode;
							String type = annotationElement.getAttribute("id");

							DataAnnotationType annotationType = DataAnnotationType
									.valueOf(type);

							String name = annotationElement
									.getElementsByTagName("name").item(0)
									.getTextContent();

							String comment = annotationElement
									.getElementsByTagName("comment").item(0)
									.getTextContent();

							switch (annotationType) {
							case IMPORTANT:
								ImportantAnnotation importantAnno = new ImportantAnnotation();
								annotation = importantAnno;
								break;
							case REQUIREMENT:
								RequirementAnnotation requirementAnno = new RequirementAnnotation();

								String granularity = annotationElement
										.getElementsByTagName("granularity")
										.item(0).getTextContent();
								String displayFormat = annotationElement
										.getElementsByTagName("displayformat")
										.item(0).getTextContent();
								String requirement = annotationElement
										.getElementsByTagName("requirement")
										.item(0).getTextContent();
								String source = annotationElement
										.getElementsByTagName("source").item(0)
										.getTextContent();
								String rationale = annotationElement
										.getElementsByTagName("rationale")
										.item(0).getTextContent();
								String obligation = annotationElement
										.getElementsByTagName("obligation")
										.item(0).getTextContent();
								String priority = annotationElement
										.getElementsByTagName("priority")
										.item(0).getTextContent();

								requirementAnno.setGranularity(RequirementType
										.valueOf(granularity));
								requirementAnno
										.setDisplayFormat(RequirementFormat
												.valueOf(displayFormat));
								requirementAnno.setRequirement(requirement);
								requirementAnno.setSource(source);
								requirementAnno.setRationale(rationale);
								requirementAnno.setObligation(ObligationType
										.valueOf(obligation));
								requirementAnno.setPriority(Priority
										.valueOf(priority));

								annotation = requirementAnno;
								break;
							case CONFLICT:
								ConflictAnnotation conflictAnno = new ConflictAnnotation();

								String conflict = annotationElement
										.getElementsByTagName("conflict")
										.item(0).getTextContent();

								conflictAnno.setConflict(conflict);

								annotation = conflictAnno;
								break;
							case QUESTION:
								QuestionAnnotation questionAnno = new QuestionAnnotation();
								annotation = questionAnno;
								break;
							case CUSTOM:

								CustomAnnotation customAnno = new CustomAnnotation();

								String customannotationid = annotationElement
										.getElementsByTagName(
												"customannotationid").item(0)
										.getTextContent();
								String attribute1Value = annotationElement
										.getElementsByTagName("attribute1Value")
										.item(0).getTextContent();
								String attribute2Value = annotationElement
										.getElementsByTagName("attribute2Value")
										.item(0).getTextContent();
								String attribute3Value = annotationElement
										.getElementsByTagName("attribute3Value")
										.item(0).getTextContent();
								String attribute4Value = annotationElement
										.getElementsByTagName("attribute4Value")
										.item(0).getTextContent();
								String attribute5Value = annotationElement
										.getElementsByTagName("attribute5Value")
										.item(0).getTextContent();

								for (CustomAnnotation customAnnotation : customAnnotationList) {
									if (customAnnotation.getCustomAnnoID()
											.equalsIgnoreCase(
													customannotationid)) {
										customAnno = customAnnotation;
										break;
									}
								}

								customAnno.setAttribute1Value(attribute1Value);
								customAnno.setAttribute1Value(attribute2Value);
								customAnno.setAttribute1Value(attribute3Value);
								customAnno.setAttribute1Value(attribute4Value);
								customAnno.setAttribute1Value(attribute5Value);

								annotation = customAnno;
								break;
							}

							annotation.setName(name);
							annotation.setType(annotationType);
							annotation.setComment(comment);
						}
						shortcuts.put(shortcutKey, annotation);
					}
				}
			}
		}
		return shortcuts;
	}

	public static List<CustomAnnotation> getCustomAnnotationList() {
		return customAnnotationList;
	}
}
