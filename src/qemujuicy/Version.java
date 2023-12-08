
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
 * Version information of the project.
 * Yes, this could be searched in a more complicated way in a manifest file.
 */
public class Version {

	/** !!! TODO: CHANGE BEFORE A NEW RELEASE !!! */

	public static final int MAJOR = 0;
	public static final int MINOR = 6;
	public static final int RELEASE = 7;

	/**
	 * No external construction.
	 */
	private Version() {

	}

	/**
	 * Returns the version numbers as <code>String</code> (eg "2.0.13").
	 *
	 * @return the version numbers as <code>String</code>
	 */
	public static String getAsString() {

		return MAJOR + "." + MINOR + "." +  RELEASE;
	}

	/**
	 * Returns the major version number (eg the 2 of "2.0.13").
	 *
	 * @return the major version number
	 */
	public static int getMajor() {

		return MAJOR;
	}

	/**
	 * Returns the minor version number (eg the 0 of "2.0.13").
	 *
	 * @return the minor version number
	 */
	public static int getMinor() {

		return MINOR;
	}

	/**
	 * Returns the release version number (eg the 13 of "2.0.13").
	 *
	 * @return the release version number
	 */
	public static int getRelease() {

		return RELEASE;
	}

	/**
	 * Prints a version message.
	 */
	public static void print() {
		
		System.out.println(Main.APP_NAME + " version " + getAsString());
	}
}
