
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
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import qemujuicy.*;
import qemujuicy.vm.*;

import static qemujuicy.Images.*;
import static qemujuicy.Message.*;
import static qemujuicy.AppProperties.*;

/**
 * A JDialog for general settings of the application.
 * At the first start of the application (no configuration file, no QEMU path, etc),
 * this Dialog changes to wizard mode, with different button panel (back/next-finished/cancel)
 * and another behaviour (toolbar buttons disabled to stay in wizard mode).
 */
public class SettingsDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static enum ActivePnl {						// used as a state machine
		SETUP_PNL, QEMU_INSTALL_PNL, GENERAL_SETTINGS_PNL, FOLDERS_PNL, FINISHED_PNL
	}
	
	public final static int PIXELS_BTN = 28;			// images will be scaled down to # of pixels
	public final static float TITLE_FONT_SIZE = 16f;

	public final static String SETUP_WIZARD = "SetupWizard";
	public final static String QEMU = "Qemu";
	public final static String GENERAL_SETTINGS = "GeneralSettings";
	public final static String FOLDERS = "Folders";
	public final static String EXIT = "ExitFromSetup";
	
	public final static String ENGLISH = "English";

	private JToolBar toolBar;
	private JButton toolBarBtnWizard;
	private JButton toolBarBtnQemu;
	// panels to switch
	private ActivePnl activePnl;						// state machine var
	private JPanel mainPnl;
	private JPanel wizardExplanationPnl;
	private JPanel qemuInstallationPnl;
	private JPanel settingsPnl;
	private JPanel foldersPnl;
	private JPanel finishedPnl;
	// qemu installation panel
	private JLabel qemuFoundLbl;
	private JLabel qemuImgFoundLbl ;
	private JLabel qemuImgVersionLbl;
	private JComboBox<String> qemuCmdCbx ;
	private JLabel qemuVersionLbl;
	// wizard buttons
	private WizardButtonPanel wizardButtonPnl;
	private JButton backBtn;
	private JButton exitBtn;
	private JButton nextBtn;
	// settings buttons
	private JButton okBtn;
	private JButton applyBtn;

	private JTextField vmDirTxt;
	private boolean wizardMode;
	private String qemuImgCmd;
	private String qemuImgVersion;
	private ArrayList<String> qemuCmdList;
	private ArrayList<String> versionList;
	private HashMap<String, String> changedItems = new HashMap<>();

	/**
	 * Create the dialog and show it, either as a wizard or a usual settings dialog.
	 * 
	 * @param wizardMode		true if the settings dialog is started in 
	 * 							wizard mode (back/next-finish/cancel) or in settings 
	 * 							dialog mode (ok/apply/cancel)
	 * @param mainView			the parent view
	 * @param qemuImgCmd		qemu-img
	 * @param qemuImgVersion
	 * @param qemuCmdList		list of commands for the emulators
	 * @param versionList
	 */
	public SettingsDlg(boolean wizardMode, MainView mainView, String qemuImgCmd, String qemuImgVersion,
			ArrayList<String> qemuCmdList, ArrayList<String> versionList) {
	
		this.wizardMode = wizardMode;
		this.qemuImgCmd = qemuImgCmd;
		this.qemuImgVersion = qemuImgVersion;
		this.qemuCmdList = qemuCmdList;
		this.versionList = versionList;
		setTitle(Msg.get(SETTINGS_BTN_MSG));
		setIconImage(Images.get(APP_ICON).getImage());
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// create a panel for the components
		mainPnl = new JPanel(new GridBagLayout());		// for dynamic resizing
		mainPnl.setBorder(new EmptyBorder(11, 10, 6, 10));
		mainPnl.setPreferredSize(new Dimension(600, 500));
		getContentPane().add(mainPnl, BorderLayout.CENTER);
		toolBar = createToolBar();
		getContentPane().add(toolBar, BorderLayout.WEST);
		// create all panels within the right area, layout is done dynamically in actionPerformed()
		if (wizardMode) {
			wizardExplanationPnl = createWizardExplanationPanel();
		}
		qemuInstallationPnl = createQemuInstallationPanel();
		settingsPnl = createSettingsPnl();
		foldersPnl = createFoldersPnl();
		finishedPnl = createFinishedPnl();
		// button panel for dialog behavior (does nothing in wizard mode)
		JPanel dialogButtonPnl = new JPanel(new GridBagLayout());
		getContentPane().add(dialogButtonPnl, BorderLayout.SOUTH);
		dialogButtonPnl.add(Gbc.filler(), new Gbc(0, 0, 1, 1, 10.0, 0, "S B"));
		// button Ok  (does nothing in wizard mode)
		okBtn = CompFactory.createOkButton();
		dialogButtonPnl.add(okBtn, new Gbc(1, 0));
		okBtn.addActionListener(e-> {
			storeChangedItems();
			dispose();
		});
		// button Apply  (does nothing in wizard mode)
		applyBtn = CompFactory.createApplyButton();
		dialogButtonPnl.add(applyBtn, new Gbc(2, 0));
		applyBtn.addActionListener(e-> {
			storeChangedItems();
		});
		// button Cancel  (does nothing in wizard mode)
		JButton cancelBtn = CompFactory.createCancelButton();
		dialogButtonPnl.add(cancelBtn, new Gbc(3, 0));
		cancelBtn.addActionListener(e-> {
			dispose();
		});
		// button panel for wizard behavior 
		if (wizardMode) {
			// trigger the setup wizard toolbar button
			SwingUtilities.invokeLater(() -> toolBarBtnWizard.doClick());
			wizardButtonPnl = new WizardButtonPanel(this, this);
			getContentPane().add(wizardButtonPnl, BorderLayout.SOUTH);
			backBtn = wizardButtonPnl.getBackBtn();
			nextBtn = wizardButtonPnl.getNextBtn();
			// if in first setup wizard mode, change cancel to exit 
			exitBtn = wizardButtonPnl.getCancelBtn();
			exitBtn.setText(Msg.get(EXIT_BTN_MSG));
			exitBtn.setIcon(Images.scale(Images.EXIT_BUTTON, Gui.BUTTON_ICON_SIZE));
			exitBtn.setActionCommand(EXIT);
		} else {
			// trigger the first toolbar button
			SwingUtilities.invokeLater(() -> toolBarBtnQemu.doClick());
			getContentPane().add(dialogButtonPnl, BorderLayout.SOUTH);
		}
		// if we are called from another caller than setup, we need to look for an existing QEMU
		pack();
		Gui.center(this);
		setVisible(true);
	}

	/**
	 * Action queue dispatcher.
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCmd = event.getActionCommand();
		if (wizardMode) {
			// do not react on button clicks, only react on back/next-finish/exit(cancel)
			if (actionCmd.equals(EXIT)) {
				// exit without storing anything, QEMU might not be installed
		 		Logger.info(Main.APP_NAME + ": exit from setup wizard without finish");
				Logger.close();
				Util.verbose(Main.APP_NAME + ", bye.");
		        System.exit(0);
			} else if (actionCmd.equals(SETUP_WIZARD)) {
				// this may be the action of first start doClick()
				display(wizardExplanationPnl);
				activePnl = ActivePnl.SETUP_PNL;
				backBtn.setEnabled(false);
			} else if (actionCmd.equals(WizardButtonPanel.BACK_BTN)) {
				// simple state machine
				switch (activePnl) {
				case QEMU_INSTALL_PNL: 
					backBtn.setEnabled(false);
					display(wizardExplanationPnl);
					activePnl = ActivePnl.SETUP_PNL;
					break;
				case GENERAL_SETTINGS_PNL: 
					displayQemuInstallation();
					activePnl = ActivePnl.QEMU_INSTALL_PNL;
					break;
				case FOLDERS_PNL: 
					display(settingsPnl);
					activePnl = ActivePnl.GENERAL_SETTINGS_PNL;
					break;
				case FINISHED_PNL: 
					display(foldersPnl);
					wizardButtonPnl.changeToFinishedBtn(false);
					activePnl = ActivePnl.FOLDERS_PNL;
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + activePnl);
				}
			} else if (actionCmd.equals(WizardButtonPanel.NEXT_AND_FINISH_BTN)) {
				switch (activePnl) {
				case SETUP_PNL: 		
					backBtn.setEnabled(true);
					displayQemuInstallation();
					activePnl = ActivePnl.QEMU_INSTALL_PNL;
					break;
				case QEMU_INSTALL_PNL: 
					display(settingsPnl);
					activePnl = ActivePnl.GENERAL_SETTINGS_PNL;
					break;
				case GENERAL_SETTINGS_PNL: 
					display(foldersPnl);
					activePnl = ActivePnl.FOLDERS_PNL;
					break;
				case FOLDERS_PNL: 
					display(finishedPnl);
					wizardButtonPnl.changeToFinishedBtn(true);
					activePnl = ActivePnl.FINISHED_PNL;
					break;
				case FINISHED_PNL: 
					storeChangedItems();
					dispose();
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + activePnl);
				}
			}
			return;
		}
		// not in wizard mode (settings was called, may be from menu)
		if (actionCmd.equals(QEMU)) {
			displayQemuInstallation();
		} else if (actionCmd.equals(GENERAL_SETTINGS)) {
			display(settingsPnl);
		} else if (actionCmd.equals(FOLDERS)) {
			display(foldersPnl);
        } else {
        	String missing = "ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName() 
            		+ ": " + actionCmd;
            System.out.println(missing);
            Logger.error(missing);
		}
	}

	/**
	 * Create the settings dialog from an existing and working qemu installation.
	 * 
	 * @param mainView
	 */
	public static void create(MainView mainView) {
		
		QemuSetup setup = Main.getQemuSetup();
		new SettingsDlg(false, mainView, setup.getQemuImg(), setup.getQemuImgVersion(), 
				setup.getQemuCmdList(), setup.getVersionList());
	}

	/**
	 * Creates the setup finished panel.
	 * 
	 * @return the panel
	 */
	private JPanel createFinishedPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(QEMUJUICY_SETUP_FINISH_WIZ_MSG), Images.get(APPLY_BUTTON));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: hard disk
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_FINISH_ALL_OK_HEADER_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// notes / hints
		label = new JLabel(Msg.get(SETUP_WIZ_FINISH_PRESS_WIZ_MSG));
		panel.add(label, new Gbc(1, row, 6, 1, 0, 0, "W H"));
		row++;
		label = new JLabel(Msg.get(SETUP_WIZ_NOTE_WIZ_MSG));
		panel.add(label, new Gbc(1, row, 6, 1, 0, 0, "W H"));
		row++;
		return panel;
	}

	/**
	 * Creates the folders panel.
	 * 
	 * @return the folders panel
	 */
	private JPanel createFoldersPnl() {

		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(CONF_FILES_TT_MSG), Images.get(FOLDER));
		int row = 1;		// due to createTemplateSettingsPnl() start with 1
		int pathTextFieldLength = 280;
		// app dir
		JLabel label = new JLabel(Msg.get(APP_DIR_MSG) + ":", SwingConstants.RIGHT);
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		JTextField homeDirTxt = new JTextField(Files.getAppDirPath());
		panel.add(homeDirTxt, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		homeDirTxt.setEditable(false);
		homeDirTxt.setBackground(Gui.TEXT_INACTIVE_BACKGROUND);
		Gui.setComponentWidth(homeDirTxt, pathTextFieldLength);
		row++;
		// VM dir
		label = new JLabel(Msg.get(VM_DIR_MSG) + ":", SwingConstants.RIGHT);
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		String vmDisksPath = Main.getProperty(AppProperties.VM_DISK_PATH);
		if (vmDisksPath.trim().equals("")) {
			vmDisksPath = Files.getAppDirPath() + Main.VM_DISKS_DIR;
			Main.getProperties().setProperty(AppProperties.VM_DISK_PATH, vmDisksPath);		// may not exist, due to exit during setup
			Files.setVmDiskDirPath(vmDisksPath);
		}
		vmDirTxt = new JTextField(vmDisksPath);
		panel.add(vmDirTxt, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		vmDirTxt.setEditable(false);
		vmDirTxt.setBackground(Gui.TEXT_INACTIVE_BACKGROUND);
		Gui.setComponentWidth(vmDirTxt, pathTextFieldLength);
		JButton fileSelBtn = new JButton(Images.scale(Images.FOLDER, 16));
		panel.add(fileSelBtn, new Gbc(2, row, 1, 1, 0, 0, "W H"));
		fileSelBtn.setToolTipText(Msg.get(SELECT_DIR_MSG));
		fileSelBtn.addActionListener(event -> {
			FileChooserDlg chooser = new FileChooserDlg(
					Msg.get(SELECT_DIR_MSG), 
					Msg.get(OK_BTN_MSG), 
					Msg.get(OK_BTN_MSG), 
					JFileChooser.DIRECTORIES_ONLY, 
					null);
	        if (chooser.showOpenDialog(SettingsDlg.this) == JFileChooser.APPROVE_OPTION) {
	        	String vmDiskPath = chooser.getSelectedFile().getAbsolutePath();
	        	Util.verbose("VM disks path selected: " + vmDiskPath);
	            Logger.info("VM disks path selected: " + vmDiskPath);
	            vmDirTxt.setText(vmDiskPath);
	            changedItems.put(VM_DISK_PATH, vmDiskPath);
				Files.setVmDiskDirPath(vmDiskPath);
				try {
					Files.ensureVmDisksDir();
				} catch (IOException e) {
					Main.exitOnException(e);
				}
	        }
		});
		row++;
		return panel;
	}

	/**
	 * Creates the settings panel.
	 * 
	 * @return the settings panel
	 */
	private JPanel createSettingsPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(CONF_GENERAL_SETTINGS_TT_MSG), Images.get(CONFIG));
		int row = 1;		// due to createTemplateSettingsPnl() start with 1
		JLabel label = CompFactory.createChapterLabel("QemuJuicy");
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// language
		label = new JLabel("Language:", SwingConstants.RIGHT);
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
	    String[] languages = { 
		    	ENGLISH,
		    	};
		JComboBox<String> languageCbx = new JComboBox<String>(languages);
		panel.add(languageCbx, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		languageCbx.setPreferredSize(new Dimension(100, Gui.DEFAULT_BTN_HEIGHT));
		languageCbx.setMaximumRowCount(5);
		languageCbx.addActionListener(
				
				// TODO xxx    SettingsDlg  languageCbx.addActionListener  language changed

				e -> Util.verbose(languageCbx.getSelectedIndex() + ": "
                + languageCbx.getSelectedItem()));
		row++;
		// hints
		JCheckBox hintsChk = new JCheckBox(Msg.get(HINTS_MSG));
		panel.add(hintsChk, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		hintsChk.setSelected(Main.getPropertyBool(GIVE_HINTS));
		hintsChk.addActionListener(e -> changedItems.put(GIVE_HINTS, "" + hintsChk.isSelected()));
		row++;
		// VM
		label = CompFactory.createChapterLabel("Virtual machine");
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// CPUs
		label = new JLabel("Default CPUs:", SwingConstants.RIGHT);
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		JComboBox<String> cpusCbx = new JComboBox<String>(Cpu.getCpuArraySmall());
		panel.add(cpusCbx, new Gbc(1, row, 1, 1, 0, 0, "W"));
		cpusCbx.setPreferredSize(new Dimension(60, Gui.DEFAULT_BTN_HEIGHT));
		((JLabel) cpusCbx.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		cpusCbx.setMaximumRowCount(14);
		cpusCbx.setSelectedItem(Main.getPropertyInt(DEFAULT_CPUS));
		cpusCbx.addActionListener(e -> changedItems.put(DEFAULT_CPUS, (String) cpusCbx.getSelectedItem()));
		row++;
		// memory
		int memory = Main.getPropertyInt(DEFAULT_MEM);
		JLabel memoryLbl = new JLabel(Msg.get(MEMORY_DEFAULT_MSG, memory), SwingConstants.RIGHT);
		panel.add(memoryLbl, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		JLabel dummy = new JLabel(Msg.get(MEMORY_DEFAULT_MSG, 900000), SwingConstants.RIGHT);
		memoryLbl.setPreferredSize(dummy.getPreferredSize());	// save some space, the label extends with the amount of memory
		JSlider memorySld = new JSlider();
		panel.add(memorySld, new Gbc(1, row, 3, 1, 0, 0, "W"));
		memorySld.setPreferredSize(new Dimension(300, 50));
		memorySld.setMinimum(0);			// min/max values
		memorySld.setMaximum(8000);
		// distance of markers
		memorySld.setMajorTickSpacing(2000);
		memorySld.setMinorTickSpacing(200);
		memorySld.setSnapToTicks(true);
		memorySld.createStandardLabels(1);
		memorySld.setPaintTicks(true);
		memorySld.setPaintLabels(true);
		memorySld.setValue(memory);
		memorySld.addChangeListener(e -> {
			int mem = memorySld.getValue();
			changedItems.put(DEFAULT_MEM, "" + mem);
			memoryLbl.setText(Msg.get(MEMORY_DEFAULT_MSG, mem));
			});
		row++;
		return panel;
	}

	/**
	 * Creates the toolbar for this view.
	 * 
	 * @return the JToolBar
	 */
	protected JToolBar createToolBar() {
		
		JToolBar tb = Gui.createDefaultToolBar();
		int pixels = PIXELS_BTN;		// images will be scaled down to # of pixels
		if (wizardMode) {
			toolBarBtnWizard = createToolBarButton(null, Images.scale(Images.FIRST_SETUP_WIZARD_BUTTON, pixels), 
					0, Msg.get(SETUP_WIZARD_TT_MSG), SETUP_WIZARD);
			tb.add(toolBarBtnWizard);
		}
		toolBarBtnQemu = createToolBarButton(null, Images.scale(Images.QEMU28x28, pixels), 
				0, Msg.get(QEMU_INSTALLATION_TT_MSG), QEMU);
		tb.add(toolBarBtnQemu);
		JButton button = createToolBarButton(null, Images.scale(Images.CONFIG, pixels), 
				0, Msg.get(CONF_GENERAL_SETTINGS_TT_MSG), GENERAL_SETTINGS);
		tb.add(button);
		button = createToolBarButton(null, Images.scale(Images.FOLDER, pixels), 
				0, Msg.get(CONF_FILES_TT_MSG), FOLDERS);
		tb.add(button);
		return tb;
	}
	
	/**
	 * Creates a toolbar button.
	 * 
	 * @param label					the label of the button
	 * @param icon					the icon of the button or <code>null</code>
	 * @param mnemonic				the mnemonic of the button
	 * @param toolTip				the toolTip of the button
	 * @param actionCommand			the action command of the button
	 * @return
	 */
	protected JButton createToolBarButton(String label, ImageIcon icon, int mnemonic,
		String toolTip, String actionCommand) {
		
		JButton button = new JButton(label);
		if (icon != null) {
			button.setIcon(icon);
		}
		if (mnemonic != 0) {
			button.setMnemonic(mnemonic);
		}
		button.setMargin(Gui.DEFAULT_TOOLBAR_BTN_INSETS);
	    button.setToolTipText(toolTip);
		button.setActionCommand(actionCommand);
	    button.addActionListener(this);
		return button;
	}

	/**
	 * Creates the wizard panel as first panel, containing an explanation about the setup.
	 * 
	 * @return the wizard panel
	 */
	private JPanel createQemuInstallationPanel() {

		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(QEMU_INSTALL_SETTINGS_MSG), Images.get(QEMU32x32));
		int row = 1;		// due to createTemplateSettingsPnl() QEMU_INSTALL_OK_WIZ_MSG with 1
		qemuFoundLbl = CompFactory.createChapterLabel("");
		panel.add(qemuFoundLbl, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// installation qemu-img
		qemuImgFoundLbl = new JLabel("");
		panel.add(qemuImgFoundLbl, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		qemuImgVersionLbl = new JLabel("");
		panel.add(qemuImgVersionLbl, new Gbc(2, row, 1, 1, 0, 0, "W H"));
		row++;
		// installation emulator(s)
		qemuCmdCbx = new JComboBox<String>();
		panel.add(qemuCmdCbx, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		qemuCmdCbx.setPreferredSize(new Dimension(150, Gui.DEFAULT_BTN_HEIGHT));
		qemuCmdCbx.setMaximumRowCount(12);
		qemuCmdCbx.addItemListener((e) -> {
			if (qemuCmdCbx.getSelectedIndex() >= 0) {
				qemuVersionLbl.setText(" -> " + versionList.get(qemuCmdCbx.getSelectedIndex()));
			}
		});
		qemuVersionLbl = new JLabel("");
		panel.add(qemuVersionLbl, new Gbc(2, row, 1, 1, 0, 0, "W H"));
		row++;
		// empty line
		JLabel label = new JLabel("");
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		label = new JLabel("");
		panel.add(label, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// qemu installation dir
		label = CompFactory.createChapterLabel(Msg.get(SELECT_QEMU_INSTALL_DIR_LBL_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		JButton fileSelBtn = new JButton(Images.scale(Images.FOLDER, Gui.BUTTON_FILE_CHOOSER_SIZE));
		panel.add(fileSelBtn, new Gbc(2, row, 1, 1, 0, 0, "C"));
		Gui.setComponentWidth(fileSelBtn, Gui.DEFAULT_BTN_WIDTH);
		fileSelBtn.setToolTipText(Msg.get(SELECT_QEMU_DLG_TITLE_MSG));
		fileSelBtn.addActionListener(e -> {
			FileChooserDlg chooser = new FileChooserDlg(
					Msg.get(SELECT_QEMU_DLG_TITLE_MSG), 
					Msg.get(OPEN_BTN_MSG), 
					Msg.get(OPEN_BTN_MSG), 
					JFileChooser.DIRECTORIES_ONLY, 
					null);
	        if (chooser.showOpenDialog(SettingsDlg.this) == JFileChooser.APPROVE_OPTION) {
	        	String directory = chooser.getSelectedFile().getAbsolutePath();
	        	Util.verbose("QEMU directory selected: " + directory);
	            Logger.info("QEMU directory selected: " + directory);
	            // test if this a valid QEMU installation directory
	            QemuSetup setup = Main.getQemuSetup();
	            setup.checkQemuInstallation(directory + File.separator);
	            qemuImgVersion = setup.getQemuImgVersion();
	            qemuImgCmd = setup.getQemuImg();
	            qemuCmdList = setup.getQemuCmdList();
	            versionList = setup.getVersionList();
	    		if (qemuImgVersion != null && qemuCmdList.size() > 0) {
					changedItems.put(QEMU_IMG, qemuImgCmd);
					for (int i = 0; i < qemuCmdList.size(); i++) {
						changedItems.put(QEMU_CMD + i, qemuCmdList.get(i));
					}
	    		}
	            displayQemuInstallation();
	        }
		});
		row++;
		return panel;
	}

	/**
	 * Display the installation status of QEMU.
	 */
	public void displayQemuInstallation() {
		
		display(qemuInstallationPnl);
		boolean isQemuInstallationOk = false;
		if (qemuImgVersion == null || qemuCmdList.size() == 0) {
			// installation not found or incomplete
			qemuFoundLbl.setText(Msg.get(QEMU_INSTALL_FAIL_WIZ_MSG));
		} else {
			isQemuInstallationOk = true;
			qemuFoundLbl.setText(Msg.get(QEMU_INSTALL_OK_WIZ_MSG));
		}
		if (wizardMode) {
			nextBtn.setEnabled(isQemuInstallationOk);			// might be a missing QEMU installation
		} else {
			okBtn.setEnabled(isQemuInstallationOk);				// might be a missing QEMU installation
			applyBtn.setEnabled(isQemuInstallationOk);
		}
		// qemu-img
		if (qemuImgVersion == null) {
			qemuImgFoundLbl.setText(Msg.get(QEMU_INSTALL_FAIL_DISK_WIZ_MSG));
			qemuImgVersionLbl.setText("");
		} else {
			qemuImgFoundLbl.setText(Msg.get(QEMU_INSTALL_DISK_WIZ_MSG));
			qemuImgVersionLbl.setText(" -> " + qemuImgVersion);
		}
		// emulator(s)
		if (qemuCmdList.size() == 0) {
			qemuCmdCbx.removeAllItems();
			qemuVersionLbl.setText("");
		} else {
			qemuCmdCbx.removeAllItems();
			for (String qemuCmd : qemuCmdList) {
				qemuCmdCbx.addItem(qemuCmd);
			}
			qemuCmdCbx.setSelectedIndex(0);
			qemuVersionLbl.setText(" -> " + versionList.get(0));
		}
		mainPnl.revalidate();
		mainPnl.repaint();
	}

	/**
	 * Creates the wizard panel as first panel, containing an explanation about the setup.
	 * 
	 * @return the wizard panel
	 */
	private JPanel createWizardExplanationPanel() {

		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(SETUP_WIZ_MSG), Images.get(Images.SETUP_WIZARD));
		int row = 1;		// due to createTemplateSettingsPnl() start with 1
		JLabel label = CompFactory.createChapterLabel(Msg.get(QEMU_INSTALL_WIZ_MSG));
		if (qemuImgVersion == null || qemuCmdList.size() == 0 ) {
			// installation not found or incomplete
			label = new JLabel(Msg.get(QEMU_INSTALL_FAIL_WIZ_MSG));
		}
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// installation
		if (qemuImgVersion != null) {
			label = new JLabel(Msg.get(QEMU_INSTALL_DISK_WIZ_MSG));
		} else {
			label = new JLabel(Msg.get(QEMU_INSTALL_FAIL_DISK_WIZ_MSG));
		}
		panel.add(label, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		row++;
		if (qemuCmdList.size() > 0) {
			label = new JLabel(Msg.get(QEMU_INSTALL_EMULATOR_WIZ_MSG));
		} else {
			label = new JLabel(Msg.get(QEMU_INSTALL_FAIL_EMULATOR_WIZ_MSG));
		}
		panel.add(label, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		row++;
		// hint
		label = new JLabel("");
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		label = CompFactory.createChapterLabel(Msg.get(QEMU_INSTALL_HINT_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		return panel;
	}

	/**
	 * Display a selected panel.
	 * 
	 * @param panel
	 */
	public void display(JPanel panel) {
		
		mainPnl.removeAll();
		mainPnl.add(panel, new Gbc(0, 0, 1, 1, 100.0, 0,"N H"));
		mainPnl.add(Gbc.filler(), new Gbc(0, 1, 1, 1, 0, 100.0, "S V"));		// to squeeze the above to the top
		mainPnl.revalidate();
		mainPnl.repaint();
	}

	/**
	 * Store any changed items/values as a property.
	 */
	private void storeChangedItems() {
		 
		AppProperties properties = Main.getProperties();
		// create the VM directory if not existing
		File vmDir = new File(vmDirTxt.getText());
		vmDir.mkdirs();
		// store changed items
		if (SettingsDlg.this.changedItems.size() > 0) {
			for (Map.Entry<String, String> entry : SettingsDlg.this.changedItems.entrySet()) {
				Util.verbose(entry.getKey() + " -> " + entry.getValue());
			    properties.setProperty(entry.getKey(), entry.getValue());
			}
			changedItems.clear();
		}
		properties.storeToXML();
	}
}
