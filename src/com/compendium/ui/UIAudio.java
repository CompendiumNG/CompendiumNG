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

package com.compendium.ui;

import java.io.*;

import javax.sound.midi.*;
import javax.sound.sampled.*;

import com.compendium.ProjectCompendium;


/**
 * Handles the playing of UI audio files for specific action in the application.
 *
 * @author	Michelle Bachler
 */
 public class UIAudio extends Thread {

	/** Represents a linking action.*/
	public static final int LINKING_ACTION		= 400;

	/** Represents an abort  action.*/
	public static final int ABORT_ACTION		= 401;

	/** Represents an about box action.*/
	public static final int ABOUT_ACTION		= 402;

	/** Represents a delinking action.*/
	public static final int DELINKING_ACTION	= 403;

	/** Represents a purging action.*/
	public static final int PURGING_ACTION		= 404;

	/** Is audio turned on or off? true is on, false is off.*/
	private boolean bPlayAudio = true;

	/**
	 * Constructor.
	 */
	public UIAudio() {}

	/**
	 * Start the audio thread.
	 */
	public void run() {}

 	/**
	 * Plays the audio clip for the given action.
	 * @param action, the action to play the audio clip for.
	 * Current actions are:
	 * <li>UIAudio.LINKING_ACTION
	 * <li>UIAudio.ABORT_ACTION
	 * <li>UIAudio.ABOUT_ACTION
	 * <li>UIAudio.DELINKING_ACTION
	 * <li>UIAudio.PURGING_ACTION
	 */
	public void playAudio(int action) {

		String	sPATH = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Audio"+ProjectCompendium.sFS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String file = ""; //$NON-NLS-1$
		int playTime = 0;

		if(!bPlayAudio)
			return;

		switch(action) {

			case(LINKING_ACTION):
				file = "link.au"; //$NON-NLS-1$
				playTime = 400;
			break;

			case(DELINKING_ACTION):
				file = "delink.au"; //$NON-NLS-1$
				playTime = 400;
			break;

			case(ABOUT_ACTION):
				file = "about.au"; //$NON-NLS-1$
				playTime = 1000;
			break;

			case(ABORT_ACTION):
				file = "abort.au"; //$NON-NLS-1$
				playTime = 400;
			break;

			case(PURGING_ACTION):
				file = "purge.au"; //$NON-NLS-1$
				playTime = 1000;
			break;

			default:
				file = "boing.au"; //$NON-NLS-1$
				playTime = 500;
			break;
		}

		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(sPATH + file));
			AudioFormat format = stream.getFormat();

			if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
				(format.getEncoding() == AudioFormat.Encoding.ALAW))  {
				AudioFormat tmp = new AudioFormat(
				                          AudioFormat.Encoding.PCM_SIGNED,
				                          format.getSampleRate(),
				                          format.getSampleSizeInBits() * 2,
				                          format.getChannels(),
				                          format.getFrameSize() * 2,
				                          format.getFrameRate(),
				                          true);
				stream = AudioSystem.getAudioInputStream(tmp, stream);
				format = tmp;
			}

			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
			                      ((int) stream.getFrameLength() * format.getFrameSize()));

			final Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);

			Thread thread = new Thread("Audio: start clip") { //$NON-NLS-1$
				public void run() {
    		        clip.start();
				}
			};
			thread.start();

	        try { Thread.currentThread().sleep(playTime); }
			catch (Exception e) { }

    		clip.stop();
    		clip.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Set the audio as on or off.
	 * @param state, true if audio is on, else false.
	 */
	public void setAudio(boolean state) {
		bPlayAudio = state;
		FormatProperties.setFormatProp( "audioOn", new Boolean(bPlayAudio).toString() ); //$NON-NLS-1$
		FormatProperties.saveFormatProps();
	}

	/**
	 * Return if the audio is switched on or off.
	 * @return boolean, true if the audio is on, else false.
	 */
	public boolean getAudio() {
		return bPlayAudio;
	}
}


