package malictus.tagcorral.ui;

import java.util.prefs.*;

/**
 * Contains preferences information.
 */
public class TCPrefs {

	static private final String PREF_PACKAGE = "/malictus/tagcorral/prefs";
	//value will be location of last opened folder
	static public final String PREF_LAST_OPENED_FOLDER = "lastfolder";
	//value will be "true" or "false"
	static public final String PREF_SHOW_UNSUPPORTED_FILES = "showunsupported";
	//value will be "true" or "false"
	static public final String PREF_REVERSE_SORT = "reversesort";
	//value will be column number
	static public final String PREF_SORT_COLUMN = "sortcolumn";

    private TCPrefs() {}
	
    static public String getPrefValueFor(String pref) {
    	Preferences prefs = Preferences.userRoot().node(PREF_PACKAGE);
    	return prefs.get(pref, "");
    }
    
    static public void setPrefValue(String pref, String value) {
    	Preferences prefs = Preferences.userRoot().node(PREF_PACKAGE);
    	prefs.put(pref, value);
    }
	
}
