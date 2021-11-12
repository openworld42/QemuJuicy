
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

/**
 * Enumeration of CPU count (NUMAs) used by QEMU. 
 */
public enum Cpu {
	
    CPU1("1"),
    CPU2("2"),
    CPU3("3"),
    CPU4("4"),
    CPU5("5"),
    CPU6("6"),
    CPU7("7"),
    CPU8("8"),
    CPU10("10"),
    CPU12("12"),
    CPU14("14"),
    CPU16("16"),
    CPU24("24"),
    CPU32("32"),
    CPU48("48"),
    CPU64("64"),
    NONE("None/Advanced Tab"),				// left to user: custom defined on Advanced-Tab
    ;
	
	public static final EnumSet<Cpu> ALL = EnumSet.allOf(Cpu.class);
	public static final Cpu[] ARRAY = ALL.toArray(new Cpu[0]);
	
	private String count;
	private int cpus;

	Cpu(String count) {
		
		this.count = count;
		cpus = 0;			// Cpu.NONE value
		try {
			cpus = Integer.parseInt(count);
		} catch (Exception e) {
			// intentionally do nothing
		}
	}

	/**
	 * Finds the JComboBox selection index for the number of CPUS of a VM.
	 * 
	 * @param vm
	 * @return the JComboBox selection index
	 */
	public static int findCbxIndexFor(VM vm) {
		
		int cpus = vm.getCpus();
		if (cpus == 0) {
			// Cpu.NONE value
			return ARRAY.length - 1;
		}
		String cpu = "" + cpus;
		for (int i = 0; i < ARRAY.length; i++) {
			if (ARRAY[i].count.equals(cpu)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @return the number of cpus or -1 for NONE
	 */
	public int getCpus() {
		
		return cpus;
	}
	
	/**
	 * @param index
	 * @return the number of cpus or -1 for NONE
	 */
	public static int getCpus(int index) {
		
		return ARRAY[index].cpus;
	}
	
	/**
	 * @return an array of CPUs values
	 */
	public static String[] getCpuArray() {
		
		String[] array = new String[ARRAY.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = ARRAY[i].count;
		}
		return array;
	}
	
	/**
	 * @return an array of CPUs values (less, smaller values)
	 */
	public static String[] getCpuArraySmall() {
		
		int lessCount = 12;
		String[] array = new String[lessCount];
		for (int i = 0; i < lessCount; i++) {
			array[i] = ARRAY[i].count;
		}
		return array;
	}
}


