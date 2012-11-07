/*
 * @(#)RenderingThread.java
 * Created: 15 Nov 2007
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

package javax.media.renderer.video;

import javax.media.Buffer;
import javax.media.Renderer;
import javax.media.protocol.DataSource;

import net.crew_vre.media.processor.DataSink;


/**
 * A thread to render a datasource
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RenderingThread extends DataSink {

    private Renderer renderer = null;

    /**
     * A thread for processing a track from a data source
     *
     * @param dataSource The data source
     * @param track The track to process
     * @param renderer The renderer to use
     */
    public RenderingThread(DataSource dataSource, int track,
            Renderer renderer) {
        super(dataSource, track);
        this.renderer = renderer;
    }

    /**
     *
     * @see simplejmf.DataSink#handleBuffer(javax.media.Buffer)
     */
    public void handleBuffer(Buffer buffer) {
        renderer.process(buffer);
    }
}
