package application.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.database.SQLiteDataBase;
import application.model.annotation.DataAnnotation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Die Klasse dient zur Darstellung eines GanttCharts, der bez�glich der
 * Timeline verwendet wird, um die erstellten Annotationen visuell im Kontext
 * des Zeitpunktes im Video darzustellen. Die x-Achse rep�sentiert hierbei die
 * Zeit und die y-Achse listet die verschiedenen Annotationstypen auf.
 * 
 * @author Oliver Karras
 *
 * @param <X>
 * @param <Y>
 */
public class GanttChart<X, Y> extends XYChart<X, Y> {

	/**
	 * H�he eines Annotations-Elementes auf dem Chart
	 */
	private double blockHeight = 10;

	/**
	 * Timeline, die den Chart enth�lt
	 */
	private Timeline parent;

	/**
	 * Konstruktor, um ein GanttChart-Objekt zu erstellen
	 * 
	 * @param xAxis
	 *            x-Achse des GanttCharts
	 * @param yAxis
	 *            y-Achse des GanttCharts
	 * @param parent
	 *            der den GanttChart enth�lt
	 */
	public GanttChart(Axis<X> xAxis, Axis<Y> yAxis, Timeline parent) {
		this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList(),
				parent);
	}

	/**
	 * Konstruktor, um ein GanttChart-Objekt zu erstellen
	 * 
	 * @param xAxis
	 *            x-Achse des GanttCharts
	 * @param yAxis
	 *            y-Achse des GanttCharts
	 * @param data
	 *            Liste von Datenpunkten, die in dem Chart dargestellt werden
	 * @param parent
	 *            der den GanttChart enth�lt
	 */
	public GanttChart(Axis<X> xAxis, Axis<Y> yAxis,
			ObservableList<Series<X, Y>> data, Timeline parent) {
		super(xAxis, yAxis);

		if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
			throw new IllegalArgumentException(
					"Axis type incorrect, X and Y should both be NumberAxis");
		}

		this.parent = parent;
		setData(data);
	}

	/**
	 * Die Methode erstellt das Layout der einzelnen Datenpunkte, die im
	 * GanttChart dargestellt werden.
	 */
	@Override
	protected void layoutPlotChildren() {

		/*
		 * Iteration �ber alle Datenserien, die im Graphen dargestellt werden
		 * sollen
		 */
		for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {

			Series<X, Y> series = getData().get(seriesIndex);

			/*
			 * Iteration �ber alle Datenpunkte der entsprechenden Datenserie
			 */
			Iterator<Data<X, Y>> iter = getDisplayedDataIterator(series);
			while (iter.hasNext()) {
				Data<X, Y> item = iter.next();
				double x = getXAxis().getDisplayPosition(item.getXValue());
				double y = getYAxis().getDisplayPosition(item.getYValue());
				if (Double.isNaN(x) || Double.isNaN(y)) {
					continue;
				}

				/*
				 * Layout-Erstellung des Datenpunktes
				 */
				Node block = item.getNode();
				Rectangle ellipse;
				if (block != null) {
					if (block instanceof StackPane) {
						StackPane region = (StackPane) item.getNode();
						if (region.getShape() == null) {
							ellipse = new Rectangle(
									getLength(item.getExtraValue()),
									getBlockHeight());
						} else if (region.getShape() instanceof Rectangle) {
							ellipse = (Rectangle) region.getShape();
						} else {
							return;
						}

						/*
						 * Skalierung der Gr��e des Datenpunktes
						 */
						ellipse.setWidth(getLength(item.getExtraValue())
								* ((getXAxis() instanceof NumberAxis) ? Math
										.abs(((NumberAxis) getXAxis())
												.getScale()) : 1));
						ellipse.setHeight(getBlockHeight()
								* ((getYAxis() instanceof NumberAxis) ? Math
										.abs(((NumberAxis) getYAxis())
												.getScale()) : 1));

						y -= getBlockHeight() / 2.0;
						x -= ellipse.getWidth() / 2.0;

						ellipse.setArcHeight(5);
						ellipse.setArcWidth(5);

						// Note: workaround for RT-7689 - saw this in
						// ProgressControlSkin
						// The region doesn't update itself when the shape is
						// mutated in place, so we
						// null out and then restore the shape in order to force
						// invalidation.
						region.setShape(null);
						region.setShape(ellipse);
						region.setScaleShape(false);
						region.setCenterShape(false);
						region.setCacheShape(false);

						block.setLayoutX(x);
						block.setLayoutY(y);

						/*
						 * Hinzuf�gen des Tooltip zu jedem Datenpunkt
						 */
						DataAnnotation anno = getAnnotation(item
								.getExtraValue());
						AnnotationToolTip tip = new AnnotationToolTip(anno);
						Tooltip.install(block, tip);
					}
				}
			}
		}
	}

	/**
	 * Die Methode stellt fest, wenn ein neuer Datenpunkt hinzugef�gt worden ist
	 * und erstellt dann das entsprechende Layout f�r den Datenpunkt.
	 */
	@Override
	protected void dataItemAdded(Series<X, Y> series, int itemIndex,
			Data<X, Y> item) {
		Node block = createContainer(series, getData().indexOf(series), item,
				itemIndex);
		getPlotChildren().add(block);

	}

	/**
	 * DIe Methode erkennt, wenn ein Datenpunkt entfernt worden ist und entfernt
	 * das Layout des zugeh�rigen Datenpunktes.
	 */
	@Override
	protected void dataItemRemoved(final Data<X, Y> item,
			final Series<X, Y> series) {
		final Node block = item.getNode();
		getPlotChildren().remove(block);
		removeDataItemFromDisplay(series, item);
	}

	/**
	 * Die Methode erkennt, wenn sich die Werte eines Datenpunktes ver�ndert und
	 * aktualisiert den MediaPlayer entsprechend zur Auswahl des gew�nschten
	 * Zeitpuntkes f�r die Annotation im Video.
	 */
	@Override
	protected void dataItemChanged(Data<X, Y> item) {
		parent.seekMediaPlayer(((Number) item.getXValue()).longValue());
	}

	/**
	 * Die Methode erkennt, wenn eine neue Datenserie zum Chart hinzugef�gt
	 * worden ist und erstellt das Layout f�r alle Datenpunkte der Datenserie.
	 */
	@Override
	protected void seriesAdded(Series<X, Y> series, int seriesIndex) {
		for (int j = 0; j < series.getData().size(); j++) {
			Data<X, Y> item = series.getData().get(j);
			Node container = createContainer(series, seriesIndex, item, j);
			getPlotChildren().add(container);
		}
	}

	/**
	 * Die Methode erkennt, wenn eine Datenserie aus dem Chart entfernt worden
	 * ist und entfernt entsprechend die Layouts der zugeh�rigen Datenpunkte.
	 */
	@Override
	protected void seriesRemoved(final Series<X, Y> series) {
		for (XYChart.Data<X, Y> d : series.getData()) {
			final Node container = d.getNode();
			getPlotChildren().remove(container);
		}
		removeSeriesFromDisplay(series);

	}

	/**
	 * Die Methode aktualisiert den Bereich der Achsen bei jeglicher Ver�nderung
	 * der Datenwerte der Achsen.
	 */
	@Override
	protected void updateAxisRange() {
		final Axis<X> xa = getXAxis();
		final Axis<Y> ya = getYAxis();
		List<X> xData = null;
		List<Y> yData = null;
		if (xa.isAutoRanging())
			xData = new ArrayList<X>();
		if (ya.isAutoRanging())
			yData = new ArrayList<Y>();
		if (xData != null || yData != null) {
			for (Series<X, Y> series : getData()) {
				for (Data<X, Y> data : series.getData()) {
					if (xData != null) {
						xData.add(data.getXValue());
						xData.add(xa.toRealValue(xa.toNumericValue(data
								.getXValue()) + getLength(data.getExtraValue())));
					}
					if (yData != null) {
						yData.add(data.getYValue());
					}
				}
			}
			if (xData != null)
				xa.invalidateRange(xData);
			if (yData != null)
				ya.invalidateRange(yData);
		}
	}

	/**
	 * Getter-Methode f�r die H�he eines Annotationsdatenpunktes.
	 * 
	 * @return H�he des Datenpunktes einer Annotation
	 */
	public double getBlockHeight() {
		return blockHeight;
	}

	/**
	 * Setter-Methode f�r die H�he eines Annotationsdatenpunktes
	 * 
	 * @param blockHeight
	 *            neue H�he des Datenpuntkes einer Annotation
	 */
	public void setBlockHeight(double blockHeight) {
		this.blockHeight = blockHeight;
	}

	/*
	 * Die Methode erstellt den Container f�r das Layout eines Datenpunktes und
	 * setzt weiterhin die EventHandler f�r die Interaktion mit den
	 * Datenpunkten.
	 */
	private Node createContainer(Series<X, Y> series, int seriesIndex,
			final Data<X, Y> item, int itemIndex) {
		Node container = item.getNode();

		if (container == null) {
			container = new StackPane();
			item.setNode(container);
		}

		container.getStyleClass().add(getStyleClass(item.getExtraValue()));

		container.setCursor(Cursor.HAND);

		/*
		 * EventHandler, wenn der container gedraggt wird, erlaubt das
		 * Verschieben des Datenpunktes auf der x-Achse, um den Zeitpunkt der
		 * Annotation im Video anzupassen.
		 */
		container.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(MouseEvent e) {
				NumberAxis xAxis = (NumberAxis) getXAxis();

				Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
				double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
				Number x = xAxis.getValueForDisplay(xAxisLoc);

				if (x.doubleValue() >= xAxis.getLowerBound()
						&& x.doubleValue() <= xAxis.getUpperBound()) {
					item.setXValue((X) x);
				}
			}
		});

		/*
		 * Erlaubt die Selektion des Annotations-Tabs durch einen einfachen
		 * Klick auf den Datenpunkt
		 */
		container.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown()) {
					DataAnnotation anno = getAnnotation(item.getExtraValue());
					parent.selectAnnotation(anno.getId());
				}
			}
		});

		/*
		 * Aktualisiert die Zeit beim Loslassen der Maustaste, wenn sich die
		 * Zeit des Datenpunktes wirklich ver�ndert hat.
		 * 
		 * Bei einem Doppelklick wird an die entsprechende Stelle im Video
		 * gesprungen
		 */
		container.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				DataAnnotation anno = getAnnotation(item.getExtraValue());
				int id = anno.getId();
				DataAnnotation annotation = SQLiteDataBase.getInstance()
						.selectAnnotationByID(id);
				long annoTime = annotation.getMoment();
				long itemTime = ((Number) item.getXValue()).longValue();
				if (annoTime != itemTime) {
					annotation.setMoment(itemTime);
					SQLiteDataBase.getInstance().updateAnnotationData(
							annotation);
					parent.updateAnnotationData(annotation);
				}

				if (event.getClickCount() == 2) {
					parent.seekMediaPlayer(((Number) item.getXValue())
							.longValue());
				}
			}
		});

		return container;
	}

	/**
	 * Die Klasse dient als Container f�r zus�tzliche Informationen, die zu
	 * einem Datenpunkt mit �bergeben werden sollen.
	 * 
	 * @author Oliver Karras
	 *
	 */
	public static class ExtraData {

		/**
		 * L�nge bzw. Breite eines Datenpunktes im Chart
		 */
		public long length;

		/**
		 * Setzt das Styling f�r den Datenpunkt Hier: je nach
		 * Datenannotationstyp wird eine bestimmte Farbe verwendet
		 */
		public String styleClass;

		/**
		 * Datenannotation, die durch den zugeh�rigen Datenpunkt dargestellt und
		 * durch dessen Ver�nderung angepasst werden muss
		 */
		public DataAnnotation annotation;

		/**
		 * Konstruktor f�r eine ExtraData-Objekt
		 * 
		 * @param lengthMs
		 *            L�nge des Datenpunktes in Millisekunden
		 * @param styleClass
		 *            zur Anpassung des Layouts des Datenpunktes
		 * @param annotation
		 *            die durch den Datenpunkt dargestellt wird
		 */
		public ExtraData(long lengthMs, String styleClass,
				DataAnnotation annotation) {
			super();
			this.length = lengthMs;
			this.styleClass = styleClass;
			this.annotation = annotation;
		}

		/**
		 * Getter-Methode f�r die L�nge eines Datenpunktes
		 * 
		 * @return L�nge des Datenpunktes
		 */
		public long getLength() {
			return length;
		}

		/**
		 * Setter-Methode f�r die L�nge eines Datenpunktes
		 * 
		 * @param length
		 *            neue L�nge des Datenpunktes
		 */
		public void setLength(long length) {
			this.length = length;
		}

		/**
		 * Getter-Methode f�r die StyleClass eines Datenpunktes
		 * 
		 * @return StyleClass des Datenpunktes
		 */
		public String getStyleClass() {
			return styleClass;
		}

		/**
		 * Setter-Methode f�r die StyleClass eines Datenpunktes
		 * 
		 * @param styleClass
		 *            neue StyleClass des Datenpunktes
		 */
		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}

		/**
		 * Getter-Methode f�r die Annotation eines Datenpunktes
		 * 
		 * @return Annotation des Datenpunktes
		 */
		public DataAnnotation getAnnotation() {
			return annotation;
		}

		/**
		 * Setter-Methode f�r die Annotation eines Datenpunktes
		 * 
		 * @param annotation
		 *            neue Annotation des Datenpunktes
		 */
		public void setAnnotation(DataAnnotation annotation) {
			this.annotation = annotation;
		}
	}

	/*
	 * interne Getter-Methode, um die StyleClass aus einem ExtraData-Objekt zu
	 * erhalten
	 */
	private static String getStyleClass(Object obj) {
		return ((ExtraData) obj).getStyleClass();
	}

	/*
	 * interne Getter-Methode, um die L�nge aus einem ExtraData-Objekt zu
	 * erhalten
	 */
	private static double getLength(Object obj) {
		return ((ExtraData) obj).getLength();
	}

	/*
	 * interne Getter-Methode, um die Annotation aus einem ExtraData-Objekt zu
	 * erhalten
	 */
	private static DataAnnotation getAnnotation(Object obj) {
		return ((ExtraData) obj).getAnnotation();
	}
}
