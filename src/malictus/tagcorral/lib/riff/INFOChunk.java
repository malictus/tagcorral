package malictus.tagcorral.lib.riff;

import java.io.*;
import java.util.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * Represents a standard RIFF INFO chunk.
 */
public class INFOChunk extends MetaChunk {
	
	private RIFFChunk parentChunk;
	private String chunkData = "";
	private String chunkName = "";
	
	public static Vector<String> STANDARD_INFO_TAGS = new Vector<String>();
	static {
		STANDARD_INFO_TAGS.add("INAM"); 	//Name, Title
		STANDARD_INFO_TAGS.add("IART"); 	//Artist, Director
		STANDARD_INFO_TAGS.add("ICOP"); 	//Copyright
		STANDARD_INFO_TAGS.add("IPRD"); 	//Product
		STANDARD_INFO_TAGS.add("ICRD"); 	//Creation Date
		STANDARD_INFO_TAGS.add("IGNR"); 	//Genre
		STANDARD_INFO_TAGS.add("ISBJ"); 	//Subject
		STANDARD_INFO_TAGS.add("IKEY"); 	//Keywords
		STANDARD_INFO_TAGS.add("ICMT"); 	//Comments
		STANDARD_INFO_TAGS.add("ISFT"); 	//Software
		STANDARD_INFO_TAGS.add("ITCH"); 	//Technician, Encoded by
		STANDARD_INFO_TAGS.add("IENG"); 	//Engineer, Digitized by
		STANDARD_INFO_TAGS.add("IDIT"); 	//Digitizing Date
		STANDARD_INFO_TAGS.add("ISMP"); 	//SMPTE time code
		STANDARD_INFO_TAGS.add("ISRF"); 	//Source Form
		STANDARD_INFO_TAGS.add("IMED"); 	//Medium
		STANDARD_INFO_TAGS.add("ISRC"); 	//Source
		STANDARD_INFO_TAGS.add("IARL"); 	//Archival Location
		STANDARD_INFO_TAGS.add("ICMS"); 	//Commissioned by
		STANDARD_INFO_TAGS.add("ICRP"); 	//Cropped
		STANDARD_INFO_TAGS.add("ISHP"); 	//Sharpness
		STANDARD_INFO_TAGS.add("IDIM"); 	//Dimensions
		STANDARD_INFO_TAGS.add("ILGT"); 	//Lightness
		STANDARD_INFO_TAGS.add("IDPI"); 	//Dots Per Inch
		STANDARD_INFO_TAGS.add("IPLT"); 	//Palette Setting
	}
	
	public static Vector<String> EXTENDED_INFO_TAGS = new Vector<String>();
	static {
		EXTENDED_INFO_TAGS.add("ISGN");		//Secondary Genre
		EXTENDED_INFO_TAGS.add("IWRI");		//Written by
		EXTENDED_INFO_TAGS.add("IPRO");		//Produced by
		EXTENDED_INFO_TAGS.add("ICNM");		//Cinematographer
		EXTENDED_INFO_TAGS.add("IPDS");		//Production Designer
		EXTENDED_INFO_TAGS.add("IEDT");		//Edited by
		EXTENDED_INFO_TAGS.add("ICDS");		//Costume Designer
		EXTENDED_INFO_TAGS.add("IMUS");		//Music by
		EXTENDED_INFO_TAGS.add("ISTD");		//Production Studio
		EXTENDED_INFO_TAGS.add("IDST");		//Distributed by
		EXTENDED_INFO_TAGS.add("ICNT");		//Country
		EXTENDED_INFO_TAGS.add("ILNG");		//Language
		EXTENDED_INFO_TAGS.add("IRTD");		//Rating
		EXTENDED_INFO_TAGS.add("ISTR");		//Starring
		EXTENDED_INFO_TAGS.add("IWEB");		//Internet Address
		EXTENDED_INFO_TAGS.add("IPRT");		//Part
		EXTENDED_INFO_TAGS.add("IFRM");		//Total Number of Parts
		EXTENDED_INFO_TAGS.add("IAS1");		//First language
		EXTENDED_INFO_TAGS.add("IAS2");		//Second language
		EXTENDED_INFO_TAGS.add("IAS3");		//Third language
		EXTENDED_INFO_TAGS.add("IAS4");		//Fourth language
		EXTENDED_INFO_TAGS.add("IAS5");		//Fifth language
		EXTENDED_INFO_TAGS.add("IAS6");		//Sixth language
		EXTENDED_INFO_TAGS.add("IAS7");		//Seventh language
		EXTENDED_INFO_TAGS.add("IAS8");		//Eighth language
		EXTENDED_INFO_TAGS.add("IAS9");		//Ninth language
		EXTENDED_INFO_TAGS.add("ICAS");		//Default audio stream
		EXTENDED_INFO_TAGS.add("IBSU");		//Base URL
		EXTENDED_INFO_TAGS.add("ILGU");		//Logo URL
		EXTENDED_INFO_TAGS.add("ILIU");		//Logo Icon URL
		EXTENDED_INFO_TAGS.add("IWMU");		//Watermark URL
		EXTENDED_INFO_TAGS.add("IMIU");		//More Info URL
		EXTENDED_INFO_TAGS.add("IMBI");		//More Info Banner Image
		EXTENDED_INFO_TAGS.add("IMBU");		//More Info Banner URL
		EXTENDED_INFO_TAGS.add("IMIT");		//More Info Text
	}
	
	/**
	 * INFOChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @param parentChunk the parent RIFF chunk to this one
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public INFOChunk(TCFile parentFile, long startByte, long length, TCRaf raf, RIFFChunk parentChunk) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		parseChunk(raf);
	}
	
	/**
	 * @return the parent to this chunk
	 */
	public RIFFChunk getParent() {
		return parentChunk;
	}
	
	/**
	 * @return the string data for this chunk (minus the null terminator)
	 */
	public String getChunkData() {
		return chunkData;
	}
	
	/**
	 * @return the four-character chunk name
	 */
	public String getChunkName() {
		return chunkName;
	}
	
	/**
	 * @return true if the INFO tag name is from the extended (nonstandard) INFO tag set)
	 */
	public boolean isExtended() {
		return EXTENDED_INFO_TAGS.contains(chunkName);
	}
	
	/**
	 * Does the actual business of parsing the chunk.
	 * @param raf pointer to the file (position not important)
	 * @throws IOException if file can't be parsed
	 */
	private void parseChunk(TCRaf raf) throws IOException {
		raf.seek(this.getStartByte());
		try {
			chunkName = raf.readFourCC();
		} catch (Exception err) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_CHUNK_NAME_NOT_ASCII);
			return;
		}
		raf.skipBytes(4);
		long pos = raf.getFilePointer();
		try {
			//first, try to read as ascii
			chunkData = raf.readNullTerminatedString("ASCII", this.getStartByte() + this.getLength(), true);
		} catch (Exception err) {
			raf.seek(pos);
			//now try UTF-8
			try {
				chunkData = raf.readNullTerminatedString("UTF-8", this.getStartByte() + this.getLength(), true);
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_INFO_CHUNK_NOT_ASCII);
			} catch (Exception err3) {
				//see if null char missing
				try {
					raf.seek(pos);				
					chunkData = raf.readNullTerminatedString("UTF-8", this.getStartByte() + this.getLength(), false);
					this.addInvalidReason(InvalidReasons.INVALID_CHUNK_NO_NULL_TERMINATOR);
				} catch (Exception err2) {
					err2.printStackTrace();
					this.addInvalidReason(InvalidReasons.INVALID_CHUNK_INVALID);
					return;
				}
			}
		}
	}
	
}
