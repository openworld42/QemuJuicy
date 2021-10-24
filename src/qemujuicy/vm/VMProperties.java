
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
package qemujuicy.vm;

import java.io.*;
import java.util.*;

import qemujuicy.*;

/**
 * Properties of a VM.
 * 
 * Note: Do not forget to handle additional properties if a new release is going to be rolled out.
 */
public class VMProperties extends Properties {

	private static final long serialVersionUID = 1L;		// for the compiler
	
	// property keys: do NOT forget to add a new property to checkDefaults() !!!
	
	public static final String CPUS = "cpus"; 
	public static final String CREATION_TYPICAL = "creation.typical"; 
	public static final String DISK_NAME = "disk.name";
	public static final String DISK_SIZE_GB = "disk.size.GB";			// in GB
	public static final String ICON_PATH = "icon.path";
	public static final String NAME = "vm.name"; 
	public static final String NETWORK = "network"; 
	public static final String OS = "os"; 
	public static final String VM_MEMORY_MB = "vm.memory.MB"; 			// in MB
	
	private String pathname;

	/**
	 * Construction from file or it takes the default values.
	 * Use storeToXML() to store the properties in a file.
	 * 
	 * @param pathname			the path to the properties file
	 * @param loadFromXmlFile	if true, use loadFromXML()to load the 
	 * 							properties, otherwise construct the properties with (almost empty) defaults
	 * @throws Exception
	 */
	public VMProperties(String pathname, boolean loadFromXmlFile) throws Exception {

		super(); 
		this.pathname = pathname;
		File file = new File(pathname);
		if (loadFromXmlFile) {
			if (file.exists()) {
				loadFromXML(new FileInputStream(file));
				checkDefaults();
			} else {
				Logger.error("Cannot log VM properties from file '" + pathname + "'");
				throw new Exception("Cannot log VM properties from file '" + pathname + "'");
			}
		} else {
			checkDefaults();
		}
	}

	/**
	 * Check if all properties for this release are present, the user may use an older release.
	 */
	private void checkDefaults() {
		
		// set not here
		checkProperty(CPUS, "");
		checkProperty(CREATION_TYPICAL, "");
		checkProperty(DISK_NAME, "");
		checkProperty(DISK_SIZE_GB, "");
		checkProperty(ICON_PATH, "");
		checkProperty(NAME, "");
		checkProperty(NETWORK, "");
		checkProperty(OS, "");
		checkProperty(VM_MEMORY_MB, "");
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
		
//		setProperty(VERSION, Version.getAsString());
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
