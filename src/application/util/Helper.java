package application.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Die Klasse dient als Helfer bei h�ufig ben�tigten Funktionen an den
 * verschiedensten Stellen im Code.
 * 
 * @author Oliver Karras
 *
 */
public class Helper {

	/**
	 * Die Methode konvertiert die �bergebene Zeit in Millisekunden in die
	 * entsprechende String-Darstellung der Form
	 * [0-9][0-9]:[0-5][0-9]:[0-5][0-9].
	 * 
	 * @param time
	 *            die konvertiert werden soll
	 * @return time als String in dem obigen Format
	 */
	public static String milliToTimeFormat1(long time) {
		long second = (time / 1000) % 60;
		long minute = (time / (1000 * 60)) % 60;
		long hour = (time / (1000 * 60 * 60)) % 24;
		String result = String.format("%02d:%02d:%02d", hour, minute, second);
		return result;
	}
	
	/**
	 * Die Methode konvertiert die �bergebene Zeit in Millisekunden in die
	 * entsprechende String-Darstellung der Form
	 * [0-9][0-9].[0-5][0-9].[0-5][0-9].
	 * 
	 * @param time
	 *            die konvertiert werden soll
	 * @return time als String in dem obigen Format
	 */
	public static String milliToTimeFormat2(long time) {
		long second = (time / 1000) % 60;
		long minute = (time / (1000 * 60)) % 60;
		long hour = (time / (1000 * 60 * 60)) % 24;
		String result = String.format("%02d.%02d.%02d", hour, minute, second);
		return result;
	}

	/**
	 * Die Methode wandelt eine Liste von Strings in einen durch Kommas
	 * separierten String um.
	 * 
	 * @param list
	 *            Liste die als ein einzelner String dargestellt werden soll
	 * @return List als ein durch Kommas separierter String
	 */
	public static String listToString(List<String> list) {
		String completeString = "";
		Iterator<String> iterator = list.iterator();

		while (iterator.hasNext()) {
			String element = iterator.next();
			completeString = completeString + element;

			if (iterator.hasNext()) {
				completeString = completeString + "#";
			}
		}
		return completeString;
	}

	/**
	 * Die Methode wandelt einen durch Kommas separierten String in eine Liste
	 * von String um.
	 * 
	 * @param content
	 *            durch Kommas separierter String der als Liste von String
	 *            dargestellt werden soll
	 * @return Liste von Strings
	 */
	public static List<String> stringToList(String content) {
		List<String> list = new ArrayList<String>();
		if (content != null && (content.equalsIgnoreCase("") == false)) {
			String[] temp = content.split("#");
			list = new ArrayList<String>(Arrays.asList(temp));
		}
		return list;
	}
}
