package malictus.tagcorral.lib.chunk;

import java.io.*;
import java.util.*;
import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.ui.TCStrings;

/**
 * A MetaChunk is any metadata-carrying chunk in a file.
 */
public abstract class MetaChunk extends Chunk {
	
	private Vector<String> invalidReasons = new Vector<String>();
	
	public MetaChunk(TCFile parentFile, long startByte, long length) throws IOException {
		super(parentFile, startByte, length);
	}
	
	public boolean chunkIsValid() {
		return invalidReasons.size() == 0;
	}
	
	public Vector<String> getInvalidReasons() {
		return invalidReasons;
	}
	
	public void addInvalidReason(String reason) {
		invalidReasons.add(reason);
	}
	
	public String getFullInfo() {
		return TCStrings.getStringFor("ERROR_GETTING_TAG_DATA");		
	}

}
