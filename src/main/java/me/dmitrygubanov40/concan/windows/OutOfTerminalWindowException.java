package me.dmitrygubanov40.concan.windows;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Exception caused by attempt to move cursor out of terminal window.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class OutOfTerminalWindowException extends Exception
{
    
    // coordinates which can be applied instead
    private ConCord maxAllowedCoords;
    
    /**
     * Rewritten constructor for coordinate purposes.
     * @param msg exception comment
     * @param cause for search of root reason
     * @param coords coordinates which can be applied instead
     */
    public OutOfTerminalWindowException(final String msg, Throwable cause, final ConCord coords) {
        super(msg, cause);
        this.maxAllowedCoords = coords;
    }
    public OutOfTerminalWindowException(final String msg, final ConCord coords) {
        super(msg);
        this.maxAllowedCoords = coords;
    }
    
    /**
     * To get the coordinates from the exception.
     * @return coordinates which can be applied instead
     * @throws NullPointerException when coordinates are absent
     */
    public ConCord getAllowedCoords() {
        if ( null == this.maxAllowedCoords ) {
            String excMsg = "No coordinates were given by exception to apply instead";
            throw new NullPointerException(excMsg);
        }
        //
        return this.maxAllowedCoords;
    }
    
    
    
}
