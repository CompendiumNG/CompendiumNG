/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.ui.edits;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.plaf.ComponentUI;

import com.compendium.core.datamodel.*;
import com.compendium.ui.*;

/**
 * Transferable class for the Clipboard.
 * This class handles multiple node/link transfers for later undo/redo edits operations.
 *
 * @author ? / Michelle Bachler
 */
public class ClipboardTransferables implements Transferable {

	/** A List of object required for the edit.*/
	private Vector	vtTransferables = new Vector();

	/**
	 * Constructor. Does nothing.
	 */
	public ClipboardTransferables() {}

	/**
	 * Add a ComponentUI object to the list for the edit undo/redo.
	 * @param componentui the component to add.
	 */
	public void addTransferables(ComponentUI componentui) {

		if(!vtTransferables.contains(componentui))
			vtTransferables.addElement(componentui);
	}

	/**
	 * Add a NodePosition object to the list for the edit undo/redo.
	 * @param componentui the NodePosition to add.
	 */
	public void addTransferables(NodePosition np) {

		if(!vtTransferables.contains(np))
			vtTransferables.addElement(np);
	}

	/**
	 * Add a Movie object to the list for cut/copy and paste action to use.
	 * @param componentui the Movie to add.
	 */
	public void addTransferables(Movie m) {

		if(!vtTransferables.contains(m))
			vtTransferables.addElement(m);
	}

	/**
	 * Returns the list of objects to use for the edit undo/redo operation.
	 * @return Enumeration, the list of objects to use for the edit undo/redo operation.
	 */
	public Enumeration getTransferables() {

		return vtTransferables.elements();
	}

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in.
     * @return an array of data flavors in which this data can be transferred, HRERE: always returns null.
     */
	public DataFlavor [] getTransferDataFlavors() {

		  return null;
	}

    /**
     * Returns whether or not the specified data flavor is supported for this object.
	 *
     * @param flavor the requested flavor for the data
     * @return boolean, always returns true
     */
	public boolean isDataFlavorSupported(DataFlavor data) {
		return true;
	}

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
	 * @return Object, returns this object.
     */
	public Object getTransferData(DataFlavor data) {
		return this;
	}
}
