
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

import static qemujuicy.Message.*;

import javax.swing.*;

import qemujuicy.*;
import qemujuicy.ui.*;

/**
 * Enumeration and handling of devices for a VM.
 */
public enum Device {
	
    HDA(VMProperties.DRIVE_HDA_NAME, "Drive hda"),
    HDB(VMProperties.DRIVE_HDB_NAME, "Drive hdb"),
    HDD(VMProperties.DRIVE_HDD_NAME, "Drive hdd"),
    CD_DVD(VMProperties.DRIVE_CD_DVD_NAME, "CD/DVD"),
    FLOPPY_A(VMProperties.FLOPPY_A_NAME, "Floppy A"),
    FLOPPY_B(VMProperties.FLOPPY_B_NAME, "Floppy B"),
    ;
	
	private String propertyName;
	private String displayName;

	/**
	 * Construction, the path will be set later.
	 */
	Device(String propertyName, String displayName) { 
		
		this.propertyName = propertyName;
		this.displayName = displayName;
	}

	/**
	 * Adds a CD/DVD device or *.iso file to the devices of this VM.
	 * 
	 * @param mainView
	 * @param vmList
	 * @param deviceList
	 */
	public static void addCD(MainView mainView, JList<VM> vmList, JList<VM.VMDevice> deviceList) {
		
		int selectedIndex = vmList.getSelectedIndex();
		if (selectedIndex < 0) {
			return;
		}
		VM vm = Main.getVm(selectedIndex);
		Device newDevice = null;
		if (CD_DVD.isEmpty(vm)) {
			// no CD defined
			newDevice = CD_DVD;
		} else {
			// CD already defined, choose an empty disk drive
			for (Device dev : new Device[] {HDA, HDB, HDD}) {
				if (dev.isEmpty(vm)) {
					newDevice = dev;
					break;
				}
			}
		}
		if (newDevice == null) {
			// no empty device slot available
			Logger.error("Device.addCD(): no empty device slot available");
			Gui.errorDlg(mainView, 
					Msg.get(DEVICE_SLOT_NOT_AVAILABLE), Msg.get(ERROR_TITLE_DLG_MSG));
			return;
		}
		
		
		path erfragen
		
		enabler für die device tab buttons
		
		
//		vm.addDevice();
		
		DefaultListModel<VM.VMDevice> deviceListModel = Main.getVmManager().getDeviceListModel();
		deviceListModel.clear();
		deviceListModel.addAll(vm.getDeviceList());
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		
		return displayName;
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
	
	/**
	 * @return true, if the device is not set in the VM, false otherwise
	 */
	public boolean isEmpty(VM vm) {
		
		String name = vm.getProperty(getDisplayName());
		return name == null || name.trim().equals("");
	}
}


