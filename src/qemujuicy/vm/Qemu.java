
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
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

import qemujuicy.*;

/**
 * Actions related to the QEMU programs like "qemu-img", "qemu-system-x86_64" and others.
 */
public class Qemu {

	/**
	 * Adds the extra parameters of the VM to the command list, if any.
	 * 
	 * @param cmdList
	 * @param vm
	 */
	public static void addExtraParameters(ArrayList<String> cmdList, VM vm) {

		String params = vm.getProperty(VMProperties.EXTRA_PARAMETERS);
		if (params.trim().equals("")) {
			return;
		}
		Scanner scanner = new Scanner(params);
		while (scanner.hasNext()) {
			cmdList.add(scanner.next());
		}
		scanner.close();
	}

	/**
	 * Create a QEMU command ArrayList for a VM, using a string of parameters.
	 * 
	 * @param command			the QEMU command and a string of parameters
	 * @return the command ArrayList
	 */
	public static ArrayList<String> createCommandList(String command) {
		
		ArrayList<String> cmdList = new ArrayList<String>();
		Scanner scanner = new Scanner(command);
		while (scanner.hasNext()) {
			cmdList.add(scanner.next());
		}
		scanner.close();
		return cmdList;
	}

	/**
	 * Create a QEMU command ArrayList for a VM, using its properties.
	 * 
	 * @param vm				the VM
	 * @param vmInstallPath		an one-time installation image path or null for an 
	 * 							existing and installed VM
	 * @return the command ArrayList
	 */
	public static ArrayList<String> createCommandList(VM vm, String vmInstallPath) {
		
		String qemuCmd = Architecture.ARRAY[Architecture.findCbxIndexFor(vm)].getQemuCmd();
		String diskPath = Main.getProperty(AppProperties.VM_DISK_PATH)+ File.separator + vm.getDiskName();
		int maxMemMB = vm.getMemorySizeMB();
		// create the parameters
		ArrayList<String> cmdList = new ArrayList<>();
		cmdList.add(qemuCmd);
		
//		cmdList.add("-monitor");
//		cmdList.add("stdio");
		
		String accel = Accelerator.ARRAY[Accelerator.findCbxIndexFor(vm)].getAccelOptionString();
		if (accel != null) {			// else -> advanced tab/default tcg
			cmdList.add("-machine");
			cmdList.add("accel=" + accel);
		}
		int cpus = Cpu.findCbxIndexFor(vm);
		if (cpus > 0) {					// if == 0 -> advanced tab/default
			cmdList.add("-smp");
			cmdList.add("" + vm.getCpus());
		}
		cmdList.add("-m");
		cmdList.add(maxMemMB + "M");
//		"-m", maxMemMB/4 + "M,slots=3,maxmem=" + maxMemMB + "M",   		// min 512M
		
		cmdList.add("-drive");
		cmdList.add("file=" + diskPath + ",index=0,media=disk");
		
		// TODO xxx    Qemu runVm()      change hard coded CDROM  
		
		if (vmInstallPath != null) {
			// VM should do a one-time installation boot run from image file or DVD/CD
			cmdList.add("-drive");
			cmdList.add("file=" + vmInstallPath + ",index=3,media=cdrom");
		}
		cmdList.add("-boot");
		String bootParams = vmInstallPath != null ? "order=cd,once=d" : "order=c";
		if (vm.getPropertyBool(VMProperties.QEMU_BOOT_MENU)) {
			bootParams += ",menu=on";
		}
		cmdList.add(bootParams);			
		
		// TODO xxx    Qemu runVm()      change hard coded nic / networking  
		cmdList.add("-nic");
		cmdList.add("user,ipv6=off,model=e1000,mac=52:54:98:76:54:32");	

		cmdList.add("-name");
		cmdList.add(vm.getNameSafe());	
		return cmdList;
	}

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
	 * @param vm				the VM to run
	 * @param vmInstallPath		an one-time installation image path or null for an 
	 * 							existing and installed VM
	 * @return true, if the command worked, false otherwise (Exception caught)
	 */
	public boolean runVm(VM vm, String vmInstallPath) {

		ArrayList<String> cmdList = null;
		if (vm.getPropertyBool(VMProperties.FULL_QEMU_DEFINITION)) {
			String cmd = vm.getProperty(VMProperties.FULL_QEMU_DEFINITION_CMD).trim();
			cmdList = createCommandList(cmd);
		} else {
			cmdList = createCommandList(vm, vmInstallPath);
		}
		addExtraParameters(cmdList, vm);
		// process the generated command
		String[] cmdArr = cmdList.toArray(new String[0]);
		String cmdString = toCommandString(cmdList);
		Logger.info("executing: " + " " + cmdString);
		vm.verbose("executing: " + " " + cmdString);
		try {
			ProcessBuilder builder = new ProcessBuilder(cmdArr);
			Process process = builder.start();
			VmRunnable runnable = new VmRunnable(vm, process);
			Thread vmWatcherThread = Executors.defaultThreadFactory().newThread(runnable);
			vmWatcherThread.setName("vmWatcherThread_" + vm.getNameSafe());
			vmWatcherThread.start();
			return true;
		} catch (Exception e) {
			// something went completely wrong
			e.printStackTrace();
			Logger.error("Error running the VM '" + vm.getName() + "'", e); 
			return false;
		}
	}

