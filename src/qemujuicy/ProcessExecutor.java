
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

import java.io.*;
import java.util.concurrent.*;

/**
 * Executor of a process.
 * The output of the process is stored in a String object.
 */
public class ProcessExecutor  {

	private StringBuilder out = new StringBuilder();
	private int exitValue;

	/**
	 * Construction and execute a process.
	 * 
	 * @param command			command and parameters
	 * @throws Exception
	 */
	public ProcessExecutor(String... command) throws Exception {

		ProcessBuilder builder = new ProcessBuilder(command);
		
		Process process = builder.start();
		process.waitFor(10L, TimeUnit.SECONDS);
		exitValue = process.exitValue();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				out.append(line);
				out.append("\n");
			}
			System.out.println(out);
		}
	}

	/**
	 * @return the exitValue
	 */
	public int getExitValue() {
		
		return exitValue;
	}

	/**
	 * @return the output of the executed command
	 */
	public String getOutput() {
		
		return out.toString();
	}
}