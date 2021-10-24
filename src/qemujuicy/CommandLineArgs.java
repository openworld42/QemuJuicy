
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

/**
 * Command line parser and option container for the program.
 */
public class CommandLineArgs {

	private boolean isValid;
	private int cliIndex;
	
	private boolean isVerbose = true;

	/**
	 * Construct CommandLineArgs using the command line arguments.
	 * 
	 * @param args 
	 */
	public CommandLineArgs(String[] args) {

//	    System.out.println("Argument count: " + args.length);
		
//		-t -url www.xy
		
//        if (args.length < 3) {		// TODO  minimum argument check
//        	isValid = false;
//        	return;
//        }
	    for (cliIndex = 0; cliIndex < args.length; cliIndex++) {
          if (args[cliIndex].equals("-h")) {
                Usage.exit(0);
            } else if (args[cliIndex].equals("-v")) {
            	Version.print();
            	System.exit(0);
            } else if (args[cliIndex].equals("-q")) {
            	// qiet option
            	isVerbose = false;
//            } else if (args[cliIndex].equals("-url")) {
//            	// needs one additional parameter (the URL)
//            	if (args.length - cliIndex < 2) {
//                   	isValid = false;
//                	return;
//				}
//            	url = args[++cliIndex];
            } else {
            	// not a valid option
            	isValid = false;
            	return;
            }
	    }
	    
	    // checks for incompatibility
//	    if (hasTOption && url != null) {		// TODO:  option incompatibility check
//        	isValid = false;
//        	return;
//		}
	    
		isValid = true;
	}

	/**
	 * @return true if the command line parsing had no errors and incompatibilities, false otherwise
	 */
	public boolean isValid() {
		
		return isValid;
	}

	/**
	 * Flag for verbose messages to System.out.
	 * 
	 * @return the isVerbose
	 */
	public boolean isVerbose() {
		
		return isVerbose;
	}
}
