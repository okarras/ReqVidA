package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.main.ReqVidAMain;
import application.model.GlossaryEntry;
import application.model.Stakeholder;
import application.model.annotation.Annotation;
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
import application.util.DomParser;
import application.util.Helper;

/**
 * Die Klasse dient der Erstellung und Verwendung einer Datenbank zur
 * Speicherung der erfassten Daten f�r ein Glossar, eine Stakeholder-Liste und
 * allen erzeugten Annotationen im ReqVidA-System.
 * 
 * @author Oliver Karras
 *
 */
public class SQLiteDataBase {

	private static Connection connection = null;
	private static Statement statement = null;
	private static SQLiteDataBase instance;

	/*
	 * Singelton-Pattern
	 */
	private SQLiteDataBase() {
	}

	/**
	 * Die Methode gibt die einzige Instanz der SQLDataBase nach dem
	 * Singelton-Pattern zur�ck.
	 * 
	 * @return Instanz der SQLDataBase
	 */
	public static SQLiteDataBase getInstance() {
		if (SQLiteDataBase.instance == null) {
			SQLiteDataBase.instance = new SQLiteDataBase();
			SQLiteDataBase.instance.init();
		}
		return SQLiteDataBase.instance;
	}

	/**
	 * Die Mehtode initialisiert die Datenbank und erstellt alle erforderlichen
	 * Tabellen.
	 */
	private void init() {
		try {
			if (ReqVidAMain.absoluteFolderPath.equalsIgnoreCase("") == false) {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ ReqVidAMain.absoluteFolderPath + "\\"
						+ ReqVidAMain.workshopName + ".db");
				statement = connection.createStatement();
				// statement.executeUpdate("drop table if exists GLOSSARY");
				// statement.executeUpdate("drop table if exists STAKEHOLDER");
				// statement.executeUpdate("drop table if exists ANNOTATION");

				String sql = "CREATE TABLE IF NOT EXISTS ANNOTATION"
						+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " NAME TEXT, VIDEONAME TEXT, MOMENT INTEGER, "
						+ " TYPE TEXT,"
						+ "COMMENT TEXT, QUESTIONS TEXT, "
						+ "CONFLICT TEXT, REFS TEXT, GRANULARITY TEXT, DISPLAYFORMAT TEXT,"
						+ " REQUIREMENT TEXT,SOURCE TEXT, RATIONALE TEXT, OBLIGATION TEXT, "
						+ "PRIORITY INTEGER, ARTEFACTS TEXT, CUSTOMID TEXT, ATTRIBUTE1 TEXT, "
						+ "ATTRIBUTE2 TEXT, ATTRIBUTE3 TEXT, ATTRIBUTE4 TEXT, ATTRIBUTE5 TEXT)";
				statement.executeUpdate(sql);

				sql = "CREATE TABLE IF NOT EXISTS GLOSSARY "
						+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " TERM TEXT NOT NULL," + " DESCRIPTION TEXT) ";
				statement.executeUpdate(sql);

				sql = "CREATE TABLE IF NOT EXISTS STAKEHOLDER "
						+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " NAME TEXT NOT NULL," + " FUNCTION TEXT,"
						+ " CONTACT TEXT," + " AIRTIME TEXT) ";
				statement.executeUpdate(sql);

				statement.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode f�gt einen neuen Stakeholder-Datensatz zu der Datenbank
	 * hinzu.
	 * 
	 * @param name
	 *            Name des Stakeholders
	 * @param function
	 *            Funktion des Stakeholders
	 * @param contact
	 *            Kontakt des Stakeholders
	 * @param airtime
	 *            Verf�gbarkeit des Stakeholders
	 */
	public void insertStakeholderData(String name, String function,
			String contact, String airtime) {
		try {
			statement = connection.createStatement();
			String sql = "INSERT INTO STAKEHOLDER (NAME, FUNCTION, CONTACT, AIRTIME)"
					+ "VALUES("
					+ "'"
					+ name
					+ "', "
					+ "'"
					+ function
					+ "', "
					+ "'" + contact + "', " + "'" + airtime + "')";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode ruft alle Stakeholderdaten aus der Datenbank ab.
	 * 
	 * @return Liste aller Stakeholder in der Datenbank
	 */
	public ArrayList<Stakeholder> selectStakeholderData() {
		ArrayList<Stakeholder> stakeholderList = new ArrayList<Stakeholder>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM STAKEHOLDER;");

			while (result.next()) {
				Stakeholder stakeholder = new Stakeholder();
				stakeholder.setId(result.getInt("ID"));
				stakeholder.setName(result.getString("NAME"));
				stakeholder.setFunction(result.getString("FUNCTION"));
				stakeholder.setContact(result.getString("CONTACT"));
				stakeholder.setAirtime(result.getString("AIRTIME"));
				stakeholderList.add(stakeholder);
			}

			result.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stakeholderList;
	}

	/**
	 * Die Methode aktualisiert den entsprechenden Datensatz eines Stakeholders
	 * in der Datenbank.
	 * 
	 * @param name
	 *            Name des Stakeholders
	 * @param function
	 *            Funktion des Stakeholders
	 * @param contact
	 *            Kontakt des Stakeholders
	 * @param airtime
	 *            Verf�gbarkeit des Stakeholders
	 */
	public void updateStakeholderData(int id, String name, String function,
			String contact, String airtime) {
		try {
			statement = connection.createStatement();
			String sql = "UPDATE STAKEHOLDER set NAME = '" + name
					+ "', FUNCTION = '" + function + "', CONTACT = '" + contact
					+ "', AIRTIME = '" + airtime + "' WHERE ID = " + id + ";";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode l�scht den entsprechenden Datensatz eines Stakehodlers aus
	 * der Datenbank.
	 * 
	 * @param id
	 *            ID des Stakeholders in der Datenbank
	 */
	public void deleteStakeholderData(int id) {
		try {
			statement = connection.createStatement();
			String sql = "DELETE from STAKEHOLDER WHERE ID = " + id + ";";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode schlie�t die offene Verbindung zur Datenbank.
	 */
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode f�gt einen neuen GlossaryEntry-Datensatz zu der Datenbank
	 * hinzu.
	 * 
	 * @param term
	 *            Wort des Glossareintrages
	 * @param description
	 *            Beschreibung des Glossareintrages
	 */
	public void insertGlossaryEntryData(String term, String description) {
		try {
			statement = connection.createStatement();
			String sql = "INSERT INTO GLOSSARY (TERM, DESCRIPTION)" + "VALUES("
					+ "'" + term + "', " + "'" + description + "')";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode ruft alle Glossardaten aus der Datenbank ab.
	 * 
	 * @return Liste aller Glossareintr�ge in der Datenbank
	 */
	public ArrayList<GlossaryEntry> selectGlossaryData() {
		ArrayList<GlossaryEntry> glossaryEntryList = new ArrayList<GlossaryEntry>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM GLOSSARY;");

			while (result.next()) {
				GlossaryEntry entry = new GlossaryEntry();
				entry.setId(result.getInt("ID"));
				entry.setTerm(result.getString("TERM"));
				entry.setDescription(result.getString("DESCRIPTION"));
				glossaryEntryList.add(entry);
			}

			result.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return glossaryEntryList;
	}

	/**
	 * Die Methode aktualisiert den entsprechenden Datensatz eines
	 * Glossareintrages in der Datenbank.
	 * 
	 * @param term
	 *            Wort des Glossareintrages
	 * @param description
	 *            Beschreibung des Glossareintrages
	 */
	public void updateGlossaryEntryData(int id, String term, String description) {
		try {
			statement = connection.createStatement();
			String sql = "UPDATE GLOSSARY set TERM = '" + term
					+ "', DESCRIPTION = '" + description + "' WHERE ID = " + id
					+ ";";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode l�scht den entsprechenden Datensatz eines Glossareintrages
	 * aus der Datenbank.
	 * 
	 * @param id
	 *            ID des Glossareintrages in der Datenbank
	 */
	public void deleteGlossaryEntryData(int id) {
		try {
			statement = connection.createStatement();
			String sql = "DELETE from GLOSSARY WHERE ID = " + id + ";";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode f�gt eine neue Annotation zu der Datenbank hinzu.
	 * 
	 * @param type
	 *            Typ der zu speichernden Annotation
	 */
	public int insertAnnotationWithButton(DataAnnotationType type,
			String videoName, long moment) {
		int id = -1;
		try {
			String comment = "";
			String sql = "INSERT INTO ANNOTATION (VIDEONAME, MOMENT, TYPE, "
					+ "COMMENT, CONFLICT, REQUIREMENT, SOURCE, RATIONALE, "
					+ "CUSTOMID, ATTRIBUTE1, ATTRIBUTE2, ATTRIBUTE3, ATTRIBUTE4, "
					+ "ATTRIBUTE5)" + "VALUES('" + videoName + "', " + moment
					+ ", '" + type.toString() + "', '" + comment
					+ "', '', '', '', '', '', '', '', '', '', '')";
			PreparedStatement preparedStatement = connection.prepareStatement(
					sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();

			id = preparedStatement.getGeneratedKeys().getInt(1);
			preparedStatement.close();

			statement = connection.createStatement();
			sql = "UPDATE ANNOTATION set NAME = '" + type.toString() + "_" + id
					+ "' WHERE ID = " + id + ";";
			statement.executeUpdate(sql);

			statement.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return id;
	}

	/**
	 * Die Methode f�gt die �bergeben DataAnnotation in die Datenbank ein.
	 * 
	 * @param annotation
	 *            die hinzugef�gt werden soll
	 * @param videoName
	 *            des Videos zu dem die Annotation geh�rt
	 * @param moment
	 *            Zeitpunkt zu dem die Annotation im Video erzeugt worden ist
	 */
	public int insertDataAnnotation(DataAnnotation annotation,
			String videoName, long moment) {
		int id = -1;
		try {
			DataAnnotationType type = annotation.getType();

			String sql = "INSERT INTO ANNOTATION (NAME, VIDEONAME, MOMENT, TYPE, COMMENT";

			/*
			 * Je nach Typ der hinzuzuf�genden Annotation, muss das
			 * entsprechende SQL-Statement aufgebaut werden.
			 */
			switch (type) {
			case IMPORTANT:
				ImportantAnnotation importantAnno = (ImportantAnnotation) annotation;
				sql = sql + ") VALUES('" + importantAnno.getName() + "', '"
						+ videoName + "', " + moment + ", '"
						+ type.toString() + "', '" + importantAnno.getComment()
						+ "')";
				break;
			case CONFLICT:
				ConflictAnnotation conflictAnno = (ConflictAnnotation) annotation;
				String conflictList = Helper.listToString(conflictAnno
						.getReferenceList());

				sql = sql + ", CONFLICT, REFS) VALUES('"
						+ conflictAnno.getName() + "', '" + videoName + "', "
						+ moment + ", '" + type.toString() + "', '"
						+ conflictAnno.getComment() + "', '"
						+ conflictAnno.getConflict() + "', '" + conflictList
						+ "')";
				break;
			case QUESTION:
				QuestionAnnotation questionAnno = (QuestionAnnotation) annotation;
				String questionList = Helper.listToString(questionAnno
						.getQuestionList());

				sql = sql + ", QUESTIONS) VALUES('" + questionAnno.getName()
						+ "', '" + videoName + "', " + moment + ", '"
						+ type.toString() + "', '" + questionAnno.getComment()
						+ "', '" + questionList + "')";
				break;
			case REQUIREMENT:
				RequirementAnnotation requirementAnno = (RequirementAnnotation) annotation;
				String artefactList = Helper.listToString(requirementAnno
						.getArtefactList());

				sql = sql
						+ ", GRANULARITY, DISPLAYFORMAT, REQUIREMENT, SOURCE, "
						+ "RATIONALE, OBLIGATION, PRIORITY, ARTEFACTS) "
						+ "VALUES('" + requirementAnno.getName() + "', '"
						+ videoName + "', " + moment + ", '"
						+ type.toString() + "', '"
						+ requirementAnno.getComment() + "', '"
						+ requirementAnno.getGranularity() + "', '"
						+ requirementAnno.getDisplayFormat().toString()
						+ "', '" + requirementAnno.getRequirement() + "', '"
						+ requirementAnno.getSource() + "', '"
						+ requirementAnno.getRationale() + "', '"
						+ requirementAnno.getObligation().toString() + "', '"
						+ requirementAnno.getPriority().toString() + "', '"
						+ artefactList + "')";
				break;
			case CUSTOM:
				CustomAnnotation customAnno = (CustomAnnotation) annotation;
				sql = sql
						+ ", CUSTOMID, ATTRIBUTE1, ATTRIBUTE2, ATTRIBUTE3, ATTRIBUTE4, ATTRIBUTE5) "
						+ "VALUES('" + customAnno.getName() + "', '"
						+ videoName + "', " + moment + ", '"
						+ type.toString() + "', '" + customAnno.getComment()
						+ "', '" + customAnno.getCustomAnnoID() + "', '"
						+ customAnno.getAttribute1Value() + "', '"
						+ customAnno.getAttribute2Value() + "', '"
						+ customAnno.getAttribute3Value() + "', '"
						+ customAnno.getAttribute4Value() + "', '"
						+ customAnno.getAttribute5Value() + "')";
				break;
			}

				PreparedStatement preparedStatement;
				preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				preparedStatement.execute();

				id = preparedStatement.getGeneratedKeys().getInt(1);
				preparedStatement.close();

				if (annotation.getName().equalsIgnoreCase("")) {
				statement = connection.createStatement();
				sql = "UPDATE ANNOTATION set NAME = '" + type.toString() + "_"
						+ id + "' WHERE ID = " + id + ";";
				statement.executeUpdate(sql);
				statement.close();
			} else {
				statement = connection.createStatement();
				statement.executeUpdate(sql);
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Die Methode ruft alle Annotationdaten des Videos aus der Datenbank ab.
	 * 
	 * @param videoName
	 *            Name des Videos, dessen Annotationen geladen werden sollen
	 * 
	 * @return Liste aller Annotation in der Datenbank
	 */
	public ArrayList<Annotation> selectAnnotationData(String videoName) {
		ArrayList<Annotation> annotationList = new ArrayList<Annotation>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM ANNOTATION WHERE VIDEONAME = '"
							+ videoName + "' ORDER BY MOMENT ASC;");

			while (result.next()) {
				annotationList.add(getAnnotationFromResult(result));
			}

			result.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.reverse(annotationList);
		return annotationList;
	}

	/*
	 * Die Methode liefert die DatenAnnotation eines ResultSets einer
	 * Datenbank-Anfrage. Aufgrund unterschiedlicher Arten von select-Anfragen,
	 * die aber alle eine DatenAnnotation liefern, ist die Konstruktion in eine
	 * separate Methode ausgelagert worden.
	 */
	private DataAnnotation getAnnotationFromResult(ResultSet result) {

		DataAnnotation annotation = null;
		DataAnnotationType type;
		try {
			type = DataAnnotationType.valueOf(result.getString("TYPE"));

			switch (type) {
			case IMPORTANT:
				ImportantAnnotation importantAnno = new ImportantAnnotation();
				importantAnno.setId(result.getInt("ID"));
				importantAnno.setName(result.getString("NAME"));
				importantAnno.setVideoName(result.getString("VIDEONAME"));
				importantAnno.setMoment(result.getLong("MOMENT"));
				importantAnno.setComment(result.getString("COMMENT"));
				annotation = importantAnno;
				break;
			case CONFLICT:
				ConflictAnnotation conflictAnno = new ConflictAnnotation();
				conflictAnno.setId(result.getInt("ID"));
				conflictAnno.setName(result.getString("NAME"));
				conflictAnno.setVideoName(result.getString("VIDEONAME"));
				conflictAnno.setMoment(result.getLong("MOMENT"));
				conflictAnno.setComment(result.getString("COMMENT"));
				conflictAnno.setConflict(result.getString("CONFLICT"));

				String references = result.getString("REFS");
				conflictAnno.setReferenceList(Helper.stringToList(references));

				annotation = conflictAnno;
				break;
			case REQUIREMENT:
				RequirementAnnotation requirementAnno = new RequirementAnnotation();
				requirementAnno.setId(result.getInt("ID"));
				requirementAnno.setName(result.getString("NAME"));
				requirementAnno.setVideoName(result.getString("VIDEONAME"));
				requirementAnno.setMoment(result.getLong("MOMENT"));
				requirementAnno.setComment(result.getString("COMMENT"));

				String granularity = result.getString("GRANULARITY");
				if (granularity != null) {
					requirementAnno.setGranularity(RequirementType
							.valueOf(granularity));
				}

				String displayFormat = result.getString("DISPLAYFORMAT");
				if (displayFormat != null) {
					requirementAnno.setDisplayFormat(RequirementFormat
							.valueOf(displayFormat));
				}
				requirementAnno.setRequirement(result.getString("REQUIREMENT"));
				requirementAnno.setSource(result.getString("SOURCE"));
				requirementAnno.setRationale(result.getString("RATIONALE"));

				String obligation = result.getString("OBLIGATION");
				if (obligation != null) {
					requirementAnno.setObligation(ObligationType
							.valueOf(obligation));
				}

				String priority = result.getString("PRIORITY");
				if (priority != null) {
					requirementAnno.setPriority(Priority.valueOf(priority));
				}

				String artefacts = result.getString("ARTEFACTS");
				requirementAnno.setArtefactList(Helper.stringToList(artefacts));

				annotation = requirementAnno;
				break;
			case QUESTION:
				QuestionAnnotation questionAnno = new QuestionAnnotation();
				questionAnno.setId(result.getInt("ID"));
				questionAnno.setName(result.getString("NAME"));
				questionAnno.setVideoName(result.getString("VIDEONAME"));
				questionAnno.setMoment(result.getLong("MOMENT"));
				questionAnno.setComment(result.getString("COMMENT"));

				String questions = result.getString("QUESTIONS");
				questionAnno.setQuestionList(Helper.stringToList(questions));

				annotation = questionAnno;
				break;
			case CUSTOM:
				CustomAnnotation customAnno = new CustomAnnotation();
				List<CustomAnnotation> customAnnos = DomParser
						.getCustomAnnotationList();

				for (CustomAnnotation customAnnotation : customAnnos) {
					if (customAnnotation.getCustomAnnoID().equalsIgnoreCase(
							result.getString(("CUSTOMID")))) {
						customAnno.setCustomAnnoID(customAnnotation
								.getCustomAnnoID());
						customAnno.setAttribute1(customAnnotation
								.getAttribute1());
						customAnno.setAttribute2(customAnnotation
								.getAttribute2());
						customAnno.setAttribute3(customAnnotation
								.getAttribute3());
						customAnno.setAttribute4(customAnnotation
								.getAttribute4());
						customAnno.setAttribute5(customAnnotation
								.getAttribute5());
						break;
					}
				}

				customAnno.setId(result.getInt("ID"));
				customAnno.setName(result.getString("NAME"));
				customAnno.setVideoName(result.getString("VIDEONAME"));
				customAnno.setMoment(result.getLong("MOMENT"));
				customAnno.setComment(result.getString("COMMENT"));

				customAnno.setCustomAnnoID(result.getString("CUSTOMID"));
				customAnno.setAttribute1Value(result.getString("ATTRIBUTE1"));
				customAnno.setAttribute2Value(result.getString("ATTRIBUTE2"));
				customAnno.setAttribute3Value(result.getString("ATTRIBUTE3"));
				customAnno.setAttribute4Value(result.getString("ATTRIBUTE4"));
				customAnno.setAttribute5Value(result.getString("ATTRIBUTE5"));

				annotation = customAnno;
				break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return annotation;
	}

	/**
	 * Die Methode ruft die Annotation bez�glich der �bergebenen ID ab.
	 * 
	 * @param id
	 *            der Annotation, die abgerufen werden soll
	 * @return DataAnnotation der �bergebenen ID
	 */
	public DataAnnotation selectAnnotationByID(int id) {
		DataAnnotation annotation = null;
		try {
			statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM ANNOTATION WHERE ID = " + id
							+ ";");

			while (result.next()) {
				annotation = getAnnotationFromResult(result);
			}

			result.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return annotation;
	}

	/**
	 * Die Methode aktualisiert den entsprechenden Datensatz eines
	 * Annotationeintrages in der Datenbank.
	 * 
	 * @param annotation
	 *            die in der Datenbank aktualisiert werden soll
	 */
	public void updateAnnotationData(DataAnnotation annotation) {
		/*
		 * Da jede anwendungsspezifische DataAnnotation grundlegend �ber alle
		 * Eigenschaften einer Important-Annotation verf�gt, k�nnen diese auf
		 * jeden Fall immer in dem sql-String gesetzt werden.
		 */
		String sql = "UPDATE ANNOTATION set NAME = '" + annotation.getName()
				+ "', MOMENt = " + annotation.getMoment() + ", TYPE = '"
				+ annotation.getType().toString() + "', COMMENT = '"
				+ annotation.getComment() + "'";
		try {

			switch (annotation.getType()) {
			case CONFLICT:
				ConflictAnnotation conflictAnno = (ConflictAnnotation) annotation;
				String allReferences = Helper.listToString(conflictAnno
						.getReferenceList());

				sql = sql + ", CONFLICT = '" + conflictAnno.getConflict()
						+ "', REFS = '" + allReferences + "'";
				break;
			case QUESTION:
				QuestionAnnotation questionAnno = (QuestionAnnotation) annotation;
				String allQuestions = Helper.listToString(questionAnno
						.getQuestionList());

				sql = sql + ", QUESTIONS = '" + allQuestions + "'";
				break;
			case REQUIREMENT:
				RequirementAnnotation requirementAnno = (RequirementAnnotation) annotation;

				/*
				 * Die Liste von Artefakten wird vorerst als einfacher durch
				 * Kommas separierter String gespeichert.
				 */
				String allArtefacts = Helper.listToString(requirementAnno
						.getArtefactList());

				sql = sql + ", GRANULARITY = '"
						+ requirementAnno.getGranularity().toString()
						+ "', DISPLAYFORMAT = '"
						+ requirementAnno.getDisplayFormat().toString()
						+ "', REQUIREMENT = '"
						+ requirementAnno.getRequirement() + "', SOURCE = '"
						+ requirementAnno.getSource() + "', RATIONALE = '"
						+ requirementAnno.getRationale() + "', OBLIGATION = '"
						+ requirementAnno.getObligation().toString()
						+ "', PRIORITY = '"
						+ requirementAnno.getPriority().toString()
						+ "', ARTEFACTS = '" + allArtefacts + "'";
				break;
			case CUSTOM:
				CustomAnnotation customAnno = (CustomAnnotation) annotation;

				sql = sql + ", CUSTOMID = '" + customAnno.getCustomAnnoID()
						+ "', ATTRIBUTE1 = '" + customAnno.getAttribute1Value()
						+ "', ATTRIBUTE2 = '" + customAnno.getAttribute2Value()
						+ "', ATTRIBUTE3 = '" + customAnno.getAttribute3Value()
						+ "', ATTRIBUTE4 = '" + customAnno.getAttribute4Value()
						+ "', ATTRIBUTE5 = '" + customAnno.getAttribute5Value()
						+ "'";
				break;
			default:
				break;
			}

			sql = sql + " WHERE ID = " + annotation.getId() + ";";

			statement = connection.createStatement();

			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Methode l�scht den entsprechenden Datensatz eines
	 * Annotationseintrages aus der Datenbank.
	 * 
	 * @param id
	 *            ID des Annotationseintrages in der Datenbank
	 */
	public void deleteAnnotationData(int id) {
		try {
			statement = connection.createStatement();
			String sql = "DELETE from ANNOTATION WHERE ID = " + id + ";";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
