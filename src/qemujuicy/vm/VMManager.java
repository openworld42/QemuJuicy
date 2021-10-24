
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

import java.util.*;

/**
 * A virtual machine manager, containing all known VMs.
 * It runs all the actions for a VM, creates, deletes, starts and more.
 */
public class VMManager {

	private ArrayList<VM> vmList;			// contains all VM objects
	
	/**
	 * Construction
	 */
	public VMManager() {
		
		vmList = new ArrayList<>(); 
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
		vmList.add(vm);
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
}
