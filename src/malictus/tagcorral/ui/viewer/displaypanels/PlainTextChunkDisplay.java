package malictus.tagcorral.ui.viewer.displaypanels;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.chunk.*;

public class PlainTextChunkDisplay extends JPanel {

	public PlainTextChunkDisplay(PlainTextChunk chunk) {
		String charsetString = chunk.getCharsetInfo().getCharset().name();
		if (chunk.getCharsetInfo().containsBOM()) {
			charsetString = charsetString + " " + TCStrings.getStringFor("PTCD_CONTAINS_BOM");
		}
		TCLabel lblCharset = new TCLabel(TCStrings.getStringFor("PTCD_CHARSET") + " " + charsetString, 14, Font.PLAIN);
		JPanel top = new JPanel();
		BoxLayout f1 = new BoxLayout(top, BoxLayout.Y_AXIS);
		top.setLayout(f1);
		top.add(lblCharset);
		this.setLayout(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		TCTextArea textArea = new TCTextArea(chunk.getChunkText(), chunk.getCharsetInfo().getCharset(), chunk.getParentFile().getName() + "_" + UIUtils.getChunkTypeString(chunk) + ".txt");
		textArea.getTextArea().setEditable(false);
		this.add(textArea, BorderLayout.CENTER);
	}
	
}