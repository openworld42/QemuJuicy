
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
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import qemujuicy.*;

import static qemujuicy.Message.*;

/**
 * A generic JFileChooser dialog with language support.
 */
public class FileChooserDlg extends JFileChooser {

	private static final long serialVersionUID = 1L;

	private File selFile;
	
	/**
	 * Create the dialog and show it. If the path was set to null, cancel was clicked.
	 * 
	 * @param titel				dialog title
	 * @param openBtnText
	 * @param openBtnToolTip
	 * @param selectionMode		one of JFileChooser.FILES_ONLY, .DIRECTORIES_ONLY, .FILES_AND_DIRECTORIES
	 * @param filter			e.g. new FileNameExtensionFilter("JPG and GIF", "jpg", "gif") or null (if none)
	 */
	public FileChooserDlg(String titel, String openBtnText, String openBtnToolTip, 
			int selectionMode, FileNameExtensionFilter filter) {

		selFile = null;
		initUI();
		// set the text in JFileChooser
		UIManager.put("FileChooser.openButtonText", openBtnText);
		UIManager.put("FileChooser.openButtonToolTipText", openBtnToolTip);
		
//	    UIManager.put("FileChooser.saveButtonText","Custom text acept");
//	    setApproveButtonToolTipText("New Approve Tool Tip");
//		fileChooser.setApproveButtonText("Select " + (dirOnly ? "Directory" : "File"));
		
		SwingUtilities.updateComponentTreeUI(this);			// update UI
	    setDialogTitle(titel);
	    if (filter != null) {
	    	setFileFilter(filter);
		}
//		setMultiSelectionEnabled(false);
	    setFileSelectionMode(selectionMode);
	}

	/**
	 * @return the selected File or null
	 */
	public File getFilePath() {
		
		return selFile;
	}

	private void initUI() {
		
		/*
		FileChooser.fileNameLabelText
		FileChooser.homeFolderToolTipText
		FileChooser.newFolderToolTipText
		FileChooser.listViewButtonToolTipTextlist
		FileChooser.detailsViewButtonToolTipText
		FileChooser.saveButtonText=Save
		FileChooser.openButtonText=Open
		FileChooser.cancelButtonText=Cancel
		FileChooser.updateButtonText=Update
		FileChooser.helpButtonText=Help
		FileChooser.saveButtonToolTipText=Save
		FileChooser.openButtonToolTipText=Open
		FileChooser.cancelButtonToolTipText=Cancel
		FileChooser.updateButtonToolTipText=Update
		FileChooser.helpButtonToolTipText=Help
		 */
		// icons (if we will change them
		/*
		UIManager.put("FileView.directoryIcon", new ImageIcon(FileSystem.class.getResource("folder.png")));
		UIManager.put("FileChooser.homeFolderIcon", new ImageIcon(FileSystem.class.getResource("user-home.png")));
		UIManager.put("FileView.computerIcon", new ImageIcon(FileSystem.class.getResource("computer.png")));
		UIManager.put("FIleView.floppyDriveIcon", new ImageIcon(FileSystem.class.getResource("media-floppy.png")));
		UIManager.put("FileView.hardDriveIcon", new ImageIcon(FileSystem.class.getResource("drive-harddisk.png")));
		UIManager.put("FileView.fileIcon", new ImageIcon(FileSystem.class.getResource("file.png")));
		UIManager.put("FileChooser.upFolderIcon", new ImageIcon(FileSystem.class.getResource("go.png")));
		UIManager.put("FileChooser.newFolderIcon", new ImageIcon(FileSystem.class.getResource("folder-new.png")));
		UIManager.put("FileView.fileIcon", new ImageIcon(FileSystem.class.getResource("file.png")));
		UIManager.put("FileChooser.listViewIcon", new ImageIcon(FileSystem.class.getResource("listIcon.png")));
		UIManager.put("FileChooser.detailsViewIcon", new ImageIcon(FileSystem.class.getResource("details.png")));
		*/
		
		// TODO xxx    FileChooserDlg  		following messages in Message.java 
		
		UIManager.put("FileChooser.lookInLabelText", "Look in");
		UIManager.put("FileChooser.cancelButtonText", Msg.get(CANCEL_BTN_MSG));
		UIManager.put("FileChooser.cancelButtonToolTipText",Msg.get(CANCEL_BTN_MSG));
		UIManager.put("FileChooser.fileNameHeaderText","File name");
		UIManager.put("FileChooser.fileNameLabelText", "File name");
		UIManager.put("FileChooser.filesOfTypeLabelText", "File types");
		UIManager.put("FileChooser.filterLabelText", "File types");
		UIManager.put("FileChooser.homeFolderToolTipText","Home");
		UIManager.put("FileChooser.upFolderToolTipText", "Up one level");
		UIManager.put("FileChooser.newFolderButtonText","Create new folder");
		UIManager.put("FileChooser.newFolderToolTipText","Create new folder");
		UIManager.put("FileChooser.listViewButtonToolTipText","List");
		UIManager.put("FileChooser.renameFileButtonText", "Rename file");
		UIManager.put("FileChooser.deleteFileButtonText", "Delete file");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Details");
		UIManager.put("FileChooser.fileSizeHeaderText","Size");
		UIManager.put("FileChooser.fileDateHeaderText", "Date modified");
	}
	
	@Override
	public int showOpenDialog(Component parent) {
		
		int retVal = super.showOpenDialog(parent);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			selFile = getSelectedFile();
		}
		return retVal;
	}
}
