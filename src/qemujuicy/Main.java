
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
import java.util.*;

import javax.swing.UIManager; 
import javax.swing.UIManager.LookAndFeelInfo;

import qemujuicy.ui.*;
import qemujuicy.vm.*;

/**
 * QEMU Juicy - VM emulator GUI (main entry), QEMU Java User Interface Contributed Yet. 
 */
public class Main {

	public static final String APP_NAME = "QemuJuicy";
	
	public static final String APP_DIR_MS_WIN = "QemuJuicy";
	public static final String APP_DIR_LINUX_UNIX = ".qemujuicy";
	public static final String CONFIG_FILE = "config.xml";
	public static final String LOG_FILE = "log.txt";
	public static final String VM_DISKS_DIR = "vm";

	private static Main instance;				// the one and only instance of this application
	private CommandLineArgs args;				// command line arguments
	private AppProperties properties; 			// application properties (config file, version, ...)
	private MainView mainView;
	private QemuSetup qemuSetup;			// setup information for qemu commands

	private VMManager vmManager;

	private boolean isVerbose; 					// verbose messages to System.out
	private boolean isFirstStart; 				// true, if this is the first start of Qemix

	/**
	 * Construction.
	 * 
	 * @param arguments			command line arguments
	 * @throws Exception
	 */
	public Main(String[] arguments) throws Exception {

		instance = this;
		// parse command line arguments
		args = new CommandLineArgs(arguments);
		if (!args.isValid()) {
			Usage.exit(1);
		} 
		isVerbose = args.isVerbose();
		System.getProperties().list(System.out);
		Util.verbose(Msg.get(STARTING_MSG, APP_NAME));
		Files.init();
		OSType.getOS();					// init OS detection
		if (OSType.isWindows()) {
			String appDirWindows =  System.getenv("APPDATA");
			if (appDirWindows == null) {
				appDirWindows = System.getenv("LOCALAPPDATA");
			}
			if (appDirWindows == null) {
				appDirWindows = APP_DIR_MS_WIN;
			} else {
				appDirWindows = appDirWindows + File.separator + APP_DIR_MS_WIN;
			}
			Util.verbose("Windows application directory '" + appDirWindows + "'");
			Files.ensureAppDir(APP_DIR_MS_WIN);
		} else {
			Files.ensureAppDir(Files.getHomeDirPath() + APP_DIR_LINUX_UNIX);
		}
		// maybe this is the first start, so we have to do some setup
		String configFilePath = Files.getAppDirPath() + CONFIG_FILE;
		File f = new File(configFilePath);
		isFirstStart = !f.isFile();
		// read in the project XML properties from a configuration file
		// if the file does not exist, it will be created with the default properties
		Util.verbose("Reading configuration file from " + configFilePath);
		properties = new AppProperties(configFilePath, true);
		Util.verbose("Logging to file " + Files.getAppDirPath() + LOG_FILE + " ...");
		Logger.init(Files.getAppDirPath() + LOG_FILE);
		Logger.logErrorsToConsole(true);
		Logger.info(APP_NAME + ": started ...");
		logSystemAndUserInfos();
		OSType.getOS();
		OSType.logInfo();
		Logger.info(APP_NAME + " application directory: '" + Files.getAppDirPath() + "'");
		Logger.info("reading configuration file: " + CONFIG_FILE);
		// finish some setup
		if (isFirstStart) {
			String vmDisksPath = Files.getAppDirPath() + VM_DISKS_DIR;
			properties.setProperty(AppProperties.VM_DISK_PATH, vmDisksPath);		// may not exist, ensure it later
			Files.setVmDisksDirPath(vmDisksPath);
		}
		// read the existing VMs into VMManager
		vmManager = new VMManager();
		
		// TODO xxx    Main  read the existing VMs into VMManager

		
		// start GUI
		System.setProperty("awt.useSystemAAFontSettings","on");	// render fonts in a better way
		Gbc.setDefaultInset(7);				// generic inset to next grid cell
		Gbc.setDefaultBorderInset(4);		// generic inset to the view border
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // in case Nimbus is not found
    	String lookAndFeel = Main.getProperty(AppProperties.LOOK_AND_FEEL);
    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		if (lookAndFeel.equals(info.getName())) {
    			UIManager.setLookAndFeel(info.getClassName());
    			break;
    		}
    	}
    	mainView = new MainView();
	}

	/**
	 * Logs an Exception and performs System.exit(1) WITHOUT closing 
	 * anything except the Logger.
	 *
	 * @param e		the Exception
	 */
	public static void exitOnException(Exception e) {

        System.out.println("\n*****  Exception caught, exit: " + e);
		e.printStackTrace();
		Logger.error("Exception caught, exit", e);
		Logger.close();
		
		System.exit(1);			// indicate an unexpected exception exit with an error code
	}
	
	/**
	 * @return the parsed command line arguments
	 */
	public static CommandLineArgs getArgs() {
	
		return instance.args;
	}

	/**
	 * @return the mainView
	 */
	public static MainView getMainView() {
		
		return instance.mainView;
	}
	
	/**
	 * @return the application properties
	 */
	public static AppProperties getProperties() {
		
		return instance.properties;
	}

	/**
	 * Gets a property.
	 * 
	 * @param key
	 * @return the property value
	 */
	public static String getProperty(String key) {
		
		return instance.properties.getProperty(key);
	}

	/**
	 * Gets an boolean property (a flag).
	 * 
	 * @param key
	 * @return true id the value is "true", false otherwise
	 */
	public static boolean getPropertyBool(String key) {
		
		return instance.properties.getPropertyBool(key);
	}

	/**
	 * Gets an integer property.
	 * 
	 * @param key
	 * @return the integer value
	 */
	public static int getPropertyInt(String key) {
		
		return Integer.parseInt(instance.properties.getProperty(key));
	}
	
	/**
	 * @return the qemuSetup
	 */
	public static QemuSetup getQemuSetup() {
		
		return instance.qemuSetup;
	}

	/**
	 * @return the vmManager
	 */
	public static VMManager getVmManager() {
		
		return instance.vmManager;
	}

	/**
	 * @return the main instance of this application
	 */
	public Main instance() {
		
		return instance;
	}

	/**
	 * 
	 * @return true, if the application started never before (setup needed), false otherwise
	 */
	public static boolean isFirstStart() {
		
		return instance.isFirstStart;
	}

	/**
	 * Flag for verbose messages sent to System.out.
	 * 
	 * @return true, if in verbose mode, false otherwise
	 */
	public static boolean isVerbose() {
		
		return instance.isVerbose;
	}

	/**
	 * Logs system and user infos.
	 * Bug fix use only, please.
	 */
	private void logSystemAndUserInfos() {

		Logger.info("java.vm.name: " + System.getProperty("java.vm.name"));
		Logger.info("java.runtime.version: " + System.getProperty("java.runtime.version"));
		Logger.info("java.version: " + System.getProperty("java.version"));
		Logger.info("java.home: " + System.getProperty("java.home"));
		Logger.info("java.library.path: " + System.getProperty("java.library.path"));
		Logger.info("file.separator: " + System.getProperty("file.separator"));
		Logger.info("user.home: " + System.getProperty("user.home"));
		Logger.info("user.dir: " + System.getProperty("user.dir"));
		Logger.info("user.language: " + System.getProperty("user.language"));
		// environment
		String keyWords[] = {
				// linux/unix
				"PATH",
				"LANG",
				"LOGNAME",
				"LANGUAGE",
				"SHELL",
				"DESKTOP",
				"USER",
				"HOME",
				// windows
				"LOCAL",
				"JAVA",
				"PROCESSOR",
				"HOME",
				"USER",
//				"PATH",		already available from linux
				"PROGRAM",
				"OS",
				"COMPUTER",
				"WIN",
				"PATH",
		};
        Map <String, String> map = System.getenv();
        for (Map.Entry <String, String> entry: map.entrySet()) {
        	String key = entry.getKey().toUpperCase();
        	for (int i = 0; i < keyWords.length; i++) {
				if (key.contains(keyWords[i])) {
					Logger.info("Env - " + entry.getKey() + " -> " + entry.getValue());
					break;
				}
			}
//            System.out.println("Env var - " + entry.getKey() + " -> " + entry.getValue());
        }
	}

	/**
	 * Main entry.
	 * 
	 * @param args		command line arguments
	 */
	public static void main(String[] args) {

        try {
            new Main(args);
        } catch (Exception e) {
            e.printStackTrace();
			Logger.error("Unexpected exception, exit", e);
			// generic error exit (so we don't forget anything, there are a lot of possibilities to fail)
			exitOnException(e);
			System.exit(2);		// needed for GUI
		}
	}

	/**
	 * 
	 */
	public static void onExit() {
		
 		instance.properties.storeToXML();
 		Logger.info(APP_NAME + ": exit under normal conditions");
		Logger.close();
        System.out.println(APP_NAME + ", bye.");
        System.exit(0);
	}

	/**
	 * Sets the first start flag (usually resets it).
	 */
	public static void setFirstStart(boolean flag) {
		
		instance.isFirstStart = flag;
	}

	/**
	 * Set the QEMU commands information.
	 * 
	 * @param qemuSetup
	 */
	public static void setQemuSetup(QemuSetup qemuSetup) {

		instance.qemuSetup = qemuSetup;
	}
}
