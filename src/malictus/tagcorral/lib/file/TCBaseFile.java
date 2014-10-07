package malictus.tagcorral.lib.file;

import java.io.*;
import java.security.MessageDigest;
import java.util.zip.CRC32;

/**
 * TCBaseFile is the base for all Tag Corral File types. It adds additional functionality to the 
 * basic File class.
 */
public class TCBaseFile extends File {
	
	/**
	 * The progress variable is used by various time-consuming file read/write processes. User interface components can read this variable to find out how long
	 * a the task will take to complete. The variable value will range from 0 (just started) to 100 (completed).
	 */
	public static int progress_var = 0;
	private static final int BUFFER_SIZE = 1024;
	public static final String CHECKSUM_TYPE_MD5 = "MD5";
	public static final String CHECKSUM_TYPE_SHA1 = "SHA-1";
	
	public TCBaseFile(String pathname) throws IOException {
		super(pathname);
		if ( (!this.exists()) || (!this.canRead()) || (!this.isFile()) ) {
			throw new IOException("TCBaseFile must be an existing file.");
		}
	}
	
	public TCBaseFile(File file) throws IOException {
		super(file.getPath());
		if ( (!this.exists()) || (!this.canRead()) || (!this.isFile()) ) {
			throw new IOException("TCBaseFile must be an existing file.");
		}
	}

	/**
	 * Retrieve a CRC checksum value for the specified portion of the file.
	 * Used for PNG files.
	 *
	 * @param start the start byte position
	 * @param end the end byte position
	 * @throws IOException if read error occurs
	 * @return the CRC value as a long
	 */
	public long getCRCFor(long start, long end) throws IOException {
		RandomAccessFile raf = null;
		try {
	    	raf = new RandomAccessFile(this, "r");
			byte[] buffer = new byte[BUFFER_SIZE];
			CRC32 crc = new CRC32();
	    	raf.seek(start);
	    	int numRead;
		    do {
		    	//update progress counter
	    		TCBaseFile.progress_var = 100 - (int)(((float)(end - raf.getFilePointer()) / (float)(end - start)) * 100);
		    	if (raf.getFilePointer() >= (end - BUFFER_SIZE)) {
		    		buffer = new byte[(int)(end - raf.getFilePointer())];
		    	}
		    	numRead = raf.read(buffer);
		    	if (numRead > 0) {
		    		crc.update(buffer);
		        }
		    } while (numRead > 0);
		    raf.close();
		    return crc.getValue();
	    } catch (IOException err) {
	    	if (raf != null) {
	    		try {
	    			raf.close();
	    		} catch (Exception foo) {}
	    	}
	    	throw err;
	    }
	}
	
	/**
	 * Retrieve a CRC checksum value for an entire file.
	 * 
	 * @return the checksum value as a long
	 * @throws IOException
	 */
	public long getCRC() throws IOException {
		return getCRCFor(0, this.length());
	}
	
	/**
	 * Get a checksum for a portion of this file.
	 * 
	 * @param start starting byte position
	 * @param end ending byte position
	 * @param checksumtype one of the CHECKSUM_TYPE values
	 * @return the checksum as a string (currently uses lower case)
	 * @throws IOException if read/write error occurs
	 */
    public String getChecksum(long start, long end, String checksumtype) throws IOException {
    	if ( (!checksumtype.equals(CHECKSUM_TYPE_MD5)) && (!checksumtype.equals(CHECKSUM_TYPE_SHA1))) {
    		throw new IOException("Incorrect checksum type");
    	}
		RandomAccessFile raf = new RandomAccessFile(this, "r");
		byte[] buffer = new byte[BUFFER_SIZE];
	    MessageDigest complete = null;
	    try {
	    	complete = MessageDigest.getInstance(checksumtype);
	    } catch (Exception err) {
	    	raf.close();
	    	throw new IOException("Incorrect checksum type");
	    }
	    try {
	    	raf.seek(start);
	    	int numRead;
		    do {
		    	//update progress counter
	    		TCBaseFile.progress_var = 100 - (int)(((float)(end - raf.getFilePointer()) / (float)(end - start)) * 100);
		    	if (raf.getFilePointer() >= (end - BUFFER_SIZE)) {
		    		buffer = new byte[(int)(end - raf.getFilePointer())];
		    	}
		    	numRead = raf.read(buffer);
		    	if (numRead > 0) {
		    		complete.update(buffer, 0, numRead);
		        }
		    } while (numRead > 0);
		    raf.close();
		    byte[] inn = complete.digest();
		    byte ch = 0x00;
		    int i = 0;
		    String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		    StringBuffer out = new StringBuffer(inn.length * 2);
		    while (i < inn.length) {
		        ch = (byte) (inn[i] & 0xF0);
		        ch = (byte) (ch >>> 4);
		        ch = (byte) (ch & 0x0F);
		        out.append(pseudo[ (int) ch]);
		        ch = (byte) (inn[i] & 0x0F);
		        out.append(pseudo[ (int) ch]);
		        i++;
		    }
		    return new String(out);
	    } catch (IOException err) {
	    	raf.close();
	    	throw err;
	    }
	}
    
