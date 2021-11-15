
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

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

/**
 * Container for all application images.
 */
public class Images {

	// resource path prefixes (to be loaded from qemujuicy.images.**, packed within the applications jar file)
	public static final String IMAGES = "/qemujuicy/images/";		
	public static final String OXY32 = IMAGES + "oxygen32x32/";				// or smaller
	public static final String OXY128 = IMAGES + "oxygen128x128/";
	public static final String AQEMU_OS = IMAGES + "aqemu/os_icons/";
	
	public static final String APPLY_BUTTON = OXY128 + "Oxygen480-actions-dialog-ok-apply.svg.png";
	public static final String APP_ICON = OXY128 + "Oxygen480-actions-office-chart-ring2.png";
	public static final String ARROW_RIGHT = OXY128 + "Oxygen480-actions-arrow-right.svg.png";
	public static final String BACK_BUTTON = OXY32 + "go-previous.png";
	public static final String CANCEL_BUTTON = OXY32 + "Oxygen15.04.1-dialog-cancel.svg.png";
	public static final String CONFIG = OXY128 + "Oxygen480-categories-preferences-other.svg.png";
	public static final String DOWN_BUTTON = OXY128 + "Oxygen480-actions-go-down.svg.png";
	public static final String DOWN_SEARCH = OXY128 + "Oxygen480-actions-go-down-search.svg.png";
	public static final String EXIT_BUTTON = OXY32 + "Actions-application-exit-icon.png";
	public static final String FIRST_SETUP_WIZARD_BUTTON = OXY32 + "Oxygen480-actions-games-solve.svg.png";
	public static final String FOLDER = OXY128 + "Oxygen480-places-folder-blue.svg.png";
	public static final String HARDDISK = OXY128 + "Oxygen480-devices-drive-harddisk.svg.png";
	public static final String LIST_REMOVE = OXY128 + "Oxygen480-actions-list-remove.svg.png";
	public static final String NETWORK = OXY128 + "Oxygen480-mimetypes-application-x-smb-workgroup.svg.png";
	public static final String NEXT_BUTTON = OXY32 + "go-next.png";
	public static final String OK_BUTTON = OXY128 + "Oxygen480-actions-dialog-ok.svg.png";
	public static final String PROCESS_STOP = OXY128 + "Oxygen15.04.1-process-stop.svg.png";
	public static final String QEMU = IMAGES + "qemu_128x128.png";
	public static final String QEMU28x28 = IMAGES + "qemu_28x28.png";
	public static final String QEMU32x32 = IMAGES + "qemu_32x32.png";
	public static final String SETUP_WIZARD = OXY128 + "Oxygen480-actions-games-solve.svg.png";
	public static final String UP_BUTTON = OXY32 + "Oxygen15.04.1-go-up.svg.png";
	public static final String VM = OXY32 + "Oxygen480-categories-applications-system2_20x20.svg.png";
	public static final String VM_WIZARD = OXY32 + "Oxygen480-actions-list-add.svg.png";
	public static final String VM_WIZARD_WAND = OXY32 + "Oxygen480-actions-wizard-games-solve3.png";
	public static final String VM_WIZARD_IDENT = OXY32 + "Oxygen480-actions-im-user.svg.png";
	// predefined OS icons
	public static final String OS_ICON_LINUX_PATH = OXY32 + "Oxygen-actions-im-qq.svg.png";
	public static final String OS_ICON_WINDOWS_PATH = AQEMU_OS + "default_windows32x32.png";
	public static final String OS_ICON_MAC_PATH = AQEMU_OS + "mac32x32.png";
	public static final String OS_ICON_OTHER_PATH = AQEMU_OS + "other32x32.png";
	public static final String OS_ICON_BSD_GENERIC_PATH = AQEMU_OS + "freebsd32x32.png";
	public static final String OS_ICON_DEBIAN_PATH = AQEMU_OS + "debian32x32.png";
	public static final String OS_ICON_UBUNTU_PATH = AQEMU_OS + "kubuntu_v2_32x32.png";
	public static final String OS_ICON_OPEN_SUSE_PATH = AQEMU_OS + "suse32x32.png";
	public static final String OS_ICON_RHEL_PATH = AQEMU_OS + "redhat32x32.png";
	public static final String OS_ICON_ARCH_PATH = AQEMU_OS + "arch32x32.png";
	public static final String OS_ICON_MINIX_PATH = AQEMU_OS + "minix32x32.png";
	public static final String OS_ICON_SOLARIS_PATH = AQEMU_OS + "solaris32x32.png";
	public static final String OS_ICON_FEDORA_PATH = AQEMU_OS + "fedora32x32.png";
	public static final String OS_ICON_MINT_PATH = AQEMU_OS + "mint32x32.png";

