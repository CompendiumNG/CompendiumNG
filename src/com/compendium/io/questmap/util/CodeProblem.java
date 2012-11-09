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

package com.compendium.io.questmap.util;

/**
 * class CodeProblem
 *
 * @author ?
 */
public class CodeProblem extends Message {

    protected int line;
    protected int pos;
    protected String file;
    protected boolean posSet;

    public String getFile() {
        return file;
    }

    public void setLine(int l) {
        line = l;
    }

    public int getLine() {
        return line;
    }

    public String toString() {
        if (posSet)
            return getFile() + "(l:" + new Integer(getLine()) + " p:" + new Integer(getPos()) + "):" + getMessage();
        else
            return getFile() + "(" + new Integer(getLine()) + "):" + getMessage();
    }

    public CodeProblem(String c) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
    }

    public CodeProblem(String c, int l, String f) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
        line = l;
        file = f;
    }

    public CodeProblem(String c, int l, int p, String f) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
        line = l;
        pos = p;
        file = f;
        posSet = true;
    }

    public void setPos(int p) {
        pos = p;
        posSet = true;
    }

    public int getPos() {
        return pos;
    }

    public void setFile(String f) {
        file = f;
    }
}
