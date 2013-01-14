/*
 * @(#)RTPHeader.java
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

package rtspd;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Represents the header of an RTP packet
 *
 * @author Andrew G D Rowley
 * @version 2-0-alpha
 */
public class RTPHeader {

    /**
     * The current RTP Header Version
     */
    public static final int VERSION = 2;

    /**
     * The maximum payload type
     */
    public static final int MAX_PAYLOAD = 127;

    /**
     * The size of the RTP Header
     */
    public static final int SIZE = 12;

    /**
     * The maximum RTP sequence
     */
    public static final int MAX_SEQUENCE = 65535;

    /**
     * Unsigned int to long conversion mask
     */
    public static final long UINT_TO_LONG_CONVERT = 0x00000000ffffffffL;

    /**
     * Unsigned short to int conversion mask
     */
    public static final int USHORT_TO_INT_CONVERT = 0x0000ffff;

    // The mask to extract the version from a short
    private static final int VERSION_MASK = 0xc000;

    // The shift to extract the version from a short
    private static final int VERSION_SHIFT = 14;

    // The mask to extract the padding from a short
    private static final int PADDING_MASK = 0x2000;

    // The shift to extact the padding from a short
    private static final int PADDING_SHIFT = 13;

    // The mask to extract the extension bit from a short
    private static final int EXTENSION_MASK = 0x1000;

    // The shift to extract the extension bit from a short
    private static final int EXTENSION_SHIFT = 12;

    // The mask to extract the CSRC from a short
    private static final int CSRC_MASK = 0x0f00;

    // The shift to extract the CSRC from a short
    private static final int CSRC_SHIFT = 8;

    // The mask to extract the marker bit from a short
    private static final int MARKER_MASK = 0x0080;

    // The shift to extract the marker bit from a short
    private static final int MARKER_SHIFT = 7;

    // The mask to extract the type from a short
    private static final int TYPE_MASK = 0x007f;

    // The shift to extract the type from a short
    private static final int TYPE_SHIFT = 0;

    // The first 16 bits of the header
    private int flags;

    // The second 16 bits of the header
    private int sequence;

    // The third and fourth 16 bits of the header
    private long timestamp;

    // The fifth and sixth 16 bits of the header
    private long ssrc;

    /**
     * Creates a new RTPHeader
     *
     * @param packet
     *            The packet to parse the header from
     * @throws IOException
     */
    public RTPHeader(DatagramPacket packet) throws IOException {
        this(packet.getData(), packet.getOffset(), packet.getLength());
    }

    /**
     * Creates a new RTPHeader
     *
     * @param data
     *            The packet to parse the header from
     * @param offset The offset where to start in the data
     * @param length The length of the data to read
     * @throws IOException
     */
    public RTPHeader(byte[] data, int offset, int length) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
                data, offset, length));

        // Read the header values
        this.flags = stream.readUnsignedShort();
        this.sequence = stream.readUnsignedShort();
        this.timestamp = stream.readInt() & UINT_TO_LONG_CONVERT;
        this.ssrc = stream.readInt() & UINT_TO_LONG_CONVERT;
    }

    /**
     * Returns the header flags fields
     * @return The flags of the header
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns the RTP version
     * @return The RTP version implemented
     */
    public short getVersion() {
        return (short) ((getFlags() & VERSION_MASK) >> VERSION_SHIFT);
    }

    /**
     * Returns the padding flag value
     * @return The padding in the data of the packet
     */
    public short getPadding() {
        return (short) ((getFlags() & PADDING_MASK) >> PADDING_SHIFT);
    }

    /**
     * Returns the RTP extension header
     * @return Any extension to the header
     */
    public short getExtension() {
        return (short) ((getFlags() & EXTENSION_MASK) >> EXTENSION_SHIFT);
    }

    /**
     * Returns the number of CSRCs in the packet
     * @return A count of Csrcs in the packet
     */
    public short getCsrcCount() {
        return (short) ((getFlags() & CSRC_MASK) >> CSRC_SHIFT);
    }

    /**
     * Returns the marker bit
     * @return The marker of the packet
     */
    public short getMarker() {
        return (short) ((getFlags() & MARKER_MASK) >> MARKER_SHIFT);
    }

    /**
     * Returns the RTP type
     * @return The type of the data in the packet
     */
    public short getPacketType() {
        return (short) ((getFlags() & TYPE_MASK) >> TYPE_SHIFT);
    }

    /**
     * Returns the sequence number
     * @return The sequence number of the packet
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Returns the packet timestamp
     * @return The timestamp of the packet
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the source id
     * @return The ssrc of the data source
     */
    public long getSsrc() {
        return ssrc;
    }
}
