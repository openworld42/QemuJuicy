
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
import java.util.*;

/**
 * Application properties.
 * 
 * Note: Do not forget to handle additional properties if a new release is going to be rolled out.
 */
public class AppProperties extends Properties {

	private static final long serialVersionUID = 1L;		// for the compiler
	
	// property keys
	
	public static final String VERSION = "version";
	public static final String VERSION_MAJOR = "version.major";
	public static final String VERSION_MINOR = "version.minor";
	public static final String VERSION_RELEASE = "version.release";
	
	// !!! do NOT forget to add a new property to checkDefaults() !!!
	
	public static final String DEFAULT_CPUS = "cpus"; 
	public static final String DEFAULT_DISK_SIZE = "disk.size.GB"; 		// in GB
	public static final String DEFAULT_MEM = "memoryMB"; 				// in MB
	public static final String GIVE_HINTS = "give.hints"; 
	public static final String INSTALL_DIR = "install.dir"; 				// image directory from the last install of a VM
	public static final String LOOK_AND_FEEL = "lookandfeel"; 
	public static final String QEMU_CMD = "qemu.command."; 				// a number will be appended
	public static final String QEMU_IMG = "qemu.image";
	public static final String VERBOSE = "verbose"; 
	public static final String VM_DISK_PATH = "vm.disk.path"; 
	public static final String VM_FILENAME = "vm.filename.";  			// a number for the VM will be appended
	
	private String pathname;

	/**
	 * Construction from file or it takes the default values.
	 * The default values are written to the file (and may be overwritten later).
	 * 
	 * @param pathname			the path to the properties file
	 * @param useXmlFile		if true, use loadFromXML(), key/value pairs using load() otherwise
	 * @throws Exception
	 */
	public AppProperties(String pathname, boolean useXmlFile) throws Exception {

		super(); 
		this.pathname = pathname;
		File file = new File(pathname);
		if (file.exists()) {
			if (useXmlFile) {
				loadFromXML(new FileInputStream(file));
			} else {
				load(new FileInputStream(file));
			}
			checkDefaults();
		} else {
			createDefaults();
			if (useXmlFile) {
				storeToXML();
			}
		}
	}

	/**
	 * Check if all properties for this release are present, the user may use an older release.
	 */
	private void checkDefaults() {
		
		checkProperty(DEFAULT_CPUS, "2");
		checkProperty(DEFAULT_DISK_SIZE, "30");		// GB
		checkProperty(DEFAULT_MEM, "1000");
		checkProperty(GIVE_HINTS, "true");
		checkProperty(INSTALL_DIR, "");
		checkProperty(LOOK_AND_FEEL, "Nimbus");
//		checkProperty(QEMU_CMD, "");					// not set here
//		checkProperty(QEMU_IMG, "");					// not set here
		checkProperty(VERBOSE, "true");
		checkProperty(VM_DISK_PATH, "");				// path not set here
//		checkProperty(VM_FILENAME, "");					// not set here
		
	}

	/**
	 * Check if a property for this release is present, the user may use an older release.
	 */
	private void checkProperty(String key, String value) {
		
		if (getProperty(key) == null) {
			setProperty(key, value);
		}
	}

	/**
	 * Creates the default properties.
	 * A subclass may overwrite this method.
	 */
	protected void createDefaults() {
		
		setProperty(VERSION, Version.getAsString());
		setProperty(VERSION_MAJOR, Version.getMajor());
		setProperty(VERSION_MINOR, Version.getMinor());
		setProperty(VERSION_RELEASE, Version.getRelease());
		checkDefaults();			// add additional properties in checkDefaults()
	}

	/**
	 * @return the pathname
	 */
	public String getPathname() {
	
		return pathname;
	}

	/**
	 * Gets an boolean property (a flag).
	 * 
	 * @param key
	 * @return true id the value is "true", false otherwise
	 */
	public boolean getPropertyBool(String key) {
		
		return Boolean.parseBoolean(getProperty(key));
	}

	/**
	 * Gets an integer property.
	 * 
	 * @param key
	 * @return the integer value
	 */
	public int getPropertyInt(String key) {
		
		return Integer.parseInt(getProperty(key));
	}

	/**
	 * Sets an integer property.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, int value) {
		
		setProperty(key, "" + value);
	}

	/**
	 * Sets a property and update XML file by storing it, used
	 * as a convenience method.
	 * 
	 * @param key
	 * @param value
	 */
	public void setPropertyAndStoreXml(String key, String value) {
		
		setProperty(key, value);
		storeToXML();
	}

	/**
	 * Store the properties to the key/value properties file, to persist them.
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void store() throws FileNotFoundException, IOException {
		
		File file = new File(pathname);
		store(new FileOutputStream(file), "Version " + Version.getAsString());
	}
	
	/**
	 * Store the properties into a XML properties file, to persist them.
	 * The property keys are sorted before, due to the readability of the file.
	 */
	public void storeToXML() {
		
        try {
        	Util.storeToXML(this, "Version " + Version.getAsString(), pathname);
		} catch (Exception e) {
			Main.exitOnException(e);
		}
	}
}
