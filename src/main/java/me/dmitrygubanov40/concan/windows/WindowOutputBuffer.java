package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.buffer.OutputBuffer;



/**
 * Automated buffer for output in console window.
 * For window only visual strings are important.
 * Suppose that 'add' and 'addWhole' add visual text,
 * while 'addCmd' and 'addCmdWhole' - invisible commands
 * (special chars and escape sequences).
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
//class WindowOutputBuffer extends OutputBuffer
        //!!!!!
public class WindowOutputBuffer extends OutputBuffer
{
    
    // only this autoflush mode for window
    private final static boolean WINDOW_AUTOFLUSH_MODE;
    // only this strict control mode for window
    private final static boolean WINDOW_STRICT_SIZE_CONTROL_MODE;
    // command chars and strings (escape sequences) are "empty" in window
    // and do not have a length
    private final static int WINDOW_ANY_CMD_LENGTH;
    
    static {
        WINDOW_AUTOFLUSH_MODE = true;
        WINDOW_STRICT_SIZE_CONTROL_MODE = true;
        WINDOW_ANY_CMD_LENGTH = 0;
    }
    
    
    // Counted length of only regular visual characters.
    // Here and next we consider 'send...'-methods to be invisible.
    private int bufferVisualLength;
    
    
    /////////////
    
    /**
     * Base constructor for window's buffer.
     * @param initSize max length (in chars)
     * @param isSafeAsync created for many threads(or for only one thread)?
     */
    public WindowOutputBuffer(final int initSize, final boolean isSafeAsync) {
        super(initSize,
                isSafeAsync,
                WINDOW_AUTOFLUSH_MODE,
                WINDOW_STRICT_SIZE_CONTROL_MODE);
        //
        this.setBufferVisualLength(0);
    }
    
    /////////////
    
    /**
     * Update 'bufferVisualLength' (visual part).
     * @param newVisualLength new value to install
     * @throws IllegalArgumentException for negative length
     */
    private void setBufferVisualLength(final int newVisualLength) throws IllegalArgumentException {
        if ( newVisualLength < 0 ) {
            String excMsg = "Incorrect buffer visual length to set: " + newVisualLength;
            throw new IllegalArgumentException(excMsg);
        }
        this.bufferVisualLength = newVisualLength;
    }
    
    /**
     * Add some length to visual part buffer ('bufferVisualLength').
     * @param visualString visual string to buffer
     */
    private void addToBufferVisualLength(final String visualString) {
        int newVisualLength = this.getBufferLength();
        newVisualLength += visualString.length();
        //
        this.setBufferVisualLength(newVisualLength);
    }
    
    /**
     * @return (for window) current buffer visual length
     */
    @Override
    protected int getBufferLength() {
        return this.bufferVisualLength;
    }
    
    
    
    // banned
    @Override
    public void setAutoFlush(final boolean autoFlushMode)
                    throws IllegalCallerException {
        String excMsg = "Cannot change autoflush mode for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    
    // banned
    @Override
    public void sliceOut(final int sliceSize, final int startSliceIndex)
                    throws IllegalCallerException {
        String excMsg = "Cannot slice part of buffer for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    
    // banned
    @Override
    public void setStrictSizeControlMode(final boolean bufferStrictMode)
                    throws IllegalCallerException {
        String excMsg = "Cannot change strict control mode for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    
    
    
    /**
     * Do the flush, and clear visual length.
     */
    @Override
    public void flush() {
        super.flush();
        //
        this.setBufferVisualLength(0);
    }
    
    
    
    /**
     * Suppose to add common non-command visual text via 'add',
     * need to enlarge buffer visual size.
     * @param newCharsToBuffer 
     */
    @Override
    public void add(final String newCharsToBuffer) {
        super.add(newCharsToBuffer);
        //
        // for regular buffer addition - enlarge visual counter
        // by the last added str
        // ('newCharsToBuffer' could be cut because of buffer size limit)
        this.addToBufferVisualLength(this.getLastAddedStr());
    }
    
    /**
     * Special chars or escape sequence - to buffer.
     * We assume that any special char can be added, because such characters
     * do not have length in window's output buffer.
     * @param newCmdCharsToBuffer
     */
    @Override
    public void addCmd(final String newCmdCharsToBuffer) {
        this.doAdd(newCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
    }
    
    /**
     * Suppose to add common non-command visual text via 'addWhole',
     * need to enlarge buffer visual size.
     * @param wholeCharsToBuffer 
     */
    @Override
    public void addWhole(final String wholeCharsToBuffer) {
        super.addWhole(wholeCharsToBuffer);
        //
        // for regular buffer addition - enlarge visual counter
        // For 'whole'-addition the chars would be added,
        // or an exception will come.
        this.addToBufferVisualLength(wholeCharsToBuffer);
    }
    
    /**
     * Suppose to add only command visual text via 'addCmdWhole',
     * enlargement of buffer visual size is prohibited.
     * We assume that any command can be added, because commands
     * do not have length in window's output buffer.
     * @param wholeCmdCharsToBuffer 
     */
    @Override
    public void addCmdWhole(final String wholeCmdCharsToBuffer) {
        this.doAddWhole(wholeCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
    }
    
    
    
}
