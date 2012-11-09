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

package com.compendium.core.datamodel;

import com.compendium.core.ICoreConstants;

/**
 * The IPCObject defines the interface for a remote project compendium
 * object. Every interface for a remote project compendium object must
 * derive from this object.
 *
 * @author	rema and sajid
 */
public interface IPCObject extends ICoreConstants {

	/**
	 * Defines the model of which this object is part of.
	 *
	 * @param model The model to which this object belongs to.
	 */
	public void setModel(IModel model) ;

	/**
	 * Returns the model of which this object is part of.
	 *
	 * @return The model to which this object belongs to.
	 */
	public IModel getModel() ;

	/**
	 * Defines the session of which this object is part of.
	 *
	 * @param PCSession session, the session of which this object is part of.
	 */
	public void setSession(PCSession session);

	/**
	 * Returns the session of which this object is part of.
	 *
	 * @return PCSession, the session of which this object is part of.
	 */
	public PCSession getSession();
}
