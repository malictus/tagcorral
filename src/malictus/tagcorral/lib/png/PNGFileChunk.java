package malictus.tagcorral.lib.png;

import java.util.*;
import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * A chunk that represents an entire PNG file (or an entire PNG datastream inside a file).
 */
public class PNGFileChunk extends Chunk {
	
	private Vector<MetaChunk> metadataChunks = new Vector<MetaChunk>();

	/**
	 * PNGFileChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes. For GIF files, this is simply the file length.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public PNGFileChunk(TCFile parentFile, long startByte, long length, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		parseChunk(raf);
	}
	
	public Vector<MetaChunk> getMetaChunks() {
		return metadataChunks;
	}
	
	/**
	 * Does the actual business of parsing the PNG file chunk.
	 * @param raf pointer to the file (position not important)
	 * @throws IOException if file is not a PNG file, or can't be parsed
	 */
	private void parseChunk(TCRaf raf) throws IOException {
		//confirm magic number
		byte[] buf = new byte[8];
		raf.seek(this.getStartByte());
		raf.read(buf);
		//PNG magic number
		boolean isPNG = false;
		if ( (buf[0] == -119) && (buf[1] == 80) && (buf[2] == 78) && (buf[3] == 71) &&
				(buf[4] == 13) && (buf[5] == 10) && (buf[6] == 26) && (buf[7] == 10)) {
			isPNG = true;
		}
		if (!isPNG) {
			throw new IOException("Chunk is not a PNG file; magic number incorrect");
		}
		this.metadataChunks = new Vector<MetaChunk>();
		int counter = 0;
		while (true) {
			if ((raf.getFilePointer() + 8) > this.getStartByte() + this.getLength()) {
				this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
				break;
			}
			try {
				long datasize = raf.read4ByteInt(false, true);
				String fourCC = raf.readFourCC();
				if (fourCC.equals("IEND")) {
					if (raf.getFilePointer() == (this.getLength() + this.getStartByte() - 4)) {
						break;
					} else {
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_EXTRA_DATA);
						break;
					}
				} else if (fourCC.equals("tIME")) {
					//create time chunk
					TIMEChunk newTime = new TIMEChunk(this.getParentFile(), raf.getFilePointer() - 8, datasize + 12, this, raf);
					raf.seek(newTime.getStartByte() + newTime.getLength());
					if (counter == 0) {
						newTime.addInvalidReason(InvalidReasons.INVALID_CHUNK_BEFORE_HEADER);
					}
					metadataChunks.add(newTime);
				} else if (fourCC.equals("tEXt") || fourCC.equals("zTXt") || fourCC.equals("iTXt")) {
					//create text chunk
					TextChunk newText = new TextChunk(this.getParentFile(), raf.getFilePointer() - 8, datasize + 12, this, raf);
					raf.seek(newText.getStartByte() + newText.getLength());
					if (counter == 0) {
						newText.addInvalidReason(InvalidReasons.INVALID_CHUNK_BEFORE_HEADER);
					}
					metadataChunks.add(newText);
				} else {
					raf.seek(datasize + 4 + raf.getFilePointer());
				}
				counter = counter + 1;
			} catch (IOException err) {
				err.printStackTrace();
				this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_READ_ERROR);
				break;
			}
		}
		//check for multiple time chunks
		counter = 0;
		int timeChunks = 0;
		while (counter < metadataChunks.size()) {
			MetaChunk x = metadataChunks.get(counter);
			if (x instanceof TIMEChunk) {
				timeChunks = timeChunks + 1;
			}
			counter = counter + 1;
		}
		if (timeChunks > 1) {
			counter = 0;
			while (counter < metadataChunks.size()) {
				MetaChunk x = metadataChunks.get(counter);
				if (x instanceof TIMEChunk) {
					((TIMEChunk)x).addInvalidReason(InvalidReasons.INVALID_CHUNK_SHOULD_BE_UNIQUE);
				}
				counter = counter + 1;
			}	
		}
	}
	
}
