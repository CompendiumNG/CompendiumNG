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

import java.util.Vector;

/**
 * Class Union
 *
 *     This class is used for the BYacc parser. The current
 *     version only supports integer return values from the
 *     lexer. This union class will enable the parser to
 *     support other types as well. After generating the
 *     parser code from BYacc the definition of the
 *     semantic values have to be altered to be of type
 *     Union. The attributes are made public to allow for
 *     minimal changes in the generated code. The generated
 *     code makes direct references.
 *
 * @author ?
 */
public class Union {

 	public int ival;
	public long lval;
	public char cval;
  	public float fval;
 	public double dval;
  	public boolean bval;
	public int relopval;
  	public String sval;
  	public Object oval;
	public Vector vval;

  	public Union() {
		ival = 0;
		lval = 0;
		fval = 0.0f;
		dval = 0.0;
		bval = true;
		sval = "";
		relopval = 0;
		oval = null;
		vval = null;
  	} 
}
