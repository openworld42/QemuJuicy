
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

import java.util.*;

/**
 * Operating system utility (for different operating systems and their behaviour).
 */
public enum OSType {

    WINDOWS, 
    LINUX, 
    MAC, 
    SOLARIS,
    OTHER;
	
    private static OSType osType;

    private static String osName;

	/**
     * @return the operation system we are running on
     */
    public static OSType getOS() {
    	
        if (osType == null) {
         	osName = System.getProperty("os.name", "generic");
            String os = osName.toLowerCase(Locale.ENGLISH);
            if ((os.contains("mac")) || (os.contains("darwin"))) {
            	osType = OSType.MAC;
            } else if (os.contains("win")) {
            	osType = OSType.WINDOWS;
            } else if (os.contains("nux")) {
            	osType = OSType.LINUX;
            } else if (os.contains("sunos")) {
            	osType = OSType.SOLARIS;
            } else {
            	osType = OSType.OTHER;
            }
        }
        return osType;
    }

    /**
	 * @return the operation system name
	 */
	public static String getOSName() {
		
		return osName;
	}

    /**
	 * @return true if the operation system is Linux, false otherwise
	 */
    public static boolean isLinux() {
    	
        return osType == LINUX;
    }

    /**
	 * @return true if the operation system is Mac, false otherwise
	 */
    public static boolean isMac() {
    	
        return osType == MAC;
    }

    /**
	 * @return true if the operation system is Solaris, false otherwise
	 */
    public static boolean isSolaris() {
    	
        return osType == SOLARIS;
    }

    /**
	 * @return true if the operation system is MSWindows, false otherwise
	 */
    public static boolean isWindows() {
    	
        return osType == WINDOWS;
    }

    /**
	 * @return true if the operation system is a Unix type OS, false otherwise
	 */
    public static boolean isUnix() {
    	
        return (osType == LINUX 
        		|| osType == SOLARIS
                || (osType == OTHER && osName.toLowerCase(Locale.ENGLISH).indexOf("aix") > 0));
    }

    /**
	 * Logs system and OS infos.
	 */
    public static void logInfo() {
    	
   		Logger.info("operating system name: '" + osName + "'");
   		Logger.info("operating system version: '" + System.getProperty("os.version") + "'");
   		Logger.info("operating system architecture: '" + System.getProperty("os.arch") + "'");
    }
}
