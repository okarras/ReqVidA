package application.view;

import java.util.ArrayList;
import java.util.Arrays;

import org.controlsfx.control.RangeSlider;

import application.controller.AnalyzerController;
import application.database.SQLiteDataBase;
import application.model.annotation.Annotation;
import application.model.annotation.DataAnnotation;
import application.util.Helper;
import application.view.GanttChart.ExtraData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * Die Klasse stellt die interaktive Timeline zur visuellen Anzeige der
 * erstellten Annotationen im Kontext des Video zu den entsprechenden
 * Zeitpunkten dar.
 * 
 * @author Oliver Karras
 *
 */
public class Timeline extends AnchorPane {

	private GanttChart<Number, String> chart;
	private RangeSlider hSlider;

	private XYChart.Series<Number, String> series;
	private String[] annotationTypes;
	private ArrayList<Annotation> annotations;
	private NumberAxis xAxis;
	private CategoryAxis yAxis;

	private AnalyzerController controller;

	/**
	 * Konstrukor, um ein Timeline-Objekt zu erstellen
	 */
	public Timeline() {

		/*
		 * Aufsetzen des Charts und seiner Achsen
		 */
		series = new XYChart.Series<Number, String>();
		annotationTypes = new String[1];

		xAxis = new NumberAxis(0, 3600000, 60000);
		yAxis = new CategoryAxis();

		chart = new GanttChart<Number, String>(xAxis, yAxis, this);

		xAxis.setTickLabelFill(Color.BLACK);
		xAxis.setSide(Side.TOP);
		xAxis.setMinorTickCount(6);
		xAxis.setTickLength(12);
		xAxis.setMinorTickLength(10);
		xAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number time) {
				return Helper.milliToTimeFormat1(time.longValue());
			}

