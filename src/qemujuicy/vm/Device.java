
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

import javax.swing.*;

import qemujuicy.*;

/**
 * Enumeration of devices for a VM.
 */
public enum Device {
	
    HDA(VMProperties.DRIVE_HDA_NAME),
    HDB(VMProperties.DRIVE_HDB_NAME),
    HDD(VMProperties.DRIVE_HDD_NAME),
    CD_DVD(VMProperties.DRIVE_CD_DVD_NAME),
    FLOPPY_A(VMProperties.FLOPPY_A_NAME),
    FLOPPY_B(VMProperties.FLOPPY_B_NAME),
    ;
	
	private String propertyName;

	/**
	 * Construction, the path will be set later.
	 */
	Device(String propertyName) { 
		
		this.propertyName = propertyName;
	}

	/**
	 * @return the image icon for this device type
	 */
	public Icon getImageIcon() {

		switch (this) {
		case HDA: 
		case HDB: 
		case HDD: 
			return Images.get(Images.DISK);
		case CD_DVD: 
			return Images.get(Images.CD_DVD);
		case FLOPPY_A: 
		case FLOPPY_B: 
			return Images.get(Images.FLOPPY);
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
	
	/**
	 * @return the name of the property of this device
	 */
	public String getPropertyName() {
		
		return propertyName;
	}
}


