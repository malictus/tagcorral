package malictus.tagcorral.lib.gif;

import java.util.*;
import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * A chunk that represents an entire GIF file (or an entire GIF datastream inside a file).
 * Both 87a and 89a types are represented here.
 */
public class GIFFileChunk extends Chunk {
	
	private Vector<MetaChunk> metadataChunks = new Vector<MetaChunk>();
	private boolean is87a = false;

	/**
	 * GIFFileChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes. For GIF files, this is simply the file length.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public GIFFileChunk(TCFile parentFile, long startByte, long length, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		parseChunk(raf);
	}
	
	public Vector<MetaChunk> getMetaChunks() {
		return metadataChunks;
	}
	
	public boolean is87a() {
		return is87a;
	}
	
	/**
	 * Does the actual business of parsing the GIF file chunk.
	 * 
	 * @param raf pointer to the file (position not important)
	 * @throws IOException if file is not a GIF file, or can't be parsed
	 */
	private void parseChunk(TCRaf raf) throws IOException {
		metadataChunks = new Vector<MetaChunk>();
		//find if 87a or 89a
		raf.seek(this.getStartByte());
		byte[] start = new byte[6];
		raf.read(start);
		String magic = new String(start, "US-ASCII");
		if (magic.equals("GIF87a")) {
			is87a = true;
		} else if (magic.equals("GIF89a")) {
			is87a = false;
		} else {
			throw new IOException("Incorrect magic number");
		}
		//find gct flag
		raf.seek(this.getStartByte() + 10);
		int packedByte = raf.read1ByteInt(false);
		boolean gctFlag = (packedByte & 0x80) != 0;
		int gctSize = 2 << (packedByte & 7);
		raf.skipBytes(2);
		//now at end of header
		if (gctFlag) {
			//skip global color table
			raf.skipBytes(gctSize * 3);
		}		
		//keep reading chunks to end
		while (true) {
			if (raf.getFilePointer() >= (this.getStartByte() + this.getLength())) {
				//end of file, we're done but never hit trailer
				this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
				return;
			}
			long startOfChunk = raf.getFilePointer();
			int test = raf.read1ByteInt(false);
			if (test == 0x3b) {
				//trailer found; we're done!
				return;
			}
			if (test == 0x2c) {
				//image block found
				if ((raf.getFilePointer() + 10) <= (this.getStartByte() + this.getLength() - 1)) {
					raf.skipBytes(8);
					//look for local color table falg
					int packed = raf.read1ByteInt(false);
					int localColorTableFlagExists = (packed & 0x80) >> 7;
					int rawLocalColorTableSize = packed & 0x7;
					if (localColorTableFlagExists != 0) {
						raf.skipBytes(3 * (1 << (rawLocalColorTableSize + 1))); 
			        }
					raf.skipBytes(1);
					int amt = 1;
					while ((amt != 0) && (raf.getFilePointer() < (this.getStartByte() + this.getLength()))) {
						amt = raf.read1ByteInt(false);
						raf.seek(raf.getFilePointer() + amt);
					}
				} else {
					//end of file, we're done but never hit trailer
					this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
					return;
				}
			} else if (test == 0x21) {
				//extension block found
				int secondtest = raf.read1ByteInt(false);
				if (secondtest == 0xf9) {
					if ((startOfChunk + 8) <= (this.getStartByte() + this.getLength() - 1)) {
						//graphics extension
						raf.seek(startOfChunk + 8);
					} else {
						//end of file, we're done but never hit trailer
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
						return;
					}
				} else if (secondtest == 1) {
					//text extension
					if ((startOfChunk + 15) > (this.getStartByte() + this.getLength())) {
						//end of file, we're done but never hit trailer
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
						return;
					}
					//find size of chunk
					long length = 15;
					raf.skipBytes(13);
					int amt = 1;
					while ((amt != 0) && (raf.getFilePointer() < (this.getStartByte() + this.getLength() - 1))) {
						amt = raf.read1ByteInt(false);
						length = length + amt + 1;
						raf.seek(raf.getFilePointer() + amt);
					}
				} else if (secondtest == 0xff) {
					//application extension
					if ((startOfChunk + 14) > (this.getStartByte() + this.getLength())) {
						//end of file, we're done but never hit trailer
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
						return;
					}
					//see if this an XMP chunk, otherwise we'll just ignore it
					raf.skipBytes(1);
					byte[] idarray = new byte[8];
					raf.read(idarray);
					String identifier = new String(idarray);
					byte[] authCode = new byte[3];
					raf.read(authCode);
					String auth = new String(authCode);
					
					int amt = 1;
					long length = 14;
					while ((amt != 0) && (raf.getFilePointer() < (this.getStartByte() + this.getLength() - 1))) {
						amt = raf.read1ByteInt(false);
						length = length + amt + 1;
						raf.seek(raf.getFilePointer() + amt);
					}
					if (identifier.equals("XMP Data") && auth.equals("XMP")) {
						//GIFApplicationChunk appChunk = new GIFApplicationChunk(this.getParentFile(), startOfChunk, length, this, raf);
						//raf.seek(appChunk.getStartByte() + appChunk.getLength());
					}
				} else if (secondtest == 0xfe) {
					//comment extension 
					if ((startOfChunk + 2) > (this.getStartByte() + this.getLength())) {
						//end of file, we're done but never hit trailer
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
						return;
					}
					//find size of chunk
					long length = 2;
					int amt = 1;
					while ((amt != 0) && (raf.getFilePointer() < (this.getStartByte() + this.getLength() - 1))) {
						amt = raf.read1ByteInt(false);
						length = length + amt + 1;
						raf.seek(raf.getFilePointer() + amt);
					}
					//create comment chunk
					GIFComment newComment = new GIFComment(this.getParentFile(), startOfChunk, length, this, raf);
					raf.seek(newComment.getStartByte() + newComment.getLength());
					metadataChunks.add(newComment);
				} else {
					//unknown extension; help!
					this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_DATA);
					return;
				}
			} else {
				//unknown byte found; abort
				this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_DATA);
				return;
			}
		}
	}
	
}