	/**
	 * Creates a command string from an ArrayList of command and parameters.
	 * 
	 * @param cmdList			the ArrayList
	 * @return the command string
	 */
	public static String toCommandString(ArrayList<String> cmdList) {

		String[] cmdArr = cmdList.toArray(new String[0]);
		String cmdString = "";
		for (String s : cmdArr) {
			cmdString += s + " ";
		}
		return cmdString;
	}

	/**
	 * Creates a command string from an ArrayList of command and parameters.
	 * 
	 * @param cmdList			the ArrayList
	 * @return the command string
	 */
	public static String toCommandStringStore(ArrayList<String> cmdList) {

		String cmdString = "";
		String cmdEndString = "";;
		if (OSType.isLinux() || OSType.isUnix() || OSType.isMac()) {
			cmdString = "#!/bin/sh\n# script created by QemuJuicy\n";
			cmdEndString = " $*";
		}
		return cmdString + toCommandString(cmdList) + cmdEndString;
	}
	
	/**
	 * Create a text area string of a string containing arbitrary white space.
	 * 
	 * @param cmd
	 * @return the text area string
	 */
	public static String toTextAreaString(String cmd) {
		
		ArrayList<String> cmdList = new ArrayList<String>();
		Scanner scanner = new Scanner(cmd);
		while (scanner.hasNext()) {
			cmdList.add(scanner.next());
		}
		scanner.close();
		return toTextAreaString(cmdList);
	}

	/**
	 * Create a text area string from a command ArrayList.
	 * 
	 * @param cmdList			the list of the command with options
	 * @return the text area string
	 */
	public static String toTextAreaString(ArrayList<String> cmdList) {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			sb.append(cmdList.get(i));
			sb.append(" ");
		}
		sb.append(cmdList.get(2));
		// divide in lines now
		for (int i = 3; i < cmdList.size(); i++) {
			sb.append("\n");
			sb.append(cmdList.get(i++));
			if (i < cmdList.size()) {
				if (!cmdList.get(i).startsWith("-")) {
					sb.append(" ");
					sb.append(cmdList.get(i));
				}
			}
		}
		return sb.toString();
	}
	
	/************************* inner classes *************************/
	
	/**
	 * A Runnable for a thread to watch if the VM is running.
	 */
	private class VmRunnable implements Runnable {

		private VM vm;					// the VM to watch
		private Process process;		// the process of the VM

		public VmRunnable(VM vm, Process process) {
			
			this.vm = vm;
			this.process = process;
		}
		
		public void run() {
			
			vm.setProcess(process);
			for (;;) {
				if (!process.isAlive()) {
					vm.setIsRunning(false);
					vm.setProcess(null);
					SwingUtilities.invokeLater(() -> {
						vm.verbose("VM '" + vm.getName() + "' has exited");
						Logger.info("VM '" + vm.getName() + "' has exited");
						Main.getMainView().vmListSelectionEnabler();
					});
					break;
				}
				Util.sleep(200);		// meanwhile be polite to the others
			}
		}
	}
}
