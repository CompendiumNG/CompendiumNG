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

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.beans.*;

/**
 * The PCObject defines a compendium object.
 * Every compendium object must derive from this object.
 *
 * @author	Rema and Sajid
 */
public class PCObject implements IPCObject, java.io.Serializable {

	/** Every PCObject has a reference to the model it belongs to. */
	protected IModel	oModel					= null;

	/** Every PCSession has a reference to the session of which this object is part of. */
	protected PCSession oSession				= null ;

	/** Property change support and support for propagation of events. */
	//protected PropertyChangeSupport oChangeSupport = null ;

	/** Holds a list of registered property change listeners. */
	private Vector listenerList = new Vector();

	/**
	 * Constructor, creates a new Project Compendium Object
	 */
	public PCObject() {}

	/**
	 * Intialize this object with its model and session.
	 *
	 * @param PCSession session, the session of which this object is part of.
	 * @param IModel model, the model to which this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {

		oModel = model;
		oSession = session;

		// set up the object in the model for bidirectional relationship and cache management
		// THIS CACHE IS NOT USED AT PRESENT
		//model.addObject(this);

		// set up property change support for firing property changes to the GUI
		//oChangeSupport = new PropertyChangeSupport(this) ;
	}

	/** Does nothing. */
	public void finalize() {}

	/**
	 * 	Free property change support class.
	 */
	public void cleanUp() {
		//oChangeSupport = null;
	}

	/**
	 * This method does a deep clone on a object.
	 * Since the normal Java clone method does a shallow clone, we need a cloning operation
	 * which is deep and __exact__. We do a clone using byte serialization!.
	 *
	 * @param Object the object to be cloned
	 * @return Object the cloned object
	 */
	public static Object cloneObject(Object o) throws Exception {

	   ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	   ObjectOutputStream out     = new ObjectOutputStream(bOut);

	   out.writeObject(o);

	   ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
	   ObjectInputStream in     = new ObjectInputStream(bIn);

	   return(in.readObject());
	}


	/**
	 * Returns the list of listeners registered to this object.
	 * @return Returns the listenerList.
	 * Lakshmi (2/13/06)
	 */
	public Vector getListenerList() {
		return listenerList;
	}
	
	/**
	 * Defines the model of which this object is part of
	 *
	 * @param model The model to which this object belongs to
	 */
	public void setModel(IModel model) {
		oModel = model ;
	}

	/**
	 * Returns the model of which this object is part of.
	 *
	 * @return IModel, the model to which this object belongs to.
	 */
	public IModel getModel()  {
		return oModel;
	}

	/**
	 * Defines the session of which this object is part of.
	 *
	 * @param PCSession session, the session of which this object is part of.
	 */
	public void setSession(PCSession session) {
		oSession = session ;
	}

	/**
	 * Returns the session of which this object is part of.
	 *
	 * @return PCSession, the session of which this object is part of.
	 */
	public PCSession getSession()  {
		return oSession;
	}

   /**
    * Support for reporting bound property changes. If oldValue and
    * newValue are not equal and the PropertyChangeEvent listener list
    * isn't empty, then fire a RemotePropertyChange event to each listener.
    * This method has an overloaded method for each primitive type.  For
    * example, here's how to write a bound property set method whose
    * value is an int:
    * <pre>
    * public void setFoo(int newValue) {
    *     int oldValue = foo;
    *     foo = newValue;
    *     firePropertyChange("foo", oldValue, newValue);
    * }
    * </pre>
    *
    * @param propertyName  The programmatic name of the property that was changed.
    * @param oldValue  The old value of the property.
    * @param newValue  The new value of the property.
    */
  	protected synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
   		//if (oChangeSupport != null) {
	  	//	oChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		//}

   		for (int i = listenerList.size()- 1 ; i >= 0 ; i--) {

			//if (propertyName == NodeSummary.NODE_TYPE_PROPERTY) {
			//	System.out.println("firing update for property type change + "+((PropertyChangeListener)listenerList.elementAt(i)).getClass().getName());
			//}

	   		((PropertyChangeListener)listenerList.elementAt(i)).propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
   		}
  	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
  	public void firePropertyChange(String propertyName, byte oldValue, byte newValue){
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
		//}
		firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
 	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, char oldValue, char newValue)  {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
		//}
		firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, short oldValue, short newValue)  {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
	  	//	oChangeSupport.firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
		//}
		firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
  	}

   /**
   	* @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, int oldValue, int newValue)  {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
		//}
		firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, long oldValue, long newValue)  {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
	 	//	oChangeSupport.firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
		//}
		firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
  	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, float oldValue, float newValue) {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
		//}
		firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
	}

	/**
	 * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void firePropertyChange(String propertyName, double oldValue, double newValue)  {
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
		//}
		firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
	}

   /**
    * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    */
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue){
		//if ((oChangeSupport != null) && (oldValue != newValue)) {
		//	oChangeSupport.firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
		//}
		firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
	}

   /**
    * Add a PropertyChangeListener to the listener list.
    * The listener is registered for all properties.
    * <p>
    * A PropertyChangeEvent will get fired in response to setting
    * a bound property, e.g. setFont, setBackground, or setForeground.
    * Note that if the current component is inheriting its foreground,
    * background, or font from its container, then no event will be
    * fired in response to a change in the inherited property.
	*
    * @param listener  The PropertyChangeListener to be added
    */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		//if (oChangeSupport == null) {
		//	oChangeSupport = new PropertyChangeSupport(this);
		//}
		//oChangeSupport.addPropertyChangeListener(listener);
		listenerList.addElement(listener);
	}

   /**
    * Remove a PropertyChangeListener from the listener list.
    * This removes a PropertyChangeListener that was registered
    * for all properties.
    *
    * @param listener  The PropertyChangeListener to be removed
    */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener)  {
		//if (oChangeSupport != null) {
		//	oChangeSupport.removePropertyChangeListener(listener);
		//}
		listenerList.removeElement(listener);
	}
}
