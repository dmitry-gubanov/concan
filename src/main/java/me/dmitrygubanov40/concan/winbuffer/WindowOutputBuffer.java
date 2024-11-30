package me.dmitrygubanov40.concan.winbuffer;


import java.util.ArrayList;
import java.util.List;

import me.dmitrygubanov40.concan.buffer.OutputBuffer;
import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * Automated buffer for output in console window.
 * For window only visual strings are important.
 * Suppose that 'addText' and 'addTextWhole' add visual text,
 * while 'addCmd' and 'addCmdWhole' - invisible commands
 * (special chars and escape sequences).
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class WindowOutputBuffer extends OutputBuffer
{
    
    // only this autoflush mode for window
    private final static boolean WINDOW_AUTOFLUSH_MODE;
    // only this strict control mode for window
    private final static boolean WINDOW_STRICT_SIZE_CONTROL_MODE;
    // command chars and strings (escape sequences) are "empty" in window
    // and do not have a length
    private final static int WINDOW_ANY_CMD_LENGTH;
    
    // window can have only one char width
    private final static int MIN_WINDOW_BUFFER_SIZE;
    // window need more resources, and cannot be too long
    private final static int MAX_WINDOW_BUFFER_SIZE;
    
    // list of characters we consider to be essential part
    // of commands, not regular, visual text into window
    private static final List<String> cmdCharacters;
    
    
    static {
        WINDOW_AUTOFLUSH_MODE = true;
        WINDOW_STRICT_SIZE_CONTROL_MODE = true;
        WINDOW_ANY_CMD_LENGTH = 0;
        //
        MIN_WINDOW_BUFFER_SIZE = 1;
        MAX_WINDOW_BUFFER_SIZE = 1000;
        //
        // All characters we assume can be only in command, not regular text:
        // (will be filled in descendants)
        cmdCharacters = new ArrayList<>();
        cmdCharacters.add(ConUt.ESC);// most important, must be checked first
        cmdCharacters.add(ConUt.CR);
        cmdCharacters.add(ConUt.BS);
        cmdCharacters.add(ConUt.LF);
        cmdCharacters.add(ConUt.HT);
        cmdCharacters.add(ConUt.VT);
    }
    
    
    // Counted length of only regular visual characters.
    // Here and next we consider 'send...'-methods to be invisible.
    private int bufferVisualLength;
    
    
    /////////////
    
    /**
     * Base constructor for window's buffer.
     * @param initSize max length (in chars)
     * @param isSafeAsync created for many threads (or for only one thread)?
     */
    public WindowOutputBuffer(final int initSize, final boolean isSafeAsync) {
        super(initSize,
                isSafeAsync,
                WINDOW_AUTOFLUSH_MODE,
                WINDOW_STRICT_SIZE_CONTROL_MODE,
                MIN_WINDOW_BUFFER_SIZE,
                MAX_WINDOW_BUFFER_SIZE);
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
     * Here in 'WindowOutputBuffer' we need carefully check whether it is a command or not.
     * @param strToCheck text to buffer we must to analyze
     * @return 'true' in case we consider the line to be a command (escape sequence or special character)
     */
    @Override
    protected boolean isCmdStr(final String strToCheck) {
        boolean isCmd = false;
        if ( strToCheck.length() <= 0 ) {
            return isCmd;
        }
        if ( WindowOutputBuffer.cmdCharacters.isEmpty() ) {
            return isCmd;
        }
        //
        for ( int i = 0; i < WindowOutputBuffer.cmdCharacters.size(); i++ ) {
            String currentCharCheck = WindowOutputBuffer.cmdCharacters.get(i);
            if ( strToCheck.contains(currentCharCheck) ) {
                isCmd = true;
                break;
            }
        }
        //
        return isCmd;
    }
    
    
    /**
     * Suppose to add common non-command visual text via 'add',
     * need to enlarge buffer visual size.
     * @param newCharsToBuffer 
     */
    @Override
    protected void addText(final String newCharsToBuffer) {
        super.addText(newCharsToBuffer);
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
    protected void addCmd(final String newCmdCharsToBuffer) {
        // second arguments is crucial:
        this.doAdd(newCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
    }
    
    /**
     * Suppose to add common non-command visual text,
     * need to enlarge buffer visual size.
     * @param wholeCharsToBuffer 
     */
    @Override
    protected void addTextWhole(final String wholeCharsToBuffer) {
        super.addTextWhole(wholeCharsToBuffer);
        //
        // for regular buffer addition - enlarge visual counter
        // For 'whole'-addition the chars would be added,
        // or an exception will come.
        this.addToBufferVisualLength(this.getLastAddedStr());
    }
    
    /**
     * Suppose to add only command text,
     * enlargement of buffer visual size is prohibited.
     * We assume that any command can be added, because commands
     * do not have length in window's output buffer.
     * @param wholeCmdCharsToBuffer 
     */
    @Override
    protected void addCmdWhole(final String wholeCmdCharsToBuffer) {
        // second arguments is crucial:
        this.doAddWhole(wholeCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
    }
    
    
    
}
