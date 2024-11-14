package me.dmitrygubanov40.concan.utility;

import me.dmitrygubanov40.concan.buffer.OutputBuffer;



/**
 * Final cover class to work with console/buffer.
 * It includes the base engine and all premade covers with extra methods.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConUt extends UtilityMethods
{
    
    ////////////////////
    
    /**
     * Default (empty) constructor for direct console output.
     * Operating with standard 'print' (see 'output'-method), or do not need at all.
     */
    public ConUt() {
        this.buffer = null;
    }
    
    /**
     * To send characters right ti the buffer.
     * @param initBuffer buffer the utility will work with
     * @throws NullPointerException when there is no real buffer to use
     */
    public ConUt(final OutputBuffer initBuffer) throws NullPointerException {
        //
        if ( null == initBuffer ) {
            String excMsg = "Cannot initialize buffer for ConUt (console utility)";
            throw new NullPointerException(excMsg);
        }
        //
        this.buffer = initBuffer;
    }
    
    ////////////////////
    
    
    
}
