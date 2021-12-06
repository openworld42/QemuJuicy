
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

import java.util.*;

/**
 * Uses hard coded messages as Enumeration, other languages could be added later, 
 * import this as static for convenience.
 * 
 * <p>Messages are retrieved using Msg.get(messageId). Add every message of 
 * the application here, using its own enum constant.</p>
 * 
 * Messages can have parameters, ordered by a number:<br/>
 * <pre>
 *     DAD_SAYING("I can feel $(2) disturbance in the $(1)", "force", 1),
 *     
 *     results to "I can feel 1 disturbance in the force"
 * </pre>
 *
 * Note: to add multi-language support later in the project, just let getMessagesEnumMap()
 * return another EnumMap with the messages of this language (read from a
 * property file or similar).
 * 
 * @author Heinz Silberbauer
 */
public enum Message {

	// alphabetic order, all items should end with "_MSG"
	
	ABOUT_DLG_TITLE_MSG("About " + Main.APP_NAME),
	ACCELERATOR_MSG("Accelerator"),
	ADD_MSG("Add"),
	ADD_QEMU_PARAMS_MSG("Add special QEMU parameters"),
	ADVANCED_MSG("Advanced"),
	APP_DIR_MSG(Main.APP_NAME + " directory"),
	ARCHITECTURE_MSG("Architecture"),
	BOOT_MENU_MSG("QEMU boot menu"),
	CANNOT_CREATE_OR_WRITE_TO_MSG("Cannot create or write to file '$(1)'"),
	COPY_MSG("Copy"),
	ERROR_LOADING_VM_DLG_MSG("VMManager: error loading VM from '$(1)'"),
	ERROR_TITLE_DLG_MSG("Error"),
	EXTRA_PARAMS_MSG("Extra parameters"),
	DEVICES_MSG("Devices"),
	DEVICES_MANAGER_MSG("Device Manager"),
	VM_EXITS_ALREADY_MSG("A VM with the name '$(1)' exists already"),
	VM_EXITS_WITH_CODE("The VM exits with code '$(1)', was it already running?"),
	FILE_EXITS_ALREADY_MSG("A file '$(1)' exists already"),
	FIRST_SETUP_DLG_MSG("Setup: should " + Main.APP_NAME + " search for QEMU installations?"),
	HINTS_MSG("Hints in status line"),
	LOCALTIME_MSG("Use local time"),
	MANAGE_MSG("Manage"),
	MEMORY_MSG("Memory"),
	MEMORY_DEFAULT_MSG("Default memory ($(1) MB)"),
	MEMORY_MAX_MSG("Maximum ($(1) MB)"),
	QEMU_BY_DEFINITION_MSG("Full QEMU definition (ignore other settings)"),
	QEMU_INSTALL_SETTINGS_MSG("QEMU Installation Settings"),
	QEMU_OUTPUT_LBL_MSG("QEMU output: "),
	REMOVE_VM_QUESTION_MSG("Delete the virtual machine '$(1)': delete all its files/disks too?"),
	SAVE_TO_FILE_MSG("Save to file"),
	SELECT_DIR_MSG("Select the directory"),
	SELECT_OS_ICON_MSG("Select an icon for the operation system"),
	SELECT_QEMU_DLG_TITLE_MSG("Select QEMU Installation Directory"),
	SELECT_QEMU_DLG_EXPLANATION_MSG("To run a virtual machine (VM),\n"
			+ "a QEMU installation is necessary"),
	SELECT_QEMU_INSTALL_DIR_LBL_MSG("Select QEMU install directory"),
	SOUND_MSG("Sound"),
	STARTING_MSG("\nStarting '$(1) ..."),
	STORE_MSG("Store"),
	STORE_LINES_MSG("Store \\"),
	VERBOSE_MSG("Verbose output"),
	VM_DIR_MSG("Directory of VMs (disks)"),
	VM_EXIST_ALREADY_MSG("A Vm with that (or similar) name exists already"),
	VM_MSG("Virtual machine"),
	
