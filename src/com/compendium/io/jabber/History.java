package com.compendium.io.jabber;

/*
 * History.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 29 October 2002, 16:42
 */

import java.util.*;
import java.io.*;
import java.text.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>History</code> provides logging and storing of messages, etc.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class History {

    protected static int BUF_LEN = 32;
    
    /** Stores message.
     * @param myJID identifies the local user; i.e. history directory
     * @param peerJID identifies the history file (e.g. jid of peer in chat)
     * @param name is nickname of person who sent the message
     * @param subject is message subject
     * @param body is message body
     */
    public static synchronized void storeMessage(JID myJID, JID peerJID, 
                                    String name, String subject, String body) {
        if (myJID == null || peerJID == null) 
            return;
        JID peerJidWithoutRes = new JID(peerJID.getUsername(), peerJID.getServer(), null);
        JID myJidWithoutRes = new JID(myJID.getUsername(), myJID.getServer(), null);
        String dirName = new String("./logs-" + myJidWithoutRes.toString());
        
        FileWriter output;
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdir())
                    return;
            }
            String filename = new String(dirName + "/" + peerJidWithoutRes.toString() + ".log");
            output = new FileWriter(filename, true);
        } catch (IOException e) {
            System.out.println("Cannot open history file: " + e.getMessage());
            return;
        }

        String record = new String();
        record = record + "[" + getCurrentTimeStamp() + "]";
        if (name != null)
            record = record + " <" + name + ">";
        if (subject != null)
            record = record + " " + subject + ":";
        if (body != null)
            record = record + " " + body;
        record = record + "\n";
        
        try {
            output.write(record);
        } catch (IOException e) {
            System.out.println("Cannot write into history file: " + e.getMessage());
        }
        
        try {
            output.close();
        } catch (IOException e) {
            System.out.println("Cannot close history file: " + e.getMessage());
        }
        
    }
    
    /** Loads whole history for given JID to JID communication
     * @param myJID identifies the local user; i.e. history directory
     * @param peerJID identifies the history file (e.g. jid of peer in chat)
     */
    public static synchronized String getMessages(JID myJID, JID peerJID) {
        if (myJID == null || peerJID == null) 
            return null;
        JID peerJidWithoutRes = new JID(peerJID.getUsername(), peerJID.getServer(), null);
        JID myJidWithoutRes = new JID(myJID.getUsername(), myJID.getServer(), null);
        String dirName = new String("./logs-" + myJidWithoutRes.toString());
        
        FileReader input;
        try {
            String filename = new String(dirName + "/" + peerJidWithoutRes.toString() + ".log");
            input = new FileReader(filename);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open history file: " + e.getMessage());
            return null;
        }

        String history = new String();
        try {
            char[] buffer = new char[BUF_LEN];
            int charNum;
            
            do {
                charNum = input.read(buffer, 0, BUF_LEN);
                if (charNum != -1)
                    history = history + new String(buffer, 0, charNum);
            } while (BUF_LEN == charNum);
            
        } catch (IOException e) {
            System.out.println("Cannot read history file: " + e.getMessage());
            history = null;
        }
        
        try {
            input.close();
        } catch (IOException e) {
            System.out.println("Cannot close history file: " + e.getMessage());
        }
        
        return history;
    }
    
    /** Stores message.
     * @param packet is message packet
     * @param name is nickname of person who sent the message
     */
    public static synchronized void storeIncomingMessage(Message packet, String name) {

        if (packet == null) return;
        
        JID myJID = packet.getToAddress();
        JID peerJID = packet.getFromAddress();
        
        if (myJID == null || peerJID == null) return;
        
        JID peerJidWithoutRes = new JID(peerJID.getUsername(), peerJID.getServer(), null);
        JID myJidWithoutRes = new JID(myJID.getUsername(), myJID.getServer(), null);
        String dirName = new String("./logs-" + myJidWithoutRes.toString());
        
        FileWriter output;
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdir())
                    return;
            }
            String filename = new String(dirName + "/" + peerJidWithoutRes.toString() + ".log");
            output = new FileWriter(filename, true);
        } catch (IOException e) {
            System.out.println("Cannot open history file: " + e.getMessage());
            return;
        }

        String subject = packet.getSubject();
        String body = packet.getBody();
        
        String timeStamp = null;
        Enumeration extensions = packet.Extensions();
        while (extensions.hasMoreElements()) {
            Object o = extensions.nextElement();
            if (o instanceof XDelay) {
                timeStamp = History.getTimeStamp((XDelay)o);
            }
        }
        if (timeStamp == null)
            timeStamp = getCurrentTimeStamp();
            
        String record = new String();
        record = record + "[" + timeStamp + "]";
        if (name != null)
            record = record + " <" + name + ">";
        if (subject != null && !subject.equals(""))
            record = record + " " + subject + ":";
        if (body != null && !body.equals(""))
            record = record + " " + body;
        record = record + "\n";
        
        try {
            output.write(record);
        } catch (IOException e) {
            System.out.println("Cannot write into history file: " + e.getMessage());
        }
        
        try {
            output.close();
        } catch (IOException e) {
            System.out.println("Cannot close history file: " + e.getMessage());
        }
    }
    
    
    /** Returns current time-stamp in textual form */
    public static String getCurrentTimeStamp() {
        Date currentTime = new Date();
        return getTimeStamp(currentTime);
    }
    
    
    /** Converts <code>XDelay</code> into <code>Date</code> */
    public static Date getTime(XDelay delay) {
        if (delay == null) return null;
        
        String stampString = delay.getStamp();
        if (stampString == null) return null;
        
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("yyyyMMdd'T'HH:mm:ss");
        
        Date stampDate;
        try {
            stampDate = df.parse(stampString);
        } catch (ParseException e) { 
            return null;
        }
        return stampDate;
    }
    
    /** Converts <code>XDelay</code> into <code>String</code> */
    public static String getTimeStamp(XDelay delay) {
        Date stampDate = getTime(delay);
        return (stampDate != null)? getTimeStamp(stampDate) : null;
    }
    
    /** Converts <code>Date</code> into <code>String</code> */
    public static String getTimeStamp(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }
}