    /**
     * Return a checksum for the entire file.
     * 
     * @param checksumtype one of the CHECKSUM_TYPE values
     * @return the MD5 checksum as a string
     * @throws IOException
     */
    public String getChecksum(String checksumtype) throws IOException {
    	return getChecksum(0, this.length(), checksumtype);
    }
    
    /**
	 * Copy data from this file to a destination file. Any existing data in the destination file will be deleted first.
	 * 
	 * @param dest the destination file. If this file doesn't exist, it will be created first.
	 * @param sourceStart start position in the source file for the data to be copied
	 * @param sourceEnd end position in the source file for the data to be copied
	 * @throws IOException if read/write errors occur
	 */
	public void copyToFile(File dest, long sourceStart, long sourceEnd) throws IOException {
		if (dest.getPath().equals(this.getPath())) {
			throw new IOException("Source and destination cannot be identical");
		}
		if (dest.exists()) {
			dest.delete();
    	}
		dest.createNewFile();
		RandomAccessFile fin = new RandomAccessFile(this, "r");
		RandomAccessFile fos = new RandomAccessFile(dest, "rw");
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
	        fin.seek(sourceStart);
	        fos.seek(0);
	        while (fin.getFilePointer() < sourceEnd) {
	            progress_var = 100 - (int)(((float)(sourceEnd - fin.getFilePointer()) / (float)(sourceEnd - sourceStart)) * 100);
	        	int len = fin.read(buffer);
	        	if ((fin.getFilePointer()) <= sourceEnd) {
	        		fos.write(buffer, 0, len);
	        	} else {
	        		fos.write(buffer, 0, ((int)((sourceEnd - sourceStart) % BUFFER_SIZE)));
	        	}
	        }
	        fin.close();
	        fos.close();
		} catch (IOException err) {
			fin.close();
			fos.close();
			throw err;
		}
	}
	
	/**
	 * Copy the entire file to a destination file.
	 *
	 * @param destinationFile the destination file; if file already exists, it will be overwritten
	 * @throws IOException if read/write error occurs
	 */
    public void copyToFile(File destinationFile) throws IOException {
    	copyToFile(destinationFile, 0, destinationFile.length());
	}
    
    /**
     * Insert a source file (in its entirety) into this file.
     * 
     * @param source the source file
     * @param start the start byte position in this file; all data after this point in the file will be moved forward
     * @throws IOException if read/write error occurs
     */
    public void insertIntoFile(File source, long start) throws IOException {
		RandomAccessFile rafDest = new RandomAccessFile(this, "rw");
		RandomAccessFile rafSource = new RandomAccessFile(source, "r");
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			long oldend = rafDest.length();
			long moveAmt = source.length();
			rafDest.setLength(rafDest.length() + moveAmt);
			rafDest.seek(oldend);
			long curpos;
			while ((rafDest.getFilePointer() - BUFFER_SIZE) >= start) {
				curpos = rafDest.getFilePointer();
				progress_var = 100 - (int)(((float)(curpos - start) / (float)(oldend - start)) * 100);
				rafDest.seek(curpos - BUFFER_SIZE);
				int x = rafDest.read(buf);
				if (x != buf.length) {
					throw new IOException("File read error");
				}
				rafDest.seek(curpos - BUFFER_SIZE + moveAmt);
				rafDest.write(buf);
				rafDest.seek(curpos - BUFFER_SIZE);
			}
			if (rafDest.getFilePointer() != start) {
				buf = new byte[(int)(rafDest.getFilePointer() - start)];
				rafDest.seek(start);
				int x = rafDest.read(buf);
				if (x != (rafDest.getFilePointer() - start)) {
					throw new IOException("File read error");
				}
				rafDest.seek(start + moveAmt);
				rafDest.write(buf);
			}
			buf = new byte[BUFFER_SIZE];
			rafSource.seek(0);
			rafDest.seek(start);
			while ((rafSource.getFilePointer() + BUFFER_SIZE) <= rafSource.length()) {
				progress_var = 100 - (int)(((float)(rafSource.length() - rafSource.getFilePointer()) / (float)(rafSource.length() - start)) * 100);
				int x = rafSource.read(buf);
				if (x != buf.length) {
					throw new IOException("File read error");
				}
				rafDest.write(buf);
			}
			if (rafSource.getFilePointer() < rafSource.length()) {
				buf = new byte[(int)(rafSource.length() - rafSource.getFilePointer())];
				int x = rafSource.read(buf);
				if (x != buf.length) {
					throw new IOException("File read error");
				}
				rafDest.write(buf);
			}
			rafDest.close();
			rafSource.close();
		} catch (IOException err) {
			rafDest.close();
			rafSource.close();
			throw err;
		} 
    }
    
    /**
     * Insert a number of blank bytes (value 0) into this file.
     * 
     * @param position the byte position to begin inserting bytes. All data after this point will be moved forward
     * @param numbytes the number of bytes to add
     * @throws IOException if read/write error occurs
     */
    public void insertBlankBytes(long position, long numbytes) throws IOException {
		//we'll accomplish this by first creating a temp file of numbytes length, then inserting it
    	File tmp = null;
    	RandomAccessFile raf = null;
    	try {
    		tmp = File.createTempFile("tagcorral", ".dat");
    		raf = new RandomAccessFile(tmp, "rw");
    		//will insert 'undefined' bytes, but these should just be empty bytes
    		raf.setLength(numbytes);
    		raf.close();
    		this.insertIntoFile(tmp, position);
    		if (!tmp.delete()) {
    			throw new IOException("Temp file not deleted");
    		}
    	} catch (IOException err) {
			if (tmp != null) {
				tmp.delete();
			}
			if (raf != null) {
				raf.close();
			}
			throw err;
		}
    }
    
    /**
     * Insert a byte array (in its entirety) into this file.
     * 
     * @param bytes the byte array
     * @param start the starting byte position in this file; all data after this point in the file will be moved forward
     * @throws IOException if read/write error occurs
     */
    public void insertIntoFile(byte[] bytes, long start) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(this, "rw");
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			long oldend = raf.length();
			raf.setLength(raf.length() + bytes.length);
			raf.seek(oldend);
			long curpos;
			while ((raf.getFilePointer() - BUFFER_SIZE) >= start) {
				curpos = raf.getFilePointer();
				progress_var = 100 - (int)(((float)(curpos - start) / (float)(oldend - start)) * 100);
				raf.seek(curpos - BUFFER_SIZE);
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new IOException("File read error");
				}
				raf.seek(curpos - BUFFER_SIZE + bytes.length);
				raf.write(buf);
				raf.seek(curpos - BUFFER_SIZE);
			}
			if (raf.getFilePointer() != start) {
				buf = new byte[(int)(raf.getFilePointer() - start)];
				raf.seek(start);
				int x = raf.read(buf);
				if (x != (raf.getFilePointer() - start)) {
					throw new IOException("Error reading file");
				}
				raf.seek(start + bytes.length);
				raf.write(buf);
			}
			raf.seek(start);
			raf.write(bytes, 0, bytes.length);
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
    }
    
    /**
     * Append a file (in its entirety) to the end of this file
     * 
     * @param source the source file
     * @throws IOException if read/write error occurs
     */
    public void append(File source) throws IOException {
    	insertIntoFile(source, this.length());
    }
    
    /**
     * Append a byte array (in its entirety) to the end of this file
     * 
     * @param bytes the byte array to append
     * @throws IOException if read/write error occurs
     */
    public void appendToFile(byte[] bytes) throws IOException {
    	insertIntoFile(bytes, this.length());
    }
    
    /**
     * Delete a portion of this file.
     * 
     * @param startpos the start byte position to delete from
     * @param endpos the end byte position to delete from
     * @throws IOException if read/write error occurs
     */
    public void deleteFromFile(long startpos, long endpos) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(this, "rw");
		try {
			if (endpos >= this.length()) {
				raf.setLength(startpos);
				raf.close();
				return;
			}
			raf.seek(endpos);
			long curpos = raf.getFilePointer();
			byte[] buf = new byte[BUFFER_SIZE];
			while ((curpos + BUFFER_SIZE) < raf.length()) {
				progress_var = 100 - (int)(((float)(endpos - curpos) / (float)(endpos - startpos)) * 100);
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new IOException("Error reading file");
				}
				raf.seek(curpos - (endpos - startpos));
				raf.write(buf);
				raf.seek(curpos + BUFFER_SIZE);
				curpos = raf.getFilePointer();
			}
			if (raf.length() != curpos) {
				buf = new byte[(int)(raf.length() - curpos)];
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new IOException("Error reading file");
				}
				raf.seek(curpos - (endpos - startpos));
				raf.write(buf);
			}
			long newEnd = raf.getFilePointer();
			raf.setLength(newEnd);
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
    }

}
