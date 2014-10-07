package malictus.tagcorral.ui.viewer.displaypanels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.png.*;

public class PNGTextDisplay extends JPanel {

	public PNGTextDisplay(TextChunk chunk) {		
		TCLabel keyword = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_KEYWORD") + " " + chunk.getKeyword(), 14, Font.PLAIN);
		TCLabel compressed = null;
		if (chunk.getCompressed()) {
			compressed = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_COMPRESSED") + " " + TCStrings.getStringFor("YES"), 14, Font.PLAIN);
		} else {
			compressed = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_COMPRESSED") + " " + TCStrings.getStringFor("NO"), 14, Font.PLAIN);
		}
		TCLabel chunkType = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_CHUNKTYPE") + " " + chunk.getChunkType(), 14, Font.PLAIN);
		TCLabel language = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_LANGUAGE") + " " + chunk.getLanguageTag(), 14, Font.PLAIN);
		TCLabel translatedKeyword = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_TRANSKEYWORD") + " " + chunk.getTranslatedKeyword(), 14, Font.PLAIN);
		String value = chunk.getValue();
		TCLabel valueText = new TCLabel(TCStrings.getStringFor("GCD_PNGTEXT_VALUE"), 14, Font.PLAIN);
		
		JPanel top = new JPanel();
		BoxLayout f1 = new BoxLayout(top, BoxLayout.Y_AXIS);
		top.setLayout(f1);
		top.add(chunkType);
		top.add(keyword);
		if (chunk.getChunkType().equals("iTXt")) {
			top.add(compressed);
			if (!chunk.getLanguageTag().equals("")) {
				top.add(language);
			}
			if (!chunk.getTranslatedKeyword().equals("")) {
				top.add(translatedKeyword);
			}
		}
		top.add(valueText);
		this.setLayout(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		//use UTF-8 (default) encoding here
		TCTextArea textArea = new TCTextArea(value, chunk.getParentFile().getName() + "_" + UIUtils.getChunkTypeString(chunk) + ".txt");
		textArea.getTextArea().setEditable(false);
		this.add(textArea, BorderLayout.CENTER);
	}
	
}
