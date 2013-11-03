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

package com.compendium.io.questmap;

import java.util.Hashtable;

/**
 * class CodeTable
 *
 * @author  Ron van Hoof
 */
class CodeTable {

	protected Hashtable symboltable;
	protected Hashtable keytable;

	public CodeTable() {
		this(10);
	}

	public CodeTable(final int size) {
		symboltable = new Hashtable(size);
		keytable = new Hashtable(size);
	}

	public void addCode(final Object key, final int sym) {
		if (!symboltable.containsKey(key)) {
			symboltable.put(key, new Integer(sym));
			keytable.put(new Integer(sym), key);
		}
	}

	public void removeCode(final Object key) {
		if (symboltable.containsKey(key)) {
			keytable.remove(symboltable.get(key));
			symboltable.remove(key);
		}
	}

	public int getCode(final Object key) {
		Integer result = (Integer)symboltable.get(key);

		if (result == null) {
			result = Integer.valueOf(-1);
		}

		return result;
	}

	public Object getKey(final int sym) {
		return keytable.get(sym);
	}
}
