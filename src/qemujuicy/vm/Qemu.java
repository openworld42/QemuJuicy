
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

import qemujuicy.*;
import qemujuicy.ui.*;

import static qemujuicy.Message.*;

/**
 * Actions related to the QEMU programs like "qemu-img", "qemu-system-x86_64" and others.
 */
public class Qemu {

	/**
	 * Creates a VM disk image.
	 * 
	 * @param vm	the VM
	 * @return true, if the commend worked, false otherwise (Exception caught)
	 */
	public boolean createDiskImage(VM vm) {

		String qemuImg = "qemu-img";
		String diskPath = Main.getProperty(AppProperties.VM_DISK_PATH)+ File.separator + vm.getDiskName();
		String output = Util.runProcess(qemuImg, "create", "-f", "qcow2", diskPath, vm.getDiskSizeGB() + "G");
		return output != null ? true : false;		
	}

	/**
	 * Runs a VM.
	 * 
	 * @param vm	the VM to run
	 * @return true, if the command worked, false otherwise (Exception caught)
	 */
	public boolean runVm(VM vm) {

		String qemuCmd = "qemu-system-x86_64";
		String diskPath = Main.getProperty(AppProperties.VM_DISK_PATH)+ File.separator + vm.getDiskName();
		int maxMemMB = vm.getMemorySizeMB();
		// create the parameters
		String[] cmdArr = {
				qemuCmd,
				"-hda", diskPath,
				"-cdrom", "/home/misc/linux/Debian/debian-testing-amd64-netinst20210919.iso",
				
				"-m", maxMemMB + "M",		// min 512M
//				"-m", maxMemMB/4 + "M,slots=3,maxmem=" + maxMemMB + "M",
				"-boot", "menu=on",
				"-smp", "" + vm.getCpus(),
				"-nic", "user,ipv6=off,model=e1000,mac=52:54:98:76:54:32",
				"-name", vm.getNameSafe(),
		};
		String cmdString = "";
		for (String s : cmdArr) {
			cmdString += s + " ";
		}
		Logger.info("executing: " + " " + cmdString);
		System.out.println("executing: " + " " + cmdString);
		try {
			ProcessExecutor executor = new ProcessExecutor(500L, cmdArr);		// small timeout to catch an exit code on error
			// this should never happen, the process should catch the timeout exception
			int exitCode = executor.getExitValue();
			Logger.error("error in execution, QEMU process has finished, exit code: "
					+ exitCode + ", output:\n" + executor.getOutput());
			Gui.errorDlg(null, Msg.get(VM_EXITS_WITH_CODE, exitCode), Msg.get(ERROR_TITLE_DLG_MSG));
			return false;
		} catch (IllegalThreadStateException itse) {
			// timeout is over, this is ok (= normal QEMU execution, the VM is running)
			Logger.info("VM '" + vm.getName() + "' is running");
			return true;
		} catch (Exception e) {
			// something went completely wrong
			e.printStackTrace();
			Logger.error("Error running the VM '" + vm.getName() + "'", e); 
			return false;
		}
	}
}
