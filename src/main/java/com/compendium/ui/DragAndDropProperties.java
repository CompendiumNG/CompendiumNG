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

package com.compendium.ui;

/**
 * Properties for Drag and Drop actions.
 * @author rudolf
 */
public class DragAndDropProperties implements Cloneable {
	
	/** Copy folder as map node or link to original folder? */
	public boolean dndFolderMap = false;
	
	/** Copy folders recursively? */
	public boolean dndFolderMapRecursively = false;
	
	/** Prompt for dropped folder? */
	public boolean dndFolderPrompt = true;
	
	/** Copy file or link to original? */
	public boolean dndFileCopy = false;
	
	/** Copy to linked files folder or to database? */
	public boolean dndFileCopyDatabase = false;
	
	/** Prompt for dropped file? */
	public boolean dndFilePrompt = true;

	/** Don't prompt for text drop **/
	public boolean dndNoTextChoice = false;
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override public DragAndDropProperties clone() {
		DragAndDropProperties prop = new DragAndDropProperties();
		prop.dndFolderMap = this.dndFolderMap;
		prop.dndFolderMapRecursively = this.dndFolderMapRecursively;
		prop.dndFolderPrompt = this.dndFolderPrompt;
		prop.dndFileCopy = this.dndFileCopy;
		prop.dndFileCopyDatabase = this.dndFileCopyDatabase;
		prop.dndFilePrompt = this.dndFilePrompt;
		prop.dndNoTextChoice = this.dndNoTextChoice;
		return prop;
	}
}
