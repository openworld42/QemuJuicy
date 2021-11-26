
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

import javax.help.*;
import javax.swing.*;

import qemujuicy.*;

import static qemujuicy.Message.*;

/**
 * A wizard to create a new VM.
 */
@SuppressWarnings("serial")
public class WizardButtonPanel extends JPanel {

	public final static String BACK_BTN = "Back";
	public final static String CANCEL_BTN = "Cancel";
	public final static String NEXT_AND_FINISH_BTN = "NextAndFinish";

	private JButton backBtn;
	private JButton cancelBtn;
	private JButton nextBtn;
	
	/**
	 * Creates the button JPanel for a wizard dialog (back/next-finish/cancel).
	 */
	public WizardButtonPanel(JDialog parent, ActionListener actionListener) {

		super(new GridBagLayout());
		// buttons 
		JButton helpBtn = CompFactory.createHelpButton();
		add(helpBtn, new Gbc(0, 0));
		helpBtn.setEnabled(true);
		Main.getHelp().enableHelpOnButton(helpBtn, "qemujuicy.intro");
		add(Gbc.filler(), new Gbc(1, 0, 1, 1, 10.0, 0, "S B"));
		// button Back, disabled at the beginning
		backBtn = CompFactory.createBackButton();
		add(backBtn, new Gbc(3, 0));
		backBtn.setEnabled(false);
		backBtn.setActionCommand(BACK_BTN);
		backBtn.addActionListener(actionListener);
		// button Next / Finish
		nextBtn = CompFactory.createNextButton();
		add(nextBtn, new Gbc(4, 0));
		nextBtn.setActionCommand(NEXT_AND_FINISH_BTN);
		nextBtn.addActionListener(actionListener);
		// spacer to cancel button
		Component spacer = Box.createVerticalStrut(1);
		spacer.setPreferredSize(new Dimension(10, 1));
		add(spacer, new Gbc(5, 0));
		// button Cancel
		cancelBtn = CompFactory.createCancelButton();
		add(cancelBtn, new Gbc(6, 0));
		cancelBtn.setActionCommand(CANCEL_BTN);
		cancelBtn.addActionListener(actionListener);
	}

	/**
	 * Changes the "Next" button of the wizard to "Finished" or back.
	 * 
	 * @param isFinishedBtn		if true, change to "Finished", to "Next" otherwise
	 */
	public void changeToFinishedBtn(boolean isFinishedBtn) {
		
		if (isFinishedBtn) {
			nextBtn.setText(Msg.get(FINISHED_BTN_MSG));
			nextBtn.setIcon(Images.scale(Images.APPLY_BUTTON, Gui.BUTTON_ICON_SIZE));
		} else {
			// user clicked "Back"
			nextBtn.setText(Msg.get(NEXT_BTN_MSG));
			nextBtn.setIcon(Images.scale(Images.NEXT_BUTTON, Gui.BUTTON_ICON_SIZE));
		}
		nextBtn.revalidate();
		nextBtn.repaint();
	}
	
	/**
	 * @return the backBtn
	 */
	public JButton getBackBtn() {
		
		return backBtn;
	}
	/**
	 * 
	 * @return the cancelBtn
	 */
	public JButton getCancelBtn() {
		
		return cancelBtn;
	}

	/**
	 * @return the nextBtn
	 */
	public JButton getNextBtn() {
		
		return nextBtn;
	}
}
