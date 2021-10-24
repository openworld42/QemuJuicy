
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

import java.util.*;

/**
 * Msg (short for Message): a utility to provide strings/messages for other languages.
 * At the beginning, messages are hard coded and may be replaced later by
 * database text or a resources bundle, containing other messages of other languages.
 * 
 * This approach is a simple message system for an easy startup with the ability to extend
 * it later for other languages (just store another EnumMap in the class Message of the other language).
 * 
 * Msg is always combined with the enumeration class Message, containing the tags and
 * their string values.
 *
 * @see Message
 */
public class Msg {

	private static Msg instance = new Msg();				// singleton instance

	private EnumMap<Message, String> msgEnumMap;			// a map containing all messages

	/**
	 * Singleton creation using hard coded messages.
	 */
	private Msg() {

		msgEnumMap = Message.getMessagesEnumMap();
	}

	/**
	 * @return the message by id (its Enumeration)
	 */
	public static String get(Message msg) {

		return instance.msgEnumMap.get(msg);
	}

	/**
	 * @return the message with parameters by id (its Enumeration)
	 */
	public static String get(Message msg, Object ... args) {

		String s = instance.msgEnumMap.get(msg);
		return Util.stringParam(s, args);
	}

 	/**
	 * @return the singleton instance
	 */
	public static Msg instance() {

		return instance;
	}

	/**
	 * Replace the current ui/system/status/info/error messages with another language or source.
	 *
	 * @param enumMap		set another enumMap for the messages (usually for other languages)
	 */
	public static void replaceMessages(EnumMap<Message, String> enumMap) {

//		Logger.info(getClass().getSimpleName() + ": replacing messages ...");

		instance.msgEnumMap = enumMap;
	}
}
