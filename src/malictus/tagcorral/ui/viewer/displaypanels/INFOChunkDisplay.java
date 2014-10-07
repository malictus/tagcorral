package malictus.tagcorral.ui.viewer.displaypanels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.riff.*;

public class INFOChunkDisplay extends JPanel {

	public INFOChunkDisplay(INFOChunk chunk) {		
		TCLabel chunkName = new TCLabel(TCStrings.getStringFor("GCD_INFO_CHUNKNAME") + " " + chunk.getChunkName() +
				" (" + TCStrings.getStringFor("INFOCHUNK_" + chunk.getChunkName()) + ")", 14, Font.PLAIN);
		TCLabel valueText = new TCLabel(TCStrings.getStringFor("GCD_INFO_VALUE"), 14, Font.PLAIN);
		JPanel top = new JPanel();
		BoxLayout f1 = new BoxLayout(top, BoxLayout.Y_AXIS);
		top.setLayout(f1);
		top.add(chunkName);
		top.add(valueText);
		this.setLayout(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		//default encoding is fine here
		TCTextArea textArea = new TCTextArea(chunk.getChunkData(), chunk.getParentFile().getName() + "_" + UIUtils.getChunkTypeString(chunk) + ".txt");
		textArea.getTextArea().setEditable(false);
		this.add(textArea, BorderLayout.CENTER);
	}
	
}
