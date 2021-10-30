
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

import java.io.File;
import java.util.*;

import javax.swing.*;

import qemujuicy.*;
import qemujuicy.ui.Gui;

import static qemujuicy.Message.*;

/**
 * A virtual machine manager, containing all known VMs.
 * It runs all the actions for a VM, creates, deletes, starts and more.
 */
public class VMManager {

	private ArrayList<VM> vmList;				// contains all VM objects
	private JList<VM> vmJList;					// JList of VMs (mainView)
	private DefaultListModel<VM> listModel;		// data model for the JList of VMs

	/**
	 * Construction with no VMs.
	 */
	public VMManager() {
		
		vmList = new ArrayList<>(); 
	}

	/**
	 * Construction from the AppProperties VMs (if any).
	 * 
	 * @param properties
	 */
	public VMManager(AppProperties properties) {
		
		this();
		String vmDiskPath = properties.getProperty(AppProperties.VM_DISK_PATH);
		int vmNr = 0;
		for (;; vmNr++) {
			String vmFile = properties.getProperty(AppProperties.VM_FILENAME + vmNr);
			if (vmFile == null) {
				break;
			}
			String vmPath = vmDiskPath + File.separator + vmFile;
			try {
				VM vm = new VM(vmPath);
				vmList.add(vm);
				Logger.info("VMManager: creating VM #" + vmNr + ", file: '" + vmPath + "'");
			} catch (Exception e) {
				Logger.error("VMManager: error loading VM from '" + vmPath + "'", e);
				Gui.errorDlg(null, Msg.get(ERROR_LOADING_VM_DLG_MSG, vmPath), Msg.get(ERROR_TITLE_DLG_MSG));
			}
		}
	}

	/**
	 * Creates a Jlist data model, adding all VMs.
	 * 
	 * @param vmList 		the JList of VMs
	 * @return the model
	 */
	public ListModel<VM> createVmListModel(JList<VM> vmJList) {
		
		this.vmJList = vmJList;
		listModel = new DefaultListModel<VM>();
		for (VM vm : vmList) {
			listModel.addElement(vm);
		}
		return listModel;
	}

	/**
	 * Crates a VM defined by its properties.
	 * 
	 * @param vmProperties
	 */
	public void createVM(VMProperties vmProperties) {
		
		VM vm = new VM(vmProperties);
		Qemu qemuImg = new Qemu();
		qemuImg.createDiskImage(vm);
		AppProperties properties = Main.getProperties();
		int vmNr = 0;
		for (;; vmNr++) {
			String vmFile = properties.getProperty(AppProperties.VM_FILENAME + vmNr);
			if (vmFile == null) {
				break;
			}
		}
		// vmNr: next free VM
		String filename = vmProperties.getProperty(VMProperties.VM_FILENAME);
		Logger.info("creating VM #" + vmNr + ", file: '" + filename + "'");
		properties.setProperty(AppProperties.VM_FILENAME + vmNr, filename);
		vmProperties.storeToXML();
		properties.storeToXML();
		vmList.add(vm);
		listModel.addElement(vm);
		vmJList.setSelectedIndex(vmList.size() - 1);
		vmJList.ensureIndexIsVisible(vmList.size() - 1);
	}

	/**
	 * Test if a VM with the given name exists already.
	 * 
	 * @param vmName
	 * @return true, if a VM with that name exits, false otherwise
	 */
	public boolean exists(String vmName) {
		
		for (VM vm : vmList) {
			if (vmName.equals(vm.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the vmList
	 */
	public ArrayList<VM> getVmList() {
		
		return vmList;
	}
}
