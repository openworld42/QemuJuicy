
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
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import qemujuicy.*;

import static qemujuicy.Images.*;
import static qemujuicy.Message.*;

/**
 * A JDialog to select the command for QEMU on this computer and OS.
 */
@SuppressWarnings("serial")
public class AboutDlg extends JDialog {

	private static Dimension TAB_PANEL_SIZE = new Dimension(650, 420); // size of the tabs (panels of the JTabbedPane)

	private JButton okBtn;
	
	/**
	 * Create the dialog and show it.
	 * 
	 * @param parentFrame
	 */
	public AboutDlg(JFrame parentFrame) {

		setTitle(Msg.get(ABOUT_DLG_TITLE_MSG));
		setIconImage(Images.get(APP_ICON).getImage());
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// create a panel for the components
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(16, 22, 16, 22));
		getContentPane().add(panel, BorderLayout.CENTER);
		// view panel widgets
		JPanel topPnl = new JPanel();
		panel.add(topPnl, BorderLayout.NORTH);
		topPnl.setBorder(new EmptyBorder(4, 0, 8, 4));
		topPnl.setLayout(new GridBagLayout());
		JLabel imageLbl = new JLabel(Images.get(APP_ICON));
//		JLabel imageLbl = new JLabel(Images.scale(Images.APP_ICON, 64));
		topPnl.add(imageLbl, new Gbc(0, 0, 1, 4, "NE"));
		JLabel qemuJuicyLbl = new JLabel(Main.APP_NAME);
		Font font = new Font(qemuJuicyLbl.getFont().getFamily(), Font.BOLD, qemuJuicyLbl.getFont().getSize());
		qemuJuicyLbl.setFont(font.deriveFont(28f));
		qemuJuicyLbl.setBorder(new EmptyBorder(4, 0, 2, 0));
		topPnl.add(qemuJuicyLbl, new Gbc(1, 0, 1, 1, "NE H"));
		JLabel qemuJuicyDescLbl = new JLabel("QEMU Java User Interface Contributed Yet");
		qemuJuicyDescLbl.setFont(font.deriveFont(14f));
		topPnl.add(qemuJuicyDescLbl, new Gbc(1, 1, 1, 1, "NE H"));
		JLabel versionLbl = new JLabel("Version: " + Version.getAsString());
		versionLbl.setFont(font.deriveFont(12f));
		versionLbl.setBorder(new EmptyBorder(26, 0, 0, 0));
		topPnl.add(versionLbl, new Gbc(1, 2, 1, 1, "NE H"));
		topPnl.add(Gbc.filler(), new Gbc(1, 3, 1, 1, 1.0, 1.0, "NE H"));
		// tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane, BorderLayout.CENTER);
		// tab About
		addAboutTab(tabbedPane);
		// tab Credits
		addCreditsTab(tabbedPane);
		// tab Contribution
		addContributionTab(tabbedPane);
		// tab License 
		addLicenseTab(tabbedPane);
		// tab License Oxygen Icons
		addLicenseOxygenTab(tabbedPane);
		// tab License aqemu Icons
		addLicenseAqemuTab(tabbedPane);
		// button Ok
		JPanel buttonPnl = new JPanel();
		panel.add(buttonPnl, BorderLayout.SOUTH);
		okBtn = CompFactory.createOkButton();
		buttonPnl.add(okBtn, BorderLayout.SOUTH);
		okBtn.addActionListener(e ->  dispose());
		pack();
		Gui.center(this);
		setVisible(true);
	}

	/**
	 * Adds the "About" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addAboutTab(JTabbedPane tabbedPane) {
		
		JPanel aboutPnl = new JPanel(false);
		tabbedPane.addTab(Msg.get(ABOUT_BTN_MSG), null, aboutPnl);
		aboutPnl.setBorder(new EtchedBorder());
		aboutPnl.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		aboutPnl.setPreferredSize(TAB_PANEL_SIZE);
		aboutPnl.setLayout(new GridBagLayout());
		int defaultInset = Gbc.getDefaultBorderInset();
		Insets insets = new Insets(2, defaultInset, 0, defaultInset);
		int row = 0;
		aboutPnl.add(Box.createVerticalStrut(4), new Gbc(0, row));
		row++;
		JLabel label = new JLabel("QemuJuicy is a graphical user interface to run QEMU VMs in an easy way");
		Font font = new Font(label.getFont().getFamily(), Font.BOLD, label.getFont().getSize());
		label.setFont(font.deriveFont(14f));
		aboutPnl.add(label, new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		row++;
		aboutPnl.add(Box.createVerticalStrut(1), new Gbc(0, row));
		row++;
		aboutPnl.add(new JLabel("(You need QEMU installed to run this application)"), 
				new Gbc(0, row, 5, 1, 0, 0, "N", insets));
		row++;
		aboutPnl.add(Box.createVerticalStrut(4), new Gbc(0, row));
		row++;
		aboutPnl.add(new JLabel("Copyright 2021 Heinz Silberbauer and contributors"), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(new JLabel("License: Apache License Version 2.0, January 2004, http://www.apache.org/licenses/"), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(Box.createVerticalStrut(1), new Gbc(0, row));
		row++;
		label = new JLabel("Author:");
		aboutPnl.add(label, new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(new JLabel("Heinz Silberbauer"), new Gbc(1, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(Box.createVerticalStrut(4), new Gbc(0, row));
		row++;
		aboutPnl.add(new JLabel("github@silberbauer.xyz"), new Gbc(1, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(Box.createVerticalStrut(16), new Gbc(0, row));
		row++;
		label = new JLabel("Repository:");
		aboutPnl.add(label, new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(new JLabel("https://github.com/openworld42/QemuJuicy"), new Gbc(1, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(Gbc.filler(), new Gbc(4, row, 1, 1, 10, 0, "H"));
		row++;
		label = new JLabel("Issues & contribution:");
		aboutPnl.add(label, new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(new JLabel("https://github.com/openworld42/QemuJuicy/issues"), new Gbc(1, row, 1, 1, 0, 0, "NW", insets));
		aboutPnl.add(Gbc.filler(), new Gbc(4, row, 1, 1, 10, 0, "H"));
		row++;
		aboutPnl.add(Box.createVerticalStrut(16), new Gbc(0, row, 1, 1, 10, 0, "H"));
		row++;
		aboutPnl.add(new JLabel("Oxygen (The Oxygen Icon Theme) license: GNU Lesser General Public License version 3"), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(new JLabel("https://commons.wikimedia.org/wiki/Category:Oxygen_icons"), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(new JLabel("https://github.com/KDE/oxygen-icons"), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(new JLabel("License and contributors: see LICENSE_Oxygen."), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		aboutPnl.add(Box.createVerticalStrut(1), new Gbc(0, row));
		row++;
		// push the above
		row++;
		aboutPnl.add(Gbc.filler(), new Gbc(0, row, 1, 1, 0, 10, "V"));
		return aboutPnl;
	}

	/**
	 * Adds the "Contribution" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addContributionTab(JTabbedPane tabbedPane) {
		
		JPanel contribTab = new JPanel(false);
		tabbedPane.addTab(Msg.get(CONTRIBUTION_BTN_MSG), null, contribTab);
		contribTab.setBorder(new EtchedBorder());
		contribTab.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		contribTab.setPreferredSize(TAB_PANEL_SIZE);
		contribTab.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea contribTxa = new JTextArea();
		panel.add(contribTxa);
		contribTxa.setTabSize(4);
		contribTxa.setEditable(false);
		contribTxa.setLineWrap(true);
		contribTxa.setWrapStyleWord(true);
		String contrib = 
				"\nContributors:\n\n"
				+ "\tHeinz Silberbauer, developer/architect\n"
				+ "\t<this-could-be-your-name>      (contribution field)\n"
				+ "\n"
				+ "Please keep in mind to precisely define your issues, in case of bugs include the log.txt file\n"
				+ "\n"
				+ "Contribute to:\n"
				+ "\tTranslation: Spanish, French, Italian, Russian, (German: work in progress), ... \n"
				+ "\tTesting: Windows 10/11, Macs, non-Debians, ... \n"
				+ "\tDocumentation: a wiki, plain HTML, (or javahelp?) \n"
				+ "\tInformation: environment/paths for non-Debians\n"
				+ "\tTesting other CPUs/VMs: Arm (Raspi), MIPS, ... \n"
				+ "\tPackage managers: Debian, others, ... \n"
				+ "\tUse cases: common usage, QEMU options, ideas (real causes)\n"
				+ "\tBugs: submit what you have done, what happens, and the log file (log.txt)\n"
				+ "\tPublic relations, if we are ready for it\n"
				+ "\tA Github star, if you like it, or want it ;-)\n"
				+ "\n"
				+ "Contribute either through\n"
				+ "\tMail: github@silberbauer.xyz   (serious subjects only)\n"
				+ "\tGithub issue: https://github.com/openworld42/QemuJuicy/issues\n"
				;
		contribTxa.setText(contrib);
		JScrollPane scrollPane = new JScrollPane (panel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contribTab.add(scrollPane, BorderLayout.CENTER);
		contribTxa.setCaretPosition(0);
		return contribTab;
	}

	/**
	 * Adds the "Credits" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addCreditsTab(JTabbedPane tabbedPane) {
		
		JPanel creditsPnl = new JPanel(false);
		tabbedPane.addTab(Msg.get(CREDITS_BTN_MSG), null, creditsPnl);
		creditsPnl.setBorder(new EtchedBorder());
		creditsPnl.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		creditsPnl.setPreferredSize(TAB_PANEL_SIZE);
		creditsPnl.setLayout(new GridBagLayout());
		int defaultInset = Gbc.getDefaultBorderInset();
		Insets insets = new Insets(2, defaultInset, 0, defaultInset);
		int row = 0;
		creditsPnl.add(Box.createVerticalStrut(4), new Gbc(0, row));
		row++;
		JLabel label = new JLabel("Credits, Kudos and Attribution");
		Font font = new Font(label.getFont().getFamily(), Font.BOLD, label.getFont().getSize());
		label.setFont(font.deriveFont(14f));
		creditsPnl.add(label, new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(Box.createVerticalStrut(1), new Gbc(0, row));
		row++;
		creditsPnl.add(new JLabel("QEMU(tm) - the QEMU team, Fabrice Bellard, QEMU is open source (FOSS)"), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("(You need QEMU installed to run this application)"), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(Box.createVerticalStrut(1), new Gbc(0, row));
		row++;
		creditsPnl.add(new JLabel("QEMU emulates a complete computer in software as a VM (virtual machine)."), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("Even though QEMU is not part of this software, without QEMU QemuJuicy would not exist."), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("https://github.com/qemu/qemu"), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(Box.createVerticalStrut(8), new Gbc(0, row));
		row++;
		creditsPnl.add(new JLabel("Oxygen (The Oxygen Icon Theme) for the icons/images used in this application."), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("https://github.com/KDE/oxygen-icons"), 
				new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(Box.createVerticalStrut(8), new Gbc(0, row));
		row++;
		label = new JLabel("AQEMU, Andrey Rijov (original author), Tobias Gläßer (last maintainer):");
		creditsPnl.add(label, new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(
				new JLabel("For some basic ideas, for AQEMU itself, some icons, and the contribution to the community."), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(
				new JLabel("Unfortunately, it was not active for a while, some QEMU options are deprecated meanwhile."), 
				new Gbc(0, row, 5, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("(That is the reason why I wrote this)"), new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("https://github.com/tobimensch/aqemu"), new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		row++;
		creditsPnl.add(new JLabel("https://sourceforge.net/projects/aqemu/"), new Gbc(0, row, 1, 1, 0, 0, "NW", insets));
		// push the above
		row++;
		creditsPnl.add(Gbc.filler(), new Gbc(0, row, 1, 1, 0, 10, "V"));
		return creditsPnl;
	}

	/**
	 * Adds the "License" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addLicenseAqemuTab(JTabbedPane tabbedPane) {
		
		JPanel licenseTab = new JPanel(false);
		tabbedPane.addTab(Msg.get(LICENSE_AQEMU_BTN_MSG), null, licenseTab);
		licenseTab.setBorder(new EtchedBorder());
		licenseTab.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		licenseTab.setPreferredSize(TAB_PANEL_SIZE);
		licenseTab.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea licenseTxa = new JTextArea();
		panel.add(licenseTxa);
		licenseTxa.setEditable(false);
		licenseTxa.setLineWrap(true);
		licenseTxa.setWrapStyleWord(true);
		String license = "\nNo license file: get the license from\n\n"
				+ "GNU LESSER GENERAL PUBLIC LICENSE\n"
				+ "Version 2, June 1991\n"
				+ "You should have received a copy of the GNU Lesser General Public\n"
				+ "License along with this library. If not, see http://www.gnu.org/licenses/\n";
		try {
			license = Util.readFileIntoString("LICENSE_aqemu", 1024 * 100);
		} catch (IOException e) {
			Logger.error("License file 'LICENSE_aqemu' read error", e);
			e.printStackTrace();
		}
		licenseTxa.setText(license);
		JScrollPane scrollPane = new JScrollPane (panel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		licenseTab.add(scrollPane, BorderLayout.CENTER);			
		licenseTxa.setCaretPosition(0);
		return licenseTab;
	}

	/**
	 * Adds the "License" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addLicenseTab(JTabbedPane tabbedPane) {
		
		JPanel licenseTab = new JPanel(false);
		tabbedPane.addTab(Msg.get(LICENSE_BTN_MSG), null, licenseTab);
		licenseTab.setBorder(new EtchedBorder());
		licenseTab.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		licenseTab.setPreferredSize(TAB_PANEL_SIZE);
		licenseTab.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea licenseTxa = new JTextArea();
		panel.add(licenseTxa);
		licenseTxa.setEditable(false);
		licenseTxa.setLineWrap(true);
		licenseTxa.setWrapStyleWord(true);
		String license = "\nNo license file: get the license from\n\n"
				+ "You should have received a copy of the GNU GENERAL PUBLIC LICENSE\n"
				+ "Version 2 along with this library. If not, see <http://www.gnu.org/licenses/>.\n";
		try {
			license = Util.readFileIntoString("LICENSE", 1024 * 100);
		} catch (IOException e) {
			Logger.error("License file 'LICENSE' read error", e);
			e.printStackTrace();
		}
		licenseTxa.setText(license);
		JScrollPane scrollPane = new JScrollPane (panel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		licenseTab.add(scrollPane, BorderLayout.CENTER);
		licenseTxa.setCaretPosition(0);
		return licenseTab;
	}

	/**
	 * Adds the "License" tab.
	 * 
	 * @param tabbedPane
	 * @return the tab
	 */
	private JPanel addLicenseOxygenTab(JTabbedPane tabbedPane) {
		
		JPanel licenseTab = new JPanel(false);
		tabbedPane.addTab(Msg.get(LICENSE_OXYGEN_BTN_MSG), null, licenseTab);
		licenseTab.setBorder(new EtchedBorder());
		licenseTab.setBackground(Gui.ABOUT_PANEL_BACKGROUND);
		licenseTab.setPreferredSize(TAB_PANEL_SIZE);
		licenseTab.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea licenseTxa = new JTextArea();
		panel.add(licenseTxa);
		licenseTxa.setEditable(false);
		licenseTxa.setLineWrap(true);
		licenseTxa.setWrapStyleWord(true);
		String license = "\nNo license file: get the license from\n\n"
				+ "GNU LESSER GENERAL PUBLIC LICENSE\n"
				+ "Version 3, 29 June 2007\n"
				+ "You should have received a copy of the GNU Lesser General Public\n"
				+ "License along with this library. If not, see http://www.gnu.org/licenses/\n";
		try {
			license = Util.readFileIntoString("LICENSE_Oxygen", 1024 * 100);
		} catch (IOException e) {
			Logger.error("License file 'LICENSE_Oxygen' read error", e);
			e.printStackTrace();
		}
		licenseTxa.setText(license);
		JScrollPane scrollPane = new JScrollPane (panel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		licenseTab.add(scrollPane, BorderLayout.CENTER);			
		licenseTxa.setCaretPosition(0);
		return licenseTab;
	}
}
