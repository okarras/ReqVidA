package application.view;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

/**
 * Die Klasse dient als angepasst Zeit- und Volume-Silder f�r den integrierten
 * MediaPlayer. Aufgrund der M�glichkeit der Interaktion mit dem Slider �ber die
 * Maus bezgl. Draggen und Klicken ist es f�r eine kontinuierliche Anpassung der
 * Slider-Werte erforderliche mittels entsprechender EventFilter die Zeitpunkte
 * der letzten MouseEvents zu bestimmen.
 * 
 * @author Oliver Karras
 *
 */
public class CustomSlider extends Slider {

	/**
	 * Letzte Zeitpunkt zu dem ein MousePressed-Event aufgetreten ist
	 */
	private long lastTimeMousePressed = 0;

	/**
	 * Letzte Zeitpunkt zu dem ein MouseDragged-Event aufgetreten ists
	 */
	private long lastTimeMouseDragged = 0;

	/**
	 * Konstruktor um ein Custom-Slider-Objekt zu erstellen
	 */
	public CustomSlider() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/resources/ui/custom_slider.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		loader.setClassLoader(getClass().getClassLoader());
		try {
			loader.load();
		} catch (Exception e) {

		}

		addEventFilter(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						lastTimeMousePressed = System.currentTimeMillis();
					}
				});

		addEventFilter(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						lastTimeMouseDragged = System.currentTimeMillis();
					}
				});
	}

	/**
	 * Die Methode pr�ft, ob ein MousePressed-Event innerhalb der letzten t
	 * Millisekunden aufgetreten ist.
	 * 
	 * @param t
	 *            Zeitspanne, f�r die gepr�ft werden soll, ob ein
	 *            MousePressed-Event aufgetreten ist
	 * @return TRUE, falls ein MousePressed-Event innerhalb von t Millisekunden
	 *         aufgetreten ist, ansonsten FALSE
	 */
	public boolean mouseWasPressedWithinLast(long t) {
		return (System.currentTimeMillis() - lastTimeMousePressed) <= t;
	}

	/**
	 * Die Methode pr�ft, ob ein MouseDragged-Event innerhalb der letzten t
	 * Millisekunden aufgetreten ist.
	 * 
	 * @param t
	 *            Zeitspanne, f�r die gepr�ft werden soll, ob ein
	 *            MouseDragged-Event aufgetreten ist
	 * @return TRUE, falls ein MouseDragged-Event innerhalb von t Millisekunden
	 *         aufgetreten ist, ansonsten FALSE
	 */
	public boolean mouseWasDraggedWithonLast(long t) {
		return (System.currentTimeMillis() - lastTimeMouseDragged) <= t;
	}
}
