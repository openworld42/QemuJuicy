
/**
	This file is part of QemuJuicy, a graphical user interface to run QEMU.
	
	Copyright (C) 2021 Heinz Silberbauer and contributors.

	This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
    
    See <http://www.gnu.org/licenses/>.
 */
package qemujuicy;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * Utility class, supporting the project with several static methods and constants.
 */
public class Util {
	
	public static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("0.00");
	public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("####0.00");
	
	/**
	 * Deny external construction.
	 */
	private Util() {
		
	}

	/**
	 * Appends a newline or the separator string before appending the text,
	 * depending on the index and nrOfcolumns.<br/>
	 * Use: to write lists and add newlines if lines get too long.
	 *
	 * @param sb			a StringBuilder to store the text items
	 * @param text			the text to append
	 * @param index			the index, usually taken from a for() loop
	 * @param nrOfcolumns	the number of columns of a line
	 * @param speparator	a string separating the columns
	 * @param minColLength	a text of a length less than minColLength
	 * 						will be extended to minColLength using the space character
	 */
	public static void appendColumns(StringBuilder sb, String text, int index,
			int nrOfcolumns, String speparator, int minColLength) {

		if (index % nrOfcolumns == 0) {
			if (index != 0) {
				sb.append("\n");
			}
		} else {
			sb.append(speparator);
		}
		sb.append(text);
		for (int i = text.length(); i <= minColLength; i++) {
			sb.append(" ");
		}
	}

	/**
	 * Creates a date and time stamp (without milliseconds) as a <code>String</code>.
	 * 
	 * @return the time stamp string
	 */
	public static String createDateAndTimeStamp() {
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return formatter.format(new Date());
	}

