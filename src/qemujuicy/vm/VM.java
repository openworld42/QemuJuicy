
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

import java.util.*;

import javax.swing.*;

import qemujuicy.*;

/**
 * A virtual machine object.
 */
public class VM {

	public enum OSType {
		
		LINUX("Linux (generic)"),
		WINDOWS("Windows"),
		MAC("Mac"),
		OTHER("Other OS"),
		BSD("BSD (generic)"),
		DEBIAN("Debian"),
		UBUNTU("Ubuntu"),
		OPENSUSE("openSUSE"),
		RHEL("RHEL"),
		ARCH("Arch Linux"),
		MINIX("Minix"),
		SOLARIS("Solaris"),
		MINT("Linux Mint"),
		FEDORA("Fedora"),
		;

		private final String osName;
	   
	   private OSType(String osName)  {
		   this.osName = osName;
	   }
	   
	   private static String[] toArray() {
		   String[] osNames = new String[OSType.values().length];
		   for (int i = 0; i < osNames.length; i++) {
			   osNames[i] = (String) OSType.values()[i].osName;
		   }
		   return osNames;
	   }
	};
	
	// all available OS types (by name)
	public static final String[] OS_NAMES = OSType.toArray();

	private VMProperties vmProperties;
	private Process process;					// the process running this VM, if running
	private ArrayList<VMDevice> vmDeviceList;		// the devices of this VM

	private ImageIcon imageIcon;
	private boolean isRunning;					// indicates if the VM has been started

	/**
	 * Construction from properties (usually a new VM).
	 * 
	 * @param vmProperties 		the properties of the VM
	 */
	public VM(VMProperties vmProperties) {
		
		this.vmProperties = vmProperties;
		imageIcon = Images.find(vmProperties.getProperty(VMProperties.ICON_PATH));
		imageIcon = Images.scale(imageIcon, 32);
		vmDeviceList = new ArrayList<VMDevice>();
		for (Device device : Device.values()) {
			String path = vmProperties.getProperty(device.getPropertyName());
			if (path != null && !path.trim().equals("")) {
				VMDevice vmDev = new VMDevice(device);
				vmDeviceList.add(vmDev);
			}
		}
	}

	/**
	 * Construction from a properties file (usually an existing VM at startup).
	 * 
	 * @param vmPropertiesPath 			the file path of the properties of the VM
	 * @throws Exception 
	 */
	public VM(String vmPropertiesPath) throws Exception {
		
		this(new VMProperties(vmPropertiesPath, true));
	}

	/**
	 * @return the accelerator
	 */
	public String getAccelerator() {

		return  vmProperties.getProperty(VMProperties.ACCELERATOR);
	}

	/**
	 * @return the architecture (the QEMU emulator command)
	 */
	public String getArchitecture() {
		
		return  vmProperties.getProperty(VMProperties.VM_QEMU);
	}

	/**
	 * @return the (maximum) number of CPUs used by the VM (QEMU supports much more feature options) or 0
	 */
	public int getCpus() {

		try {
			return Integer.parseInt(vmProperties.getProperty(VMProperties.CPUS));
		} catch (NumberFormatException e) {
			// was NONE
			return 0;
		}
	}

	/**
	 * @return an ArrayList of devices for this VM
	 */
	public ArrayList<VMDevice> getDeviceList() {
		
		return vmDeviceList;
	}

	/**
	 * @return the name of the hda drive (the VM default boot image)
	 */
	public String getDriveHdaName() {
		
		return vmProperties.getProperty(VMProperties.DRIVE_HDA_NAME);
	}

	/**
	 * @return the (maximum size) of the VM image in GB
	 */
	public int getDriveHdaSizeGB() {

		return Integer.parseInt(vmProperties.getProperty(VMProperties.DRIVE_HDA_SIZE_GB));
	}

