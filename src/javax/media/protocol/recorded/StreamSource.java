/*
 * @(#)StreamSource.java
 * Created: 2005-04-21
 * Version: 2-0-alpha
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package javax.media.protocol.recorded;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rtspd.RTPHeader;

/**
 * A Stream to be played back
 *
 * @author Andrew G D Rowley
 * @version 2-0-alpha
 */
public class StreamSource {

    private static final int RTP_PACKET = 0;

    // The number of usecs per ms
    private static final int USECS_PER_MS = 1000;

    // The number of ms per second
    private static final int MS_PER_SEC = 1000;

    // The mask for a short from an int
    private static final int SHORT_MASK = 0x0000FFFF;

    // The second last item in an array
    private static final int SECOND_LAST = 2;

    private static final String EXCEPTION_MESSAGE = "Exception ";

    // The amount of delay between packets when paused
    private static final int PAUSE_DELAY = 1000;

    // The size of an IP address in bytes
    private static final int IP_ADDRESS_SIZE = 4;

    // The minimum time before a packet is scheduled
    private static final long MIN_DELAY = 10;

    // The maximum time before a packet is dropped
    private static final long MAX_NEG_DELAY = -500;

    // The log file
    private static Log logger = LogFactory.getLog(StreamSource.class.getName());

    // The last timestamp seen
    private long lastPacketTimestamp = 0;

    // The current packet timestamp
    private long packetTimestamp = 0;

    // The current packet group to send
    private Vector<DatagramPacket> packets = new Vector<DatagramPacket>(0);

    // True if the end of the file has been reached
    private boolean qEof = false;

    // True if there has been an error
    private boolean qError = false;

    // True if an RTP packet was found in the file
    private boolean qFoundRtpPacket = false;

    // The data channel of the stream file
    private DataInputStream streamFile = null;

    // The control channel of the stream file
    private FileChannel streamFileControl = null;

    // The first timestamp in this stream
    private long startTime = 0;

    // A timer for sending packets at the right time
    private Timer timer = new Timer();

    // The type of the stream
    private int type = 0;

    // The length of the current group of packets
    private Vector<Integer> lengths = new Vector<Integer>();

    // The offset of the current packet from the first packet
    private Vector<Long> offsets = new Vector<Long>();

    // The offset of the first sent packet
    private long firstOffset = 0;

    // The time at which all streams should start in clock time
    private long allStartTime = 0;

    // True if the first packet has been sent
    private boolean firstPacketSent = false;

    // The speed at which packets are played back
    private double scale = 1.0;

    // A vector of positions of packets in the stream file
    private Vector<Long> packetPositions = new Vector<Long>();

    // A vector of offsets of packets in the stream file
    private Vector<Long> packetOffsets = new Vector<Long>();

    // The current position in the packetPositions vector
    private int currentPos = 0;

    // The number of packets sent
    private int packetCount = 0;

    // The number of octets sent
    private int octetCount = 0;

    // True if the stream has been terminated
    private boolean terminated = false;

    // The lowest sequence number in the current block
    private int lowestSequence = 0;

    // The datasource to notify of a new packet
    private DataSource dataSource = null;

    // The packet data
    private byte[][] packetData = new byte[50][4096];

    // The format of the streams
    private int rtpFormat = 0;

    /**
     * Sets up the stream to be sent
     * @param dataSource The datasource that this stream will be used in
     * @param filename The name of the file
     *
     */
    public StreamSource(DataSource dataSource, String filename) {

        this.dataSource = dataSource;

        // Open a connection to the data and process some meta-info
        openStream(filename);
        readHeader();
        readIndexFile(filename);
        readPacket();
    }

    /**
     * Gets the RTP format
     * @return The rtp format number
     */
    public int getRtpFormat() {
        return rtpFormat;
    }

