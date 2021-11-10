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

import javax.swing.*;
import javax.swing.border.*;

import qemujuicy.*;

import static qemujuicy.Message.*;

import java.awt.*;

/**
 * Factory for Swing components for this application.
 */
public class CompFactory {

	/**
	 * Construction.
	 */
	private CompFactory() {
		
	}

	/**
	 * Creates an "Back" button.
	 * 
	 * @return the button
	 */
	public static JButton createBackButton() {
		
		JButton button = new JButton(Msg.get(BACK_BTN_MSG), 
				Images.scale(Images.BACK_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates an "Apply" button.
	 * 
	 * @return the button
	 */
	public static JButton createApplyButton() {
		
		JButton button = new JButton(Msg.get(APPLY_BTN_MSG), 
				Images.scale(Images.APPLY_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates a "Cancel" button.
	 * 
	 * @return the button
	 */
	public static JButton createCancelButton() {
		
		JButton button = new JButton(Msg.get(CANCEL_BTN_MSG), 
				Images.scale(Images.CANCEL_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates a label for a chapter.
	 * 
	 * @param text
	 * @return the label
	 */
	public static JLabel createChapterLabel(String text) {

		JLabel label = new JLabel(text);
		label.setForeground(Gui.PANEL_CHAPTER);
		label.setBorder(new EmptyBorder(new Insets(2, 4, 0, 0)));
		return label;
	}

	/**
	 * Creates an "Exit" button.
	 * 
	 * @return the button
	 */
	public static JButton createExitButton() {
		
		JButton button = new JButton(Msg.get(EXIT_BTN_MSG), 
				Images.scale(Images.EXIT_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates an "Indentation" component.
	 * This should be placed in Gbc column 0. 
	 * Chapter headers span more component widths, other components on 
	 * the indented left start at Gbc column position 1.
	 * 
	 * @return the "Indentation" component
	 */
	public static Component createIndentation() {
		
		return createIndentation(20);
	}

	/**
	 * Creates an "Indentation" component.
	 * This should be placed in Gbc column 0. 
	 * Chapter headers span more component widths, other components on 
	 * the indented left start at Gbc column position 1.
	 * 
	 * @return the "Indentation" component
	 */
	public static Component createIndentation(int indentWidth) {
		
		Component indent = Box.createVerticalStrut(1);
		indent.setPreferredSize(new Dimension(indentWidth, 1));
		return  indent;
	}

	/**
	 * Creates an "Next" button.
	 * 
	 * @return the button
	 */
	public static JButton createNextButton() {
		
		JButton button = new JButton(Msg.get(NEXT_BTN_MSG), 
				Images.scale(Images.NEXT_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates an "Ok" button.
	 * 
	 * @return the button
	 */
	public static JButton createOkButton() {
		
		JButton button = new JButton(Msg.get(OK_BTN_MSG), 
				Images.scale(Images.OK_BUTTON, Gui.BUTTON_ICON_SIZE));
		button.setPreferredSize(Gui.DEFAULT_BTN_SIZE);
		button.setMinimumSize(Gui.DEFAULT_BTN_SIZE);
		return button;
	}

	/**
	 * Creates an "Indentation" component.
	 * This should be placed in Gbc column 0. 
	 * Chapter headers span more component widths, other components on 
	 * the indented left start at Gbc column position 1.
	 * 
	 * @return the "Indentation" component
	 */
	public static Component createTabIndentation() {
		
		return Box.createRigidArea(new Dimension(6, 0));
	}

	/**
	 * Creates a JLabel.
	 * 
	 * @return the label
	 */
	public static JLabel createTabLabel(String text) {
		
		return new JLabel(text + ":", SwingConstants.RIGHT);
	}
	
	/**
	 * Creates a JTextField.
	 * 
	 * @param text
	 * @param columns 			the estimated number of characters
	 * @return the JTextField
	 */
	public static JTextField createTabJTextField(String text, int columns) {
		
		JTextField textField = new JTextField(text, SwingConstants.RIGHT);
		textField.setColumns(columns);
		return textField;
	}

	/**
	 * Creates a template panel for all of the wizard/settings panels.
	 * The panel has an icon and title already and squeezes its
	 * components to the top on the left side.
	 * It has a GridBagLayout set. Due to the title label, additional 
	 * components start in row 1.
	 * 
	 * @param title
	 * @param icon
	 * @return the panel template
	 */
	public static JPanel createTemplatePanel(String title, ImageIcon icon) {
		
		JPanel panel = new JPanel(new GridBagLayout());
		CompoundBorder border = BorderFactory.createCompoundBorder(
				new LineBorder(Gui.PANEL_BORDER), new EmptyBorder(new Insets(8, 8, 8, 8)));
		panel.setBorder(border);
		panel.setBackground(Gui.PANEL_BACKGROUND);
		icon = Images.scale(icon, Gui.PIXELS_BTN);
		JLabel titleLbl = new JLabel(" " + title, icon, JLabel.LEFT);
		//title
		panel.add(titleLbl, new Gbc(0, 0, 3, 1, 0, 0, "NW H"));
		Font font = new Font(titleLbl.getFont().getFamily(), Font.BOLD, titleLbl.getFont().getSize());
		titleLbl.setFont(font.deriveFont(Gui.TITLE_FONT_SIZE));
		titleLbl.setBorder(new EmptyBorder(4, 0, 2, 0));
		// push components to the top on the left side, the are placed by caller
		panel.add(Gbc.filler(), new Gbc(0, 100, 1, 1, 0, 10.0, "S B"));
		panel.add(Gbc.filler(), new Gbc(100, 100, 1, 1, 10.0, 0, "S B"));
		return panel;
	}
}
