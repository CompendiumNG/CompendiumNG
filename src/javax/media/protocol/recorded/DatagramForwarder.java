/*
 * @(#)DatagramForwarder.java
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
import java.util.HashMap;
import java.util.LinkedList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

import rtspd.RTPHeader;

/**
 * Forwards received datagrams
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DatagramForwarder implements PushBufferStream {

    private LinkedList<byte[]> queue = new LinkedList<byte[]>();

    private boolean done = false;

    private BufferTransferHandler transferHandler = null;

    private Format format = null;

    private static final HashMap<Integer, Format> FORMAT_MAP =
        new HashMap<Integer, Format>();

    static {
        FORMAT_MAP.put(0, new AudioFormat(AudioFormat.ULAW_RTP, 8000, 16, 1));
        FORMAT_MAP.put(26, new VideoFormat("jpeg/rtp"));
        FORMAT_MAP.put(31, new VideoFormat("h261/rtp"));
        FORMAT_MAP.put(32, new VideoFormat("mpeg/rtp"));
        FORMAT_MAP.put(34, new VideoFormat("h263/rtp"));
        FORMAT_MAP.put(42, new VideoFormat("h263-1998/rtp"));
        FORMAT_MAP.put(77, new VideoFormat("h261as/rtp"));
        FORMAT_MAP.put(84, new AudioFormat(AudioFormat.ULAW_RTP, 16000, 16, 1));
        FORMAT_MAP.put(112, new AudioFormat(AudioFormat.LINEAR, 16000,
                16, 1, AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED));
    }

    /**
     * Set the packet type
     * @param packetType The packet type to set
     * @throws UnsupportedFormatException
     */
    public void setFormat(int packetType) throws UnsupportedFormatException {
        format = getFormat(packetType);
    }

    private Format getFormat(int type) throws UnsupportedFormatException {
        Format format = FORMAT_MAP.get(type);
        if (format == null) {
            throw new UnsupportedFormatException(
                    "No format for RTP type " + type, format);
        }
        return format;
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public void read(Buffer buffer) {
        if (done) {
            buffer.setDiscard(true);
            buffer.setEOM(true);
            return;
        }

        byte[] data = null;
        synchronized (queue) {
            if (queue.isEmpty()) {
                buffer.setDiscard(true);
                buffer.setEOM(false);
                return;
            }

            data = queue.removeFirst();
        }
        try {
            RTPHeader header = new RTPHeader(data, 0, data.length);
            buffer.setData(data);
            buffer.setOffset(RTPHeader.SIZE);
            buffer.setLength(data.length - RTPHeader.SIZE);
            buffer.setTimeStamp(header.getTimestamp());
            buffer.setSequenceNumber(header.getSequence());
            buffer.setFormat(format);
            int flags = Buffer.FLAG_RTP_TIME;
            if (header.getMarker() == 1) {
                flags = flags | Buffer.FLAG_RTP_MARKER;
            }
            buffer.setFlags(flags);
        } catch (IOException e) {
            e.printStackTrace();
            buffer.setDiscard(true);
        }

    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#setTransferHandler(
     *     javax.media.protocol.BufferTransferHandler)
     */
    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        return done;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String arg0) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     * Closes the stream
     *
     */
    public void close() {
        done = true;
    }

    /**
     * Adds a packet to the queue
     * @param packet The packet to add
     */
    public void handlePacket(DatagramPacket packet) {
        if ((transferHandler != null) && !done) {
            if (packet.getLength() >= 20) {
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(),
                        packet.getOffset(), data, 0, data.length);
                synchronized (queue) {
                    queue.addLast(data);
                }
                transferHandler.transferData(this);
            }
        }
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public Format getFormat() {
        return format;
    }

}
