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

package com.compendium.io.jabber;

import java.util.*;
import java.net.*;

import com.compendium.ProjectCompendium;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * This class handles the Jabber client side connection for Compendium-IXPanel messaging.
 *
 * @author  Jiri Komzak /  Michelle Bachler
 */
public class IXPanel implements BSAuthListener, ConnectionListener,
                               RosterListener, BSPresenceListener,
                               BSMessageListener, PacketListener {

	/** The <code>BSConnectionBean</code> object used by this Jabber client class.*/
    protected BSConnectionBean connection;

	/** The <code>BSInfoQueryBean</code> object used by this Jabber client class.*/
    protected BSInfoQueryBean infoQuery;

	/** The <code>BSAuthorizationBean</code> object used by this Jabber client class.*/
    protected BSAuthorizationBean auth;

	/** The <code>BSRosterBean</code> object used by this Jabber client class.*/
    protected BSRosterBean roster;

	/** The <code>BSPresenceBean</code> object used by this Jabber client class.*/
    protected BSPresenceBean presence;

	/** The <code>BSMessengerBean</code> object used by this Jabber client class.*/
    protected BSMessengerBean messenger;

	/** The counter used to create message sequence numbers.*/
    protected static int idCounter;

	/** Holds the username for the jabber account this client is connecting as.*/
    public static String username = "mifflin-user";

	/** Holds the resource of the jabber account this client is connecting as.*/
    public static String resource = "uniqueID";

	/** Holds the password for the jabber account this client is connecting as.*/
    public static String password = "jabber";

	/** Holds the server info for the server this client is connecting to.*/
	public static String server = "akt.aiai.ed.ac.uk";

	/** Holds the Jabber port number used to connect on.*/
    public static int port = 5222;


	/**
	 * Constructor. Creates all the helper bean classes.
	 */
    public IXPanel() {

        idCounter = 0;

        connection = new BSConnectionBean();
        connection.addConnectionListener(this);

        infoQuery = new BSInfoQueryBean();

        auth = new BSAuthorizationBean();
        auth.addAuthListener(this);

        roster = new BSRosterBean();
        roster.addRosterListener(this);

        presence = new BSPresenceBean();
        presence.addPresenceListener(this);

        messenger = new BSMessengerBean();
        messenger.addMessageListener(this);
    }

	/**
	 * Call the destroy methid for all the helper bean classes, and then null them.
	 */
	public void destroy() {

        disconnect();

        // tries to make it deallocate all classes
        connection.prepareToDestroy();
        infoQuery.prepareToDestroy();
        auth.prepareToDestroy();
        roster.prepareToDestroy();
        presence.prepareToDestroy();
        messenger.prepareToDestroy();

        messenger = null;
        presence = null;
        roster = null;
        auth = null;
        infoQuery = null;
        connection = null;
	}

	/**
	 * Create a connection using the given account information and complete setup of the helper beans.
	 *
	 * @param theserver java.lang.String, the server for this client to connect to.
	 * @param theusername java.lang.String, the user name of the account to connect as.
	 * @param thepassword java.lang.Sting, the password of the account to connect as.
	 * @param theresource java.lang.String, the resource to use when connecting.
	 */
    public void connect(String theserver, String theusername, String thepassword, String theresource) {

		if ( !theserver.equals("") && !theusername.equals("") && !thepassword.equals("") && !theresource.equals("") ) {
	 		this.server = theserver;
			this.username = theusername;
			this.password = thepassword;
			this.resource = theresource;
		}

        // if disconnected
        if (connection.getState() == ConnectionEvent.STATE_DISCONNECTED) {

            // if connection succeeded
            if (connection.connect(server)) {

                // sets the new connection for beans
                infoQuery.setConnection(connection.getConnection());
                presence.setConnection(connection.getConnection());
                messenger.setConnection(connection.getConnection());
                auth.setIQBean(infoQuery.getIQBean());
                roster.setIQBean(infoQuery.getIQBean());

				connection.getConnection().addPacketListener(this);

                // starts athentication
                auth.authorize(username, password, resource);
            }
        }
    }

	/**
	 * Disconnect the current Jabber connection, if connected.
	 */
    public void disconnect() {
        if (connection.getState() != ConnectionEvent.STATE_DISCONNECTED)
            connection.disconnect();
    }

    /**
	 * Return the next id number for messages.
	 * @return int, the next id number for messages.
	 */
    public static int getNextID() {
        return idCounter++;
    }

	/**
	 * Return the String of the Jabber account this client is connected as.
	 */
	public String getSender() {
		return (username+"@"+server+"/"+resource);
	}

	/**
	 * Return the <code>BSPresenceBean</code> being used by this client.
	 */
	public BSPresenceBean getPresence() {
		return presence;
	}

    /**
	 * Send a presence message to server after first connected showing this client as online.
	 */
    public void sendPresence() {
        PresenceBuilder presenceBuilder = new PresenceBuilder();

        // human understandable description
        presenceBuilder.setStatus("Online");

        // one of normal, away, chat, dnd, xa
        presenceBuilder.setStateShow("normal");

        // one of available, unavailable plus other for subscriptions
        presenceBuilder.setType("available");

        try {
            connection.getConnection().send(presenceBuilder.build());
        }
		catch (InstantiationException e) {
            logEvent("Core", "presence builder failed");
        }
    }

    /**
	 * Send a message to server.
	 *
	 * @param jid, the Jabber id of the person to send the message to.
	 * @param body, the message to send.
	 */
    public void sendMessage(JID jid, String body) {

		//System.out.println("JID = "+jid.toString());
		//System.out.println("body = "+body);

		if (connection.getState() == ConnectionEvent.STATE_CONNECTED)
	        messenger.sendMessage(jid, body);
		else
			System.out.println("Not connected");
    }

    // *** authentication actions ***

    /**
	 * Event which occurs when there has been an error with autheticating the Jabber account
	 * this client tried to connect as to. It disconnects from the connection.
	 *
	 * @param ae com.compendium.io.jabber.BSAuthEvent, the event generated by the error message from the server.
	 */
    public void authError(BSAuthEvent ae) {
        connection.disconnect();
    }

    /**
	 * Event which occurs when the Jabber account this client is connect as is authorised
	 * Sends out online presence status and requests the roster.
	 *
	 * @param ae com.compendium.io.jabber.BSAuthEvent, the event generated by the authorisation message from the server.
	 */
    public void authorized(BSAuthEvent ae) {

		System.out.println("authorized");
		sendPresence();

        roster.refreshRoster();

		if (ProjectCompendium.APP != null) {
			ProjectCompendium.APP.ixPanelConnectionOpened();
			//ProjectCompendium.APP.checkIXMessages();
		}
   	}

    /**
	 * Event which occurs while the Jabber account this client is connect as,
	 * is in the process of being authorised.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param ae com.compendium.io.jabber.BSAuthEvent, the event generated by the authorisation message from the server.
	 */
    public void authorizing(BSAuthEvent ae) {
        //nothing
    }

    // *** connection changes handling ***

    /**
	 * Event which occurs when the connection status changes.
	 *
	 * @param ce, the event generated by the connection status changing.
	 */
    public void connectionChanged(ConnectionEvent ce) {

        ConnectionEvent.EState connState = ce.getState();
        if (connState == ConnectionEvent.STATE_CONNECTED) {
			System.out.println("IXPanel - State changed to connected");
       }
        else if (connState != ConnectionEvent.STATE_CONNECTED) {
			System.out.println("IXPanel - State changed to not connected");
			if (ProjectCompendium.APP != null)
				ProjectCompendium.APP.disableIXMenu();
 	    }

		if (connState == ConnectionEvent.STATE_DISCONNECTED ) {
			System.out.println("IXPanel - State changed to disconnected");
		}
    }

    // *** roster handling ***

    /**
	 * Called when there has been a change in the Roster contents.
	 * Replaces the currently held roster.
	 *
	 * @param r, the new Roster object.
	 */
    public void changedRoster(Roster r) {
		System.out.println("in changed roster");
		replacedRoster(r);
    }

    /**
	 * Called when the Roster needs to be replaced.
	 *
	 * @param r, the new Roster object to replace the current Roster with.
	 */
    public void replacedRoster(Roster r) {
		System.out.println("in replaced roster");
		ProjectCompendium.APP.createIXRoster();
    }

    /**
	 * Return the current Roster.
	 *
	 * @return Enumeration, a list of the current items on the roster.
	 */
	public Enumeration getRoster() {
		return roster.entries();
	}

    // *** presence handling ***
    /**
	 * Event which occurs when a roster account changes their presence.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param pi, the new presence information object for the account whose presence has changed.
	 */
    public void presenceChanged(BSPresenceInfo pi) {
        //Enumeration rosterEntries = roster.entries();
        //showRoster(rosterEntries);
    }

    /** Called when subscription request was received */
    public void subscriptionRequested(JID jid) {}

    /** Called when subscription request was approved */
    public void subscriptionApproved(JID jid) {}

    /** Called after disconnecting - no presence info is available */
    public void presencesCleared() {}

    // *** messages handling ***
    /**
	 * Event which occurs when a message is recived by this client.
	 *
	 * @param fromAddress, of the person sending the message.
	 */
    public void messageReceived(JID fromAddress) {

		String newMessage = messenger.popFirstMessage(fromAddress);
		String name = roster.getFriendlyName(fromAddress);

		if (ProjectCompendium.APP != null) {
			ProjectCompendium.APP.displayJabberReply(name, newMessage, "IXPanel");
		}
    }

    /**
	 * Event which occurs when a plain message is received by this client.
	 *
	 * @param msg, the Message received.
	 */
	public void plainMessageReceived(Message msg) {
		String newMessage = messenger.popFirstMessage(msg.getFromAddress());
		String name = roster.getFriendlyName(msg.getFromAddress());

		if (ProjectCompendium.APP != null)
			ProjectCompendium.APP.displayJabberReply(name, msg.getBody(), "MeetingReplay");
	}

    /**
	 * Event which occurs when a chat message is received by this client.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param msg, the Message received.
	 */
    public void chatMessageReceived(Message msg) {}

    /**
	 * Event which occurs when a group chat message is received by this client.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param msg, the Message received.
	 */
    public void groupchatMessageReceived(Message msg) {}

    /**
	 * Event which occurs when a headline message is received by this client.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param msg, the Message received.
	 */
    public void headlineMessageReceived(Message msg) {}

    /**
	 * Event which occurs when a message has been read.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param msg, the message read.
	 */
    public void messageRead(Message msg) {}

    /**
	 * Event which occurs when a message has been read.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param fromAddress, of the person sending the message.
	 */
    public void messageRead(JID fromAddress) {
       	//Enumeration rosterEntries = roster.entries();
        //showRoster(rosterEntries);
    }

    /**
	 * Event which occurs when a message error has occured.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param msg, the message.
	 */
    public void messageError(Message msg) {}

    /**
	 * Event which occurs when a message error has occured.
	 * <p>
	 * DOES NOTHING.
	 *
	 * @param toAddress, of the person being sent the message.
	 * @param errType, the type of the error which occurred.
	 * @param error, the error mesage.
	 */
	public void messageError(JID toAddress, String errType, String error) {}

    /**
	 * Write out to the current log file the given event message.
	 *
	 * @param source, the class which orriginated the log message.
	 * @param event, the message to log.
	 */
    public static void logEvent(String source, String event) {
        System.out.println("[" + source + "] " + event + "\n");
    }

	// PACKET STUFF FOR TESTING
    /**
	 * Event which occurs when a message packet is received.
	 * <p>
	 * DOES NOTHING but write to the log file.
	 *
	 * @param pe, the associated <code>PacketEvent</code>.
	 */
    public void receivedPacket(PacketEvent pe) {
		System.out.println("in ixPanel packet received = "+pe.getPacket().toString());
    }

    /**
	 * Event which occurs when sending a message fails.
	 * <p>
	 * DOES NOTHING but write to the log file.
	 *
	 * @param pe, the associated <code>PacketEvent</code>.
	 */
    public void sendFailed(PacketEvent pe) {
		System.out.println("in ixPanel packet failed = "+pe.getPacket().toString());
    }

    /**
	 * Event which occurs when message has been sent.
	 * <p>
	 * DOES NOTHING but write to the log file.
	 *
	 * @param pe, the associated <code>PacketEvent</code>.
	 */
    public void sentPacket(PacketEvent pe) {
		System.out.println("in ixPanel packet sent = "+pe.getPacket().toString());
    }
}
