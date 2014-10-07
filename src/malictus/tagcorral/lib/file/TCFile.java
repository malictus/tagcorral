package malictus.tagcorral.lib.file;

import java.io.*;
import java.util.*;

import malictus.tagcorral.lib.chunk.*;

public abstract class TCFile extends TCBaseFile implements TCFileInterface {
	
	protected Vector<MetaChunk> metaChunks = new Vector<MetaChunk>();
	private Vector<String> invalidReasons = new Vector<String>();
	
	public TCFile(String pathname) throws IOException {
		super(pathname);
		parseMetadataChunks();
	}
	
	public Vector<MetaChunk> getMetadataChunks() {
		return metaChunks;
	}
	
	public void addMetadataChunk(MetaChunk meta) {
		metaChunks.add(meta);
	}
	
	public boolean fileIsValid() {
		return invalidReasons.size() == 0;
	}
	
	public Vector<String> getInvalidReasons() {
		return invalidReasons;
	}
	
	public void addInvalidReason(String reason) {
		invalidReasons.add(reason);
	}
	
}