    /**
     * Returns after the first packet has been played
     *
     */
    public synchronized void waitForFirstPacket() {
        while (!firstPacketSent) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(EXCEPTION_MESSAGE, e);
            }
        }
    }

    // Notifies a reciever that the first packet has been sent
    private synchronized void notifyFirstPacket() {
        firstPacketSent = true;
        notifyAll();
    }

    /**
     * Handles the case where the timer ticks
     *
     * @param task
     *            The task to handle
     */
    public void handleTimeout(TimerTask task) {
        if (task instanceof UpdateTimer) {

            // Time to send the next packet{
            sendCurrentAndScheduleNextPacket();
        }
    }

    /**
     * Starts playback of the stream
     * @param scale The scale at which to play (1.0 = normal)
     * @param seek The position in ms to seek from
     *
     */
    public void play(double scale, long seek) {

        // Calculate new start time, so that we're aligned w/other streams in
        // this session

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        lastPacketTimestamp = 0;

        this.scale = scale;

        // If the scale is 0, we just need to send RTCP packets to say we are
        // still here
        if (scale != 0) {

            // Seek the stream to the starting position
            long playoutDelay = 0;
            if (seek != -1) {
                streamSeek(seek);
            }

            // Get the first packet
            while (!qFoundRtpPacket && !qEof) {
                readPacket();
                if (qError || qEof) {
                    terminateStream();
                    return;
                }
            }

            // Calculate the initial delay for the first packet
            playoutDelay = computePlayoutDelay();

            // Play the stream
            if (playoutDelay > MIN_DELAY) {
                scheduleTimer(playoutDelay);
            } else {
                Thread startThread = new Thread() {
                    public void run() {
                        sendCurrentAndScheduleNextPacket();
                    }
                };
                startThread.start();
            }
        }
    }

    /**
     * Stops the playback of the stream
     *
     */
    public void teardown() {
        terminated = true;
        cancelTimers();
    }

    // Stops the timers
    private void cancelTimers() {
        if (timer != null) {
            timer.cancel();
        }
    }

    // Searches through the stream for the first packet to play after the given
    // time
    private void streamSeek(long seek) {
        int offsetPos = 0;
        firstOffset = seek;
        offsetPos = Collections.binarySearch(packetOffsets, new Long(
                seek));
        if (offsetPos < 0) {
            offsetPos = (-1 * offsetPos) + 1;
        }
        currentPos = offsetPos;
        try {
            if ((currentPos >= (packetOffsets.size() - 1)) && (scale < 0)) {
                currentPos = packetOffsets.size() - SECOND_LAST;
                int packetType = 1;
                while (packetType != 0) {
                    long pos = packetPositions.get(currentPos);
                    streamFileControl.position(pos);
                    streamFile.readShort();
                    packetType = streamFile.readShort() & SHORT_MASK;
                    currentPos--;
                }
            }
        } catch (IOException e) {
            logger.error(EXCEPTION_MESSAGE, e);
            qEof = true;
        }
    }

    // Open the stream file
    private boolean openStream(String filename) {
        boolean qSuccess = false;

        // Open the file for reading
        if (openStreamFile(filename)) {
            logger.debug("Stream_Source::openStream: opened stream file");
            qSuccess = true;
        } else {
            logger.debug("Stream_Source::openStream:  failed to open "
                            + "stream file\n");
        }

        return qSuccess;
    }

    // Actually open the stream file
    private boolean openStreamFile(String filename) {
        boolean qSuccess = true;

        try {
            FileInputStream stream = new FileInputStream(filename);
            streamFile = new DataInputStream(stream);
            streamFileControl = stream.getChannel();
        } catch (IOException e) {
            e.printStackTrace();
            qSuccess = false;
        }

        return qSuccess;
    }

    // Read the header from the stream file
    private boolean readHeader() {
        boolean qSuccess = true;

        try {

            // Read the start time of the stream
            long seconds =
                (streamFile.readInt() & RTPHeader.UINT_TO_LONG_CONVERT);
            long uSeconds =
                (streamFile.readInt() & RTPHeader.UINT_TO_LONG_CONVERT);

            byte addr[] = new byte[IP_ADDRESS_SIZE];
            startTime = (seconds * MS_PER_SEC) + (uSeconds / USECS_PER_MS);

            // Read the sender of the original stream
            streamFile.read(addr, 0, IP_ADDRESS_SIZE);
            streamFile.readUnsignedShort();
        } catch (IOException e) {
            logger.error(EXCEPTION_MESSAGE, e);
            qSuccess = false;
        }

        return qSuccess;
    }

    /**
     * Tells the source to read it's index file in preparation for a play
     * @param filename The name of the file to read
     *
     */
    public void readIndexFile(String filename) {
        try {

            // Open the index file
            filename += "_index";
            DataInputStream indexFile = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(filename)));

            try {
                while (true) {
                    long off = indexFile.readLong();
                    long pos = indexFile.readLong();

                    packetOffsets.add(new Long(off));
                    packetPositions.add(new Long(pos));
                }
            } catch (EOFException e) {
                indexFile.close();
            }
        } catch (IOException e) {
            logger.error(EXCEPTION_MESSAGE, e);
        }

    }

    // Read a packet from the stream file
    private void readPacket() {
        synchronized (packets) {
            DatagramPacket packet = null;
            try {
                long pos = 0;
                long lastReadTimestamp = -1;
                lengths.clear();
                packets.clear();
                offsets.clear();
                qError = false;
                qFoundRtpPacket = false;
                qEof = false;

                // Move into the next position
                if (currentPos >= packetPositions.size() || currentPos < 0) {
                    qEof = true;
                    return;
                }
                pos = packetPositions.get(currentPos);
                streamFileControl.position(pos);
                currentPos += Double.valueOf(scale).intValue();
                lowestSequence = -1;

                // Read packets while the timestamps are the same
                while (!qFoundRtpPacket || (lastReadTimestamp
                        == packetTimestamp)) {

                    // Read packet header
                    long offset = 0;
                    byte[] packetBuffer = packetData[packets.size()];
                    packet = new DatagramPacket(packetBuffer,
                            packetBuffer.length);
                    int length = streamFile.readShort()
                        & RTPHeader.USHORT_TO_INT_CONVERT;
                    type = streamFile.readShort()
                        & RTPHeader.USHORT_TO_INT_CONVERT;
                    offset = streamFile.readInt()
                        & RTPHeader.UINT_TO_LONG_CONVERT;

                    // calculate packet body size and read it
                    streamFile.readFully(packetBuffer, 0, length);

                    // If this is an RTP packet, set it up to be read
                    if (type == RTP_PACKET) {
                        RTPHeader header = new RTPHeader(packetBuffer, 0,
                                length);
                        rtpFormat = header.getPacketType();
                        packetTimestamp = header.getTimestamp();
                        if ((packetTimestamp == lastReadTimestamp)
                                || (lastReadTimestamp == -1)) {
                            int sequence = header.getSequence();
                            lengths.add(new Integer(length));
                            packets.add(packet);
                            offsets.add(new Long(offset));
                            lastReadTimestamp = packetTimestamp;
                            if ((sequence < lowestSequence)
                                    || (lowestSequence == -1)) {
                                lowestSequence = sequence;
                            }
                        }
                        qFoundRtpPacket = true;
                    }
                }
            } catch (EOFException e) {
                qEof = true;
            } catch (IOException e) {
                logger.error(EXCEPTION_MESSAGE, e);
                qError = true;
            }
        }
    }

    // Calculate the delay for the next packet
    private long computePlayoutDelay() {

        long timeOffset = 0;
        long delay = 0;

        // If the scale is stopped, pause by 5 second
        if (scale == 0) {
            return PAUSE_DELAY;
        }

        // Get the current offset
        if (allStartTime == 0) {
            allStartTime = System.currentTimeMillis();
        }
        timeOffset = System.currentTimeMillis() - allStartTime;
        timeOffset = (long) (timeOffset * scale);

        // Calculate the delay before sending the next packet
        delay = (offsets.get(0) - firstOffset) - timeOffset;
        delay = (long) (delay / scale);

        return delay;
    }

    // Set up a timer to play the next packet
    private void scheduleTimer(long playoutDelay) {
        if (!terminated) {
            timer.schedule(new UpdateTimer(this), playoutDelay);
        }
    }

    // Send the current packet and prepare the next one
    private void sendCurrentAndScheduleNextPacket() {

        if (terminated) {
            return;
        }

        // Send out the packet that's waiting
        sendPacket();
        boolean qTimerScheduled = false;

        // Execute this loop until we've queued up the next packet
        while (!qTimerScheduled && !qEof && !qError) {

            // Read the next packet
            readPacket();

            if (qError) {
                terminateStream();

                // We read some data; look at what we have
            } else if (!qEof) {
                if (qFoundRtpPacket) {
                    long playoutDelay = 0;

                    // If this packet is the same as the last, send it now
                    if ((packetTimestamp == lastPacketTimestamp)
                            && (scale != 0)) {
                        sendPacket();
                        continue;
                    }

                    // Work out the delay for the next packet
                    playoutDelay = computePlayoutDelay();

                    // Schedule a timer for sending the packet if the delay is
                    // greater than 10ms. This stops the loop.
                    if (playoutDelay > MIN_DELAY) {
                        scheduleTimer(playoutDelay);
                        qTimerScheduled = true;

                    } else if (playoutDelay < MAX_NEG_DELAY) {
                        continue;
                    } else {

                        // send it right away.
                        sendPacket();
                    }
                } else {

                    // the data we read was an RTCP packet. Ignore it
                }
            } else {

                // Stream has finished. Clean up
                terminateStream();
            }
        }
    }

    // Sends a packet
    private void sendPacket() {

        // Notify that the first packet has been sent
        if (!firstPacketSent) {
            notifyFirstPacket();
        }

        // If the packet is an RTP packet, play it
        if (type == RTP_PACKET) {
            for (int i = 0; (i < lengths.size()) && !terminated; i++) {
                int length = lengths.get(i);
                DatagramPacket packet = packets.get(i);
                try {

                    // Remember items for next time
                    if (lastPacketTimestamp != packetTimestamp) {
                        lastPacketTimestamp = packetTimestamp;
                    }

                    packet.setLength(length);
                    if (!terminated) {
                        dataSource.handleRTPPacket(packet);
                    }
                    packetCount++;
                    octetCount += length - RTPHeader.SIZE;
                } catch (Exception e) {
                    logger.error(EXCEPTION_MESSAGE, e);
                }
            }
        }
    }

    // Stop the stream
    private void terminateStream() {
        cancelTimers();
        notifyFirstPacket();
        logger.debug("Stream Finished");
    }

    /**
     * Returns the start time of the source
     * @return The start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * The current time being played
     * @return The current time
     */
    public long getCurrentTime() {
        if (currentPos >= (packetOffsets.size() - 1)) {
            currentPos = packetOffsets.size() - SECOND_LAST;
        }
        return packetOffsets.get(currentPos);
    }

    /**
     * Gets the length of the stream in milliseconds
     * @return The length og the stream in milliseconds
     */
    public long getDuration() {
        return packetOffsets.get(packetOffsets.size() - 1);
    }
}
