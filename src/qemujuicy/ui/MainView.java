
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
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

import qemujuicy.*;
import qemujuicy.vm.*;

import static qemujuicy.Images.*;
import static qemujuicy.Message.*;

/**
 * The main view of this application.
 */
@SuppressWarnings("serial")
public class MainView extends JFrame implements ActionListener {

	// constants

	public final static String ABOUT = "About";
	public final static String DEVICE_ADD_CD_DVD = "DeviceAddCdDvd";
	public final static String DEVICE_ADD_DRIVE = "DeviceAddDrive";
	public final static String DEVICE_ADD_FLOPPY = "DeviceAddFloppy";
	public final static String DEVICE_REMOVE = "DeviceRemove";
	public final static String DISK_IMAGE = "DiskImage";
	public final static String EXIT = "Exit";
	public final static String HELP = "Help";
	public final static String MOVE_DOWN = "MoveDown";
	public final static String MOVE_UP = "MoveUp";
	public final static String REMOVE_VM = "RemoveVM";
	public final static String QEMU_SETUP = "QemuSetup";
	public final static String SETTINGS = "Settings";
	public final static String START_VM = "StartVM";
	public final static String STOP_VM = "StopVM";
	public final static String VM_RUN_INSTALL = "VMRunInstall";
	public final static String VM_WIZARD = "VMWizard";

	private static MainView instance;
	
	// members
	private JPanel mainPanel;
	private JPanel centerPanel;
	private JTabbedPane vmTabbedPane;
	private JToolBar toolBar;
	private JPanel statusBar;
	private JLabel statusLbl;
	private JLabel memoryLbl;
	private JButton helpBtn;
	// VM properties tabbed pane
	private JPanel vmPnl;							// VM properties tab
	private JTextField nameTxt; 	
	private JComboBox<String> architectureCbx;
	private JComboBox<String> acceleratorCbx;
	private JComboBox<String> cpusCbx;
	private JSlider memorySld;
	private JComboBox<String> soundCbx;
	private JCheckBox verboseChk;
	private JCheckBox localtimeChk;
	private JCheckBox bootMenuChk;
	private JRadioButton addParametersRBt;			// Advanced tab
	private JRadioButton qemuDefinitionRBt;
	private JTextArea qemuParamsTxa;
	private JTextArea extraParamsTxa;
	private JList<VM.VMDevice> deviceList;
	// toolbar buttons
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnRemoveVM;
	private JButton btnDiskImage;
	private JButton btnRunVmInstall;
	private JButton btnVmWizard;
	private JButton btnUp;
	private JButton btnDown; 
	// menu items
	private JMenuItem menuItemStart;
	private JMenuItem menuItemStop;
	private JMenuItem menuItemRemoveVM;
	private JMenuItem menuItemRunVmInstall;

	// other components
	JList<VM> vmList;

	/**
	 * Construct main view of an application.
	 * 
	 * @throws Exception 
	 */
	public MainView() throws Exception {

		super(Main.APP_NAME);
		instance = this;
		setIconImage(Images.get(APP_ICON32).getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                Main.onExit();
            }
            public void windowOpened(WindowEvent event) {
//                onWindowOpened(args);
            }});
 		initFrame();
		pack();
		Gui.center(this);
