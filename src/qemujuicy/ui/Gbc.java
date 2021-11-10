
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
import java.util.*;

import javax.swing.*;

/** 
 * Utility to create a <code>GridBagConstraint</code> object.
 *
 * <p><code>Gbc</code> may be used together with <code>GridBagLayout</code>
 * and has various constructors to serve as a utility
 * that supersedes/extends <code>GridBagConstraint</code> for convenience.
 * See the example below to get an impression of <code>Gbc</code>.</p>
 * <p>Example of creation of some GUI elements:</p>
 * <pre>
 * public class MyLovelyView extends JFrame {
 *
 *      public MyLovelyView(String title) {
 *
 *          super(title);
 *          mainPanel = new JPanel();
 *          mainPanel.setLayout(new GridBagLayout());
 *          getContentPane().add(mainPanel);
 *          // add a label at x = 0, y = 0: anchor is west, extended insets at top and left side
 *          mainPanel.add(new JLabel("This is a label"), new Gbc(0, 0, "W tl");
 *          JTextField myTextField = new JTextField();
 *          myTextField.setPreferredSize(new Dimension(...));
 *          myTextField.addFocusListener(...);
 *          // add a JTextField at x = 1, y = 0, using two columns: anchor is west, 
 *          // extended insets at the top side, stretching in Both directions (horizontal and vertical)
 *          mainPanel.add(myTextField, new Gbc(1, 0, 2, 1, "W B t");
 *          // add a button: x = 3, y = 1 (next row), anchor is south-east, stretching in horizontal direction
 *          mainPanel.add(new JButton("It's me!"), new Gbc(3, 1, "SE H");
 *          .
 *          .
 * </pre>
 * <p>By convention, <code>Gbc</code> accepts several parameters for a
 * <code>GridBagConstraint</code> object and a control string. If no
 * <code>Insets</code> are passed to <code>Gbc</code>, it uses a default
 * inset as a spacing to other swing components and another to define
 * the spacing to the borders of the surrounding frame.</p>
 * <p>In the above example, the label would
 * be positioned at (0, 0) of the <code>GridBagLayout</code>, it's size
 * would fit one <code>GridBagLayout</code> cell (the default), it's anchor
 * is west, it has the default border spacing at top and left with bottom
 * and right having the default inter-cell spacing.
 * The label is positioned at (1, 0) with a horizontal width of two cells,
 * it anchors west, fills the cells horizontal and vertical (B - both),
 * has the default border spacing at top and default spacing in all other
 * directions.
 * The button's position is at (3, 1), it anchors south-east and fills its
 * cell horizontal. It has inter-cell spacing only.</p>
 * <p>The control string may have up to 3 parameters separated by a blank.
 * The order does not matter. The 3 optional parameters are:</p>
 * <pre>
 * Anchor (the default is GridBagConstraints.CENTER):
 *
 *      C   ... GridBagConstraints.CENTER
 *      N   ... GridBagConstraints.NORTH
 *      E   ... GridBagConstraints.EAST
 *      S   ... GridBagConstraints.SOUTH
 *      W   ... GridBagConstraints.WEST
 *      NE  ... GridBagConstraints.NORTHEAST
 *      NW  ... GridBagConstraints.NORTHWEST
 *      SE  ... GridBagConstraints.SOUTHEAST
 *      SW  ... GridBagConstraints.SOUTHWEST
 *
 * Fill (the default is GridBagConstraints.NONE):
 *
 *      H   ... GridBagConstraints.HORIZONTAL (the component fills its display area horizontally)
 *      V   ... GridBagConstraints.VERTICAL (the component fills its display area vertically)
 *      B   ... GridBagConstraints.BOTH (fill both, horizontally and vertically)
 *
 * Spacing (the tokens may be combined in any order, the default
 *      value is the inter-cell spacing for each direction):
 *
 *      t   ... the top side has border spacing
 *      l   ... the left side has border spacing
 *      b   ... the bottom side has border spacing
 *      r   ... right top side has border spacing
 * </pre>
 *
 * @see java.awt.GridBagConstraints
 *
 * @author Heinz Silberbauer
 */
public class Gbc extends GridBagConstraints {