	private static Images instance;						// singleton instance
	private HashMap<String, ImageIcon> iconMap;
	
	/**
	 * Construction. This will load the icons.
	 */
	private Images() {
		
		iconMap = new HashMap<String, ImageIcon>();
		add(APPLY_BUTTON);
		add(APP_ICON);
		add(ARROW_RIGHT);
		add(BACK_BUTTON);
		add(CANCEL_BUTTON);
		add(CONFIG);
		add(DOWN_BUTTON);
		add(DOWN_SEARCH);
		add(EXIT_BUTTON);
		add(FIRST_SETUP_WIZARD_BUTTON);
		add(FOLDER);
		add(HARDDISK);
		add(LIST_REMOVE);
		add(NETWORK);
		add(NEXT_BUTTON);
		add(OK_BUTTON);
		add(PROCESS_STOP);
		add(QEMU);
		add(QEMU28x28);
		add(QEMU32x32);
		add(SETUP_WIZARD);
		add(UP_BUTTON);
		add(VM);
		add(VM_WIZARD);
		add(VM_WIZARD_WAND);
		add(VM_WIZARD_IDENT);
		// predefined OS icons
		add(OS_ICON_LINUX_PATH);
		add(OS_ICON_WINDOWS_PATH);
		add(OS_ICON_MAC_PATH);
		add(OS_ICON_OTHER_PATH);
		add(OS_ICON_BSD_GENERIC_PATH);
		add(OS_ICON_DEBIAN_PATH);
		add(OS_ICON_UBUNTU_PATH);
		add(OS_ICON_OPEN_SUSE_PATH);
		add(OS_ICON_RHEL_PATH);
		add(OS_ICON_ARCH_PATH);
		add(OS_ICON_MINIX_PATH);
		add(OS_ICON_SOLARIS_PATH);
		add(OS_ICON_FEDORA_PATH);
		add(OS_ICON_MINT_PATH);
	}

	/**
	 * Adds a created ImageIcon to the map of icons.
	 * 
	 * @param resourcePath		the path to the icon resource
	 */
	private void add(String resourcePath) {
		
		URL imageURL = Images.class.getResource(resourcePath);
		if (imageURL == null) {
		    System.out.println("imageURL == null: '" + resourcePath + "'");
		    System.exit(1);
		}
		iconMap.put(resourcePath, new ImageIcon(imageURL));
	}

	/**
	 * Returns an ImageIcon by a path or resource path definition, 
	 * usually used for VM icons. 
	 * If the ImageIcon is not found within the resources (a resource path), a
	 * second try will load it from a file path. If this also fails, a default 
	 * icon is returned.
	 * 
	 * @param path		a resource or file path to the icon
	 * @return the icon
	 */
	public static ImageIcon find(String path) {
		
		// preloaded icon?
		ImageIcon icon = get(path);
		if (icon != null && icon.getIconWidth() > 0) {
			return icon;
		}
		if (icon == null) {
			// resource path icon?
			URL imageURL = Images.class.getResource(path);
			if (imageURL != null) {
				icon = new ImageIcon(imageURL);
				if (icon != null) {
					return icon;
				}
			}
		}
		// file path icon?
		File file = new File(path);
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			// neither resource nor existing file, use a default
			return get(OS_ICON_OTHER_PATH);
		}
		icon = new ImageIcon(path);
		if (icon.getIconWidth() <= 0) {
			// this was not an icon file
			return get(OS_ICON_OTHER_PATH);
		}
		return icon;
	}

	/**
	 * Returns a previously created ImageIcon by its key.
	 * 
	 * @param resourcePath		the path to the icon resource
	 */
	public static ImageIcon get(String resourcePath) {
		
		return instance.iconMap.get(resourcePath);
	}

	/**
	 * Instantiate this singleton and load all the icons.
	 */
	public static void init() {
		
		instance = new Images();
	}
	
	/**
	 * Returns an ImageIcon scaled to pixels * pixels.
	 * 
	 * @param resourcePath
	 * @param pixels
	 * @return an ImageIcon scaled to pixels*pixels
	 */
	public static ImageIcon scale(String resourcePath, int pixels) {
		ImageIcon scaled = new ImageIcon();
		scaled.setImage(get(resourcePath).getImage().getScaledInstance(pixels, pixels, Image.SCALE_DEFAULT));
		return scaled;
	}

	/**
	 * Returns an ImageIcon scaled to pixels*pixels.
	 * 
	 * @param icon
	 * @param pixels
	 * @return an ImageIcon scaled to pixels*pixels
	 */
	public static ImageIcon scale(ImageIcon icon, int pixels) {
		
		ImageIcon scaled = new ImageIcon();
		scaled.setImage(icon.getImage().getScaledInstance(pixels, pixels, Image.SCALE_DEFAULT));
		return scaled;
	}
}
