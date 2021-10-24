
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
 * Prints a usage of this program.
 */
public class Usage {

	/**
	 * Prints a usage of this program and exits.
	 * 
	 * @param exitCode
	 */
	public static void exit(int exitCode) {
		
		print(); 
		System.exit(exitCode);
	}

	/**
	 * Prints a usage of this program.
	 */
	public static void print() {
		
        System.out.println("\n" + Main.APP_NAME + " usage:");
        System.out.println("java package.Main -t TEST_NUMBER [-url CONNECTION_URL]");
        System.out.println("    -h          ... display this message and exit");
        System.out.println("    -v          ... diplay version and exit");
        System.out.println("    -q          ... quiet, no verbose messages");
        System.out.println("    -t          ... do TTT");
        System.out.println("    -url <url>  ... use XY");
        System.out.println("");
	}
}