	// hints in status bar: ends with _HINT_MSG
	CONFIG_DIR_SETTINGS_HINT_MSG("Setup wizard: configure QEMU, settings and directories"),
	CONFIG_DIR_SETTINGS_FINISH_HINT_MSG("After setup, everthing can be changed: File->Settings"),
	FIRST_VM_HINT_MSG("Read the help (menu) or create the first virtual machine using the VM wizard (+)"),
	USE_INSTALL_VM_BUTTON_HINT_MSG("Use the install button to install an OS once from CD/DVD/*.iso file"),
	
	// tooltips: ends with _TT_MSG
	ADD_CD_DVD_TT_MSG("Add a CD/DVD/*.iso file"),
	ADD_DRIVE_TT_MSG("Add a drive"),
	ADD_FLOPPY_TT_MSG("Add a floppy disk"),
	ADD_VM_TT_MSG("Create a virtual machine"),
	CONF_GENERAL_SETTINGS_TT_MSG("General settings"),
	CONF_FILES_TT_MSG("Files and folders"),
	COPY_CLIPBOARD_TT_MSG("Copy to clipboard"),
	LOCALTIME_TT_MSG("Set real time clock to local time (default: utc), required for MS-DOS/Windows "),
	DISK_IMAGE_VM_TT_MSG("Create or manage a virtual machine disk"),
	MOVE_UP_VM_TT_MSG("Move the VM up"),
	MOVE_DOWN_VM_TT_MSG("Move the VM down"),
	SETUP_WIZARD_TT_MSG("Setup Wizard"),
	QEMU_INSTALLATION_TT_MSG("QEMU installation settings"),
	REMOVE_DEVICE_TT_MSG("Remove a device"),
	REMOVE_VM_TT_MSG("Delete virtual machine"),
	START_TT_MSG("Start virtual machine"),
	STOP_TT_MSG("Stop virtual machine"),
	STORE_AS_FILE_TT_MSG("Store as file (shell script, bat file) to start this VM"),
	STORE_AS_FILE_LINES_TT_MSG("Store as file lines (shell script, bat file) to start this VM"),
	VM_TAB_ADVANCED_PROPERTIES_TT_MSG("Advanced QEMU parameters/usage"),
	VM_TAB_VM_DEVICES_TT_MSG("Virtual machine devices manager"),
	VM_TAB_VM_PROPERTIES_TT_MSG("Virtual machine general properties"),
	VM_RUN_INSTALL_TT_MSG("Install virtual machine once from DVD/image file"),
	VM_WIZARD_TT_MSG("Create a virtual machine using the VM wizard"),
	
	// GUI buttons & menu items text: ends with _BTN_MSG
	ABOUT_BTN_MSG("About"),
	APPLY_BTN_MSG("Apply"),
	BACK_BTN_MSG("Back"),
	CANCEL_BTN_MSG("Cancel"),
	CONTRIBUTION_BTN_MSG("Contribution"),
	CREDITS_BTN_MSG("Credits"),
	EXIT_BTN_MSG("Exit"),
	EXIT_APP_BTN_MSG("Exit " + Main.APP_NAME),
	FILE_BTN_MSG("File"),
	FINISHED_BTN_MSG("Finished"),
	HELP_BTN_MSG("Help"),
	LICENSE_BTN_MSG("License"),
	LICENSE_OXYGEN_BTN_MSG("License_Oxygen"),
	LICENSE_AQEMU_BTN_MSG("License_aqemu"),
	MISC_MSG("Miscellaneous"),
	NEXT_BTN_MSG("Next"),
	NO_BTN_MSG("No"),
	OK_BTN_MSG("Ok"),
	OPEN_BTN_MSG("Open"),
	QEMU_SETUP_BTN_MSG("QEMU setup"),
	SETTINGS_BTN_MSG("Settings"),
	TEST_BTN_MSG("Test QEMU"),
	VM_BTN_MSG("Virtual machine"),
	VM_WIZARD_MSG("Create a VM using a wizard"),
	YES_BTN_MSG("Yes"),
	
