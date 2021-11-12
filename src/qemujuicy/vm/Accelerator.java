
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

import java.util.EnumSet;

import qemujuicy.*;

/**
 * Enumeration of (almost) all accelerators supported by QEMU. 
 * The order is based on estimated usage.
 */
public enum Accelerator {
	
    BEST_GUESS("Best guess", ""),
    KVM("KVM", "kvm", "qemu-system-x86_64", "qemu-system-i386"),
    TCG("TCG", "tcg", "qemu-system-i386"),
    XEN("XEN", "xen"),
    NVMM("NVMM", "nvmm"),
    HVF("MacOS-HVF", "hvf"),
    HAX("HAX/HAXM", "hax"),
    WHPX("WHPX", "whpx"),
    NONE("None/Advanced Tab", ""),			// left to user: custom defined on Advanced-Tab
    ;
	
	public static final EnumSet<Accelerator> ALL = EnumSet.allOf(Accelerator.class);
	public static final Accelerator[] ARRAY = ALL.toArray(new Accelerator[0]);
	
	private String name;
	private String optionString;
	private String[] sysInfo;
	
	Accelerator(String name, String optionString, String... sysInfo) {
		
		this.name = name;
		this.optionString = optionString;
		this.sysInfo = sysInfo;
	}

	/**
	 * Finds the JComboBox selection index for the accelerator of a VM.
	 * 
	 * @param vm
	 * @return the JComboBox selection index
	 */
	public static int findCbxIndexFor(VM vm) {
		
		String accel = vm.getAccelerator();
		for (int i = 0; i < ARRAY.length; i++) {
			if (ARRAY[i].name.equals(accel)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param index
	 * @return the Accelerator at the given index
	 */
	public static Accelerator getAccelerator(int index) {
		
		return ARRAY[index];
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * @param index
	 * @return the name of the accelerator at the given index
	 */
	public static String getName(int index) {
		
		return ARRAY[index].name;
	}
	
	/**
	 * @return an array of architecture names
	 */
	public static String[] getNameArray() {
		
		String[] array = new String[ARRAY.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = ARRAY[i].name;
		}
		return array;
	}
	
	/**
	 * @return the optionString or null
	 */
	public String getOptionString() {
		
		if (this.equals(NONE)) {
			return null;
		}
		if (!this.equals(BEST_GUESS)) {
			return optionString;
		}
		int archSelIndex = Main.getMainView().getArchitectureCbxSelectedIndex();
		Architecture architecture = null;
		if (archSelIndex >= 0) {
			architecture = Architecture.values()[archSelIndex];
		} else {
			return "kvm:tcg";		// this should never happen
		}
		// try to guess the best options for the CPU, the host (and guest) of the VM
		if (OSType.isLinux()) {
			if (architecture.equals(Architecture.PC_X86_64) 
					|| architecture.equals(Architecture.PC_I386)) {
				return "kvm:tcg";
			} else {
				
				// TODO xxx    Accelerator 	ARM? (Android on ARM?), Raspberry Pi (Ubuntu & others), Cubieboard, ...
				// test: qemu-system-aarch64 -accel help
				// qemu-system-arm (ubuntu only?)
				// , RISK-V? MIPS? ...
				
				return "kvm:tcg";
			}
		} else if (OSType.isWindows()) {

			// TODO xxx    Accelerator 		Windows, depending on guest: whpx? (dependencies, auto detection?)
			// can one query if whpx is working? (also if guest is Linux?)

			return "kvm:tcg";
		}
		
		// TODO xxx    Accelerator 		others?
		// ARM-Windows on ARM Macs
		// Note that HAXM can only be used on Windows Enterprise/Pro/Education
		
		return "kvm:tcg";
	}

	/**
	 * @return the sysInfos of this accelerator
	 */
	public String[] getSysInfo() {
		
		return sysInfo;
	}
}


