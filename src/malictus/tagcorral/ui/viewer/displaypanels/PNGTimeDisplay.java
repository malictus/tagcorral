package malictus.tagcorral.ui.viewer.displaypanels;

import java.awt.Font;

import javax.swing.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.png.*;

public class PNGTimeDisplay extends JPanel {

	public PNGTimeDisplay(TIMEChunk chunk) {
		TCLabel fullTime = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_FULLTIME") + " " + chunk.getYear() + "." + make2Digit("" + chunk.getMonth()) + "." + make2Digit("" + chunk.getDay()) +
				"  " + make2Digit("" + chunk.getHour()) + ":" + make2Digit("" + chunk.getMinute()) + ":" + make2Digit("" + chunk.getSecond()), 14, Font.PLAIN);
		
		TCLabel year = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_YEAR") + " " + chunk.getYear(), 14, Font.PLAIN);
		TCLabel month = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_MONTH") + " " + chunk.getMonth(), 14, Font.PLAIN);
		TCLabel day = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_DAY") + " " + chunk.getDay(), 14, Font.PLAIN);
		TCLabel hour = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_HOUR") + " " + chunk.getHour(), 14, Font.PLAIN);
		TCLabel minute = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_MINUTE") + " " + chunk.getMinute(), 14, Font.PLAIN);
		TCLabel second = new TCLabel(TCStrings.getStringFor("GCD_PNGTIME_SECOND") + " " + chunk.getSecond(), 14, Font.PLAIN);
		
		BoxLayout f1 = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(f1);
		if (chunk.chunkIsValid()) {
			this.add(fullTime);
		} else {
			this.add(year);
			this.add(month);
			this.add(day);
			this.add(hour);
			this.add(minute);
			this.add(second);
		}
	}
	
	private String make2Digit(String input) {
		if (input.length() < 2) {
			input = "0" + input;
		}
		return input;
	}
	
}
