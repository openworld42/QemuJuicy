
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
import qemujuicy.ui.*;

import static qemujuicy.Message.*;

/**
 * A virtual machine manager, containing all known VMs.
 * It runs all the actions for a VM, creates, deletes, starts and more.
 */
public class VMManager {

	private ArrayList<VM> vmList;							// contains all VM objects
	private JList<VM> vmJList;								// JList of VMs (mainView)
	private DefaultListModel<VM> vmListModel;				// data model for the JList of VMs
	private DefaultListModel<VM.VMDevice> deviceListModel;		// data model for the JList of devices
	private VM selectedVm;									// data model for the JList of devices

	/**
	 * Construction with no VMs.
	 */
	public VMManager() {
		
		vmList = new ArrayList<>(); 
		deviceListModel = new DefaultListModel<VM.VMDevice>();
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
	 * @param vmJList 		the JList of VMs
	 * @return the model
	 */
	public ListModel<VM> createVmListModel(JList<VM> vmJList) {
		
		this.vmJList = vmJList;
		vmListModel = new DefaultListModel<VM>();
		for (VM vm : vmList) {
			vmListModel.addElement(vm);
		}
		return vmListModel;
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
		vmListModel.addElement(vm);
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
	 * Fills the device model for the selected VM or clears the model.
	 * 
	 * @param selectedIndex
	 */
	public void fillVmDeviceModel(int selectedIndex) {

		deviceListModel.clear();
		if (selectedIndex < 0) {
			selectedVm = null;
			return;
		}
		selectedVm = vmList.get(selectedIndex);
		deviceListModel.addAll(selectedVm.getDeviceList());
	}

	/**
	 * @return the deviceListModel
	 */
	public DefaultListModel<VM.VMDevice> getDeviceListModel() {
		
		return deviceListModel;
	}

	/**
	 * Returns the selected VM or null if none is selected.
	 * 
	 * @return the selected VM or null if none is selected
	 */
	public VM getSelectedVm() {
		
		return selectedVm;
	}

	/**
	 * Returns the VM at an index.
	 * 
	 * @param index
	 * @return the VM
	 */
	public VM getVm(int index) {

		return vmList.get(index);
	}
	
	/**
	 * @return the vmList
	 */
	public ArrayList<VM> getVmList() {
		
		return vmList;
	}

	/**
	 * Moves the selected VM in the list one position down, if possible.
	 * 
	 * @param vmJList 		the list of VMs
	 */
	public void moveDownVm(JList<VM> vmJList) {

		int index = vmJList.getSelectedIndex();
		if (index < 0 || index == vmList.size() - 1) {
			// do nothing
			return;
		}
		VM temp = vmList.remove(index);
		vmListModel.remove(index);
		index++;
		vmList.add(index, temp);
		vmListModel.add(index, temp);
		vmJList.setSelectedIndex(index);
		reorgAndStoreVmListToConfigFile();
	}

	/**
	 * Moves the selected VM in the list one position down, if possible.
	 * 
	 * @param vmJList 		the list of VMs
	 */
	public void moveUpVm(JList<VM> vmJList) {
		
		int index = vmJList.getSelectedIndex();
		if (index < 1) {
			// do nothing
			return;
		}
		VM temp = vmList.remove(index);
		vmListModel.remove(index);
		index--;
		vmList.add(index, temp);
		vmListModel.add(index, temp);
		vmJList.setSelectedIndex(index);
		reorgAndStoreVmListToConfigFile();
	}

	/**
	 * Removes a VM from the list and ask for wiping all files.
	 * 
	 * @param mainView 
	 * @param selectedIndex		the index in the VM list
	 */
	public void removeVm(MainView mainView, int selectedIndex) {
		
		VM vm = vmList.get(selectedIndex);
		Object[] options = {Msg.get(CANCEL_BTN_MSG),
				Msg.get(NO_BTN_MSG),
				Msg.get(YES_BTN_MSG)};
		int answer = JOptionPane.showOptionDialog(mainView,
				Msg.get(REMOVE_VM_QUESTION_MSG, vm.getName()),
				Msg.get(REMOVE_VM_TT_MSG),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		if (answer == 0) {
			return;
		}
		Logger.info("removing VM '" + vm.getName() + "'");
		vmList.remove(selectedIndex);
		vmListModel.remove(selectedIndex);
		String xmlFile = vm.getPathname();
		new File(xmlFile).delete();
		if (answer == 2) {
			// remove the disk(s) too
			String diskPath = Main.getProperty(AppProperties.VM_DISK_PATH)+ File.separator + vm.getDriveHdaName();
			Logger.info("VM '" + vm.getName() + "': removing file " + diskPath);
			new File(diskPath).delete();
		}
		// reorganize the VM list on the disk (application properties)
		reorgAndStoreVmListToConfigFile();
	}

	/**
	 * Rename a VM and its files if the name is valid.
	 * 
	 * @param selectedIndex			the index of the VM in the list
	 * @param newName				the suggested new name
	 */
	public boolean renameVm(int selectedIndex, String newName) {

		VM vm = vmList.get(selectedIndex);
		String newNameSafe = newName.replace(" ", "_");
		// check on duplicates
		boolean duplicate = false;
		for (int i = 0; i < vmList.size(); i++) {
			if (i == selectedIndex) {
				continue;					// the vm to be renamed
			}
			if (newName.equals(vmList.get(i).getName())) {
				duplicate = true;
				break;
			}
			if (newNameSafe.equals(vmList.get(i).getNameSafe())) {
				duplicate = true;
				break;
			}
		}
		if (duplicate) {
			Gui.errorDlg(Main.getMainView(), Msg.get(VM_EXIST_ALREADY_MSG), Msg.get(ERROR_TITLE_DLG_MSG));
			return false;
		}
		String vmDir = Main.getProperty(AppProperties.VM_DISK_PATH);
		String vmDiskPath = vmDir + File.separator + vm.getDriveHdaName();
		String diskName = vm.getDriveHdaName();
		String newDiskName = "";
		Logger.info("Renaming VM '" + vm.getName() + "' to '" + newName + "'");
		if (!diskName.trim().equals("")) {
			newDiskName = newNameSafe + diskName.substring(diskName.lastIndexOf("."));
			String newVmDiskPath = vmDir + File.separator + newDiskName;
			File vmDisk = new File(vmDiskPath);
			if (vmDisk.exists()) {
				vmDisk.renameTo(new File(newVmDiskPath));
			}
		}
		vm.setProperty(VMProperties.VM_NAME, newName);
		vm.setProperty(VMProperties.VM_NAME_SAFE, newNameSafe);
		vm.setProperty(VMProperties.DRIVE_HDA_NAME, newDiskName);
		String filename = vm.getProperty(VMProperties.VM_FILENAME);
		String newFilename = newNameSafe + filename.substring(filename.lastIndexOf("."));
		vm.setProperty(VMProperties.VM_FILENAME, newFilename);
		vm.getVmProperties().storeToXML();
		File vmFile = new File(vmDir + File.separator + filename);
		vmFile.renameTo(new File(vmDir + File.separator + newFilename));
		reorgAndStoreVmListToConfigFile();
		SwingUtilities.invokeLater(() -> {
			vmJList.invalidate();
			vmJList.repaint();
			vmJList.updateUI();
		});
		Logger.info("Renamed VM '" + vm.getName() + "' to '" + newName + "'");
		return true;
	}

	/**
	 * Reorganize the application properties list of VMs. This
	 * will synchronize the list of Vms to the configuration file on disk.
	 */
	private void reorgAndStoreVmListToConfigFile() {

		AppProperties properties = Main.getProperties();
		int vmNr = 0;
		for (;vmNr < vmList.size(); vmNr++) {
			properties.setProperty(AppProperties.VM_FILENAME + vmNr, 
					vmList.get(vmNr).getVmFilename());
		}
		properties.remove(AppProperties.VM_FILENAME + vmNr);	// if there is one after the last one (e.g. a remove)
		properties.storeToXML();
	}

	/**
	 * Runs a VM once from an image file or DVD/CD to install it, using its properties.
	 * 
	 * @param mainView
	 * @param vmJList
	 */
	public void runInstallVm(MainView mainView, JList<VM> vmJList) {
		
		// ask for the image file or DVD to install the VM (once)
		AppProperties properties = Main.getProperties();
		FileChooserDlg chooser = new FileChooserDlg(
				Msg.get(SELECT_OS_ICON_MSG), 
				Msg.get(OK_BTN_MSG), 
				Msg.get(OK_BTN_MSG), 
				JFileChooser.FILES_ONLY, 
				null);
		String installDir = properties.getProperty(AppProperties.INSTALL_DIR);	// might not exist or could be empty
		File dir = new File(installDir);
		if (!installDir.trim().equals("") && dir.isDirectory()) {
			// previous install from that directory, use it
			chooser.setCurrentDirectory(dir);
		}
        if (chooser.showOpenDialog(mainView) != JFileChooser.APPROVE_OPTION) {
        	return;
        }
        String vmInstallPath = chooser.getSelectedFile().getPath();
        // remember the install directory
        properties.setProperty(AppProperties.INSTALL_DIR, chooser.getCurrentDirectory().getAbsolutePath());
        properties.storeToXML();
        runVm(mainView, vmJList, vmInstallPath);
	}

	/**
	 * Runs a VM, using its properties.
	 * 
	 * @param mainView
	 * @param vmJList
	 * @param vmInstallPath		an one-time installation image path or null for an 
	 * 							existing and installed VM
	 */
	public void runVm(MainView mainView, JList<VM> vmJList, String vmInstallPath) {

		int index = vmJList.getSelectedIndex();
		VM vm = vmList.get(index);
		if (vmInstallPath != null) {
			// this is a one-time installation run of the VM, from image file or DVD/CD
			vm.getVmProperties().setPropertyAndStoreXml(
					VMProperties.INSTALLED_FROM_PATH, vmInstallPath);
	        vm.verbose("VM install path selected: " + vmInstallPath);
	        Logger.info("VM install path selected: " + vmInstallPath);
		}
		vm.setIsRunning(true);
		mainView.vmListSelectionEnabler();
		new Qemu().runVm(vm, vmInstallPath);
	}
}
