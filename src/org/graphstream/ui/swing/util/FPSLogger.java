package org.graphstream.ui.swing.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/** Very simple logger for Frame-Per-Second measurements.
 */
public class FPSLogger {
	
	String fileName ;
	
	 /** Start time for a frame. */
    protected long T1 = 0;
    
    /** End Time for a frame. */
    protected long T2 = 0;
    
    /** Output channel. */
    protected PrintStream out = null;
    
    /** @param fileName The name of the file where measurements will be written, frame by frame. */
	public FPSLogger(String fileName) {
		this.fileName = fileName ;
	}
	
	/**Start a new frame measurement. */
	public void beginFrame() {
        T1 = System.currentTimeMillis();
    }
	
	public void endFrame() {
		T2 = System.currentTimeMillis();
				
        if(out == null) {
            try {
				out = new PrintStream(fileName);
			}
            catch (FileNotFoundException e) {	e.printStackTrace();	}
            out.println("# Each line is a frame.");
            out.println("# 1 FPS instantaneous frame per second");
            out.println("# 2 Time in milliseconds of the frame");
        }
        
        long time = T2 - T1;
        double fps  = 1000.0 / time;
        out.println(fps+".2f   "+time);
	}
	
	/** Ensure the log file is flushed and closed. Be careful, calling `endFrame()`
	 * after `close()` will reopen the log file and erase prior measurements. */
	public void close() {
		if(out != null) {
			out.flush();
			out.close();
			out = null;
		}
	}
}
