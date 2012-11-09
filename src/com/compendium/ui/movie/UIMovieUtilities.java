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

package com.compendium.ui.movie;

import java.util.*;

import com.compendium.ui.movie.UIMoviePanel;


/**
 * UIMovieUtilities contains methods to help movies classes
 *
 * @author	Michelle Bachler
 */
public class UIMovieUtilities {
	
	/**
	 * Sort the given Vector of UIMoviePanel objects.
	 *
	 * @param Vector unsortedVector, the vector of Objects to sort.
	 * @return Vector, or sorted objects.
	 */
	public static Vector sortList(Vector unsortedVector) {

		Vector sortedVector = new Vector();
		Vector unsortedVector2 = new Vector();

		if(unsortedVector.size() > 0) {
			if(unsortedVector.elementAt(0) instanceof UIMoviePanel) { //For Movie panels
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					Object obj = (Object)e.nextElement();
					if (obj != null && obj instanceof UIMoviePanel) {
						String text = ((UIMoviePanel)obj).getMovieData().getMovieName();
						unsortedVector2.addElement(text);
					}
				}
			}
			else {
				return unsortedVector;
			}
		}
		else {
			return unsortedVector;
		}

		Object[] sa = new Object[unsortedVector2.size()];
		unsortedVector2.copyInto(sa);
		java.util.List l = Arrays.asList(sa);

		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				String s1 = (String)o1;
				if (s1 == null)
					s1 = ""; //$NON-NLS-1$

				String s2 = (String)o2;
				if (s2 == null)
					s2 = ""; //$NON-NLS-1$

				String s1L = s1.toLowerCase();
		        String s2L = s2.toLowerCase();
				return  (s1L.compareTo(s2L));
			}
		});

	 	// add sorted elements from list to vector
	 	for (Iterator it = l.iterator(); it.hasNext(); ) {
			String sortedElement = (String) it.next();

			// add it to vector rearranged with the objects
			for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {

				Object pcobject = (Object)e.nextElement();

				String text = ""; //$NON-NLS-1$
				if (pcobject instanceof UIMoviePanel) {
					text = ((UIMoviePanel)pcobject).getMovieData().getMovieName();
				} 
				
				if (text.equals(sortedElement)) {
					sortedVector.addElement(pcobject);

					//remove this element so it can't be found again in case there
					//is more than one object with the same text.
					unsortedVector.removeElement(pcobject);
					break;
				}
			}
		 }

		return sortedVector;
	}

	/**
	 * Calculate the given percentage of the given movie duration and return as a long.
	 * @param duration movie duration
	 * @param percentage percentage to return
	 * @return the given percentage of the given movie duration and return as a long
	 */
	public static long getMovieDurationPercentage(double duration, int percentage) {
    	double per = (duration/100)*percentage;
    	long finalPer = new Double(per).longValue();
    	return finalPer;
	}
}
