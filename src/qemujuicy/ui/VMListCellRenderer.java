
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
package qemujuicy.ui;

import java.awt.*;

import javax.swing.*;

import qemujuicy.*;
import qemujuicy.vm.*;

/**
 * A renderer for the VMs in the main view VM list.
 */
public class VMListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public VMListCellRenderer() {
		
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, 
			int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel label = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		// render the selected VM
	 	VM vm = Main.getVmManager().getVm(index);
		label.setText(" " + vm.getName());
		label.setIcon(vm.getImageIcon());
		return label;
	}
}
