
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
 */package qemujuicy.vm;

import java.util.EnumSet;

import javax.swing.*;

import qemujuicy.*;

/**
 * Enumeration of devices for a VM.
 */
public enum Device {
	
    FLOPPY_A,
    FLOPPY_B,
    HDA,
    HDB,
    HDD,
    CD_DVD,
    ;
	
	private String path;

	/**
	 * Construction, the path will be set later.
	 */
	Device() { 
		
	}

	/**
	 * @return the image icon for this device type
	 */
	public Icon getImageIcon() {

		switch (this) {
		case FLOPPY_A: 
		case FLOPPY_B: 
			return Images.get(Images.FLOPPY);
		case HDA: 
		case HDB: 
		case HDD: 
			return Images.get(Images.DISK);
		case CD_DVD: 
			return Images.get(Images.CD_DVD);
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
	
	/**
	 * @return the path of this device
	 */
	public String getPath() {
		
		return path;
	}

	/**
	 * Sets the path for this device.
	 * 
	 * @param path 		
	 */
	public void setPath(String path) {
		
		this.path = path;
	}
}


