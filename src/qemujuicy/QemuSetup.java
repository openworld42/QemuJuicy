
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

import javax.swing.*;

import qemujuicy.ui.*;
import qemujuicy.vm.*;

/**
 * QEMU setup of the application, looking for a valid QEMU installation.
 * This could also be a fresh install.
 * The values of this setup are stored for the settings dialog.
 */
public class QemuSetup {
	
	private static final String QEMU_IMG = "qemu-img";			// qemu image command
	
	private String cmdOutput;
	private String qemuImg;
	private String qemuImgVersion;
	private ArrayList<String> qemuCmdList;
	private ArrayList<String> versionList;
	private MainView mainView;
	private boolean isRunnigApp;

	/**
	 * Deny external construction, use run().
	 * 
	 * @param mainView 
	 * @param isRunnigApp		true, if called from a running application, 
	 * 							false if immediately after start
	 */
	private QemuSetup(MainView mainView, boolean isRunnigApp) {

		this.mainView = mainView;
		this.isRunnigApp = isRunnigApp;
	}

	/**
	 * Check for an installed QEMU (command).
	 * The resulting command output, if any, is stored in cmdOutput
	 * 
	 * @param cmd		the command/path for a QEMU program
	 * @return true, if running the command works, false otherwise 
	 */
	private boolean checkQemuCommandFor(String cmd) {
		
		cmdOutput = null;
		ProcessExecutor procExec = null;
		try {
			Util.verbose("looking for QEMU, trying command '" + cmd + "'");
			Logger.info("looking for QEMU, trying command '" + cmd + "'");
			procExec = new ProcessExecutor(cmd, "--version");
			cmdOutput = procExec.getOutput();
			Logger.info("command output:\n" + procExec.getOutput());
			Util.verbose("QEMU -> output:\n" + cmdOutput);
			if (cmdOutput.toLowerCase().indexOf("qemu") >= 0 
					&& cmdOutput.toLowerCase().indexOf("version") >= 0) {
				// QEMU available
				Logger.info("qemu found: " + cmd);
			}
			return true;
		} catch (IOException e) {
			// QEMU program with this command does not exist
			Util.verbose("QEMU not found: " + e.getMessage());
			Logger.error("QEMU '" + cmd + "' not found: " + e.getMessage());
			return false;
		} catch (Exception e) {
			// QEMU program  with this command raises another Exception
			Util.verbose("QEMU not found: " + e.getMessage());
			Logger.error("QemuSetup", e);
			Logger.error("QEMU '" + cmd + "' not found");
			return false;
		}
	}

	/**
	 * Check for QEMU installation in a directory.
	 * The results of this check will be set in variables.
	 * 
	 * @param directory		the QEMU installation directory, it may be empty ("")
	 */
	public void checkQemuInstallation(String directory) {
		
		AppProperties properties = Main.getProperties();
		// search for a QEMU installation
		
		// TODO xxx    QemuSetup je nach OS? Windows + andere testen Suche (+ flavor wie Arch, Ubuntu, Suse, Windows) Suche starten 

		// need qemu-img to create VM disks
		qemuImgVersion = null;
		if (checkQemuCommandFor(directory + QEMU_IMG)) {
			qemuImg = directory + QEMU_IMG;
			qemuImgVersion = scanVersion(cmdOutput);
			properties.setProperty(AppProperties.QEMU_IMG, qemuImg);
		}
		// look for emulators
		qemuCmdList = new ArrayList<>();
		versionList = new ArrayList<>();
		for (Architecture arch : Architecture.ARRAY) {
			if (checkQemuCommandFor(directory + arch.getQemuCmd())) {
				qemuCmdList.add(directory + arch.getQemuCmd());
				versionList.add(scanVersion(cmdOutput));
			}
		}
		for (int i = 0; i < qemuCmdList.size(); i++) {
			properties.setProperty(AppProperties.QEMU_CMD + i, qemuCmdList.get(i));
		}
	}