	/**
	 * Creates a string by repeating another string, which may be a single character.
	 * Example: createStringOf(50, "*");
	 * 
	 * @param count		the number of repeating
	 * @param s			the string to be repeated
	 * @return the repeated result string
	 */
	public static String createStringOf(int count, String s) {
	
		StringBuilder sb = new StringBuilder(count * s.length());
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Creates a time stamp (without milliseconds, without date) as a <code>String</code>.
	 * 
	 * @return the time stamp string
	 */
	public static String createTimeStamp() {
	
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(new Date());
	}

	/**
	 * Creates a <code>FilenameFilter</code> for files that start with a given prefix.
	 * 
	 * @param fileNamePrefix						the prefix a file has to start with
	 * @return the <code>FilenameFilter</code>
	 */
	public static FilenameFilter createFilenameFilterStartingWith(final String fileNamePrefix) {
		
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileNamePrefix);
			}
		};
	}

	/**
	 * Returns a String array for all files in a directory that start with a given prefix.
	 * 
	 * @param parentDirectory
	 * @param fileNamePrefix				the prefix a file has to start with
	 * @return the String array with the file names
	 */
	public static String[] findFilesStartingWith(File parentDirectory, final String fileNamePrefix) {
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileNamePrefix);
			}
		};
		return parentDirectory.list(filter);
	}

	/**
	 * Returns String of all integer elements of an List, separated by
	 * another (separation) String (e.g. a space).
	 * 
	 * @param list
	 * @param separator
	 * @return the resulting string
	 */
	public static String listToString(java.util.List<? extends Object> list, String separator) {

		if (list.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder("" + list.get(0));
		for (int i = 1; i < list.size(); i++) {
			sb.append(separator);
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	/**
	 * Writes key/values pairs of a Properties object to the log file, 
	 * with the keys sorted in alphabetical order.
	 * 
	 * @param properties	the properties to be logged
	 */
	public static void logProperties(String logText, Properties properties) {
		
    	ArrayList<String> keyList = toSortedKeyList(properties);
    	StringBuilder sb = new StringBuilder(logText);
    	for (String key : keyList) {
    		sb.append("\n\t");
       		sb.append(key);
       		sb.append(" -> ");
       		sb.append(properties.getProperty(key));
		}
    	Logger.info(sb.toString());
	}

	/**
     * Prompts for a text message in the <i>System.out</i> window, blocks until the user
     * types <i>RETURN/ENTER</i> and returns the string typed in.
     * This method intentionally encapsulates possible exceptions.
     *
     * @param text the text string to be displayed
     * @return a String containing the input
	 */
	public static String prompt(String text) {

		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream sysOut = new DataOutputStream(System.out);
        String s = null;
		try {
			sysOut.writeBytes(text);
			sysOut.flush();
            s = sysIn.readLine();
			// do not close System.out or System.in!
		}
		catch (Exception e) {
			System.out.println("\nError in H.prompt()!");
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Reads an input file into a String object.
	 *
	 * @param inputFileName
	 * @param charBufferSize				the size of a (very large) temporary input buffer
	 * @return a string containing the contents of the whole input file
	 * @throws IOException on errors reading the input file
	 */
	public static String readFileIntoString(String inputFileName, int charBufferSize) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(inputFileName));
		char[] inputCharArr = new char[charBufferSize];
		int size = in.read(inputCharArr, 0, charBufferSize);
		in.close();
		return new String(inputCharArr, 0, size);
	}
    
    /**
     * Reads the contents of an URL into a String object.<br/>
     * Note: this is a very simple wget.
     * 
     * @param urlString		the URL to read from
     * @return the contents of the URL
     * @throws Exception
     */
    public static String readFromUrl(String urlString) throws Exception {
        
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

	/**
	 * Renames a file to a backup file, if the file exists .
	 * If an old backup file exists already, it will be deleted before renaming the file.

	 * @param filename
	 * @param backupFilename
	 * @throws IOException
	 */
	public static void renameToBackupFile(String filename, String backupFilename) throws IOException {

		File file = new File(filename);
		File backupFile = new File(backupFilename);
		if (!file.exists()) {
			return;
		}
		if (!file.canWrite()) {
			throw new IOException("Cannot write to file " + filename);
		}
		if (backupFile.exists()) {
			if (!backupFile.delete()) {
				throw new IOException("Cannot delete file " + backupFilename);
			}
			backupFile.delete();
		}
		file.renameTo(backupFile);
	}
	
	/**
	 * Runs a process with parameters, returns its output.
	 * 
	 * @param cmd
	 * @param parameters
	 * @return the output of the process or null on exception
	 */
	public static String runProcess(String... cmdAndParameters) {
		
		String cmdOutput = null;
		ProcessExecutor procExec = null;
		String cmdString = "";
		try {
			for (String s : cmdAndParameters) {
				cmdString += s + " ";
			}
			Logger.info("executing: " + " " + cmdString);
			System.out.println("executing: " + " " + cmdString);
			procExec = new ProcessExecutor(cmdAndParameters);
			cmdOutput = procExec.getOutput();
			int exitValue = procExec.getExitValue();
			System.out.println("exitValue " + exitValue + " -> output:\n" + cmdOutput);
			Logger.info("exitValue " + exitValue + " -> output:\n" + cmdOutput);
			return cmdOutput;
		} catch (Exception e) {
			System.out.println("Exception executing " + cmdString);
			Logger.error("Exception executing: " + cmdString, e);
			return null;
		}
	}

	/**
	 * Convenience method for Thread.sleep(millis).<br/>
	 * 
	 * Note: the thread calling this method will sleep, a (unlikely) happening 
	 * InterruptedException is ignored, but the stack trace is printed.
	 *
	 * @param millis	the milliseconds to sleep
	 */
	public static void sleep(long millis) {

		try {
			Thread.sleep(millis);		// meanwhile be polite to the others ;-)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the stack trace of an Exception as a string.
	 *
	 * @param e		the Exception
	 * @return		the stack trace as String
	 */
	public static String stackTraceToString(Throwable e) {

		StringWriter writer = new StringWriter();
		if (e == null) {
			return "<!!! not even an Exception !!!>";
		}
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	/**
	 * Store the properties into a XML properties file, to persist them.
	 * The property keys are sorted before, due to the readability of the file.
	 * 
	 * @param properties		the properties to store
	 * @param comment			a comment within the XML properties file (not null)
	 * @param pathname			the path name of the XML properties file
	 * @throws FileNotFoundException 
	 */
	public static void storeToXML(Properties properties, String comment, 
			String pathname) throws FileNotFoundException {
		
    	ArrayList<String> keyList = toSortedKeyList(properties);
    	// write to file
    	PrintWriter writer = new PrintWriter(new File(pathname));
    	writer.println(
    			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    			+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
    			+ "<properties>");
    	writer.println("<comment>" + comment + "</comment>");
    	for (String key : keyList) {
        	writer.println("<entry key=\"" + key + "\">" + properties.getProperty(key) + "</entry>");				
		}
    	writer.println("</properties>");
    	writer.close();
	}

	/**
	 * Replaces parameter arguments of "$(N)" in a string and returns the result.
	 * Note: N has to be an integer and counts from <b>one</b> to N.
	 * <pre>
	 *
	 * Example:
	 *   String s = stringParam("I can feel $(2) disturbance in the $(1)", "force", 1);
	 *   results in "I can feel 1 disturbance in the force".
	 * </pre>
	 *
	 * @param string	the string (with 0..N parameters)
	 * @param args		a bunch of arguments (...), 0..N replacement objects
	 * @return the replaced result string
	 */
	public static String stringParam(String string, Object ... args) {

		ArrayList<String> stringList = new ArrayList<String>();
		Parser.tokenizeToArrayList(new StringTokenizer(string, " \t\n\r\f$()", true), stringList);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringList.size(); i++) {
			String s = stringList.get(i);
			if (s.charAt(0) == '$' && stringList.size() >= i + 3
					&& stringList.get(i + 1).equals("(")
					&& stringList.get(i + 3).equals(")")) {
				// a parameter found
				try {
					int paramNr = Integer.parseInt(stringList.get(i + 2));
					if (paramNr <= args.length) {
						s = args[paramNr - 1].toString();
						i += 3;
					}
				} catch (NumberFormatException e) {
					// intentionally do nothing
				}
			}
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Expands a String object to a given length, either by cutting the string or
	 * by extending it with spaces.<br/>
	 * Use: create output with fix-sized columns.
	 * 
	 * @param text
	 * @param length		length to expand or cut
	 * @return the resulting string
	 */
	public static String toLength(String text, int length) {

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < text.length() && i < length; i++) {
			sb.setCharAt(i, text.charAt(i));
		}
		for (int i = text.length(); i < length; i++) {
			sb.setCharAt(i, ' ');
		}
		return sb.toString();
	}

	/**
	 * Returns an ArrayList of the keys from a Properties object, sorted in alphabetical order.
	 * 
	 * @param properties	the properties to get the keys from
	 * @return an ArrayList of the keys
	 */
	public static ArrayList<String> toSortedKeyList(Properties properties) {
		
		Enumeration<Object> keysEnum = properties.keys();
    	ArrayList<String> keyList = new ArrayList<String>();
    	while (keysEnum.hasMoreElements()) {
    		keyList.add((String) keysEnum.nextElement());
    	}
    	Collections.sort(keyList);
		return keyList;
	}

    /**
     * Returns a string of % from a double value.
     * 
     * @param value
     * @return a string of %
     */
    public static String toStringPercent(double value) {
        
        double percent = (value - 1) * 100;
        return PERCENT_FORMAT.format(percent) + "%";
    }

	/**
	 * Writes the content of a String object to a file.
	 * 
	 * @param fileName
	 * @param content			the content of the file to write
	 * @throws IOException
	 */
	public void writeFile(String fileName, String content) throws IOException {

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
     * Do System.out.println of a text string to console, if the verbose flag property is on (default).
     *
     * @param text the text string to be displayed
 	 */
	public static void verbose(String text) {

		if (Main.isVerbose()) {
			System.out.println(text);
		}
	}
}
