/*
 * @(#)RGBRenderer.java
 * Created: 17-Oct-2005
 * Version: 1-1-alpha3
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

import java.awt.Component;
import java.awt.Rectangle;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.PlugInManager;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.renderer.VideoRenderer;

import net.crew_vre.media.processor.SimpleProcessor;

import com.sun.media.BasicCodec;

/**
 * A Renderer of RGB Data
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RGBRenderer extends BasicCodec implements VideoRenderer {

    private static final int UPDATE_TIME = 1000;

    private SimpleProcessor processor = null;

    private SimpleProcessor effectProcessors[] = new SimpleProcessor[0];

    private VideoRenderer renderer = null;

    private VideoRenderer preview = null;

    private Effect[] renderEffects = new Effect[0];

    private long lastUpdateTime = 0;

    private RenderingThread thread = null;

    /**
     * Creates a new Renderer of RGB Data
     *
     * @param renderEffects Effects to apply before rendering
     *
     */
    public RGBRenderer(Effect[] renderEffects) {
        inputFormats = new Format[]{new VideoFormat(null)};
        this.renderEffects = renderEffects;
        effectProcessors = new SimpleProcessor[renderEffects.length];
    }

    /**
     * Sets the data source to use
     * @param dataSource The data source
     * @param track The track to render
     */
    public void setDataSource(DataSource dataSource, int track) {
        thread = new RenderingThread(dataSource, track, this);
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        return 0;
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format arg0) {
        return new Format[0];
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    @SuppressWarnings("unchecked")
    public Format setInputFormat(Format inputFormat) {

        // Find a processor from the input format to the effects
        for (int i = 0; i < renderEffects.length; i++) {
            System.err.println("Finding input for effect " + renderEffects[i]);
            Format format = renderEffects[i].setInputFormat(inputFormat);
            if (format == null) {
                Format[] inputs = renderEffects[i].getSupportedInputFormats();
                SimpleProcessor proc = null;
                for (int j = 0; (j < inputs.length) && (proc == null); j++) {
                    try {
                        proc = new SimpleProcessor(inputFormat, inputs[j]);
                    } catch (Exception e) {
                        proc = null;
                    }
                }
                if (proc != null) {
                    effectProcessors[i] = proc;
                    inputFormat = proc.getOutputFormat();
                }
            }
        }
        Vector<String> renderers;
        renderers = PlugInManager.getPlugInList(
                new VideoFormat(null), null, PlugInManager.RENDERER);
        renderers = new Vector<String>();
        //renderers.add("com.sun.media.renderer.video.DDRenderer");
        renderers.add("net.sf.fmj.media.renderer.video.Java2dRenderer");
        //renderers.add("rtpReceiver.GDIYUVRenderer");
        //renderers.add("rtpReceiver.AWTRenderer");
        for (int i = 0; (i < renderers.size()) && (renderer == null); i++) {
            String rendererClassName = renderers.get(i);
            System.out.println("renderer " + rendererClassName);

            try {
                System.err.println("Trying renderer " + rendererClassName);
                Class rendererClass = Class.forName(rendererClassName);
                VideoRenderer r = (VideoRenderer)
                    rendererClass.newInstance();
                Format input = r.setInputFormat(inputFormat);
                if (input == null) {
                    processor = new SimpleProcessor(inputFormat, r);
                } else {
                    r.open();
                }
                System.err.println("renderer " + rendererClassName + " " + input + " " + processor);
                if ((input != null) || (processor != null)) {
                    renderer = r;
                    renderer.start();
                    preview = (VideoRenderer) rendererClass.newInstance();
                    if (processor != null) {
                        preview.setInputFormat(processor.getOutputFormat());
                    } else if (input != null) {
                        preview.setInputFormat(input);
                    }
                    preview.open();
                    preview.start();
                }
                System.err.println("Renderer class = " + rendererClass);
            } catch (Exception e) {
                e.printStackTrace();
                renderer = null;
            }
        }
        if (renderer != null) {
            return inputFormat;
        }
        return null;
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "RGBRenderer";
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#getComponent()
     */
    public Component getComponent() {
        return renderer.getComponent();
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#setComponent(java.awt.Component)
     */
    public boolean setComponent(Component comp) {
        return false;
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#setBounds(java.awt.Rectangle)
     */
    public void setBounds(Rectangle rect) {
        renderer.setBounds(rect);
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#getBounds()
     */
    public Rectangle getBounds() {
        return renderer.getBounds();
    }

    /**
     *
     * @see javax.media.Renderer#start()
     */
    public void start() {
        if (thread != null) {
            thread.start();
        }
    }

    /**
     *
     * @see javax.media.Renderer#stop()
     */
    public void stop() {
        if (thread != null) {
            thread.close();
        }
    }

    private boolean updatePreview() {
        if ((System.currentTimeMillis() - lastUpdateTime) > UPDATE_TIME) {
            lastUpdateTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     *
     * @see javax.media.Renderer#process(javax.media.Buffer)
     */
    public int process(Buffer input) {
        int retval = 0;
        boolean visible = getComponent().isVisible();
        retval = BUFFER_PROCESSED_OK;

        // Run through the effects
        for (int i = 0; (i < effectProcessors.length)
                && ((retval == BUFFER_PROCESSED_OK)
                    || (retval == INPUT_BUFFER_NOT_CONSUMED)); i++) {
            if (effectProcessors[i] != null) {
                retval = effectProcessors[i].process(input, false);
                input = effectProcessors[i].getOutputBuffer();
                if (visible && ((retval == BUFFER_PROCESSED_OK)
                    || (retval == INPUT_BUFFER_NOT_CONSUMED))) {
                    Buffer b = new Buffer();
                    b.setOffset(0);
                    b.setLength(0);
                    b.setFlags(0);
                    b.setSequenceNumber(0);
                    b.setTimeStamp(0);

                    retval = renderEffects[i].process(input, b);
                    input = b;
                }
            }
        }


        // Run the final processor
        if ((processor != null) && ((retval == BUFFER_PROCESSED_OK)
                || (retval == INPUT_BUFFER_NOT_CONSUMED))) {
            retval = processor.process(input, false);
        }

        if ((retval == BUFFER_PROCESSED_OK)
                || (retval == INPUT_BUFFER_NOT_CONSUMED)) {
            if (visible) {
                if (processor != null) {

                    retval = renderer.process(processor.getOutputBuffer());
                } else {
                    retval = renderer.process(input);
                }
            }
            if (updatePreview()) {
                if (processor != null) {
                    retval = preview.process(processor.getOutputBuffer());
                } else {
                    retval = preview.process(input);
                }
            }
        }
        return retval;
    }

    /**
     * Gets the video renderer for the preview
     * @return the preview video renderer
     */
    public VideoRenderer getPreviewRenderer() {
        return preview;
    }

    /**
     *
     * @see com.sun.media.BasicPlugIn#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        for (int i = 0; i < effectProcessors.length; i++) {
            if (effectProcessors[i] != null) {
                Object control = effectProcessors[i].getControl(className);
                if (control != null) {
                    return control;
                }
            }
        }
        if (processor != null) {
            Object control = processor.getControl(className);
            if (control != null) {
                return control;
            }
        }
        return renderer.getControl(className);
    }
}
