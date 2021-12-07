
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
package qemujuicy.vm;

import java.util.EnumSet;

/**
 * Enumeration of sound hardware used by QEMU. 
 */
public enum Sound {
	
    NONE("None/Advanced Tab", ""),				// left to user: custom defined on Advanced-Tab
    ALSA_HDA("Host Alsa, Intel High Def.", 
       		"-audiodev", "alsa,id=snd0,out.try-poll=off", 
       		"-device", "ich9-intel-hda", 
       	    "-device", "hda-output,audiodev=snd0"),
    PULSEAUDIO_HDA("Pulseaudio, Intel High Def.", 
       		"-audiodev", "pa,id=hda,out.mixing-engine=off", 
       		"-device", "intel-hda", 
       	    "-device", "hda-output,audiodev=hda"),
    AC97("PC, Intel AC97", 
    		"-audiodev", "driver=pa,id=pa1", 
    		"-device", "AC97,audiodev=pa1"),
    ;
	
	public static final EnumSet<Sound> ALL = EnumSet.allOf(Sound.class);
	public static final Sound[] ARRAY = ALL.toArray(new Sound[0]);
	
	private String name;
	private String[] qemuParameters;
	
	/**
	 * Construction.
	 * 
	 * @param name				the name to be displayed
	 * @param qemuParameters	the parameters for QEMU of this method/card
	 */
	Sound(String name, String... qemuParameters) {
		
		this.name = name;
		this.qemuParameters = qemuParameters;
	}

	/**
	 * Finds the JComboBox selection index for the sound method/card of a VM.
	 * 
	 * @param vm
	 * @return the JComboBox selection index
	 */
	public static int findCbxIndexFor(VM vm) {
		
		String sound = vm.getSound();
		if (sound.trim().equals("")) {
			// Sound.NONE value
			return 0;
		}
		for (int i = 0; i < ARRAY.length; i++) {
			if (ARRAY[i].name().equals(sound)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		
		return name;
	}

	/**
	 * @return an array of sound method/card names
	 */
	public static String[] getNameArray() {

		String[] array = new String[ARRAY.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = ARRAY[i].name;
		}
		return array;
	}
	
	/**
	 * @param index
	 * @return the name of the sound
	 */
	public static String getSound(int index) {
		
		return ARRAY[index].name();
	}

	/**
	 * @param soundIndex
	 * @return the parameter array of the sound
	 */
	public static String[] getParameters(int soundIndex) {

		return ARRAY[soundIndex].qemuParameters;
	}
}


