
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

import javax.swing.*;
import javax.swing.border.*;

import qemujuicy.*;

/**
 * Utility class for the GUI (Graphical User Interface), supporting the project's GUI with several static methods.
 */
public class Gui {
	
	public static final int COMP_HEIGHT = 21;		// the default component height within the GUI
	public static final int BUTTON_ICON_SIZE = 18;	// size of button icons
	public static final int BUTTON_FILE_CHOOSER_SIZE = 18;	
	public static final int CHAR_WIDTH = 15;		// an assumed character width in a text component (for width estimations)
	public static final int DEFAULT_BTN_WIDTH = 100;
	public static final int DEFAULT_BTN_HEIGHT = 30;
	public final static int PIXELS_BTN = 32;		// images will be scaled down to # of pixels
	public final static float TITLE_FONT_SIZE = 16f;
	public static final Dimension DEFAULT_BTN_SIZE = new Dimension(DEFAULT_BTN_WIDTH, DEFAULT_BTN_HEIGHT);
	public static final Insets DEFAULT_TOOLBAR_BTN_INSETS = new Insets(4, 2, 2, 2);

	public static final Color ABOUT_PANEL_BACKGROUND= new Color(210, 210, 220);
//	public static final Color PANEL_BACKGROUND = new Color(243, 243, 244);		// var 1
	public static final Color PANEL_BACKGROUND = new Color(230, 230, 235);		// var 2
	public static final Color PANEL_BORDER = new Color(150, 150, 196);
	public static final Color TEXT_INACTIVE_BACKGROUND = new Color(230, 245, 245);
//	public static final Color PANEL_CHAPTER = new Color(100, 100, 220);
	public static final Color PANEL_CHAPTER = new Color(10, 10, 240);
	
	/**
	 * Deny external construction.
	 */
	private Gui() {

	}

	/**
	 * Blocks the current thread until a message dialog is confirmed.
	 * Uses SwingUtilities.invokeAndWait() and JOptionPane.showMessageDialog() to do this.
	 *
	 * @param message		the message
	 * @param title			the title of the dialog
	 * @throws RuntimeException if called on the event dispatch thread
	 */
	public static void blockingConfirmDlg(final String message, final String title) {

		if (SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("Do not call this method from the event dispatch thread!");
		}
		try {
			SwingUtilities.invokeAndWait(() ->
				JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE));
		} catch (Exception e) {
			e.printStackTrace();
			Logger.exception(e);
		}
	}

	/**
     * Centers a window (JFrame, JDialog, etc.) on the screen.
     *
     * @param window 		the window object (JFrame, JDialog, etc.) to center
	 */
	public static void center(Window window) {

		Dimension screenSize = window.getToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		screenSize.width -= windowSize.width;
		screenSize.height -= windowSize.height;
		window.setLocation(screenSize.width / 2, screenSize.height / 2);
	}

	/**
	 * Creates a default toolbar with default properties.
	 * 
	 * @return the JToolBar
	 */
	public static JToolBar createDefaultToolBar() {
		
		JToolBar tb = new JToolBar();
		tb.setOrientation(SwingConstants.VERTICAL);
		tb.setFloatable(false);
		int inset = 6;
		tb.setBorder(new EmptyBorder(40, inset + 10, inset, inset));
		return tb;
	}
	
	/**
	 * Enable/disable a container and all contained components recursively.
	 * 
	 * @param container
	 * @param enable		true to enable, flase otherwise
	 */
	public static void enableComponents(Container container, boolean enable) {

		Component[] components = container.getComponents();
		for (Component component: components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

	/**
     * Request the focus of a component using .
     *
     * @param component 	the JComponent to get the focus
	 */
	public static void requestFocus(JComponent component) {
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
				component.requestFocusInWindow(); 
			    } 
			});
	}
	
	/**
     * Sets the height of a JComponent, as a convenience method.
     *
     * @param component
	 */
	public static void setComponentHeight(JComponent component) {
		
		component.setPreferredSize(new Dimension(
				(int) component.getPreferredSize().getWidth(), DEFAULT_BTN_HEIGHT));
	}
	
	/**
     * Sets the preferred height of a JComponent, as a convenience method.
     *
     * @param component
	 */
	public static void setPreferredHeight(JComponent component, int height) {
		
		component.setPreferredSize(new Dimension(
				(int) component.getPreferredSize().getWidth(), height));
	}
	
	/**
     * Sets the width of a JComponent, as a convenience method.
     *
     * @param component
     * @param width
	 */
	public static void setComponentWidth(JComponent component, int width) {
		
		component.setPreferredSize(new Dimension(
				width, (int) component.getPreferredSize().getHeight()));
	}
	
	/**
     * Sets an EmptyBorder (display the background only) with the 
     * default Gbc border inset, usually for a JPanel of a JFrame.
     *
     * @param component 	the JComponent object to receive the border
	 */
	public static void setDefaultEmptyBorder(JComponent component) {
		
		int inset = Gbc.getDefaultBorderInset();
		component.setBorder(new EmptyBorder(inset, inset, inset, inset));
	}

	/**
	 * Convenience method: show an error dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void errorDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Convenience method: show an info dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void infoDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Convenience method: show a warning dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void warnDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
