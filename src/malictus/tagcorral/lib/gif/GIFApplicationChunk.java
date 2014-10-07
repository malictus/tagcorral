package malictus.tagcorral.lib.gif;

import java.io.IOException;
import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.lib.chunk.*;

/**
 * Chunk for an Application Extension chunk.
 */
public class GIFApplicationChunk extends Chunk {
	
	private GIFFileChunk parentChunk;
	private String identifier;
	private byte[] authCode;
	
	/**
	 * Application Extension chunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param parentChunk the GIFFileChunk that this chunk is a part of
	 * @param raf a pointer to the file, can be in any byte position
	 * @throws IOException if file is invalid, or if this chunk can't be parsed into a valid chunk
	 */
	public GIFApplicationChunk(TCFile parentFile, long startByte, long length, GIFFileChunk parentChunk, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		if (parentFile.length() < (startByte + length)) {
			throw new IOException("Chunk does not have enough bytes");
		}
		if (length < 15) {
			throw new IOException("Incorrect number of bytes");
		}
		raf.seek(startByte);
		if (raf.read1ByteInt(false) != 0x21) {
			throw new IOException("Incorrect extension introducer");
		}
		if (raf.read1ByteInt(false) != 0xff) {
			throw new IOException("Incorrect extension label");
		}
		if (raf.read1ByteInt(false) != 0xb) {
			throw new IOException("Incorrect block size");
		}
		byte[] idarray = new byte[8];
		raf.read(idarray);
		identifier = new String(idarray);
		authCode = new byte[3];
		raf.read(authCode);
	}
	
	/**
	 * Getter method for identifier
	 * 
	 * @return the identifier string for this block
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Getter method for authentication code
	 * 
	 * @return the authentication code for this block
	 */
	public byte[] getAuthenticationCode() {
		return authCode;
	}
	
	/**
	 * Getter method for parent chunk.
	 * 
	 * @return the parent PNGChunk
	 */
	public GIFFileChunk getParentChunk() {
		return parentChunk;
	}

}
