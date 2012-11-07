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


/**
 * The ShortCutNodeSummary object represents a short cut to a Node
 * in the system. The short cut can be created or deleted, its reference
 * cannot be reset. So this class does not broadcast or listen to any property changes.
 *
 * @author	rema and sajid
 */
public interface IShortCutNodeSummary extends INodeSummary {

	/**
	 *	Returns the node summary to which this short cut node points to
	 *
	 *	@return the INodeSummary of the referred node
	 */
	public NodeSummary getReferredNode();

	/**
	 *	Sets the node summary of the node to which this short cut node points to, in the local data ONLY.
	 *
	 *	@param NodeSummary nodeSummary, the node summary to which this short cut points to.
	 */
	public void setReferredNode(NodeSummary oNodeSummary);
}
