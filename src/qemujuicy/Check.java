
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

/**
 * Helper class for development checks (similar to "assert").
 */
public class Check {
	
	public static final String NOT_IMPLMENTED = "Internal check failed due to implementation (this is a bug).\n"
			+ "Please report an error.";
	
	/**
	 * Deny external construction.
	 */
	private Check() {
	}

	/**
	 * Checks according to name.
	 * 
	 * @param flag
	 */
	public static void ifTrue(boolean flag) {
		
		if (!flag) {
			throw new RuntimeException(NOT_IMPLMENTED);
		}
	}

}
