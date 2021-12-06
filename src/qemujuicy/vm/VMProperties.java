
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
@SuppressWarnings("serial")
public class VMProperties extends Properties {
	
	// property keys: do NOT forget to add a new property to checkDefaults() !!!
	
	public static final String ACCELERATOR = "accelerator"; 
	public static final String CPUS = "cpus"; 
	public static final String CREATION_TYPICAL = "creation.typical"; 
	public static final String DRIVE_CD_DVD_NAME = "drive.cd.name";
	public static final String DRIVE_HDA_NAME = "drive.hda.name";
	public static final String DRIVE_HDA_SIZE_GB = "drive.hda.size.GB";		// in GB
	public static final String DRIVE_HDB_NAME = "drive.hdb.name";
	public static final String DRIVE_HDD_NAME = "drive.hdd.name";
	public static final String EXTRA_PARAMETERS = "extra.parameters";
	public static final String FLOPPY_A_NAME = "floppy.a.name";
	public static final String FLOPPY_B_NAME = "floppy.b.name";
	public static final String FULL_QEMU_DEFINITION = "full.qemu.definition";
	public static final String FULL_QEMU_DEFINITION_CMD = "full.qemu.definition.command";
	public static final String ICON_PATH = "icon.path";
	public static final String INSTALLED_FROM_PATH = "installed.from.path";
	public static final String LOCALTIME = "localtime";				// use local time (instad of utc)
	public static final String NETWORK = "network"; 
	public static final String OS = "os"; 
	public static final String QEMU_BOOT_MENU = "qemu.boot.menu"; 	
	public static final String SOUND = "sound"; 
	public static final String VERBOSE = "verbose";					// verbose output on stdout
	public static final String VM_FILENAME = "vm.filename"; 		// the xml file containing this properties
	public static final String VM_MEMORY_MB = "vm.memory.MB"; 		// in MB
	public static final String VM_NAME = "vm.name"; 				// the visible name of the VM
	public static final String VM_NAME_SAFE = "vm.name.safe"; 		// the visible name of the VM, but spaces 
																	// within the name are replaced by underscores
	public static final String VM_QEMU = "vm.qemu"; 				// the emulator of the VM 
	
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
	@SuppressWarnings("resource")
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
		
		// not here set
		checkProperty(ACCELERATOR, "");
		checkProperty(CPUS, "");
		checkProperty(CREATION_TYPICAL, "");
		checkProperty(DRIVE_CD_DVD_NAME, "");
		checkProperty(DRIVE_HDA_NAME, "");
		checkProperty(DRIVE_HDA_SIZE_GB, "");
		checkProperty(DRIVE_HDB_NAME, "");
		checkProperty(DRIVE_HDD_NAME, "");
		checkProperty(EXTRA_PARAMETERS, "");
		checkProperty(FLOPPY_A_NAME, "");
		checkProperty(FLOPPY_B_NAME, "");
		checkProperty(FULL_QEMU_DEFINITION, "false");
		checkProperty(FULL_QEMU_DEFINITION_CMD, "");
		checkProperty(ICON_PATH, "");
		checkProperty(INSTALLED_FROM_PATH, "");
		checkProperty(LOCALTIME, "false");
		checkProperty(NETWORK, "");
		checkProperty(OS, "");
		checkProperty(QEMU_BOOT_MENU, "");
		checkProperty(SOUND, "");
		checkProperty(VERBOSE, VMProperties.VERBOSE);
		checkProperty(VM_FILENAME, "");
		checkProperty(VM_MEMORY_MB, "");
		checkProperty(VM_NAME, "");
		checkProperty(VM_NAME_SAFE, "");
		checkProperty(VM_QEMU, "");
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

	/**
	 * Format a test JTextArea string as a property.
	 * 
	 * @param text		the text to be formatted as a one line string
	 * @return the tex in one line
	 */
	public static String qemuTextToProperty(String text) {

		if (text.trim().equals("")) {
			return "";
		}
		Scanner scanner = new Scanner(text.trim());
		StringBuilder sb = new StringBuilder(scanner.next());
		while (scanner.hasNext()) {
			sb.append(" ");
			sb.append(scanner.next());
		}
		return sb.toString();
	}
}