	private static final long serialVersionUID = 1L;

	// constants

	public static final char TOP = 't';
    public static final char LEFT = 'l';
    public static final char BOTTOM = 'b';
    public static final char RIGHT = 'r';
    
	public static final Insets DEFAULT_INSETS = new Insets(4, 4, 4, 4);

    // static variables

    private static int defaultInset = 4;
    private static int defaultBorderInset = 8;

    /**
     * Construction with all parameters.
     * The control string may be empty or <code>null</code>.
     * If the insets parameter is null, a default <code>Insets</code>
     * with the default inter-cell spacing is used.
     *
     * @param   gridX       the x-position of the cell
     * @param   gridY       the y-position of the cell
     * @param   gridWidth  	the width of the cell in columns
     * @param   gridHeight  the height of the cell in rows
     * @param   weightX     the weight in x-direction of the cell
     * @param   weightY     the weight in y-direction of the cell
     * @param   control     the control parameter string for the cell
     * @param   insets      the Insets if no control string parameters are defined for the cell
     * @see java.awt.GridBagConstraints
     */
	public Gbc(int gridX, int gridY, int gridWidth, int gridHeight,
			double weightX, double weightY, String control, Insets insets) {

        this.gridx = gridX;
        this.gridy = gridY;
		this.gridheight = gridHeight;
		this.gridwidth = gridWidth;
		this.weightx = weightX;
		this.weighty = weightY;
        if (insets == null) {
            // create insets from default (may have changed before call)
            this.insets = new Insets(defaultInset, defaultInset, 
            		defaultInset, defaultInset);
        } else {
            this.insets = insets;
        }
		this.fill = GridBagConstraints.NONE;
        StringTokenizer tokenizer = new StringTokenizer(control.trim());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
			if (token.equals("B")) this.fill = GridBagConstraints.BOTH;
			else if (token.equals("H"))	this.fill = GridBagConstraints.HORIZONTAL;
			else if (token.equals("V"))	this.fill = GridBagConstraints.VERTICAL;
			else if (token.equals("C"))	this.anchor = GridBagConstraints.CENTER;
			else if (token.equals("N")) this.anchor = GridBagConstraints.NORTH;
			else if (token.equals("E")) this.anchor = GridBagConstraints.EAST;
			else if (token.equals("S")) this.anchor = GridBagConstraints.SOUTH;
			else if (token.equals("W")) this.anchor = GridBagConstraints.WEST;
			else if (token.equals("NE")) this.anchor = GridBagConstraints.NORTHEAST;
			else if (token.equals("NW")) this.anchor = GridBagConstraints.NORTHWEST;
			else if (token.equals("SE")) this.anchor = GridBagConstraints.SOUTHEAST;
			else if (token.equals("SW")) this.anchor = GridBagConstraints.SOUTHWEST;
            else {
                // control character field or bad token
                this.insets = createInsetsFrom(token);
            }
        }
	}

    /**
     * Construction with some defaults.
     * The control string may be empty or <code>null</code>.
     * If the insets parameter is null, a default <code>Insets</code>
     * with the default inter-cell spacing is used.
     *
     * @param   gridX       the x-position of the cell
     * @param   gridY       the y-position of the cell
     * @param   gridWidth  	the width of the cell in columns
     * @param   gridHeight  the height of the cell in rows
     * @param   weightX     the weight in x-direction of the cell
     * @param   weightY     the weight in y-direction of the cell
     * @param   control     the control parameter string for the cell
     * @see java.awt.GridBagConstraints
     */
	public Gbc(int gridX, int gridY, int gridWidth, int gridHeight,
            double weightX, double weightY, String control) {

		this(gridX, gridY, gridWidth, gridHeight, weightX, weightY, control, null);
    }

    /**
     * Construction with some defaults.
     * The control string may be empty or <code>null</code>.
     * If the insets parameter is null, a default <code>Insets</code>
     * with the default inter-cell spacing is used.
     *
     * @param   gridX       the x-position of the cell
     * @param   gridY       the y-position of the cell
     * @param   gridWidth  	the width of the cell in columns
     * @param   gridHeight  the height of the cell in rows
     * @param   control     the control parameter string for the cell
     * @see java.awt.GridBagConstraints
     */
	public Gbc(int gridX, int gridY, int gridWidth, int gridHeight, String control) {

		this(gridX, gridY, gridWidth, gridHeight, 0, 0, control);
    }

    /**
     * Construction with some defaults.
     * The control string may be empty or <code>null</code>.
     * If the insets parameter is null, a default <code>Insets</code>
     * with the default inter-cell spacing is used.
     *
     * @param   gridX       the x-position of the cell
     * @param   gridY       the y-position of the cell
     * @param   control     the control parameter string for the cell
     * @see java.awt.GridBagConstraints
     */
	public Gbc(int gridX, int gridY, String control) {

		this(gridX, gridY, 1, 1, 0, 0, control);
    }

    /**
     * Construction with some defaults.
     * The control string may be empty or <code>null</code>.
     * If the insets parameter is null, a default <code>Insets</code>
     * with the default inter-cell spacing is used.
     *
     * @param   gridX       the x-position of the cell
     * @param   gridY       the y-position of the cell
     * @see java.awt.GridBagConstraints
     */
	public Gbc(int gridX, int gridY) {

		this(gridX, gridY, 1, 1, 0, 0, "");
    }

    /**
     * Returns the default border inset if the tokens parameter
     * contains the direction character, or the default inter-cell spacing
     * inset if the direction character is not found.
     * The tokens string may be empty or <code>null</code>.
     *
     * @param   direction   the direction character to be searched for
     * @param   tokens      may contain one or more direction characters
     *      for border spacing
     */
    protected int checkForBorderInset(char direction, String tokens) {

        if (tokens != null && tokens.indexOf(direction) >= 0) {
            return defaultBorderInset;
        }
        return defaultInset;
    }

    /**
     * Creates <code>Insets</code> depending on the the  parameter.
     * The token may contain zero of more direction characters.
     * If a direction character is found in the token parameter, the
     * corresponding inset is the default border inset, or the default
     * inter-cell spacing inset if not found.
     * The token string may be empty or <code>null</code>.
     *
     * @param   token       the token string containing zero or more
     *      direction characters
     */
    protected Insets createInsetsFrom(String token) {

        String directions = new String(new char[] {TOP, LEFT, BOTTOM, RIGHT});
        Insets insets = new Insets(checkForBorderInset(TOP, token),
            checkForBorderInset(LEFT, token),
            checkForBorderInset(BOTTOM, token),
            checkForBorderInset(RIGHT, token));
        // if bad token throw IllegalArgumentException
        if (token == null) return insets;
        token = token.trim();
        for (int i = 0; i < token.length(); i++) {
            char directionChar = token.charAt(i);
            if (directions.indexOf(directionChar) < 0) {
                throw new IllegalArgumentException("Character '"
                    + directionChar + "' is not a direction "
                    + " character from '" + directions
                    + "' (top, left, bottom, right)!");
            }
        }
        return insets;
    }

    /**
     * Creates an empty component, usually to expand and compress other components in a row or column.
     * 
     * @return the empty component
     */
    public static Component filler() {
    	
    	return Box.createVerticalStrut(8);
    }
    
    /**
     * Returns the default border spacing inset.
     *
     * @return     the inset value
     */
    public static int getDefaultBorderInset() {return defaultBorderInset;}

    /**
     * Returns the default inter-cell spacing inset.
     * Since usually two cells share one cell border, this is the half
     * of the spacing between two cells.
     *
     * @return     the inset value
     */
    public static int getDefaultInset() {return defaultInset;}

    /**
     * Sets the default border spacing inset.
     *
     * @param   newDefaultBorderInset   the new inset value
     */
    public static void setDefaultBorderInset(int newDefaultBorderInset) {

        defaultBorderInset = newDefaultBorderInset;
    }

    /**
     * Sets the default inter-cell spacing inset.
     * Since usually two cells share one cell border, the new value is the half
     * of the spacing between two cells.
     *
     * @param   newDefaultInset     the new inset value
     */
    public static void setDefaultInset(int newDefaultInset) {

        defaultInset = newDefaultInset;
    }
}
