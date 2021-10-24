
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

/**
 * Actions related to the QEMU programs like "qemu-img", "qemu-system-x86_64" and others.
 */
public class Qemu {

	/**
	 * Creates a VM diskimage.
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
}
