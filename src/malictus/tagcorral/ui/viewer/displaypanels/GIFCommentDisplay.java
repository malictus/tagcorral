package malictus.tagcorral.ui.viewer.displaypanels;

import java.awt.*;
import javax.swing.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.gif.*;

public class GIFCommentDisplay extends JPanel {

	public GIFCommentDisplay(GIFComment chunk) {
		//default encoding for export should be fine here
		TCTextArea textArea = new TCTextArea(chunk.getComment(), chunk.getParentFile().getName() + "_" + UIUtils.getChunkTypeString(chunk) + ".txt");
		textArea.getTextArea().setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(textArea, BorderLayout.CENTER);
		JPanel topPart = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		topPart.setLayout(fl);
		this.add(topPart, BorderLayout.NORTH);
		topPart.add(new TCLabel(TCStrings.getStringFor("GCD_COMMENT_TITLE"), 14, Font.PLAIN));
	}
	
}