			@Override
			public Number fromString(String string) {
				return null;
			}

		});

		yAxis.setTickLabelFill(Color.BLACK);
		yAxis.setTickLabelGap(5);

		chart.setPadding(new Insets(0, 15, 0, 5));
		chart.setLegendVisible(false);
		chart.setBlockHeight(15);

		/*
		 * Aufsetzen des RangeSliders zum Zooming im Graphen
		 */
		hSlider = new RangeSlider(0, 3600000, 0, 3600000);

		hSlider.setShowTickMarks(true);
		hSlider.setShowTickLabels(true);
		hSlider.setBlockIncrement(60000);
		hSlider.setMajorTickUnit(60000);
		hSlider.setMinorTickCount(5);
		hSlider.setMinHeight(25);
		hSlider.setPadding(new Insets(5, 15, 15, 15));
		hSlider.setLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number time) {
				return Helper.milliToTimeFormat1(time.longValue());
			}

			@Override
			public Number fromString(String string) {
				return null;
			}
		});

		/*
		 * Anpassung der unteren Grenze des Graphen an den niedrigeren Wert des
		 * RangeSliders
		 */
		hSlider.lowValueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (oldValue != newValue) {
					double difference = hSlider.getHighValue()
							- hSlider.getLowValue();
					if (difference < 59000) {
						hSlider.setLowValue(oldValue.doubleValue());
						xAxis.setLowerBound(oldValue.doubleValue());
					} else {
						xAxis.setLowerBound(newValue.doubleValue());
					}
					updateDataPoints();

				}
			}
		});

		/*
		 * Anpassung der oberen Grenze des Graphen an den h�chsten Wert des
		 * RangeSliders
		 */
		hSlider.highValueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (oldValue != newValue) {
					double difference = hSlider.getHighValue()
							- hSlider.getLowValue();
					if (difference < 59000) {
						hSlider.setHighValue(oldValue.doubleValue());
						xAxis.setUpperBound(oldValue.doubleValue());
					} else {
						xAxis.setUpperBound(newValue.doubleValue());
					}
					updateDataPoints();
				}
			}
		});

		VBox box = new VBox(0, chart, hSlider);

		setTopAnchor(box, 0.0);
		setRightAnchor(box, 0.0);
		setBottomAnchor(box, 0.0);
		setLeftAnchor(box, 0.0);

		getChildren().add(box);
	}

	/**
	 * Setter-Methode f�r den Controller der ParentView, die die Timeline enh�lt
	 * 
	 * @param controller
	 *            der ParentView, die die Timeline enth�lt
	 */
	public void setController(AnalyzerController controller) {
		this.controller = controller;
	}

	/**
	 * Getter-Methode f�r Annotationstypes in String-Darstellung
	 * 
	 * @return String-Array aller DatenAnnotationstypen
	 */
	public String[] getAnnotationTypes() {
		return annotationTypes;
	}

	/**
	 * Setter-Methode f�r die AnnotationsTypen in String-Darstellung
	 * 
	 * @param annotations
	 *            neues Array aller Datenannotationstypen in String-Darstellung
	 */
	public void setAnnotationTypes(String[] annotations) {
		this.annotationTypes = annotations;
	}

	/**
	 * Setter-Methode f�r die obere Grenze der x-Achse des Graphen
	 * 
	 * @param upperBound
	 *            Obere Grenze des Graphen
	 */
	public void setxAxisUpperBound(double upperBound) {
		xAxis.setUpperBound(upperBound);
	}

	/**
	 * Setter-Methode f�r den oberen Wert des RangeSliders
	 * 
	 * @param value
	 *            obere Wert des RangeSliders
	 */
	public void setSliderHighValue(double value) {
		hSlider.setMax(value);
		hSlider.setHighValue(value);
	}

	/**
	 * Setter-Methode f�r die Annotationsdaten, die im Graph dargestellt werden
	 * sollen
	 * 
	 * @param annotations
	 *            Daten, die im Graph dargestellt werden sollen
	 */
	public void setAnnotationData(ArrayList<Annotation> annotations) {
		this.annotations = annotations;
		series.getData().clear();

		double difference = hSlider.getHighValue() - hSlider.getLowValue();
		double xLength = difference * 0.0075;

		for (Annotation annotation : annotations) {
			DataAnnotation anno = (DataAnnotation) annotation;
			series.getData().add(
					new XYChart.Data<Number, String>(anno.getMoment(), anno
							.getType().toString(), new ExtraData(
							(long) xLength, anno.getType().toString(), anno)));
		}
	}

	/*
	 * Die Methode dient der Aktualisierung der Gr��e der Datenpunkte, wenn �ber
	 * den Range Slider der Zeitbereich des Graphen ver�ndert wird.
	 */
	private void updateDataPoints() {
		setAnnotationData(annotations);
	}

	/**
	 * Auswahl einer Annotation, die im Tab ge�ffnet werden soll bzw. zu deren
	 * offenen Tab gewechselt werden soll
	 * 
	 * @param id
	 *            der Annotation, die betrachtet werden soll
	 */
	public void selectAnnotation(int id) {
		DataAnnotation annotation = SQLiteDataBase.getInstance()
				.selectAnnotationByID(id);
		controller.openAnnotationTab(annotation);
	}

	/**
	 * Die Methode dient der Aktualisierung der Annotationsdaten, sobald eine
	 * �nderung �ber die Daten im Graph erfolgt ist.
	 * 
	 * @param annotation
	 *            neue Annotationsdaten, die aktualisiert werden m�ssen
	 */
	public void updateAnnotationData(DataAnnotation annotation) {
		controller.updateAnnotations(annotation, false);
	}

	/**
	 * Die Methode dient dazu den MediaPlayer an die �bergebene Stelle im Video
	 * zu springen.
	 * 
	 * @param time
	 *            Zeitpunkt im Video, der im MediaPlayer dargestellt werden soll
	 */
	public void seekMediaPlayer(long time) {
		controller.seekTo(time);
	}

	/**
	 * Die Methode dient dem Aufsetzen der Timeline, sodass dann die ben�tigten
	 * Daten gesetzt werden k�nnen, um diese anzeigen zu k�nnen.
	 */
	@SuppressWarnings("unchecked")
	public void setup() {
		yAxis.setCategories(FXCollections.<String> observableArrayList(Arrays
				.asList(annotationTypes)));

		chart.getData().addAll(series);

		chart.getStylesheets().add(
				getClass().getResource("/resources/ui/ganttchart.css").toExternalForm());
	}
}
