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

package com.compendium.meeting.io;


import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Vector;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Date;

import java.net.MalformedURLException;

import net.memeticvre.josekiclient.HttpAdd;
import net.memeticvre.josekiclient.HttpRemove;
import net.memeticvre.josekiclient.HttpAuthenticator;
import net.memeticvre.josekiclient.QueryEngineHTTP;

import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryExecution;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.Meeting;

import com.compendium.meeting.*;
import com.compendium.ui.UINodeTypeManager;


/**
 * Read and write data from/to the memetic meeting triple store.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class TripleStoreConnection {

	/** The RDF namespace.*/
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; //$NON-NLS-1$

	/** The first part of the Memetic uri.*/
	public static final String MEMETIC_STUB = "http://www.memetic-vre.net/"; //$NON-NLS-1$

	/** The Memetic namespace.*/
    public static final String MEMETIC_NS = MEMETIC_STUB+"ontologies/memetic-20050106-1#"; //$NON-NLS-1$

	/** The Portal namespace.*/
    public static final String PORTAL_NS = "http://www.aktors.org/ontology/portal#"; //$NON-NLS-1$

	/** The Support namespace.*/
    public static final String SUPPORT_NS = "http://www.aktors.org/ontology/support#"; //$NON-NLS-1$

	/** The Meeting namespace.*/
    public static final String MEETING_NS = "http://www.aktors.org/coakting/ontology/meeting-20040304-1#"; //$NON-NLS-1$

	/** The DC nsmaespace.*/
    public static final String DC_NS = "http://purl.org/dc/elements/1.1/"; //$NON-NLS-1$

	/** the username to use when accessing the triplestore.*/
	private String sUserID = ""; //"memetic"; //$NON-NLS-1$

	/** The password to use when accessing the triplestore.*/
	private String sPassword = ""; //"apple_789"; //$NON-NLS-1$

	/** This is the URL that is used to access the triplestore.*/
	private String sUrl = ""; //"http://petersam.ecs.soton.ac.uk/joseki/memetic"; //$NON-NLS-1$

	/** This is the port to use to access the triplstore.*/
	//private String sPort = ""; //"80"

	/** Holds the Access Grid data required to make a connection to the triplstore.*/
	private AccessGridData oAccessGridData = null;

    /** The URI of the Access Grid user */
    private String userURI = null;


	/**
	 * Constructor.
	 * @param oData the Access grid Connection data.
	 * @see AccessGridData
	 */
	public TripleStoreConnection(AccessGridData oData) throws AccessGridDataException {

		oAccessGridData = oData;

		if (!oAccessGridData.canAccessTriplestore()) {
			throw new AccessGridDataException(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "TripleStoreConnection.missingDataA")+"\n\n"+//$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "TripleStoreConnection.missingDataB")+"\n\n"); //$NON-NLS-1$
		}

		sUserID = oAccessGridData.getUserName();
		sPassword = oAccessGridData.getPassword();
		sUrl = oAccessGridData.getTriplestoreURL();
		//sPort = oAccessGridData.getTriplestorePort(); // Now appened to url before sent

		//this authenticates you to the triplestore
		//this only needs to be called once
		HttpAuthenticator.setAuthParams(sUserID, sPassword);

		String sProxySet = System.getProperty("proxySet"); //$NON-NLS-1$
		if (oAccessGridData.hasLocalProxy() && sProxySet.equals("false")) { //$NON-NLS-1$
			System.setProperty("proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("http.proxyHost", oAccessGridData.getLocalProxyHostName()); //$NON-NLS-1$
			System.setProperty("http.proxyPort", oAccessGridData.getLocalProxyPort()); //$NON-NLS-1$
		}
        userURI = getUserURI(sUserID);
	}

	/**
	 * Return the session id associated with the given meeting id
	 * @param sMeetingID the id to return the session for.
	 */
	public String getSessionID(String sMeetingID) {

		String sSessionID = ""; //$NON-NLS-1$

		String sQuery = "SELECT ?sessionid WHERE (<" + sMeetingID + "> memetic:has-session-id ?sessionid) " //$NON-NLS-1$ //$NON-NLS-2$
										+ "USING memetic FOR <" + MEMETIC_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$

        Query query = new Query(sQuery);
        QueryExecution qe = new QueryEngineHTTP(query, sUrl) ;
        QueryResults results = qe.exec() ;
        Iterator iter = results;

        if (iter.hasNext()){
            ResultBinding rbind = (ResultBinding)iter.next() ;

            Object obj = rbind.get("sessionid") ; //$NON-NLS-1$
            sSessionID = obj.toString();

			//System.out.println("SessionID = "+sSessionID);
			//System.out.flush();

        } else {
            System.out.println ("No meeting session id found for "+sMeetingID); //$NON-NLS-1$
        }

        return sSessionID;
	}

	/**
	 * Download the meeting data for the given meeting id.
	 *
	 * @param sMeetingID, the id of the meeting whose data to download.
	 * @return Meeting, the object holding the meeting data.
	 */
    public Meeting downloadMeetingData (String sMeetingID) {

		String sName = getTitle(sMeetingID);
		String sTime = getStartTime(sMeetingID);

		Meeting meeting = new Meeting(sMeetingID);
		meeting.setName(sName);
		if (!sTime.equals("")) { //$NON-NLS-1$
			try {
				meeting.setStartDate( new Date( (new Long(sTime)).longValue() ) );
			} catch (Exception ex) {
				ex.printStackTrace();
				meeting.setStartDate(new Date());
			}
		}

		meeting.setAttendees(getAttendees(sMeetingID));
		meeting.setAgenda(getAgenda(sMeetingID));
		meeting.setDocuments(getDocuments(sMeetingID));

		return meeting;
	}

    // Returns the uri of the given user
    private String getUserURI(String username) {
        String uri = null;
        String sQuery = "SELECT ?user WHERE (?user <memetic:has-username> \"" + username + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
        sQuery += " USING memetic FOR <" + MEMETIC_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$
        Query query = new Query(sQuery);
        QueryExecution qe = new QueryEngineHTTP(query, sUrl) ;
        QueryResults results = qe.exec();
        if (results.hasNext()) {
            ResultBinding rbind = (ResultBinding) results.next();
            uri = rbind.get("user").toString(); //$NON-NLS-1$
            //System.out.println("User URI = " + uri);
        } else {
            System.out.println("User URI Not found for user " + username); //$NON-NLS-1$
        }
        return uri;
    }

	/**
	 * Download the meeting title for the given meeting uri.
	 *
	 * @param sMeetingID, the id of the meeting whose title to download.
	 * @return the title of the meeting as a String.
	 */
	public String getTitle(String sMeetingID) {
		String sName = ""; //$NON-NLS-1$

		String sQuery = "SELECT ?title WHERE (<" + sMeetingID + "> dc:title ?title) " //$NON-NLS-1$ //$NON-NLS-2$
							   + "USING dc FOR <" + DC_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$

        Query query = new Query(sQuery);
        QueryExecution qe = new QueryEngineHTTP(query, sUrl) ;
        QueryResults results = qe.exec() ;
        Iterator iter = results;

        if (iter.hasNext()){
            ResultBinding rbind = (ResultBinding)iter.next() ;

            Object obj = rbind.get("title") ; //$NON-NLS-1$
            sName = obj.toString();

			//System.out.println("Meeting name = "+sName);
			//System.out.flush();

        } else {
            System.out.println ("No meeting title found for "+sMeetingID); //$NON-NLS-1$
        }

        return sName;
	}

	/**
	 * Download the meeting start time for the given meeting uri.
	 *
	 * @param sMeetingID, the id of the meeting whose start time to download.
	 * @return the time the meeting is due to start as a String represting the time in milliseconds.
	 */
	public String getStartTime(String sMeetingID) {
		String sTime = ""; //$NON-NLS-1$

		String sQuery = "SELECT ?has-abs-start-time WHERE (<" + sMeetingID + "> memetic:has-abs-start-time ?has-abs-start-time) " //$NON-NLS-1$ //$NON-NLS-2$
							   + "USING memetic FOR <" + MEMETIC_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$

        Query query = new Query(sQuery);
        QueryExecution qe = new QueryEngineHTTP(query, sUrl) ;
        QueryResults results = qe.exec() ;
        Iterator iter = results;

        if (iter.hasNext()){
            ResultBinding rbind = (ResultBinding)iter.next() ;

            Object obj = rbind.get("has-abs-start-time") ; //$NON-NLS-1$
            sTime = obj.toString();

			//System.out.println("Meeting time = "+sTime);
        } else {
            System.out.println ("No meeting date found for "+sMeetingID); //$NON-NLS-1$
        }

        return sTime;
	}

	/**
	 * Download the attendee data for the given meeting uri.
	 *
	 * @param sMeetingID the id for this meeting.
	 * @return a Vector of {@Link com.compendium.meeting.MeetingAttendee MeetingAttendee} items for those people attending the meeting.
	 */
	public Vector getAttendees(String sMeetingID) {
		Vector vtAttendees = new Vector();

		String sQuery = "SELECT ?person ?name WHERE (<" + sMeetingID + "> meeting:has-local-event ?meeting), " //$NON-NLS-1$ //$NON-NLS-2$
						  + "(?meeting portal:meeting-attendee ?person), " //$NON-NLS-1$
						  + "(?person  portal:full-name ?name) " //$NON-NLS-1$
						  + "USING meeting FOR <" + MEETING_NS + "> portal FOR <" + PORTAL_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Query query = new Query(sQuery);
		QueryExecution qe = new QueryEngineHTTP(query, sUrl);
		QueryResults results = qe.exec();
		Iterator iter = results;

		String sOriginalID = ""; //$NON-NLS-1$
		String sName = ""; //$NON-NLS-1$

        for ( iter = results; iter.hasNext(); ) {

            ResultBinding rbind = (ResultBinding)iter.next() ;

	        sOriginalID = new String( (rbind.get("person")).toString() ); //$NON-NLS-1$
	        //System.out.println("URI of current person = " + sOriginalID);

	        sName = new String( (rbind.get("name")).toString() ); //$NON-NLS-1$
	        //System.out.println("name of current person = " + sName);

			MeetingAttendee item = new MeetingAttendee(sMeetingID, sName);
			item.setOriginalID(sOriginalID);
			vtAttendees.addElement(item);
        }

		return vtAttendees;
	}

	/**
	 * Download the agenda data for the given meeting uri.
	 *
	 * @param sMeetingID, the id for this meeting.
	 * @return a Vector holding a list of {@Link com.compendium.meeting.MeetingAgendaItem MeetingAgendaItem} objects to represent the meeting agenda.
	 */
	public Vector getAgenda(String sMeetingID) {

		Vector vtAgenda = new Vector();

		String sQuery =
						  "SELECT ?agendum ?label ?number WHERE (<" + sMeetingID + "> portal:has-sub-event ?agendum), " //$NON-NLS-1$ //$NON-NLS-2$
						  + "(?agendum rdf:type memetic:Agenda-Item), " //$NON-NLS-1$
						  + "(?agendum memetic:has-label ?label), " //$NON-NLS-1$
						  + "(?agendum memetic:has-item-number ?number) " //$NON-NLS-1$
						  + "USING memetic FOR <" + MEMETIC_NS + "> portal FOR <" + PORTAL_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Query query = new Query(sQuery);
		QueryExecution qe = new QueryEngineHTTP(query, sUrl);
		QueryResults results = qe.exec();
		Iterator iter = results;

		// HAS AN AGENDA BEEN SET?
		if (iter.hasNext()) {

			String sOriginalID = ""; //$NON-NLS-1$
			String sLabel = ""; //$NON-NLS-1$
			float fNumber = 0;

	        for ( iter = results; iter.hasNext(); ) {

	            ResultBinding rbind = (ResultBinding)iter.next() ;

	            sOriginalID = new String( (rbind.get("agendum")).toString() ); //$NON-NLS-1$
	            //System.out.println("URI of current agendum = " + sOriginalID);

	            sLabel = new String( (rbind.get("label")).toString() ); //$NON-NLS-1$
                if (sLabel.matches("\\d+\\.\\d+ (.*)")) { //$NON-NLS-1$
                    sLabel = sLabel.substring(sLabel.indexOf(' '));
                }
	            //System.out.println("label of current agendum = " + sLabel);

	            fNumber = new Float( (rbind.get("number")).toString() ).floatValue(); //$NON-NLS-1$
	            //System.out.println("number of current agendum = " + fNumber);

				MeetingAgendaItem item = new MeetingAgendaItem(sMeetingID, sLabel, fNumber);
				item.setOriginalID(sOriginalID);

				vtAgenda.addElement(item);
	        }
		}

		vtAgenda = sortAgenda(vtAgenda);

		return vtAgenda;
	}

	/**
	 * Sort the given Vector of {@Link com.compendium.meeting.MeetingAgendaItem MeetingAgendaItem} objects by thier agenda position numbers.
	 *
	 * @param Vector items the vector of {@Link com.compendium.meeting.MeetingAgendaItem MeetingAgendaItem} objects to sort.
	 * @return a Vector of the sorted objects.
	 */
	public Vector sortAgenda(Vector items) {

		if (items.size() <= 0) {
			return items;
		}

		Vector sortedVector = new Vector();
		Object[] sa = new Object[items.size()];
		items.copyInto(sa);
		List l = Arrays.asList(sa);

		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				MeetingAgendaItem item = (MeetingAgendaItem)o1;
				float number = item.getNumber();
				Float f1 = new Float(number);

				MeetingAgendaItem item2 = (MeetingAgendaItem)o2;
				float number2 = item2.getNumber();
				Float f2 = new Float(number2);

				return  (f1.compareTo(f2));
			}
		});

	 	// add sorted elements from list to vector
	 	for (Iterator it = l.iterator(); it.hasNext(); ) {
			sortedVector.addElement(it.next());
		}

		return sortedVector;
	}

	/**
	 * Download the reference documents for the meeting uri.
	 *
	 * @param sMeetingID the id for this meeting.
	 * @return a Vector listing the {@Link com.compendium.meeting.MeetingDocument MeetingDocument} objects representing additional documents for this meeting.
	 */
	public Vector getDocuments(String sMeetingID) {

		Vector vtDocuments = new Vector();

		String sQuery = "SELECT ?anon_ref ?ref_url ?pretty_name WHERE "+ //$NON-NLS-1$
							"(<"+ sMeetingID +"> memetic:has-relevant-resource ?anon_ref), "+ //$NON-NLS-1$ //$NON-NLS-2$
							"(?anon_ref support:has-pretty-name ?pretty_name), (?anon_ref "+ //$NON-NLS-1$
							"memetic:has-url ?ref_url) USING memetic FOR <" + MEMETIC_NS + "> "+ //$NON-NLS-1$ //$NON-NLS-2$
							"support FOR <" + SUPPORT_NS + ">"; //$NON-NLS-1$ //$NON-NLS-2$

		Query query = new Query(sQuery);
		QueryExecution qe = new QueryEngineHTTP(query, sUrl);
		QueryResults results = qe.exec();
		Iterator iter = results;

		if (iter.hasNext()) {

			String sURL = ""; //$NON-NLS-1$
			String sName = ""; //$NON-NLS-1$
			String sOriginalID = ""; //$NON-NLS-1$

	        for ( iter = results; iter.hasNext(); ) {
	            ResultBinding rbind = (ResultBinding)iter.next() ;

	            sOriginalID = new String( (rbind.get("anon_ref")).toString() ); //$NON-NLS-1$
	            //System.out.println("ID of current reference = " + sOriginalID);

	            sURL = new String( (rbind.get("ref_url")).toString() ); //$NON-NLS-1$
	            //System.out.println("URL of current reference = " + sURL);

		        sName = new String( (rbind.get("pretty_name")).toString() ); //$NON-NLS-1$
	            //System.out.println("prettyname of current reference = " + sName);

				MeetingDocument doc = new MeetingDocument(sMeetingID, sName, sURL);
				doc.setOriginalID(sURL);
				vtDocuments.addElement(doc);
			}
        }

		return vtDocuments;
	}

	/**
	 * Download the list of node creation events for the given meeting and
	 * add pass them to the given {@Link com.compendium.meeting.MeetingManager MeetingManager}.
	 *
	 * @param oMeetingManager the {@Link com.compendium.meeting.MeetingManager MeetingManager} object controlling this meeting recording/replay.
	 * @param sMeetingID the id for this meeting.
	 */
	public void loadNodes(MeetingManager oMeetingManager, String sMeetingID) {

		String sQuery = "SELECT ?node_id ?map_id ?media_start_time WHERE " + //$NON-NLS-1$
						"(<"+ sMeetingID +"> portal:has-sub-event ?creation_event), " + //$NON-NLS-1$ //$NON-NLS-2$
						"(?creation_event rdf:type memetic:Creating-Compendium-Node), " + //$NON-NLS-1$
						"(?creation_event memetic:has-media-start-time ?media_start_time), " + //$NON-NLS-1$
						"(?creation_event memetic:has-node ?node_id), " + //$NON-NLS-1$
						"(?creation_event memetic:has-map ?map_id) " + //$NON-NLS-1$
						"USING portal FOR <" + PORTAL_NS +"> memetic FOR <" + MEMETIC_NS +">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Query query = new Query(sQuery);
		QueryExecution qe = new QueryEngineHTTP(query, sUrl);
		QueryResults results = qe.exec();
		Iterator iter = results;

		String sID = ""; //$NON-NLS-1$
		String sNodeID = ""; //$NON-NLS-1$
		String sID2 = ""; //$NON-NLS-1$
		String sViewID = ""; //$NON-NLS-1$
		String sMediaIndex = ""; //$NON-NLS-1$

		for ( iter = results ; iter.hasNext() ; ) {

			ResultBinding rbind = (ResultBinding)iter.next() ;

			sID = new String( (rbind.get("node_id")).toString() ); //$NON-NLS-1$
			//System.out.println("sID = " + sID);

			if (!sID.equals("")) { //$NON-NLS-1$
				int ind = sID.lastIndexOf("-"); //$NON-NLS-1$
				sNodeID = sID.substring(ind+1);

				//System.out.println("sNodeID = "+sNodeID);
			}

			sID2 = new String( (rbind.get("map_id")).toString() ); //$NON-NLS-1$
			//System.out.println("sID2 = " + sID2);

			if (!sID2.equals("")) { //$NON-NLS-1$
				int ind2 = sID2.lastIndexOf("-"); //$NON-NLS-1$
				sViewID = sID2.substring(ind2+1);

				//System.out.println("sViewID = "+sViewID);
			}

			sMediaIndex = new String( (rbind.get("media_start_time")).toString() ); //$NON-NLS-1$

			if (!sViewID.equals("") && !sNodeID.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				oMeetingManager.addNodeView(sNodeID, sViewID, sMediaIndex);
			}
		}
	}

	/**
	 * Create the model and the meeting data for the given meeting id.
	 *
	 * @param oMeeting the object holding the meeting data.
	 * @param model the model to add the data to.
	 */
	public synchronized void addMeetingData(Meeting oMeeting, com.hp.hpl.jena.rdf.model.Model model) {

		Resource meeting = model.createResource(oMeeting.getMeetingID());

        meeting.addProperty(model.createProperty(MEETING_NS, "has-transcription"), model.createResource(oMeeting.getMeetingID()+"-"+oMeeting.getMeetingMapID())); //$NON-NLS-1$ //$NON-NLS-2$

		// Define the map owner, 'person', Reource and add their type, name, and if Compendium created (always 'true').
		UserProfile oUser = oMeeting.getUser();
		if (oUser == null) {
			oUser = ProjectCompendium.APP.getModel().getUserProfile();
		}

        Resource person = model.createResource(MEMETIC_STUB+oUser.getId());
        person.addProperty(model.createProperty(RDF_NS, "type"), model.createResource(PORTAL_NS + "Person")); //$NON-NLS-1$ //$NON-NLS-2$
        person.addProperty(model.createProperty(PORTAL_NS, "full-name"), oUser.getUserName()); //$NON-NLS-1$
        person.addProperty(model.createProperty(MEMETIC_NS, "is-compendium-created"), "true"); //$NON-NLS-1$ //$NON-NLS-2$

		// UPLOAD THE DATA ABOUT THE MEETING MAP NODE ITSELF
		MeetingEvent oMeetingEvent = new MeetingEvent(oMeeting.getMeetingID(), false, MeetingEvent.NODE_ADDED_EVENT, (View)oMeeting.getMapNode(), oMeeting.getMapNode());
		addNode(oMeetingEvent, model);
    }

	/**
	 * Create a triple for a new node being created.
	 *
	 * @param oMeetingEvent the event whose data to upload.
	 * @param model the model to add the data to.
	 */
	private synchronized void addNode(MeetingEvent oMeetingEvent, com.hp.hpl.jena.rdf.model.Model model) {

		NodeSummary oNode = oMeetingEvent.getNode();

		if (oNode == null) {
			return;
		}

		String sNodeID = oNode.getId();
        Resource oResNode = model.createResource(oMeetingEvent.getMeetingID()+"-"+sNodeID); //$NON-NLS-1$
		Property type = model.createProperty(RDF_NS, "type"); //$NON-NLS-1$
        oResNode.addProperty(type, model.createResource(MEMETIC_NS + "Compendium-Node")); //$NON-NLS-1$

		int nNodeType = oNode.getType();
		String sTripleStoreString = UINodeTypeManager.getTripleStoreDescription(nNodeType);		
		oResNode.addProperty(type, model.createResource(MEMETIC_NS + sTripleStoreString));
		if (nNodeType == ICoreConstants.REFERENCE_SHORTCUT) {
			oResNode.addProperty(model.createProperty(MEMETIC_NS, "has-reference"), oNode.getSource()); //$NON-NLS-1$
		}

		// ADD LABEL
        oResNode.addProperty(model.createProperty(MEMETIC_NS, "has-label"), oNode.getLabel()); //$NON-NLS-1$

		// ADD IF HAS TRIPLESTORE ID
		String sOriginalID = oNode.getOriginalID();

		if (sOriginalID.startsWith("TS:") && !(nNodeType == ICoreConstants.REFERENCE || nNodeType == ICoreConstants.REFERENCE)) { //$NON-NLS-1$
			int ind = sOriginalID.indexOf(":"); //$NON-NLS-1$
			sOriginalID = sOriginalID.substring(ind+1);
        	Property has_original_id = model.createProperty(MEMETIC_NS, "has-original-id"); //$NON-NLS-1$
        	Resource original_id = model.createResource(sOriginalID);
   			oResNode.addProperty(has_original_id, original_id);
		}
	}

	/**
	 * Create the given event triple.
	 *
	 * @param oMeetingEvent the event whose data to upload.
	 * @param model the model to add the data to.
	 */
	public synchronized void addEvent(MeetingEvent oMeetingEvent, com.hp.hpl.jena.rdf.model.Model model) {

        int nEventType = oMeetingEvent.getEventType();

		// IF THIS EVENT INDICATES THAT A NEW NODE WAS CREATED,
		// CREATE A NEW NODE OBJECT FOR IT IN THE TRIPLESTORE BEFORE CONTINUING.
        if (nEventType == MeetingEvent.NODE_ADDED_EVENT || nEventType == MeetingEvent.NODE_TRANSCLUDED_EVENT) {
			addNode(oMeetingEvent, model);
		}

		String id = com.compendium.core.datamodel.Model.getStaticUniqueID();
		Resource meeting = model.createResource(oMeetingEvent.getMeetingID());
        Resource event = model.createResource(MEMETIC_STUB+id);
        Property type = model.createProperty(RDF_NS, "type"); //$NON-NLS-1$

        event.addProperty(type, model.createResource(MEMETIC_NS + "Compendium-Event")); //$NON-NLS-1$

        Resource oEventType = null;
        String oTagName = ""; //$NON-NLS-1$

		switch(oMeetingEvent.getEventType()) {
		case MeetingEvent.TAG_ADDED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Tagging-Compendium-Node"); //$NON-NLS-1$
			oTagName = oMeetingEvent.getCode().getName();
			break;
		case MeetingEvent.TAG_REMOVED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Detagging-Compendium-Node"); //$NON-NLS-1$
			oTagName = oMeetingEvent.getCode().getName();
			break;
		case MeetingEvent.NODE_ADDED_EVENT:
		case MeetingEvent.NODE_TRANSCLUDED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Creating-Compendium-Node"); //$NON-NLS-1$
			break;
		case MeetingEvent.NODE_REMOVED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Deleting-Compendium-Node"); //$NON-NLS-1$
			break;
		case MeetingEvent.VIEW_SELECTED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Bringing-Map-To-Front"); //$NON-NLS-1$
			break;
		case MeetingEvent.NODE_FOCUSED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Selecting-Compendium-Node"); //$NON-NLS-1$
			break;
		case MeetingEvent.REFERENCE_LAUNCHED_EVENT:
			oEventType = model.createResource(MEMETIC_NS + "Launching-Reference-Node"); //$NON-NLS-1$
			break;
		default :
			break;
		}

        event.addProperty(type, oEventType);

        if (!oTagName.equals("")) { //$NON-NLS-1$
			event.addProperty(model.createProperty(MEMETIC_NS, "has-tag"), oTagName); //$NON-NLS-1$
		}

        event.addProperty(model.createProperty(MEMETIC_NS, "has-media-start-time"), oMeetingEvent.getMediaIndex()); //$NON-NLS-1$
        event.addProperty(model.createProperty(MEMETIC_NS, "has-node"), model.createResource(oMeetingEvent.getMeetingID()+"-"+oMeetingEvent.getNodeID())); //$NON-NLS-1$ //$NON-NLS-2$
   	    event.addProperty(model.createProperty(MEMETIC_NS, "has-map"), model.createResource(oMeetingEvent.getMeetingID()+"-"+oMeetingEvent.getViewID())); //$NON-NLS-1$ //$NON-NLS-2$
        event.addProperty(model.createProperty(PORTAL_NS, "sender-of-information"), model.createResource(userURI)); //$NON-NLS-1$
        event.addProperty(model.createProperty(MEMETIC_NS, "created-post-meeting"), oMeetingEvent.creatingPostMeeting()); //$NON-NLS-1$
        meeting.addProperty(model.createProperty(PORTAL_NS, "has-sub-event"), event); //$NON-NLS-1$
	}


	/**
	 * Write the given model to a file with the given name.
	 *
	 * @param model the model to write to a file.
	 * @param sFileName the name of the file to write to.
	 * @throws FileNodeFountException if a new FileOutputStream cannot be created.
	 * @throws IOException if the FileOutputStream cannot be closed.
	 */
	public void writeFile(com.hp.hpl.jena.rdf.model.Model model, String sFileName) throws FileNotFoundException, IOException {

		FileOutputStream out = new FileOutputStream(sFileName);
		model.write (out, "N3"); //$NON-NLS-1$
   		model.close();
		out.close();
	}

	/**
	 * Upload the given file of n3 data.
	 *
	 * @param sFileName the name of the file to upload.
	 * @param sMeetingID the id of the meeting whose data is being uploaded.
	 * @throws MalformedURLException {@Link #uploadModel(com.hp.hpl.jena.rdf.model.Model,String) uploadModel} method throw it back to this method.
	 */
	public void uploadFile(String sFileName, String sMeetingID) throws MalformedURLException {

		com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        model.read ("file:///"+sFileName, "N3"); //$NON-NLS-1$ //$NON-NLS-2$
   		uploadModel(model, sMeetingID);
   		model.close();
	}

	/**
	 * Upload the given model to the triplestore
	 *
	 * @param model, the model to upload.
	 * @param sMeetingID the id of the meeting whose data is being uploaded.
	 * @throws MalformedURLException if the urls used to create the HttpRemove and HttpAdd is malformed.
	 */
	public void uploadModel(com.hp.hpl.jena.rdf.model.Model oModel, String sMeetingID) throws MalformedURLException {

		//System.out.println("About to try and upload: "+oModel.toString());

		com.hp.hpl.jena.rdf.model.Model oInnerModel = ModelFactory.createDefaultModel();
		Resource meeting = oInnerModel.createResource(sMeetingID);
		Property comp_is_proc = oInnerModel.createProperty(MEMETIC_NS, "compendium-is-processed"); //$NON-NLS-1$
		meeting.addProperty(comp_is_proc, "true"); //$NON-NLS-1$

		HttpRemove removeOp = new HttpRemove(sUrl);
		removeOp.setModel(oInnerModel);
		removeOp.exec();
   		oInnerModel.close();

        HttpAdd addOp = new HttpAdd(sUrl);
        addOp.setModel(oModel);
        addOp.exec();
 	}
}
