package malictus.tagcorral.ui;

import java.util.*;
import javax.swing.filechooser.*;
import java.io.File;

/**
 * General file filter that filters based on extension
 */
public class TCFileFilter extends FileFilter {

	private Vector<String> exts;
	private String title;
	
	public TCFileFilter(Vector<String> exts, String title) {
		super();
		this.exts = exts;
		this.title = title;
	}

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String filename = f.getName();
		int i = filename.lastIndexOf('.');
		String extension = "";
        if ((i > 0) &&  (i < filename.length() - 1)) {
            extension = filename.substring(i+1);
        }
        extension = extension.toLowerCase();
        int counter = 0;
        while (counter < exts.size()) {
        	String cand = exts.get(counter);
        	if (cand.trim().toLowerCase().equals(extension)) {
        		return true;
        	}
        	counter = counter + 1;
        }
        return false;
    }

    public String getDescription() {
        return this.title;
    }
}