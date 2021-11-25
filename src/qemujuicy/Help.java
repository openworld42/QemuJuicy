
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

import java.net.*;

import javax.help.*;

/**
 * Help handler.
 * 
 * @see https://docs.oracle.com/cd/E19253-01/819-0913/819-0913.pdf
 */
public class Help {

	public static final String HELP_SET = "/qemujuicy/help/helpSet.hs";			// resource path (jar and class files)
	
	private HelpBroker helpBroker;
	
	/**
	 * Create and initiate the help system
	 */
	public Help() {
		
		Logger.info("intiating help system");
		try {      
			URL hsURL = Images.class.getResource(HELP_SET);
			HelpSet helpSet = new HelpSet(null, hsURL);   
			helpBroker = helpSet.createHelpBroker();
			// set a help key (usually F1)
			helpBroker.enableHelpKey(Main.getMainView().getRootPane(), "overview", helpSet);
//			displayHelp = new CSH.DisplayHelpFromSource(helpBroker);
		} catch (Exception e) {
			Util.verbose("Help: cannot load HelpSet "+ HELP_SET);
			Logger.error("Help: cannot load HelpSet "+ HELP_SET, e);
		}
	}

	/**
	 * @return the helpBroker
	 */
	public HelpBroker getHelpBroker() {
		
		return helpBroker;
	}

	/**
	 * Shows the help system.
	 */
	public void show() {
		
		Logger.info("showing help system");
		helpBroker.setCurrentView("TOC");
		helpBroker.setDisplayed(true);
	}
}
