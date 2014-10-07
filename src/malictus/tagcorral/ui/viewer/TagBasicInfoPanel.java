package malictus.tagcorral.ui.viewer;

import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.chunk.*;

/**
 * A panel that contains basic (non-specific) information about the currently selected tag
 */
public class TagBasicInfoPanel extends HTMLTextPane {
	
	public TagBasicInfoPanel() {
		super("", null);
		MSG_mainWindowNewChunk(null);
	}
	
	//message from TagInfoPanel --> new chunk was selected; use null for none
	protected void MSG_mainWindowNewChunk(MetaChunk chunk) {
		if (chunk == null) {
			this.setText("<html><body> </body></html>");		//odd bug sometimes shows characters otherwise
			this.setExportName("");
		} else {
			String c = "<html><body><font face = 'sans-serif'>";
			c = c + "<b>" + TCStrings.getStringFor("TIP_CHUNKTYPE") + "</b> " + UIUtils.getChunkTypeString(chunk) + "<br/>";	
			c = c + "<b>" + TCStrings.getStringFor("TIP_START_BYTE") + "</b> " + chunk.getStartByte() + "<br/>";	
			c = c + "<b>" + TCStrings.getStringFor("TIP_LENGTH") + "</b> " + chunk.getLength() + "<br/><br/>";	
			if (!chunk.chunkIsValid()) {
    			int counter = 0;
    			c = c + "<b><font color='red'>" + TCStrings.getStringFor("TIP_INVALID_CHUNK") + "</font></b><ul>";
    			while (counter < chunk.getInvalidReasons().size()) {
    				c = c + "<li>" + TCStrings.getStringFor(chunk.getInvalidReasons().get(counter)) + "</li>";
    				counter = counter + 1;
    			}
    			c = c + "</ul>";
    		}
			c = c + "</font></body></html>";
			this.setText(c);
			this.setExportName(chunk.getParentFile().getName() + "_" + UIUtils.getChunkTypeString(chunk) + ".html");
		}
	}

}
