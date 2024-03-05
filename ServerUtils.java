

/**
 * ServerUtils Class
 * 
 * @author 	Majid Ghaderi
 * @version	2024
 *
 */


import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.text.*;


public class ServerUtils {
	
	private final static String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy hh:mm:ss zzz";
	
	/**
	 * Returns the current date of the system
	 * 
	 * @return String 	The current date as a string following HTTP format
	 * 
	 */
	public static String getCurrentDate() {
		return dateLongToString(System.currentTimeMillis());
	}

	
	/**
	 * Returns the content type of the file object
	 * 
	 * @param object The File object to be probed for its type
	 * @return String Type of the object
	 * 
	 */
	public static String getContentType(File object) throws IOException { 
		return Files.probeContentType(object.toPath());	
	}


	/**
	 * Returns the content length of the file object as a string
	 * 
	 * @param object The File object to be probed for its content length
	 * @return String Length of the object
	 * 
	 */
	public static String getContentLength(File object) {
		return String.format("%d", object.length());	
	}


	/**
	 * Returns the last modified date of the object
	 * 
	 * @param object The File object to be probed for its last modified date
	 * @return String Last modified date of the object
	 * 
	 */
	public static String getLastModified(File object) {
		return dateLongToString(object.lastModified());
	}

	
	// Coverts a date from long (in milli seconds) format to a string format
	private static String dateLongToString(long longDate) {
		SimpleDateFormat simple = new SimpleDateFormat(HTTP_DATE_FORMAT);
		return simple.format(new Date(longDate));
	}
	
}
