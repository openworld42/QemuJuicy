
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

import javax.swing.*;
import javax.swing.border.*;

import qemujuicy.*;
import qemujuicy.vm.*;

import static qemujuicy.AppProperties.*;
import static qemujuicy.Message.*;
import static qemujuicy.vm.VMProperties.*;

/**
 * A wizard to create a new VM.
 */
public class VMWizard extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static enum ActivePnl {						// used as a state machine
		CREATE_PNL, IDENT_PNL, DISK_PNL, NETWORK_PNL, FINISHED_PNL
	}
	
	public final static int PIXELS_BTN = 28;			// images will be scaled down to # of pixels
	public final static int PIXELS_OS_ICON = 32;		// images will be scaled down to # of pixels
	public final static float TITLE_FONT_SIZE = 16f;

	// panels to switch
	private ActivePnl activePnl = ActivePnl.CREATE_PNL;	// state machine var
	private JPanel mainPnl;
	private JPanel vmCreationPnl;
	private JPanel vmIdentificationPnl;
	private JPanel vmDiskPnl;
	private JPanel vmNetworkPnl;
	private JPanel vmFinishedPnl;
	// wizard buttons
	private WizardButtonPanel buttonPnl;
	private JButton backBtn;
//	private JButton nextBtn;
	
	private JRadioButton typicalCreationRBt;
	private JComboBox<String> osCbx;
	private JTextField nameTxt;
	private JLabel osIconLbl;
	private JSlider diskSizeSld;
	private JRadioButton netConnectedRBt;
	private Icon osIcon;
	private String vmIconPath;
	private int hardDiskSize;
	
	private VM.OSType osType;

	private VMProperties vmProperties;

	/**
	 * A wizard to create a VM.
	 * Create the dialog and show it.
	 */
	public VMWizard() {

		setTitle(Msg.get(CREATE_VM_WIZ_MSG));
		setIconImage(Images.get(Images.VM_WIZARD_WAND).getImage());
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// create a panel for the components
		mainPnl = new JPanel(new BorderLayout());		// for dynamic resizing
		mainPnl.setBorder(new EmptyBorder(11, 10, 6, 10));
		mainPnl.setPreferredSize(new Dimension(600, 500));
		getContentPane().add(mainPnl, BorderLayout.CENTER);
		// create all panels, layout is done dynamically in actionPerformed(), only the first panel is added
		vmCreationPnl = createCreateVMPnl();
		mainPnl.add(vmCreationPnl, BorderLayout.CENTER);
		vmIdentificationPnl = createVMIdentificationPnl();		// we start with first panel, no add now
		vmDiskPnl = createVMDiskPnl();							// we start with first panel, no add now
		vmNetworkPnl = createVMNetworkPnl();					// we start with first panel, no add now
		vmFinishedPnl = createVMCreationFinishedPnl();			// we start with first panel, no add now
		// buttons 
		buttonPnl = new WizardButtonPanel(this, this);
		getContentPane().add(buttonPnl, BorderLayout.SOUTH);
		backBtn = buttonPnl.getBackBtn();
		// display dialog
		pack();
		Gui.center(this);
		setVisible(true);
	}

	/**
	 * Action queue dispatcher.
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCmd = event.getActionCommand();
		if (actionCmd.equals(WizardButtonPanel.CANCEL_BTN)) {
			dispose();
		} else if (actionCmd.equals(WizardButtonPanel.BACK_BTN)) {
			mainPnl.removeAll();
			// simple state machine
			switch (activePnl) {
			case CREATE_PNL: 			// this should never happen
				backBtn.setEnabled(false);
				mainPnl.add(vmCreationPnl, BorderLayout.CENTER);
				return;					
			case IDENT_PNL: 
				backBtn.setEnabled(false);
				mainPnl.add(vmCreationPnl, BorderLayout.CENTER);
				activePnl = ActivePnl.CREATE_PNL;
				break;
			case DISK_PNL: 
				mainPnl.add(vmIdentificationPnl, BorderLayout.CENTER);
				activePnl = ActivePnl.IDENT_PNL;
				break;
			case NETWORK_PNL: 
				mainPnl.add(vmDiskPnl, BorderLayout.CENTER);
				activePnl = ActivePnl.DISK_PNL;
				break;
			case FINISHED_PNL: 
				mainPnl.add(vmNetworkPnl, BorderLayout.CENTER);
				buttonPnl.changeToFinishedBtn(false);
				activePnl = ActivePnl.NETWORK_PNL;
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + activePnl);
			}
			// redraw actual panel
			mainPnl.revalidate();
			mainPnl.repaint();
		} else if (actionCmd.equals(WizardButtonPanel.NEXT_AND_FINISH_BTN)) {
			mainPnl.removeAll();
			// simple state machine
			switch (activePnl) {
			case CREATE_PNL: 		
				backBtn.setEnabled(true);
				mainPnl.add(vmIdentificationPnl, BorderLayout.CENTER);
				updateVMIdentificationPnl();		// set the OS icon according to OS
				activePnl = ActivePnl.IDENT_PNL;
				break;
			case IDENT_PNL: 
				// check if the name exists already
				String vmName = nameTxt.getText();
				if (Main.getVmManager().exists(vmName)) {
					Gui.errorDlg(this, Msg.get(VM_EXITS_ALREADY_MSG, vmName),
							Msg.get(ERROR_TITLE_DLG_MSG));
					mainPnl.add(vmIdentificationPnl, BorderLayout.CENTER);
					break;
				}
				mainPnl.add(vmDiskPnl, BorderLayout.CENTER);
				activePnl = ActivePnl.DISK_PNL;
				break;
			case DISK_PNL: 
				mainPnl.add(vmNetworkPnl, BorderLayout.CENTER);
				activePnl = ActivePnl.NETWORK_PNL;
				break;
			case NETWORK_PNL: 
				mainPnl.add(vmFinishedPnl, BorderLayout.CENTER);
				buttonPnl.changeToFinishedBtn(true);
				activePnl = ActivePnl.FINISHED_PNL;
				break;
			case FINISHED_PNL: 
				boolean ok = collectVmProperties();
				if (ok) {
					// error handling tests have been done in collectVmProperties()
					Main.getVmManager().createVM(vmProperties);
//					SwingUtilities.invokeLater(() -> Main.getVmManager().createVM(vmProperties));
					dispose();
				} else {
					mainPnl.add(vmFinishedPnl, BorderLayout.CENTER);
					buttonPnl.changeToFinishedBtn(true);
					activePnl = ActivePnl.FINISHED_PNL;
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + activePnl);
			}
			// redraw actual panel
			mainPnl.revalidate();
			mainPnl.repaint();
        } else {
        	String missing = "ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName() 
            		+ ": " + actionCmd;
            System.out.println(missing);
            Logger.error(missing);
		}
	}

	/**
	 * Collects the properties of the VM for creation.
	 * 
	 * @return true, if wizard passes all checks , false otherwise
	 */
	private boolean collectVmProperties() {
		
		AppProperties appProps = Main.getProperties();
		String vmName = nameTxt.getText();
		String vmDir = appProps.getProperty(AppProperties.VM_DISK_PATH);
		String vmNameSafe = vmName.replace(" ", "_");
		String vmFilename = vmNameSafe + ".xml";
		String vmFilePath = vmDir + File.separator + vmFilename;
		if (!testNewFile(vmFilePath)) {
			return false;
		}
		String vmDiskName = vmNameSafe + ".qcow2";
		String vmDiskPath = vmDir + File.separator + vmDiskName;
		if (!testNewFile(vmDiskPath)) {
			return false;
		}
		// add wizard properties
		try {
			vmProperties = new VMProperties(vmFilePath, false);
		} catch (Exception e) {
			// this should never happen
			Main.exitOnException(e);
		}
		// wizard properties
		vmProperties.setProperty(VM_NAME, vmName);
		vmProperties.setProperty(VM_NAME_SAFE, vmNameSafe);
		vmProperties.setProperty(VMProperties.VM_FILENAME, vmFilename);
		vmProperties.setProperty(DISK_NAME, vmDiskName);
		Check.ifTrue(typicalCreationRBt.isSelected());		// customized creation not implemented now
		vmProperties.setProperty(CREATION_TYPICAL, "" + typicalCreationRBt.isSelected());
		vmProperties.setProperty(OS, VM.OSType.values()[osCbx.getSelectedIndex()].name());
		vmProperties.setProperty(ICON_PATH, vmIconPath);
		vmProperties.setProperty(DISK_SIZE_GB, diskSizeSld.getValue());
		vmProperties.setProperty(NETWORK, "" + netConnectedRBt.isSelected());
		// add general setting properties
		vmProperties.setProperty(CPUS, appProps.getProperty(DEFAULT_CPUS));
		vmProperties.setProperty(VM_MEMORY_MB, appProps.getProperty(DEFAULT_MEM));

		// TODO xxx    VMWizard additional properties 
		
		Util.logProperties(this.getClass().getSimpleName() + ": creating new VM with properties:", vmProperties);
		vmProperties.storeToXML();
		return true;
	}

	/**
	 * Test the name for a new file (should not be existing, should be writable).
	 * 
	 * @param filePath		the file path to be tested
	 */
	private boolean testNewFile(String filePath) {
		// test the file path
		File vmFile = new File(filePath);
		try {
			if (vmFile.exists()) {
				Logger.error("A file '" + filePath + "' exists already");
				Gui.errorDlg(this, Msg.get(FILE_EXITS_ALREADY_MSG, vmFile.getAbsolutePath()),
						Msg.get(ERROR_TITLE_DLG_MSG));
				return false;
			}
			vmFile.createNewFile();
			vmFile.delete();			// this was just a test, other errors may follow
		} catch (IOException e) {
			Logger.error("Cannot create a VM with path '" + filePath + "'");
			Gui.errorDlg(this, 
					Msg.get(CANNOT_CREATE_OR_WRITE_TO_MSG, filePath), Msg.get(ERROR_TITLE_DLG_MSG));
			return false;
		}
		return true;
	}

	/**
	 * Creates the create VM panel (the first panel).
	 * 
	 * @return the panel
	 */
	private JPanel createCreateVMPnl() {
		
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(CREATE_VM_WIZ_MSG), Images.get(Images.VM_WIZARD_WAND));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: VM creation mode header
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_CREATION_MODE_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 3, 1, 0, 0, "W H"));
		row++;
		typicalCreationRBt= new JRadioButton(Msg.get(VM_MODE_TYPICAL_WIZ_MSG));
		panel.add(typicalCreationRBt, new Gbc(1, row, 3, 1, 0, 0, "W H"));
		row++;
		JRadioButton cumstomCreationRBt = new JRadioButton(Msg.get(VM_MODE_CUSTOMIZED_WIZ_MSG));
		
		// TODO xxx    VMWizard  implement custom
		
		cumstomCreationRBt.setEnabled(false);		
		panel.add(cumstomCreationRBt, new Gbc(1, row, 3, 1, 0, 0, "W H"));
		ButtonGroup group = new ButtonGroup();
		group.add(typicalCreationRBt);
		group.add(cumstomCreationRBt);
		typicalCreationRBt.setSelected(true);
		row++;
		// *** header:  VM type
		label = CompFactory.createChapterLabel(Msg.get(VM_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 3, 1, 0, 0, "W H"));
		row++;
		// OS
		label = new JLabel(Msg.get(OS_WIZ_MSG));
		panel.add(label, new Gbc(1, row, 1, 1, 0, 0, "W H"));
		osCbx = new JComboBox<String>(VM.OS_NAMES);
		panel.add(osCbx, new Gbc(2, row, 1, 1, 0, 0, "W"));
		osCbx.setPreferredSize(new Dimension(150, Gui.DEFAULT_BTN_HEIGHT));
		osCbx.setMaximumRowCount(15);
		osCbx.addActionListener(
				
				// TODO xxx    SettingsDlg  languageCbx.addActionListener  language changed

				e -> System.out.println(osCbx.getSelectedIndex() + ": "
						+ osCbx.getSelectedItem()));
		row++;
		return panel;
	}

	/**
	 * Creates the VM creation finished panel.
	 * 
	 * @return the panel
	 */
	private JPanel createVMCreationFinishedPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(VM_CREATION_FINISH_WIZ_MSG), Images.get(Images.APPLY_BUTTON));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: hard disk
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_FINISH_ALL_OK_HEADER_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// notes / hints
		label = new JLabel(Msg.get(VM_FINISH_PRESS_WIZ_MSG));
		panel.add(label, new Gbc(1, row, 6, 1, 0, 0, "W H"));
		row++;
		label = new JLabel(Msg.get(VM_FINISH_NOTE_WIZ_MSG));
		panel.add(label, new Gbc(1, row, 6, 1, 0, 0, "W H"));
		row++;
		row++;
		return panel;
	}

	/**
	 * Creates the VM disk panel.
	 * 
	 * @return the panel
	 */
	private JPanel createVMDiskPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(VM_HARDDISK_WIZ_MSG), Images.get(Images.HARDDISK));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: hard disk
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_HARDDISK_HEADER_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// notes / hints
		label = new JLabel(Msg.get(VM_HARDDISK_NOTE_1_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		label = new JLabel(Msg.get(VM_HARDDISK_NOTE_2_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// disk size
		hardDiskSize = Main.getPropertyInt(DEFAULT_DISK_SIZE);
		JLabel diskSizeLbl = new JLabel("Default memory (" + hardDiskSize + " GB):", SwingConstants.RIGHT);
		panel.add(diskSizeLbl, new Gbc(0, row, 1, 1, 0, 0, "W H"));
		JLabel dummy = new JLabel("Default memory (100000 MB):", SwingConstants.RIGHT);
		diskSizeLbl.setPreferredSize(dummy.getPreferredSize());	// save some space, the label extends with the amount of memory
		diskSizeSld = new JSlider();
		panel.add(diskSizeSld, new Gbc(1, row, 3, 1, 0, 0, "W"));
		diskSizeSld.setPreferredSize(new Dimension(300, 50));
		diskSizeSld.setMinimum(0);			// min/max values
		diskSizeSld.setMaximum(500);
		// distance of markers
		diskSizeSld.setMajorTickSpacing(100);
		diskSizeSld.setMinorTickSpacing(20);
//		diskSizeSld.setSnapToTicks(true);
		diskSizeSld.createStandardLabels(1);
		diskSizeSld.setPaintTicks(true);
		diskSizeSld.setPaintLabels(true);
		diskSizeSld.setValue(hardDiskSize);
		diskSizeSld.addChangeListener(e -> {
			int size = diskSizeSld.getValue();
			if (size < 1) {
				size = 1;
				diskSizeSld.setValue(size);
			}
			hardDiskSize = size;
			diskSizeLbl.setText("Disk size (" + hardDiskSize + " GB):");
			System.out.println("Disk size (" + hardDiskSize + " GB):");
			});
		row++;
		return panel;
	}

	/**
	 * Creates the VM identification (name, icons, etc) panel.
	 * 
	 * @return the panel
	 */
	private JPanel createVMIdentificationPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(VM_IDENTIFICATION_WIZ_MSG), Images.get(Images.VM_WIZARD_IDENT));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: name
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_NAME_HEADER_WIZ_MSG));
		label = CompFactory.createChapterLabel(Msg.get(VM_NAME_HEADER_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// name
		nameTxt = new JTextField();
		panel.add(nameTxt, new Gbc(1, row, 3, 1, 0, 0, "W H"));
		nameTxt.setText(Msg.get(VM_NAME_WIZ_MSG));
		Gui.setComponentWidth(nameTxt, 100);
		nameTxt.selectAll();
		row++;
		// *** header
		label = CompFactory.createChapterLabel(Msg.get(VM_ICON_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		// OS icon
		osIconLbl = new JLabel();			// set on panel activation, see updateVMIdentificationPnl()
		panel.add(osIconLbl, new Gbc(1, row, 1, 1, 0, 0, "W"));
		Component spacer = Box.createVerticalStrut(1);		// spacer to file selection button
		spacer.setPreferredSize(new Dimension(20, 1));
		panel.add(spacer, new Gbc(2, row, "W"));
		JButton fileSelBtn = new JButton(Images.scale(Images.FOLDER, Gui.BUTTON_FILE_CHOOSER_SIZE));
		panel.add(fileSelBtn, new Gbc(3, row, 1, 1, 0, 0, "W"));
		fileSelBtn.setToolTipText(Msg.get(SELECT_OS_ICON_MSG));
		fileSelBtn.addActionListener(e -> {
			FileChooserDlg chooser = new FileChooserDlg(
					Msg.get(SELECT_OS_ICON_MSG), 
					Msg.get(OK_BTN_MSG), 
					Msg.get(OK_BTN_MSG), 
					JFileChooser.FILES_ONLY, 
					null);
			chooser.setCurrentDirectory(new File("src/qemujuicy/images/aqemu/os_icons"));
	        if (chooser.showOpenDialog(VMWizard.this) == JFileChooser.APPROVE_OPTION) {
	        	vmIconPath = chooser.getSelectedFile().getPath();
	            System.out.println("VM icon path selected: " + vmIconPath);
	            Logger.info("VM icon path selected: " + vmIconPath);
	            osIcon = Images.scale(Images.find(vmIconPath), PIXELS_OS_ICON);
				osIconLbl.setIcon(osIcon);
	        }
		});
		row++;
		return panel;
	}

	/**
	 * Creates the VM networking panel (the first panel).
	 * 
	 * @return the panel
	 */
	private JPanel createVMNetworkPnl() {
		
		JPanel panel = CompFactory.createTemplatePanel(
				Msg.get(VM_NETWORK_WIZ_MSG), Images.get(Images.NETWORK));
		int row = 1;		// due to createTemplatePanel() start with 1
		// indentation: header on Gbc x = 0, span 2; others start at 1
		panel.add(CompFactory.createIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		row++;
		// *** header: network
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_NETWORK_CONNECTION_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 3, 1, 0, 0, "W H"));
		row++;
		netConnectedRBt = new JRadioButton(Msg.get(VM_NETWORK_CONNECTED_WIZ_MSG));
		panel.add(netConnectedRBt, new Gbc(1, row, 3, 1, 0, 0, "W H"));
		netConnectedRBt.setSelected(true);
		row++;
		JRadioButton noNetworkRBt = new JRadioButton(Msg.get(VM_NO_NETWORK_WIZ_MSG));
		panel.add(noNetworkRBt, new Gbc(1, row, 3, 1, 0, 0, "W H"));
		ButtonGroup group = new ButtonGroup();
		group.add(netConnectedRBt);
		group.add(noNetworkRBt);
		row++;
		// notes / hints
		label = new JLabel(Msg.get(VM_NETWORK_NOTE_WIZ_MSG));
		panel.add(label, new Gbc(0, row, 6, 1, 0, 0, "W H"));
		row++;
		return panel;
	}
	
	/**
	 * Update the VM identification panel if activated (visible).
	 */
	private void updateVMIdentificationPnl() {
		
		if (nameTxt.getText().equals(Msg.get(VM_NAME_WIZ_MSG))) {
			nameTxt.selectAll();
			Gui.requestFocus(nameTxt);
		}
		int index = osCbx.getSelectedIndex();
		osType = VM.OSType.values()[index];
		Icon icon = null;
		String iconPath = null;
		switch (osType) {
		case LINUX:
			icon = Images.scale(Images.get(Images.OS_ICON_LINUX_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_LINUX_PATH;
			break;
		case WINDOWS:
			icon = Images.scale(Images.get(Images.OS_ICON_WINDOWS_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_WINDOWS_PATH;
			break;
		case MAC:
			icon = Images.scale(Images.get(Images.OS_ICON_MAC_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_MAC_PATH;
			break;
		case OTHER:
			icon = Images.scale(Images.get(Images.OS_ICON_OTHER_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_OTHER_PATH;
			break;
		case BSD:
			icon = Images.scale(Images.get(Images.OS_ICON_BSD_GENERIC_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_BSD_GENERIC_PATH;
			break;
		case DEBIAN:
			icon = Images.scale(Images.get(Images.OS_ICON_DEBIAN_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_DEBIAN_PATH;
			break;
		case UBUNTU:
			icon = Images.scale(Images.get(Images.OS_ICON_UBUNTU_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_UBUNTU_PATH;
			break;
		case OPENSUSE:
			icon = Images.scale(Images.get(Images.OS_ICON_OPEN_SUSE_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_OPEN_SUSE_PATH;
			break;
		case RHEL:
			icon = Images.scale(Images.get(Images.OS_ICON_RHEL_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_RHEL_PATH;
			break;
		case ARCH:
			icon = Images.scale(Images.get(Images.OS_ICON_ARCH_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_ARCH_PATH;
			break;
		case MINIX:
			icon = Images.scale(Images.get(Images.OS_ICON_MINIX_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_MINIX_PATH;
			break;
		case SOLARIS:
			icon = Images.scale(Images.get(Images.OS_ICON_SOLARIS_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_SOLARIS_PATH;
			break;
		case MINT:
			icon = Images.scale(Images.get(Images.OS_ICON_MINT_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_MINT_PATH;
			break;
		case FEDORA:
			icon = Images.scale(Images.get(Images.OS_ICON_FEDORA_PATH), PIXELS_OS_ICON);
			iconPath = Images.OS_ICON_FEDORA_PATH;
			break;
		default:
			throw new RuntimeException("Operating system " + osType + " not known, no icon");
		}
		if (osIcon != null) {
			// vmIconPath already set by comboBox
			osIconLbl.setIcon(osIcon);
		} else {
			osIconLbl.setIcon(icon);
			vmIconPath = iconPath;
		}
		osIconLbl.revalidate();
		osIconLbl.repaint();
	}
}