	// wizard: ends with _WIZ_MSG
	CREATE_VM_WIZ_MSG("Wizard: Create A Virtual Machine (VM)"),
	OS_WIZ_MSG("Operating system"),
	QEMU_INSTALL_WIZ_MSG("Searching for a QEMU installation:"),
	QEMU_INSTALL_FAIL_WIZ_MSG("No QEMU found, please install QEMU first or use the wizard to select an installation"),
	QEMU_INSTALL_OK_WIZ_MSG("QEMU installation found (you can select another installation)"),
	QEMU_INSTALL_DISK_WIZ_MSG("Success: a QEMU program for VM disk creation found"),
	QEMU_INSTALL_EMULATOR_WIZ_MSG("Success: QEMU emulator(s) to run VMs found"),
	QEMU_INSTALL_HINT_WIZ_MSG("Please follow the wizard to setup " + Main.APP_NAME),
	QEMU_INSTALL_FAIL_DISK_WIZ_MSG("Fail: no program for VM disk creation found"),
	QEMU_INSTALL_FAIL_EMULATOR_WIZ_MSG("Fail: no QEMU emulator to run VMs found"),
	QEMUJUICY_SETUP_FINISH_WIZ_MSG(Main.APP_NAME + " Setup Creation Finish!"),
	SETUP_WIZ_MSG("First Setup Wizard"),
	SETUP_WIZ_FAIL_MSG("Setup wizard canceled: exit " + Main.APP_NAME),
	SETUP_WIZ_NOTE_WIZ_MSG("Note: all options can be changed later"),
	SETUP_WIZ_FINISH_PRESS_WIZ_MSG("Press the 'Finish' button: the setup is complete"),
	VM_CREATION_FINISH_WIZ_MSG("VM Creation Finish!"),
	VM_CREATION_MODE_WIZ_MSG("VM creation mode"),
	VM_FINISH_ALL_OK_HEADER_WIZ_MSG("If erverthing is OK:"),
	VM_FINISH_NOTE_WIZ_MSG("Note: all options can be changed after creation"),
	VM_FINISH_PRESS_WIZ_MSG("Press the 'Finish' button: the new VM will be created"),
	VM_HARDDISK_HEADER_WIZ_MSG("Hard disk size"),
	VM_HARDDISK_NOTE_1_WIZ_MSG("Set the maximum VM hard disk capacity"),
	VM_HARDDISK_NOTE_2_WIZ_MSG("Note: it is the maximum capacity, the VM will take usually less"),
	VM_HARDDISK_SIZE_WIZ_MSG("Disk size ($(1) GB):"),
	VM_HARDDISK_WIZ_MSG("VM Hard Disk"),
	VM_ICON_WIZ_MSG("VM icon"),
	VM_IDENTIFICATION_WIZ_MSG("VM Identification"),
	VM_MODE_CUSTOMIZED_WIZ_MSG("Customized, advanced mode"),
	VM_MODE_TYPICAL_WIZ_MSG("Typical, a VM with most common options"),
	VM_NAME_HEADER_WIZ_MSG("VM identification name"),
	VM_NAME_WIZ_MSG("Name"),
	VM_NETWORK_WIZ_MSG("Network"),
	VM_NETWORK_CONNECTION_WIZ_MSG("Network connection"),
	VM_NETWORK_CONNECTED_WIZ_MSG("Connect to network (User Mode Networking)"),
	VM_NETWORK_NOTE_WIZ_MSG("Note: networking can be configured later"),
	VM_NO_NETWORK_WIZ_MSG("No networking (offline)"),
	VM_WIZ_MSG("VM"),
	;


    private final String message;

    /**
     * Construct a Message.
     *
     * @param message
     */
    private Message(String message) {

        this.message = message;
    }

    /**
     * @return the message of this Enumeration.
     */
    public String getMessage() {

		return message;
	}

    /**
     * Create an efficient EnumMap out of all messages.
      *
     * @return the EnumMap
     */
    public static EnumMap<Message, String> getMessagesEnumMap() {

    	EnumMap<Message, String> enumMap = new EnumMap<Message, String>(Message.class);
    	for (Message msg : Message.values()) {
        	enumMap.put(msg, msg.getMessage());
		}
    	return enumMap;
    }
}