//		setResizable(false);
		setVisible(true);
		Util.sleep(100);
		QemuSetup.run(MainView.this, false);
		if (Main.getVmManager().getVmList().size() > 0) {
			vmList.setSelectedIndex(0);
		}
		vmListSelectionEnabler();
		SwingUtilities.invokeLater(() -> {
			Main.getHelp().enableHelpOnButton(helpBtn, "qemujuicy.intro");
			vmTabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// at least Advanced Tab needs this
					updateVmComponents();
				}
			});
		});
	}

	/**
	 * Action queue dispatcher.
	 */
	public void actionPerformed(ActionEvent event) {

		setHint(null);		// clear all hints on actions
		String actionCmd = event.getActionCommand();
		if (actionCmd.equals(ABOUT)) {
			new AboutDlg(this);
		} else if (actionCmd.equals(DEVICE_ADD_CD_DVD)) {
			Device.addCD(this, vmList, deviceList);
		} else if (actionCmd.equals(EXIT)) {
            dispose();
            Main.onExit();
		} else if (actionCmd.equals(HELP)) {
			Main.getHelp().show();
		} else if (actionCmd.equals(MOVE_DOWN)) {
			Main.getVmManager().moveDownVm(vmList);
		} else if (actionCmd.equals(MOVE_UP)) {
			Main.getVmManager().moveUpVm(vmList);
		} else if (actionCmd.equals(QEMU_SETUP)) {
            QemuSetup.run(this, true);
		} else if (actionCmd.equals(REMOVE_VM)) {
			Main.getVmManager().removeVm(this, vmList.getSelectedIndex());
			vmTabbedPane.setSelectedIndex(0);
			nameTxt.setText("");
		} else if (actionCmd.equals(VM_RUN_INSTALL)) {
			Main.getVmManager().runInstallVm(this, vmList);
		} else if (actionCmd.equals(SETTINGS)) {
			SettingsDlg.create(this);
		} else if (actionCmd.equals(START_VM)) {
			Main.getVmManager().runVm(this, vmList, null);
		} else if (actionCmd.equals(VM_WIZARD)) {
	           new VMWizard();
        } else {
        	String missing = "ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName() 
            		+ ": " + actionCmd;
            System.out.println(missing);
            Logger.error(missing);
		}
	}

	/**
	 * ActionListener/actionPerformed for the Advanced Tab "Store" buttons.
	 * 
	 * @param separateLines			separate QEMU parameters on lines + "\"
	 */
	private void actionStoreButton(boolean separateLines) {
		
		ArrayList<String> cmdList = Qemu.createCommandList(qemuParamsTxa.getText());
		Qemu.addExtraParameters(cmdList, Main.getVm(vmList.getSelectedIndex()));
		FileChooserDlg chooser = new FileChooserDlg(Msg.get(SAVE_TO_FILE_MSG), 
				Msg.get(OK_BTN_MSG), Msg.get(OK_BTN_MSG), 
				JFileChooser.FILES_ONLY, null);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	        String path = chooser.getSelectedFile().getPath();
	        try {
				Util.writeFile(path, Qemu.toCommandStringStore(cmdList, separateLines));
				new File(path).setExecutable(true);
			} catch (IOException e2) {
				Logger.error("Cannot write commands to file '" + path + "'", e2);
			}
        }
	}

	/**
	 * Create and add the Advanced tab to the VM properties tabbed pane
	 * 
	 * @param tabbedPane
	 */
	private void addAdvancedTab(JTabbedPane tabbedPane) {

		JPanel advancedPnl = createTabPanel(tabbedPane, 
				Msg.get(ADVANCED_MSG), ADVANCED, Msg.get(VM_TAB_ADVANCED_PROPERTIES_TT_MSG));
		int row = 0;
		// machine
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_TAB_ADVANCED_PROPERTIES_TT_MSG));
		advancedPnl.add(label, new Gbc(0, row, 13, 1, 0, 0, "W H"));
		row++;
		// indentation, once only: non-chapter components start on column 1
		advancedPnl.add(CompFactory.createTabIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		// radio buttons row
		addParametersRBt = new JRadioButton(Msg.get(ADD_QEMU_PARAMS_MSG));
		advancedPnl.add(addParametersRBt, new Gbc(2, row, 1, 1, 0, 0, "W H"));
		addParametersRBt.setSelected(true);
		addParametersRBt.addActionListener(e -> {
			storeVmProperty(VMProperties.FULL_QEMU_DEFINITION, "" + false);
			updateVmComponents();
			});
		qemuDefinitionRBt = new JRadioButton(Msg.get(QEMU_BY_DEFINITION_MSG));
		advancedPnl.add(qemuDefinitionRBt, new Gbc(3, row, 1, 1, 0, 0, "W H"));
		qemuDefinitionRBt.addActionListener(e -> {
			storeVmProperty(VMProperties.FULL_QEMU_DEFINITION, "" + true);
			updateVmComponents();
			});
		ButtonGroup group = new ButtonGroup();
		group.add(addParametersRBt);
		group.add(qemuDefinitionRBt);
		row++;
		// QEMU command text area
		Gui.setComponentHeight(qemuDefinitionRBt);
		qemuParamsTxa = new JTextArea();
		qemuParamsTxa.setEditable(false);
		qemuParamsTxa.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		qemuParamsTxa.getDocument().addDocumentListener(
				new PropertyDocumentListener(VMProperties.FULL_QEMU_DEFINITION_CMD, qemuParamsTxa));
		final JPopupMenu contextMenu = new JPopupMenu("Edit");			// popup menu
		JMenuItem menuItem = new JMenuItem(Msg.get(COPY_MSG));
	    contextMenu.add(menuItem);
	    menuItem.addActionListener(e -> {
			ArrayList<String> cmdList = Qemu.createCommandList(qemuParamsTxa.getText());
	    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(new StringSelection(Qemu.toCommandString(cmdList)), null);
	    });
	    qemuParamsTxa.setComponentPopupMenu(contextMenu);
		JScrollPane scrollPane = new JScrollPane (qemuParamsTxa, 		// scroll pane
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		advancedPnl.add(scrollPane, new Gbc(2, row, 2, 3, 0, 0, "W B"));
		Gui.setPreferredHeight(scrollPane, 190);
		// buttons
		JButton button = CompFactory.createButton(Msg.get(COPY_MSG), Msg.get(COPY_CLIPBOARD_TT_MSG));
		advancedPnl.add(button, new Gbc(4, row, 1, 1, 0, 0, "NW"));
		button.addActionListener(e -> {
			ArrayList<String> cmdList = Qemu.createCommandList(qemuParamsTxa.getText());
			Qemu.addExtraParameters(cmdList, Main.getVm(vmList.getSelectedIndex()));
	    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(new StringSelection(Qemu.toCommandString(cmdList)), null);
	    });
		row++;
		button = CompFactory.createButton(Msg.get(STORE_MSG), Msg.get(STORE_AS_FILE_TT_MSG));
		advancedPnl.add(button, new Gbc(4, row, 1, 1, 0, 0, "NW"));
		button.addActionListener(e -> actionStoreButton(false));
		row++;
		button = CompFactory.createButton(Msg.get(STORE_LINES_MSG), Msg.get(STORE_AS_FILE_LINES_TT_MSG));
		advancedPnl.add(button, new Gbc(4, row, 1, 1, 0, 0, "NW"));
		button.addActionListener(e -> actionStoreButton(true));
		row++;
		// extra parameters text area
		extraParamsTxa = new JTextArea();
		extraParamsTxa.setEditable(true);
		extraParamsTxa.getDocument().addDocumentListener(
				new PropertyDocumentListener(VMProperties.EXTRA_PARAMETERS, extraParamsTxa));
		JScrollPane scrollPaneExtra = new JScrollPane (extraParamsTxa, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		advancedPnl.add(scrollPaneExtra, new Gbc(2, row, 2, 1, 0, 0, "W B"));
		Gui.setPreferredHeight(scrollPaneExtra, 140);
		label = new JLabel(Msg.get(EXTRA_PARAMS_MSG));
		advancedPnl.add(label, new Gbc(4, row, 1, 1, 0, 0, "NW"));
		row++;
		// push the above
//		row++;
//		advancedPnl.add(Gbc.filler(), new Gbc(0, row, 1, 1, 0, 10, "V"));
	}

	/**
	 * Adds a button with Gbc and specified insets.
	 * 
	 * @param name			the label of the button (and its action command)
	 * @param col
	 * @param row
	 * @param control
	 * @param insetTop
	 * @param insetLeft
	 * @param insetBottom
	 * @param insetRight
	 * @return the created button
	 * @see Gbc
	 */
	public JButton addButton(String name, int col, int row, String control, 
			int insetTop, int insetLeft, int insetBottom, int insetRight) {

		Gbc gbc = new Gbc(col, row, 1, 1, 0.0, 0.0, control, new Insets(insetTop, insetLeft, insetBottom, insetRight));
		JButton button = new JButton(name);
		button.addActionListener(this);
		centerPanel.add(button, gbc);
		return(button);
	}

	/**
	 * Create and add the Devices tab to the VM properties tabbed pane
	 * 
	 * @param tabbedPane
	 */
	private void addDevicesTab(JTabbedPane tabbedPane) {

		JPanel devicesPnl = createTabPanel(tabbedPane, 
				Msg.get(DEVICES_MSG), DISK, Msg.get(VM_TAB_VM_DEVICES_TT_MSG));
		int row = 0;
		// chapter
		JLabel label = CompFactory.createChapterLabel(Msg.get(DEVICES_MANAGER_MSG));
		devicesPnl.add(label, new Gbc(0, row, 13, 1, 0, 0, "W H"));
		row++;
		// indentation, once only: non-chapter components start on column 1
		devicesPnl.add(CompFactory.createTabIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		// add, manage

		label = new JLabel(Msg.get(ADD_MSG));
		devicesPnl.add(label, new Gbc(2, row, 1, 1, 0, 0, "W H"));
//		devicesPnl.add(label, new Gbc(2, row, 1, 1, 0, 0, "W H", insets));
		
		devicesPnl.add(Box.createHorizontalStrut(40), new Gbc(7, row, 1, 2, 0, 0, "H"));
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setPreferredSize(new Dimension(3, 50));
		devicesPnl.add(sep, new Gbc(8, row, 1, 2, 0, 0, "C B"));		// uses 2 rows (buttons too)
		label = new JLabel(Msg.get(MANAGE_MSG));
		devicesPnl.add(label, new Gbc(9, row, 1, 1, 0, 0, "W H"));
		row++;
		// button add floppy
		int pixels = 28;		// images will be scaled down to # of pixels
		JButton btnAddFloppy = createToolBarButton(null, Images.scale(Images.FLOPPY, pixels), 
				0, Msg.get(ADD_FLOPPY_TT_MSG), DEVICE_ADD_FLOPPY);
		devicesPnl.add(btnAddFloppy, new Gbc(2, row, 1, 1, 0, 0, "W H"));
		// button add CD/DVD
		JButton btnAddCdDvd = createToolBarButton(null, Images.scale(Images.CD_DVD, pixels), 
				0, Msg.get(ADD_CD_DVD_TT_MSG), DEVICE_ADD_CD_DVD);
		devicesPnl.add(btnAddCdDvd, new Gbc(3, row, 1, 1, 0, 0, "W H"));
		// button add drive
		JButton btnAddDrive = createToolBarButton(null, Images.scale(Images.DISK, pixels), 
				0, Msg.get(ADD_DRIVE_TT_MSG), DEVICE_ADD_DRIVE);
		devicesPnl.add(btnAddDrive, new Gbc(4, row, 1, 1, 0, 0, "W H"));
		// button remove device
		JButton btnRemoveDevice = createToolBarButton(null, Images.scale(Images.LIST_REMOVE, pixels - 4), 
				0, Msg.get(REMOVE_DEVICE_TT_MSG), DEVICE_REMOVE);
		devicesPnl.add(btnRemoveDevice, new Gbc(9, row, 1, 1, 0, 0, "W H"));
		row++;
		// devices list
		JPanel deviceListPnl = new JPanel(new BorderLayout());
		devicesPnl.add(deviceListPnl, new Gbc(2, row, 14, 1, 1.0, 1.0, "B"));
		deviceListPnl.setBackground(Gui.PANEL_BACKGROUND);
		// list
		deviceList = new JList<>();
		JScrollPane scrollPane = new JScrollPane(deviceList, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(640, 300));
		deviceListPnl.add(scrollPane, BorderLayout.CENTER);
		deviceList.setModel(Main.getVmManager().getDeviceListModel());
		deviceList.setCellRenderer(new LabelListCellRenderer(LabelListCellRenderer.TYPE.DEVICE));
		deviceList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
//				vmListSelectionEnabler();
//				updateVmComponents();
//				setHint(null);
			}
        });
		row++;
		// push the above
//		row++;
//		advancedPnl.add(Gbc.filler(), new Gbc(0, row, 1, 1, 0, 10, "V"));
	}

	/**
	 * Create and add the VM tab to the VM properties tabbed pane
	 * 
	 * @param tabbedPane
	 */
	private void addVmTab(JTabbedPane tabbedPane) {

		JPanel vmPnl = createTabPanel(tabbedPane, "VM", VM, Msg.get(VM_TAB_VM_PROPERTIES_TT_MSG));
		int defaultInset = Gbc.getDefaultBorderInset();
		Insets insets = new Insets(6, defaultInset, 0, defaultInset);
		int row = 0;
		// machine
		JLabel label = CompFactory.createChapterLabel(Msg.get(VM_MSG));
		vmPnl.add(label, new Gbc(0, row, 13, 1, 0, 0, "W H"));
		row++;
		// indentation, once only: non-chapter components start on column 1
		vmPnl.add(CompFactory.createTabIndentation(), new Gbc(0, row, 1, 1, 0, 0, "W H"));
		vmPnl.add(CompFactory.createTabLabel(Msg.get(VM_NAME_WIZ_MSG)), 
				new Gbc(1, row, 1, 1, 0, 0, "W H", insets));
		nameTxt = CompFactory.createTabJTextField("", 16);
		vmPnl.add(nameTxt, new Gbc(2, row, 1, 1, 0, 0, "W H", insets));
		nameTxt.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				int selectedIndex = vmList.getSelectedIndex();
				if (!Main.getVmManager().renameVm(selectedIndex, nameTxt.getText().trim())) {
					nameTxt.setText(Main.getVmManager().getVm(selectedIndex).getName());
					nameTxt.requestFocus();
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		row++;
		// architecture row
		vmPnl.add(CompFactory.createTabLabel(Msg.get(ARCHITECTURE_MSG)), new Gbc(1, row, 1, 1, 0, 0, "W H", insets));
		architectureCbx = new JComboBox<String>(Architecture.getNameArray());
		vmPnl.add(architectureCbx, new Gbc(2, row, 1, 1, 0, 0, "W H", insets));
		architectureCbx.setPreferredSize(new Dimension(120, Gui.DEFAULT_BTN_HEIGHT));
		architectureCbx.setMaximumRowCount(15);
		architectureCbx.addActionListener(e -> {
			if (architectureCbx.getSelectedIndex() >= 0) {
				storeVmProperty(VMProperties.VM_QEMU, 
					Architecture.ARRAY[architectureCbx.getSelectedIndex()].getQemuCmd());
			}
		});
		vmPnl.add(CompFactory.createTabLabel(Msg.get(ACCELERATOR_MSG)), new Gbc(4, row, 1, 1, 0, 0, "W H", insets));
		acceleratorCbx = new JComboBox<String>(Accelerator.getNameArray());
		vmPnl.add(acceleratorCbx, new Gbc(5, row, 1, 1, 0, 0, "W H", insets));
		acceleratorCbx.setPreferredSize(new Dimension(160, Gui.DEFAULT_BTN_HEIGHT));
		acceleratorCbx.setMaximumRowCount(15);
		acceleratorCbx.addActionListener(e -> {
			if (acceleratorCbx.getSelectedIndex() >= 0) {
				storeVmProperty(VMProperties.ACCELERATOR, 
						Accelerator.ARRAY[acceleratorCbx.getSelectedIndex()].getName());
			}
		});
		row++;
		// CPUs row
		vmPnl.add(CompFactory.createTabLabel("CPUs"), new Gbc(4, row, 1, 1, 0, 0, "W H", insets));
		cpusCbx = new JComboBox<String>(Cpu.getCpuArray()); 
		vmPnl.add(cpusCbx, new Gbc(5, row, 1, 1, 0, 0, "W", insets));
		cpusCbx.setPreferredSize(new Dimension(160, Gui.DEFAULT_BTN_HEIGHT));
		cpusCbx.setMaximumRowCount(18);
		((JLabel) cpusCbx.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		cpusCbx.addActionListener(e -> {
			if (cpusCbx.getSelectedIndex() >= 0) {
				storeVmProperty(VMProperties.CPUS, 
						Cpu.ARRAY[cpusCbx.getSelectedIndex()].getCount());
			}
		});
		row++;
		// memory
		label = CompFactory.createChapterLabel(Msg.get(MEMORY_MSG));
		vmPnl.add(label, new Gbc(0, row, 13, 1, 0, 0, "W H"));
		row++;
		memoryLbl = new JLabel(""); 
		vmPnl.add(memoryLbl, new Gbc(1, row, 1, 1, 0, 0, "W H", insets));
		memorySld = new JSlider();
		vmPnl.add(memorySld, new Gbc(2, row, 10, 1, 0, 0, "W H"));
//		memorySld.setPreferredSize(new Dimension((int) vmPnl.getPreferredSize().getWidth() - 80, 50));
		memorySld.setMinimum(0);			// min/max values
		memorySld.setMaximum(12000);
		// distance of markers
		memorySld.setMajorTickSpacing(2000);
		memorySld.setMinorTickSpacing(200);
//		memorySld.setSnapToTicks(true);
		memorySld.createStandardLabels(1);
		memorySld.setPaintTicks(true);
		memorySld.setPaintLabels(true);
		int selectedIndex = vmList.getSelectedIndex();
		if (selectedIndex >= 0) {
			memorySld.setValue(Main.getVm(selectedIndex).getMemorySizeMB());
		}
		memorySld.addChangeListener(e -> {
			int mem = memorySld.getValue();
			memoryLbl.setText(mem + "MB");
			storeVmProperty(VMProperties.VM_MEMORY_MB, "" + mem);
			});
		row++;
		 // miscellaneous
		label = CompFactory.createChapterLabel(Msg.get(MISC_MSG));
		vmPnl.add(label, new Gbc(0, row, 13, 1, 0, 0, "W H"));
		row++;
		// sound 
		vmPnl.add(CompFactory.createTabLabel(Msg.get(SOUND_MSG)), new Gbc(1, row, 1, 1, 0, 0, "W H", insets));
		soundCbx = new JComboBox<String>(Sound.getNameArray());
		vmPnl.add(soundCbx, new Gbc(2, row, 1, 1, 0, 0, "W H", insets));
		soundCbx.setPreferredSize(new Dimension(160, Gui.DEFAULT_BTN_HEIGHT));
		soundCbx.setMaximumRowCount(15);
		soundCbx.addActionListener(e -> {
			if (soundCbx.getSelectedIndex() >= 0) {
				storeVmProperty(VMProperties.SOUND, 
						Sound.ARRAY[soundCbx.getSelectedIndex()].name());
			}
		});
		// verbose flag
		verboseChk = new JCheckBox(Msg.get(VERBOSE_MSG));
		vmPnl.add(verboseChk, new Gbc(5, row, 1, 1, 0, 0, "W H", insets));
		if (selectedIndex >= 0) {
			VMProperties props = Main.getVmProperties(selectedIndex);
			verboseChk.setSelected(props.getPropertyBool(VMProperties.VERBOSE));
		}
		verboseChk.addActionListener(e -> storeVmProperty(VMProperties.VERBOSE, "" + verboseChk.isSelected()));
		row++;
		// -rtc base=localtime flag
		localtimeChk = new JCheckBox(Msg.get(Message.LOCALTIME_MSG));
		vmPnl.add(localtimeChk, new Gbc(5, row, 1, 1, 0, 0, "W H", insets));
		localtimeChk.setToolTipText(Msg.get(Message.LOCALTIME_TT_MSG));
		if (selectedIndex >= 0) {
			VMProperties props = Main.getVmProperties(selectedIndex);
			localtimeChk.setSelected(props.getPropertyBool(VMProperties.LOCALTIME)); 
		}
		localtimeChk.addActionListener(e -> 
			storeVmProperty(VMProperties.LOCALTIME, "" + localtimeChk.isSelected()));
		row++;
		// qemu boot menu flag
		bootMenuChk = new JCheckBox(Msg.get(BOOT_MENU_MSG));
		vmPnl.add(bootMenuChk, new Gbc(5, row, 1, 1, 0, 0, "W H", insets));
		if (selectedIndex >= 0) {
			VMProperties props = Main.getVmProperties(selectedIndex);
			bootMenuChk.setSelected(props.getPropertyBool(VMProperties.QEMU_BOOT_MENU));
		}
		bootMenuChk.addActionListener(e -> 
			storeVmProperty(VMProperties.QEMU_BOOT_MENU, "" + bootMenuChk.isSelected()));
		// push the above
		row++;
		vmPnl.add(Gbc.filler(), new Gbc(0, row, 1, 1, 0, 10, "V"));
	}

	/**
	 * @return the architectureCbx
	 */
	public JComboBox<String> getArchitectureCbx() {
		
		return architectureCbx;
	}

	/**
	 * Creates a JMenuBar for this view.
	 * 
	 * @return the JMenuBar
	 */
	public JMenuBar createMenu() {

		JMenuBar menuBar = new JMenuBar();
		// file
		JMenu menu = new JMenu(Msg.get(FILE_BTN_MSG));
		menuBar.add(menu);
		// file entries
		JMenuItem menuItem = createMenuItem(Msg.get(SETTINGS_BTN_MSG), true, SETTINGS, null);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = createMenuItem(Msg.get(EXIT_APP_BTN_MSG), true, EXIT, null);
		menu.add(menuItem);
		// VM
		menu = new JMenu(Msg.get(VM_BTN_MSG));
		menu.setToolTipText(Msg.get(VM_MSG));
		menuBar.add(menu);
		// VM entries
		menuItem = createMenuItem(Msg.get(VM_WIZARD_MSG), true, VM_WIZARD, null);
		menu.add(menuItem);
		menuItemRunVmInstall = createMenuItem(Msg.get(VM_RUN_INSTALL_TT_MSG), true, VM_RUN_INSTALL, null);
		menu.add(menuItemRunVmInstall);
		menu.addSeparator();
		menuItemStart = createMenuItem(Msg.get(START_TT_MSG), true, START_VM, null);
		menu.add(menuItemStart);
		menuItemStop = createMenuItem(Msg.get(STOP_TT_MSG), true, STOP_VM, null);
		menu.add(menuItemStop);
		menu.addSeparator();
		menuItemRemoveVM = createMenuItem(Msg.get(REMOVE_VM_TT_MSG), true, REMOVE_VM, null);
		menu.add(menuItemRemoveVM);
		
		// TODO xxx    MainView JMenu VM entries 

		// help
		menu = new JMenu(Msg.get(HELP_BTN_MSG));
		menuBar.add(menu);
		// help entries
		menuItem = createMenuItem(Msg.get(HELP_BTN_MSG), true, HELP, null);
		
		// TODO xxx    MainView JMenu Help 

		menu.add(menuItem);
		menuItem = createMenuItem(Msg.get(ABOUT_BTN_MSG), true, ABOUT, null);
		menu.add(menuItem);
		
		
		return menuBar;
    }

	/**
	 * Create a menu item.
	 * 
	 * @param name
	 * @param enabled
	 * @param actionCmd
	 * @param toolTip
	 * @return the menu item
	 */
	private JMenuItem createMenuItem(String name, boolean enabled, String actionCmd, String toolTip) {
		
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setEnabled(enabled);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(actionCmd);
		menuItem.setToolTipText(toolTip);
		return menuItem;
	}

	/**
	 * Creates a status bar for this view.
	 * 
	 * @return the status bar
	 */
	protected JPanel createStatusBar() {
		
		// create the status bar panel and show it down on the bottom of the frame
		JPanel statusPnl = new JPanel();
		statusPnl.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		int inset = 2;
		statusPnl.setPreferredSize(new Dimension(getWidth(), Gui.COMP_HEIGHT + 2 * inset));
		statusPnl.setLayout(new GridBagLayout());
		EmptyBorder labelBorder = new EmptyBorder(inset, inset, inset, inset);
		statusLbl = new JLabel("");
		statusLbl.setBorder(labelBorder);
		statusPnl.add(statusLbl, new Gbc(0, 0, 1, 1, 2, 0, "H"));
		return statusPnl;
	}

	/**
	 * Creates a template panel used as a tab for the VM tabbed pane.
	 * The panel is embedded into a JScrollPane.
	 * 
	 * @param tabbedPane
	 * @param title
	 * @param iconPath
	 * @param toolTip
	 * @return the panel
	 */
	private JPanel createTabPanel(JTabbedPane tabbedPane, String title, String iconPath, String toolTip) {

		JPanel panel = new JPanel(false);
		JScrollPane scrollPane = new JScrollPane(panel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabbedPane.addTab("", null, scrollPane, toolTip);
		scrollPane.setBorder(new EtchedBorder());
		scrollPane.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		JLabel titleLbl = new JLabel(" " + title);
		titleLbl.setBorder(new EmptyBorder(new Insets(2, 0, 0, 0)));
		titleLbl.setIcon(Images.scale(Images.get(iconPath), 20));
		tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(scrollPane), titleLbl);
		panel.setLayout(new GridBagLayout());
		// push components to the top on the left side, the are placed by caller
		panel.add(Gbc.filler(), new Gbc(0, 100, 1, 1, 0, 10.0, "S B"));
		panel.add(Gbc.filler(), new Gbc(100, 100, 1, 1, 10.0, 0, "S B"));
		return panel;
	}

	/**
	 * Creates the toolbar for this view.
	 * Enable/disable the JToolBar buttons in buttonEnabler().
	 * 
	 * @return the JToolBar
	 */
	protected JToolBar createToolBar() {
		
		JToolBar tb = Gui.createDefaultToolBar();
		int pixels = 28;		// images will be scaled down to # of pixels
		btnStart = createToolBarButton(null, Images.scale(Images.ARROW_RIGHT, pixels), 
				0, Msg.get(START_TT_MSG), START_VM);
		tb.add(btnStart);
		btnStop = createToolBarButton(null, Images.scale(Images.PROCESS_STOP, pixels), 
				0, Msg.get(STOP_TT_MSG), STOP_VM);
		tb.add(btnStop);
		tb.addSeparator();
		btnVmWizard = createToolBarButton(null, Images.scale(Images.VM_WIZARD, pixels), 
				0, Msg.get(VM_WIZARD_TT_MSG), VM_WIZARD);
		tb.add(btnVmWizard);
		btnRunVmInstall = createToolBarButton(null, Images.scale(Images.VM_RUN_INSTALL, pixels), 
				0, Msg.get(VM_RUN_INSTALL_TT_MSG), VM_RUN_INSTALL);
		tb.add(btnRunVmInstall);
		btnRemoveVM = createToolBarButton(null, Images.scale(Images.LIST_REMOVE, pixels), 
				0, Msg.get(REMOVE_VM_TT_MSG), REMOVE_VM);
		tb.add(btnRemoveVM);
		tb.addSeparator();
		btnDiskImage = createToolBarButton(null, Images.scale(Images.HARDDISK, pixels), 
				0, Msg.get(DISK_IMAGE_VM_TT_MSG), DISK_IMAGE);
		tb.add(btnDiskImage);
		JLabel dummy = new JLabel();
		dummy.setPreferredSize(new DimensionUIResource(1, 80));
		tb.add(dummy);
		btnUp = createToolBarButton(null, Images.scale(Images.UP_BUTTON, pixels), 
				0, Msg.get(MOVE_UP_VM_TT_MSG), MOVE_UP);
		tb.add(btnUp);
		btnDown = createToolBarButton(null, Images.scale(Images.DOWN_BUTTON, pixels), 
				0, Msg.get(MOVE_DOWN_VM_TT_MSG), MOVE_DOWN);
		tb.add(btnDown);
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
	 * @return the selection index of the architecture JComboBox
	 */
	public int getArchitectureCbxSelectedIndex() {
		
		return architectureCbx.getSelectedIndex();
	}

	/**
	 * GUI init.
	 * 
	 * @throws Exception 
	 */
	private void initFrame() throws Exception {
		
 		mainPanel = new JPanel(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
        // menu
		JMenuBar menuBar = createMenu();
        setJMenuBar(menuBar);

        // key input
//        addKeyListener(new ViewMainKeyListener());
		
		toolBar = createToolBar();
		getContentPane().add(toolBar, BorderLayout.WEST);
		statusBar = createStatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		centerPanel = new JPanel();
		Gui.setDefaultEmptyBorder(centerPanel);
//		centerPanel.setPreferredSize(new Dimension(900, 600));
//      centerPanel.setPreferredSize(new Dimension(
//      	AppCtx.getIntProperty("main.window.size.x"),
//      	AppCtx.getIntProperty("main.window.size.y")));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridBagLayout());
		// VM list
		JPanel vmListPnl = new JPanel(new BorderLayout());
		centerPanel.add(vmListPnl, new Gbc(0, 0, 1, 2, 1.0, 1.0, "B"));
		vmListPnl.setBackground(Gui.PANEL_BACKGROUND);
		// list
		vmList = new JList<>();
		JScrollPane scrollPane = new JScrollPane(vmList, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(280, 500));
		vmListPnl.add(scrollPane, BorderLayout.CENTER);
		vmList.setModel(Main.getVmManager().createVmListModel(vmList));
		vmList.setCellRenderer(new LabelListCellRenderer(LabelListCellRenderer.TYPE.VM));
		vmList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				vmListSelectionEnabler();
				updateVmComponents();
				setHint(null);
			}
        });
		// VM properties tabbed pane
		vmPnl = new JPanel(new BorderLayout());
		centerPanel.add(vmPnl, new Gbc(1, 0, 3, 1, 2.0, 1.0, "B"));
		vmPnl.setBorder(new EmptyBorder(new Insets(2, 0, 0, 0)));
		vmPnl.setPreferredSize(new Dimension(700, 500));
		vmTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		vmPnl.add(vmTabbedPane, BorderLayout.CENTER);
		// tabs for VM properties
		addVmTab(vmTabbedPane);
		addDevicesTab(vmTabbedPane);
		addAdvancedTab(vmTabbedPane);
		
		// help button
		helpBtn = CompFactory.createHelpButton();
		centerPanel.add(helpBtn, new Gbc(1, 1, 1, 1, 0.0, 0.0, "SW b r"));
		helpBtn.setEnabled(true);
		// exit button
        centerPanel.add(Gbc.filler(), new Gbc(2, 1, 1, 1, 1.0, 0, "C H"));
		JButton exitBtn = new JButton(EXIT);
		centerPanel.add(exitBtn, new Gbc(3, 1, 1, 1, 0.0, 0.0, "SE b r"));
		exitBtn.addActionListener(this);
        exitBtn.setIcon(Images.scale(Images.EXIT_BUTTON, Gui.BUTTON_ICON_SIZE));
		exitBtn.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		exitBtn.setText(Msg.get(EXIT_BTN_MSG));
		exitBtn.setIcon(Images.scale(Images.EXIT_BUTTON, Gui.BUTTON_ICON_SIZE));
		
		// TODO xxx    MainView  enable / disable buttons

		//		buttonXY.setEnabled(false);
	}

	/**
	 * Sets a message in the status bar.
	 * 
	 * @param hint	the message
	 */
	public static void setHint(String hint) {
		
		if (hint == null || hint.trim().equals("")) {
			// this is a clear, it will always work
			MainView.instance.statusLbl.setText("");
			return;
		}
		if (Main.getPropertyBool(AppProperties.GIVE_HINTS)) {
			MainView.instance.statusLbl.setText(hint);
		}
	}

	/**
	 * Sets a message in the status bar.
	 * 
	 * @param text		the status
	 */
	public static void setStatusMessage(String text) {
		
		MainView.instance.statusLbl.setText(text);
	}

	/**
	 * Store a property of the selected VM and write the properties file to save it.
	 * Usually called from a component vale change.
	 */
	private void storeVmProperty(String propertyKey, String value) {
		
		int selectedIndex = vmList.getSelectedIndex();
		if (selectedIndex < 0) {
			return;
		}
		if (propertyKey.equals(VMProperties.FULL_QEMU_DEFINITION_CMD) && !qemuParamsTxa.isEditable()) {
			// do not store full qemu command if it is not editable
			return;
		}
		VM vm = Main.getVmManager().getVm(selectedIndex);
		String oldProperty = vm.getVmProperties().getProperty(propertyKey);
		if (oldProperty.equals(value)) {
			return;
		}
		vm.setProperty(propertyKey, value);
		vm.getVmProperties().storeToXML();
		Logger.info("VM '" + vm.getName() + "': changed " + propertyKey + " -> " + value);
	}

	/**
	 * Update all components with the properties of the selected VM.
	 * Usually called on selection of a VM.
	 */
	private void updateVmComponents() {
		
		int selectedIndex = vmList.getSelectedIndex();
		Main.getVmManager().fillVmDeviceModel(selectedIndex);
		if (selectedIndex < 0) {
			return;
		}
		VM vm = Main.getVmManager().getVm(selectedIndex);
		// tab VM
		nameTxt.setText(vm.getName());
		architectureCbx.setSelectedIndex(Architecture.findCbxIndexFor(vm));  
		acceleratorCbx.setSelectedIndex(Accelerator.findCbxIndexFor(vm));
		cpusCbx.setSelectedIndex(Cpu.findCbxIndexFor(vm));
		memorySld.setValue(vm.getMemorySizeMB());
		VMProperties props = Main.getVmProperties(selectedIndex);
		soundCbx.setSelectedIndex(Sound.findCbxIndexFor(vm));
		verboseChk.setSelected(props.getPropertyBool(VMProperties.VERBOSE));
		localtimeChk.setSelected(props.getPropertyBool(VMProperties.LOCALTIME));
		bootMenuChk.setSelected(props.getPropertyBool(VMProperties.QEMU_BOOT_MENU));
		// tab Advanced
		if (props.getPropertyBool(VMProperties.FULL_QEMU_DEFINITION)) {
			// full QEMU definition (ignore other settings)
			qemuDefinitionRBt.setSelected(true);
			qemuParamsTxa.setEditable(true);
			qemuParamsTxa.setBackground(extraParamsTxa.getBackground());
			String cmd = props.getProperty(VMProperties.FULL_QEMU_DEFINITION_CMD).trim();
			if (cmd.equals("")) {
				// nothing has been set before, generate the default from the VM properties
				SwingUtilities.invokeLater(() -> {
					// invokeLater() is needed, since MainView setup is not finished on first call (startup)
					qemuParamsTxa.setText(Qemu.toTextAreaString(Qemu.createCommandList(vm, null)));
				});
			} else {
				qemuParamsTxa.setText(Qemu.toTextAreaString(cmd));
			}
		} else {
			// extra parameters only
			addParametersRBt.setSelected(true);
			qemuParamsTxa.setEditable(false);
			qemuParamsTxa.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
			SwingUtilities.invokeLater(() -> {
				// invokeLater() is needed, since MainView setup is not finished on first call (startup)
				qemuParamsTxa.setText(Qemu.toTextAreaString(Qemu.createCommandList(vm, null)));
			});
		}
		extraParamsTxa.setText(vm.getExtraParamsTextAreaString());
	}

	/**
	 * Enable/disable toolbar buttons - depending on VM states etc.
	 */
	public void vmListSelectionEnabler() {

		int selectedIndex = vmList.getSelectedIndex();
		if (selectedIndex < 0) {
			// nothing selected
			Logger.info("no VM selected");
			btnStart.setEnabled(false);
			menuItemStart.setEnabled(false);
			btnRunVmInstall.setEnabled(false);
			menuItemRunVmInstall.setEnabled(false);
			btnStop.setEnabled(false);
			menuItemStop.setEnabled(false);
			btnRemoveVM.setEnabled(false);
			menuItemRemoveVM.setEnabled(false);
			Gui.enableComponents(vmTabbedPane, false);
			return;
		}
		// a VM has been selected
		VM vm = Main.getVmManager().getVm(selectedIndex);
		Logger.info("VM '" + vm.getName() + "' selected");
		if (vm.isRunning()) {
			btnStart.setEnabled(false);
			menuItemStart.setEnabled(false);
			btnRunVmInstall.setEnabled(false);
			menuItemRunVmInstall.setEnabled(false);
			btnStop.setEnabled(true);
			menuItemStop.setEnabled(true);
			btnRemoveVM.setEnabled(false);
			menuItemRemoveVM.setEnabled(false);
			btnDiskImage.setEnabled(false);
			Gui.enableComponents(vmTabbedPane, false);
		} else {
			btnStart.setEnabled(true);
			menuItemStart.setEnabled(true);
			btnRunVmInstall.setEnabled(true);
			menuItemRunVmInstall.setEnabled(true);
			btnStop.setEnabled(false);
			menuItemStop.setEnabled(false);
			btnRemoveVM.setEnabled(true);
			menuItemRemoveVM.setEnabled(true);
			btnDiskImage.setEnabled(true);
			Gui.enableComponents(vmTabbedPane, true);
		}
		// not touched:
//		btnVmWizard
//		btnArguments
	}
	
	/************************* inner classes *************************/
	
	/**
	 * A DocumentListener for a VM property.
	 */
	class PropertyDocumentListener implements DocumentListener {

		private String property;
		private JTextComponent component;
		
		public PropertyDocumentListener(String property, JTextComponent component) {

			this.property = property;
			this.component = component;
		}

		public void insertUpdate(DocumentEvent e) {
			
			storeVmProperty(property, component.getText());
		}

		public void removeUpdate(DocumentEvent e) {			
			
			storeVmProperty(property, component.getText());
		}

		public void changedUpdate(DocumentEvent e) {}		// not fired for plain text
	}
}

