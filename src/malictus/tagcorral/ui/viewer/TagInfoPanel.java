package malictus.tagcorral.ui.viewer;

import javax.swing.*;
import java.awt.*;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.gif.*;
import malictus.tagcorral.lib.png.*;
import malictus.tagcorral.lib.riff.*;
import malictus.tagcorral.ui.viewer.displaypanels.*;

/**
 * A panel that contains information about the currently selected tag
 */
public class TagInfoPanel extends JScrollPane {
	
	private JPanel content;
	private TagBasicInfoPanel basicInfo = null;
	private JPanel pnlChunkDisplay;
	
	public TagInfoPanel() {
		super();
		content = new JPanel();
		this.setViewportView(content);
		basicInfo = new TagBasicInfoPanel();
		content.setLayout(new BorderLayout());
		content.add(basicInfo, BorderLayout.NORTH);
		MSG_mainWindowNewChunk(null);
	}
	
	protected void MSG_setInterfaceEnabled(boolean val) {
		//nothing here yet
	}
	
	//main window new chunk was selected; use null for none
	protected void MSG_mainWindowNewChunk(MetaChunk chunk) {
		content.removeAll();
		basicInfo = new TagBasicInfoPanel();
		basicInfo.MSG_mainWindowNewChunk(chunk);
		content.add(basicInfo, BorderLayout.NORTH);
		if (chunk == null) {
			basicInfo.setPreferredSize(new Dimension(0,0));
		} else if (chunk.chunkIsValid()){
			basicInfo.setPreferredSize(new Dimension(80, 90));
		} else {
			basicInfo.setPreferredSize(new Dimension(175, 175));
		}       
        if (chunk != null) {
    		//display sub-panel based on chunk type
    		if (chunk instanceof GIFComment) {
    			pnlChunkDisplay = new GIFCommentDisplay((GIFComment)chunk);
    			content.add(pnlChunkDisplay, BorderLayout.CENTER);
    		} else if (chunk instanceof TIMEChunk) {
    			pnlChunkDisplay = new PNGTimeDisplay((TIMEChunk)chunk);
    			content.add(pnlChunkDisplay, BorderLayout.CENTER);
    		} else if (chunk instanceof TextChunk) {
    			pnlChunkDisplay = new PNGTextDisplay((TextChunk)chunk);
    			content.add(pnlChunkDisplay, BorderLayout.CENTER);
    		} else if (chunk instanceof INFOChunk) {
    			pnlChunkDisplay = new INFOChunkDisplay((INFOChunk)chunk);
    			content.add(pnlChunkDisplay, BorderLayout.CENTER);
    		} else if (chunk instanceof PlainTextChunk) {
    			pnlChunkDisplay = new PlainTextChunkDisplay((PlainTextChunk)chunk);
    			content.add(pnlChunkDisplay, BorderLayout.CENTER);
    		} else {
    			System.err.println("No display class implemented for " + chunk.getClass().getName());
    		}
        }
        
        this.revalidate();
        this.repaint();
	}

}
