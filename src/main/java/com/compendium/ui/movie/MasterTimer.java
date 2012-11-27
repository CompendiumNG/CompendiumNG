package com.compendium.ui.movie;

import javax.media.NotRealizedError;
import javax.media.Time;

import com.compendium.LanguageProperties;
import com.sun.media.BasicController;

/**
 * This class extends a BasicController and acts as the master time line time clock.
 * @author Michelle Bachler
 *
 */public class MasterTimer extends BasicController {
	
    private String MediaTimeError = LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "MasterTimer.unrealizedPlayer"); //$NON-NLS-1$
	
    //private Time duration = new Time(0);
    
	public MasterTimer() {
		super();
	}
	
	/*public Time getDuration() {
		return duration;
	}*/

	/*public void setDuration(Time dur) {
		setStopTime(new Time(stopTime));
		duration = dur;
	}*/
	
    /**
     * Loops through the list of controllers maintained by this
     * player and invoke setMediaTime on each of them.
     * This is a "final" method and cannot be overridden by subclasses.
     * @param now the target media time.
     **/
    public void setMediaTime(Time now) {
    	boolean aboutToRestart = false;
    	
    	if (state < Realized)
    	    throwError(new NotRealizedError(MediaTimeError));
    	
    	if (getState() == Started) {
    	    aboutToRestart = true;
    	    stop();
    	}

    	super.setMediaTime(now);

    	if (aboutToRestart) {
    	    syncStart(now);
    	    aboutToRestart = false;
    	}
    }
    
    /**
     * Invoked by start() or syncstart().
     * Called from a separate thread called TimedStart thread.
     * subclasses can override this method to implement its specific behavior.
     */
    protected void doStart() {
    }

    /**
     * @return true.
     */ 
    protected boolean doRealize() {
    	return true;
    }
    
    /**
     * @return true.
     */ 
    protected  boolean doPrefetch() {
    	return true;
    }
    
    /**
     * Called when the prefetch() is aborted, i.e. deallocate() was called
     * while prefetching.  Release all resources claimed previously by the
     * prefetch call.
     */
    protected final void abortPrefetch() {
    }
    
    /**
     * Called when the realize() is aborted, i.e. deallocate() was called
     * while realizing.  Release all resources claimed previously by the
     * realize() call.
     */
    protected final void abortRealize() {
    }
    
    /**
     * returns false.
     */
    protected boolean isConfigurable() {
    	return true;
    }	    
}