	/**
	 * @return the extra parameters formatted as a JTextArea string
	 */
	public String getExtraParamsTextAreaString() {
		
		String extraParams = vmProperties.getProperty(VMProperties.EXTRA_PARAMETERS);
		Scanner scanner = new Scanner(extraParams);
		StringBuilder sb = new StringBuilder("");
		// divide in lines now
		for (int i = 0; scanner.hasNext(); i++) {
			if (i != 0) {
				sb.append("\n");
			}
			sb.append(scanner.next());
			if (scanner.hasNext()) {
				String s = scanner.next();
				sb.append(s.startsWith("-") ? "\n" : " ");
				sb.append(s);
			}
		}
		scanner.close();
		return sb.toString();
	}

	/**
	 * @return the icon of the VM
	 */
	public ImageIcon getImageIcon() {
		
		return imageIcon;
	}

	/**
	 * @return the (maximum size) of the VM memory in MB
	 */
	public int getMemorySizeMB() {

		return Integer.parseInt(vmProperties.getProperty(VMProperties.VM_MEMORY_MB));
	}
	
	/**
	 * @return the name of the VM
	 */
	public String getName() {
		
		return vmProperties.getProperty(VMProperties.VM_NAME);
	}
	
	/**
	 * @return the safe name of the VM (spaces are replaced by underscores)
	 */
	public String getNameSafe() {
		
		return vmProperties.getProperty(VMProperties.VM_NAME_SAFE);
	}

	/**
	 * @return the path name where the properties of this VM are stored
	 */
	public String getPathname() {
		
		return vmProperties.getPathname();
	}
	
	/**
	 * @return the process if the VM is executing, null otherwise
	 */
	public Process getProcess() {
		
		return process;
	}
	
	/**
	 * @param key		the name/key of the property
	 * @return the value of the property with the key
	 */
	public String getProperty(String key) {
		
		return vmProperties.getProperty(key);
	}
	
	/**
	 * @param key		the name/key of the property
	 * @return the value of the property with the key
	 */
	public boolean getPropertyBool(String key) {
		
		return vmProperties.getPropertyBool(key);
	}
	
	/**
	 * @return the sound method/card of the VM
	 */
	public String getSound() {
		
		return vmProperties.getProperty(VMProperties.SOUND);
	}
	
	/**
	 * @return the properties of this VM
	 */
	public VMProperties getVmProperties() {
		
		return vmProperties;
	}

	/**
	 * @return true, if the VM is running, false otherwise
	 */
	public boolean isRunning() {

		return isRunning;
	}

	/**
	 * @return the xml file name where the properties of this VM are stored
	 */
	public String getVmFilename() {

		return  vmProperties.getProperty(VMProperties.VM_FILENAME);
	}

	/**
	 * Sets the flag if the VM is currently executing or not.
	 * 
	 * @param flag		true, if the VM is currently executing, false otherwise
	 */
	public void setIsRunning(boolean flag) {

		isRunning = flag;
	}

	/**
	 * Sets the process running this VM, or null (on exit).
	 * 
	 * @param process		the process running this VM, or null
	 */
	public void setProcess(Process process) {

		this.process = process;
	}

	/**
	 * Sets a property of this VM.
	 * 
	 * @param propertyKey
	 * @param value
	 */
	public void setProperty(String propertyKey, String value) {

		vmProperties.setProperty(propertyKey, value);
	}

	/**
     * Do System.out.println of a text string to console, if the verbose flag property is on.
     *
     * @param text the text string to be displayed
 	 */
	public void verbose(String text) {

//		if (Main.isVerbose() || vmProperties.getPropertyBool(VMProperties.VERBOSE)) {
		if (vmProperties.getPropertyBool(VMProperties.VERBOSE)) {
			System.out.println(text);
		}
	}
	
	/************************* inner classes *************************/
	
	/**
	 * A DocumentListener for a VM property.
	 */
	public class VMDevice {
		
		private Device device;
		private int driveIndex;
		
		public VMDevice(Device device) {

			this.device = device;
			this.driveIndex = device.ordinal();
		}
	}
}

