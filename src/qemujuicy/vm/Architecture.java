
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

/**
 * Enumeration of (almost) all CPU architectures supported by QEMU. 
 * The order is based on estimated usage.
 */
public enum Architecture {
	
    PC_X86_64("PC x86 (64 bit)", "qemu-system-x86_64"),
    PC_I386("PC i386", "qemu-system-i386"),
    ARM64("ARM64 (AArch64)", "qemu-system-aarch64"),
    ARM("ARM", "qemu-system-arm"),
    AVR("AVR (Arduino)", "qemu-system-avr"),
    MIPS64("MIPS64", "qemu-system-mips64"),
    PPC64("PowerPC", "qemu-system-ppc64"),
    RISC_V("Risc-V", "qemu-system-riscv64"),
    SPARC64("SPARC64", "qemu-system-sparc64"),
    ;
	
	public static final EnumSet<Architecture> ALL = EnumSet.allOf(Architecture.class);
	public static final Architecture[] ARRAY = ALL.toArray(new Architecture[0]);
	
	private String name;
	private String qemuCmd;
	
	Architecture(String name, String qemuCmd) {
		
		this.name = name;
		this.qemuCmd = qemuCmd;
	}

	/**
	 * Finds the JComboBox selection index for the architecture of a VM.
	 * 
	 * @param vm
	 * @return the JComboBox selection index
	 */
	public static int findCbxIndexFor(VM vm) {
		
		String arch = vm.getArchitecture();
		for (int i = 0; i < ARRAY.length; i++) {
			if (ARRAY[i].qemuCmd.equals(arch)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * @param index
	 * @return the name of the Architecture at the given index
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
	 * @return the qemuCmd
	 */
	public String getQemuCmd() {
		
		return qemuCmd;
	}
	
	/**
	 * @param index
	 * @return the QEMU command of the Architecture at the given index
	 */
	public static String getQemuCmd(int index) {
		
		return ARRAY[index].qemuCmd;
	}
}


