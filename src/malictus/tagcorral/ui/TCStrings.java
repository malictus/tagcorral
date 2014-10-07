package malictus.tagcorral.ui;

import java.util.*;

/**
 * Contains static method for retrieval of UI strings. For now, all strings are in English, although this could easily be altered in the future.
 */
public class TCStrings {
	
	private TCStrings() {}
	
	private static Locale defaultLocale;
	private static ResourceBundle messages;
	
	static {
		//for now, just one locale
		defaultLocale = new Locale("en", "US");
		messages = ResourceBundle.getBundle("malictus.tagcorral.ui.MessagesBundle", defaultLocale);
	}
	
	/**
	 * Return the string associated with a string key.
	 * @param key the key 
	 * @return the string the corresonds to that key, or an empty string if no match is found
	 */
	public static String getStringFor(String key) {
		String returnval = "";
		try {
			returnval = messages.getString(key);
		} catch (Exception err) {
			err.printStackTrace();
			return "";
		}
		return returnval;
	}

}