	/**
	 * First setup, usually a fresh installation, 
	 * no configuration available.
	 * 
	 * @param mainView
	 */
	private void firstSetup(MainView mainView) {
		
		Logger.info("first setup ...");
		String qemuDir = "";
		if (OSType.isWindows()) {
			// Linux/Unix will find QEMU using the path variable, but Windows users may not have set them
			// QEMU will install itself on Windows usually here:
			qemuDir = System.getenv("ProgramFiles")
					+ File.separator + "qemu" + File.separator;
		}
		checkQemuInstallation(qemuDir);
		new SettingsDlg(true, mainView, qemuImg, qemuImgVersion, qemuCmdList, versionList);
		MainView.setHint(Msg.get(FIRST_VM_HINT_MSG));
	}

	/**
	 * @return the qemuCmdList
	 */
	public ArrayList<String> getQemuCmdList() {
		
		return qemuCmdList;
	}

	/**
	 * @return the qemuCmdOutput
	 */
	public String getQemuCmdOutput() {
		
		return cmdOutput;
	}
	
	/**
	 * @return the qemuImg
	 */
	public String getQemuImg() {
		
		return qemuImg;
	}

	/**
	 * @return the qemuImgVersion
	 */
	public String getQemuImgVersion() {
		
		return qemuImgVersion;
	}

	/**
	 * @return the versionList
	 */
	public ArrayList<String> getVersionList() {
		
		return versionList;
	}
	
	/**
	 * Execute the setup, test for a valid QEMU installation, use the setup wizard if first start.
	 * 
	 * @param mainView 
	 * @param isRunnigApp		true, if called from a running application, 
	 * 							false if immediately after start
	 */
	public static void run(MainView mainView, boolean isRunnigApp) {

		SwingUtilities.invokeLater(() -> {
			try {
				QemuSetup setup = new QemuSetup(mainView, isRunnigApp);
				Main.setQemuSetup(setup);
				setup.start();
			} catch (Exception e) {
				Logger.error("Unexpected exception, exit", e);
			}
		});
	}
	
	/**
	 * Return the version from the output of a version query.
	 * 
	 * @param output
	 * @return the output
	 */
	private String scanVersion(String output) {
		
		String version = "";
		int i =	cmdOutput.toLowerCase().indexOf("version");
		try {
			Scanner scanner = new Scanner(cmdOutput.substring(i));
			version += scanner.next() + " ";
			version += scanner.next();
		} catch (Exception e) {	}		// intentionally do nothing
		return version;
	}

	/**
	 * Execute the setup, test for a valid QEMU installation, use the setup wizard if 
	 * it is the first start.
	 */
	private void start() {

		if (Main.isFirstStart()) {
			// first setup, usually a fresh installation, run the qemu setup wizard
			firstSetup(mainView);
			Main.setFirstStart(false);
		} else {
			// not the first start
			AppProperties properties = Main.getProperties();
			int qemuCount = 10;
			// check QEMU installation
			boolean isInstalled = true;
			qemuCmdList = new ArrayList<>();
			versionList = new ArrayList<>();
			qemuImg = properties.getProperty(AppProperties.QEMU_IMG);
			if (checkQemuCommandFor(qemuImg)) {
				qemuImgVersion = scanVersion(cmdOutput);
			} else {
				qemuImg = null;
				isInstalled = false;
			}
			for (int i = 0; i < qemuCount; i++) {
				String cmd = properties.getProperty(AppProperties.QEMU_CMD + i);
				if (cmd == null || cmd.trim().equals("")) {
					break;
				}
				if (!checkQemuCommandFor(cmd)) {
					isInstalled = false;
				}
				qemuCmdList.add(cmd);
				versionList.add(scanVersion(cmdOutput));
			}
			if (!isInstalled) {
				// something went wrong with the installation, clean & do first setup
				properties.setProperty(AppProperties.QEMU_IMG, "");
				properties.remove(AppProperties.QEMU_IMG);
				for (int i = 0; i < qemuCount; i++) {
					properties.setProperty(AppProperties.QEMU_CMD + i,"");
					properties.remove(AppProperties.QEMU_CMD + i);
				}
				firstSetup(mainView);
				return;
			}
			// installation is ok, do nothing (just the main view)
		}
		Logger.flushIfDirty();
	}
}



