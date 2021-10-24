
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

import static qemujuicy.Message.*;

import java.io.*;

/**
 * Files and directories utility and container, implemented as a singleton.
 */
public class Files  {
	
	private static Files instance;				// the one and only instance
	
	private String homeDirectory;
	private String homeDirPath;
	private String appDirPath;
	private String vmDisksDirPath;

	/**
	 * Deny external construction, singleton.
	 */
	private Files() {

		homeDirectory = System.getProperty("user.home");
		homeDirPath = homeDirectory + File.separator;
	}

	/**
	 * Create an application directory for configuration, logging, etc files.
	 * 
	 * @param appDirName		the name of the application directory (in the home directory of the user)
	 * @return the File object of the application directory
	 * @throws IOException
	 */
	public static File ensureAppDir(String appDirName) throws IOException {
		
		File appDir = new File(appDirName);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		if (!appDir.isDirectory()) {
			throw new IOException(Msg.get(CANNOT_CREATE_OR_WRITE_TO_MSG, instance.appDirPath));
		}
		instance.appDirPath = appDirName + File.separator;
		return appDir;
	}

	/**
	 * Ensure an existing VM disks directory path, create it if not existing.
	 * The disks of the virtual machines are stored here, the path has to set before.
	 * 
	 * @return the File object of the VM disks directory path
	 * @throws IOException
	 */
	public static File ensureVmDisksDir() throws IOException {
		
		File appDir = new File(instance.vmDisksDirPath);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		if (!appDir.isDirectory()) {
			throw new IOException(Msg.get(CANNOT_CREATE_OR_WRITE_TO_MSG, instance.appDirPath));
		}
		return appDir;
	}

	/**
	 * @return the application directory path, ending with the OS path separator
	 */
	public static String getAppDirPath() {
		
		return instance.appDirPath;
	}

	/**
	 * @return the homeDirectory
	 */
	public static String getHomeDirectory() {
		
		return instance.homeDirectory;
	}

	/**
	 * @return the home directory path, ending with the OS path separator
	 */
	public static String getHomeDirPath() {
		
		return instance.homeDirPath;
	}

	/**
	 * @return the vmDisksDirPath
	 */
	public static String getVmDisksDirPath() {
		
		return instance.vmDisksDirPath;
	}

	/**
	 * Initializes Qemix directory, instantiate this singleton.
	 */
	public static void init() {
		
		instance = new Files();
	}

	/**
	 * Set the directory of the VM disks.
	 * 
	 * @param vmDisksDirPath 	the path to set
	 */
	public static void setVmDisksDirPath(String vmDisksDirPath) {
		
		instance.vmDisksDirPath = vmDisksDirPath;
	}
}
