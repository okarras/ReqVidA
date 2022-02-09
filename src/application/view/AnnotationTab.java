package application.view;

import application.controller.AnalyzerController;
import application.controller.AnnotationTabController;
import application.controller.ConflictAnnotationTabController;
import application.controller.CustomAnnotationTabController;
import application.controller.QuestionAnnotationTabController;
import application.controller.RecorderController;
import application.controller.RequirementAnnotationTabController;
import application.model.ControlledScene;
import application.model.annotation.DataAnnotation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * Die Klasse stellt allgemeinen einen Tab mit dem Inhalt einer Annotation dar.
 * Je nach Typ der Annotation wird ein bestimmtes Layout f�r den Tab geladen.
 * 
 * @author Oliver Karras
 *
 */
public class AnnotationTab extends Tab {

	/**
	 * Name des ImportantAnnotationTabs
	 */
	public final static String importantAnnotationTabRecorderID = "ImportantAnnotationTabRecorder";

	/**
	 * FXML-File des ImportantAnnotationTabs
	 */
	public final static String importantAnnotationTabRecorderFile = "/resources/ui/recordertab/ImportantAnnotationTabRecorder.fxml";

	/**
	 * Name des ConflictAnnotationTabs
	 */
	public final static String conflictAnnotationTabRecorderID = "ConflictAnnotationTabRecorder";

	/**
	 * FXML-File des ConflictAnnotationTabs
	 */
	public final static String conflictAnnotationTabRecorderFile = "/resources/ui/recordertab/ConflictAnnotationTabRecorder.fxml";

	/**
	 * Name des QuestionAnnotationTabs
	 */
	public final static String questionAnnotationTabRecorderID = "QuestionAnnotationTabRecorder";

	/**
	 * FXML-File des QuestiontAnnotationTabs
	 */
	public final static String questionAnnotationTabRecorderFile = "/resources/ui/recordertab/QuestionAnnotationTabRecorder.fxml";

	/**
	 * Name des RequirementAnnotationTabs
	 */
	public final static String requirementAnnotationTabRecorderID = "RequirementAnnotationTabRecorder";

	/**
	 * FXML-File des RequirementAnnotationTabs
	 */
	public final static String requirementAnnotationTabRecorderFile = "/resources/ui/recordertab/RequirementAnnotationTabRecorder.fxml";

	/**
	 * Name des CustomAnnotationTabs
	 */
	public final static String customAnnotationTabRecorderID = "customAnnotationTabRecorder";

	/**
	 * FXML-File des CustomAnnotationTabs
	 */
	public final static String customAnnotationTabRecorderFile = "/resources/ui/recordertab/CustomAnnotationTabRecorder.fxml";

	/**
	 * Name des ImportantAnnotationTabs
	 */
	public final static String importantAnnotationTabAnalyzerID = "ImportantAnnotationTabAnalyzer";

	/**
	 * FXML-File des ImportantAnnotationTabs
	 */
	public final static String importantAnnotationTabAnalyzerFile = "/resources/ui/analyzertab/ImportantAnnotationTabAnalyzer.fxml";

	/**
	 * Name des ConflictAnnotationTabs
	 */
	public final static String conflictAnnotationTabAnalyzerID = "ConflictAnnotationTabAnalyzer";

	/**
	 * FXML-File des ConflictAnnotationTabs
	 */
	public final static String conflictAnnotationTabAnalyzerFile = "/resources/ui/analyzertab/ConflictAnnotationTabAnalyzer.fxml";

	/**
	 * Name des QuestionAnnotationTabs
	 */
	public final static String questionAnnotationTabAnalyzerID = "QuestionAnnotationTabAnalyzer";

	/**
	 * FXML-File des QuestiontAnnotationTabs
	 */
	public final static String questionAnnotationTabAnalyzerFile = "/resources/ui/analyzertab/QuestionAnnotationTabAnalyzer.fxml";

	/**
	 * Name des RequirementAnnotationTabs
	 */
	public final static String requirementAnnotationTabAnalyzerID = "RequirementAnnotationTabAnalyzer";

	/**
	 * FXML-File des RequirementAnnotationTabs
	 */
	public final static String requirementAnnotationTabAnalyzerFile = "/resources/ui/analyzertab/RequirementAnnotationTabAnalyzer.fxml";

	/**
	 * Name des CustomAnnotationTabs
	 */
	public final static String customAnnotationTabAnalyzerID = "customAnnotationTabAnalyzer";

	/**
	 * FXML-File des CustomAnnotationTabs
	 */
	public final static String customAnnotationTabAnalyzerFile = "/resources/ui/analyzertab/CustomAnnotationTabAnalyzer.fxml";

	/**
	 * Konstruktor, um einen AnnotationTab zu erstellen
	 * 
	 * @param controller
	 *            der Parent-GUI, wird ben�tigt f�r sp�tere Aktualisierungen der
	 *            GUI
	 * @param annotation
	 *            die im Tab angezeigt werden soll
	 */
	public AnnotationTab(ControlledScene controller, DataAnnotation annotation) {
		try {
			setClosable(true);

			String resource = "";
			String id = Integer.toString(annotation.getId());
			AnnotationTabController tabController = null;

			switch (annotation.getType()) {
			case IMPORTANT:
				if (controller instanceof RecorderController) {
					resource = importantAnnotationTabRecorderFile;
				} else if (controller instanceof AnalyzerController) {
					resource = importantAnnotationTabAnalyzerFile;
				}

				tabController = new AnnotationTabController(controller,
						annotation);
				break;
			case CONFLICT:
				if (controller instanceof RecorderController) {
					resource = conflictAnnotationTabRecorderFile;
				} else if (controller instanceof AnalyzerController) {
					resource = conflictAnnotationTabAnalyzerFile;
				}

				tabController = new ConflictAnnotationTabController(controller,
						annotation);
				break;
			case REQUIREMENT:
				if (controller instanceof RecorderController) {
					resource = requirementAnnotationTabRecorderFile;
				} else if (controller instanceof AnalyzerController) {
					resource = requirementAnnotationTabAnalyzerFile;
				}

				tabController = new RequirementAnnotationTabController(
						controller, annotation);
				break;
			case QUESTION:
				if (controller instanceof RecorderController) {
					resource = questionAnnotationTabRecorderFile;
				} else if (controller instanceof AnalyzerController) {
					resource = questionAnnotationTabAnalyzerFile;
				}

				tabController = new QuestionAnnotationTabController(controller,
						annotation);
				break;
			case CUSTOM:
				if (controller instanceof RecorderController) {
					resource = customAnnotationTabRecorderFile;
				} else if (controller instanceof AnalyzerController) {
					resource = customAnnotationTabAnalyzerFile;
				}
				
				tabController = new CustomAnnotationTabController(controller,
						annotation);
				break;
			}

			setId(id);
			setText(annotation.getName());
			FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
			loader.setController(tabController);
			setContent(((Node) loader.load()));
			tabController.init();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
