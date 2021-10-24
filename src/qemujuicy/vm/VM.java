
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

/**
 * A virtual machine object.
 */
public class VM {

	public enum OSType {
		
		LINUX("Linux"),
		WINDOWS("Windows"),
		OTHER("Other OS"),
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
	
	// all available OS types (by name
	public static final String[] OS_NAMES = OSType.toArray();

	private VMProperties vmProperties;

	/**
	 * Construction.
	 * 
	 * @param vmProperties 		the properties of the VM
	 */
	public VM(VMProperties vmProperties) {
		
		this.vmProperties = vmProperties;
	}

	/**
	 * @return the name of the disk (the VM image)
	 */
	public String getDiskName() {
		
		return vmProperties.getProperty(VMProperties.DISK_NAME);
	}

	/**
	 * @return the (maximum size) of the VM image in GB
	 */
	public int getDiskSizeGB() {

		return Integer.parseInt(vmProperties.getProperty(VMProperties.DISK_SIZE_GB));
	}
	
	/**
	 * @return the name of the VM
	 */
	public String getName() {
		
		return vmProperties.getProperty(VMProperties.NAME);
	}
}
