/*
 * @(#)MemeticDataSource.java
 * Created: 2 Nov 2007
 * Version: 1.0
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

import java.io.IOException;
import java.net.DatagramPacket;

import javax.media.Time;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.Positionable;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DataSource extends PushBufferDataSource implements Positionable {

    // The rtp stream
    private DatagramForwarder rtpStream = new DatagramForwarder();

    private StreamSource streamSource = null;

    private String filename = null;

    private long seek = 0;

    private double scale = 1.0;

    private boolean playing = false;

    /**
     * @see javax.media.protocol.PushDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{rtpStream};
    }

    /**
     * @see javax.media.protocol.DataSource#connect()
     */
    public void connect() throws IOException {
        String locator = getLocator().getRemainder().substring(2);
        String[] parts = locator.split("\\?", 2);
        filename = parts[0];
        if (parts.length > 1) {
            String query = parts[1];
            String[] values = query.split("&");
            for (int i = 0; i < values.length; i++) {
                String[] value = values[i].split("=", 2);
                if (value[0].equals("scale")) {
                    scale = Double.parseDouble(value[1]);
                } else if (value[0].equals("seek")) {
                    seek = Long.parseLong(value[1]);
                }
            }
        }
        streamSource = new StreamSource(this, filename);
        try {
            rtpStream.setFormat(streamSource.getRtpFormat());
        } catch (UnsupportedFormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public void disconnect() {
        stop();
        rtpStream.close();
    }

    /**
     * @see javax.media.protocol.DataSource#getContentType()
     */
    public String getContentType() {
        return "raw";
    }

    /**
     * @see javax.media.protocol.DataSource#getControl(java.lang.String)
     */
    public Object getControl(String cls) {
        return rtpStream.getControl(cls);
    }

    /**
     * @see javax.media.protocol.DataSource#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     * @see javax.media.protocol.DataSource#start()
     */
    public void start() {
        playing = true;
        streamSource.play(scale, seek);
    }

    /**
     * @see javax.media.protocol.DataSource#stop()
     */
    public void stop() {
        playing = false;
        seek = streamSource.getCurrentTime();
        streamSource.teardown();
        streamSource = new StreamSource(this, filename);
    }

    /**
     * Handles an RTP Packet
     * @param packet The packet to handle
     */
    public void handleRTPPacket(DatagramPacket packet) {
        rtpStream.handlePacket(packet);
    }

    /**
     * Seeks to a new time
     * @param seek The new time to seek to
     * @param scale The new scale to play at
     */
    public void seek(long seek, double scale) {
        this.seek = seek;
        this.scale = scale;
        streamSource.teardown();
        streamSource = new StreamSource(this, filename);
        if (playing) {
            streamSource.play(scale, seek);
        }
    }

    /**
     * Gets the current time of the playback
     * @return The current time in milliseconds
     */
    public long getCurrentTime() {
        return streamSource.getCurrentTime();
    }

    /**
     * @see javax.media.protocol.DataSource#getDuration()
     */
    public Time getDuration() {
        return new Time(streamSource.getDuration() * 1000000);
    }

    /**
     *
     * @see javax.media.protocol.Positionable#isRandomAccess()
     */
    public boolean isRandomAccess() {
        return true;
    }

    /**
     *
     * @see javax.media.protocol.Positionable#setPosition(javax.media.Time, int)
     */
    public Time setPosition(Time where, int rounding) {
        boolean wasPlaying = playing;
        stop();
        seek = where.getNanoseconds() / 1000000;
        playing = wasPlaying;
        start();
        return where;
    }
}
