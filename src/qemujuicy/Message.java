
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
	APP_DIR_MSG(Main.APP_NAME + " directory"),
	CANNOT_CREATE_OR_WRITE_TO_MSG("Cannot create or write to file '$(1)'"),
	ERROR_TITLE_DLG_MSG("Error"),
	VM_EXITS_ALREADY_MSG("A VM with the name '$(1)' exists already"),
	FILE_EXITS_ALREADY_MSG("A file '$(1)' exists already"),
	FIRST_SETUP_DLG_MSG("Setup: should " + Main.APP_NAME + " search for QEMU installations?"),
	FIRST_SETUP_DLG_TITLE_MSG(Main.APP_NAME + " setup"),
	HINTS_MSG("Hints in status line"),
	QEMU_OUTPUT_LBL_MSG("QEMU output: "),
	SELECT_DIR_MSG("Select the directory"),
	SELECT_OS_ICON_MSG("Select an icon for the operation system"),
	SELECT_QEMU_DLG_TITLE_MSG("Select QEMU"),
	SELECT_QEMU_DLG_EXPLANATION_MSG("To run a virtual machine (VM),\n"
			+ "a QEMU installation (a command path) is necessary"),
	SELECT_QEMU_DLG_PATH_LBL_MSG("QEMU command/path: "),
	VM_DIR_MSG("Directory of VMs (disks)"),
	VM_MSG("Virtual machine"),
	STARTING_MSG("\nStarting '$(1) ..."),
	
	// hints in status bar: ends with _HINT_MSG
	CONFIG_DIR_SETTINGS_HINT_MSG("Hint: configure settings and directories, File->Settings"),
	FIRST_VM_HINT_MSG("Create the first virtual machine using the wizzard"),
	
	// tooltips: ends with _TT_MSG
	ADD_VM_TT_MSG("Create a virtual machine"),
	CONF_GENERAL_SETTINGS_TT_MSG("General settings"),
	CONF_FILES_TT_MSG("Files and folders"),
	DISK_IMAGE_VM_TT_MSG("Create or manage a virtual machine disk"),
	QEMU_CALL_TT_MSG("Create a virtual machine just by QEMU call"),
	REMOVE_VM_TT_MSG("Delete a virtual machine"),
	START_TT_MSG("Start a virtual machine"),
	STOP_TT_MSG("Stop a virtual machine"),
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
	NEXT_BTN_MSG("Next"),
	OK_BTN_MSG("Ok"),
	OPEN_BTN_MSG("Open"),
	QEMU_SETUP_BTN_MSG("QEMU setup"),
	SETTINGS_BTN_MSG("Settings"),
	TEST_BTN_MSG("Test QEMU"),
	VM_BTN_MSG("Virtual machine"),
	VM_WIZARD_MSG("Create a VM using a wizard"),
	
	// wizard: ends with _WIZ_MSG
	CREATE_VM_WIZ_MSG("Wizard: Create A Virtual Machine (VM)"),
	OS_WIZ_MSG("Operating system"),
	SETUP_WIZ_MSG("Setup Wizard"),
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
