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
public class WindowOutputBuffer
        extends OutputBuffer
        implements WinBufEventImpulser
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
        cmdCharacters.add(ConUt.ESC);// most important, must be checked first for 'break'
        cmdCharacters.add(ConUt.CR);
        cmdCharacters.add(ConUt.BS);
        cmdCharacters.add(ConUt.LF);
        cmdCharacters.add(ConUt.HT);
        cmdCharacters.add(ConUt.VT);
    }
    
    
    // Counted length of only regular visual characters.
    // Here and next we consider 'send...'-methods to be invisible.
    private int bufferVisualLength;
    
    // list of everything which wants to react out changes
    private List<WinBufEventListener> eventListeners;
    
    
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
        this.eventListeners = new ArrayList<>();
    }
    
    /////////////
    
    @Override
    public void addEventListener(final WinBufEventListener listener) {
        this.eventListeners.add(listener);
    }
    
    @Override
    public void removeEventListener(final WinBufEventListener listener) {
        this.eventListeners.remove(listener);
    }
    
    @Override
    public void notifyEventListeners(final WinBufEvent event) {
        if ( this.eventListeners.isEmpty() ) return;
        //
        for ( WinBufEventListener currentListener : this.eventListeners ) {
            currentListener.onWindowOutputBufferEvent(event);
        }
    }
    
    /**
     * Check if the 'potentialListener' is already in the list.
     * @param potentialListener object we check
     * @return whether we have such a listener
     */
    public boolean isAlreadyListener(WinBufEventListener potentialListener) {
        return this.eventListeners.contains(potentialListener);
    }
    
    /**
     * Full version to rapid generate of events from 'WindowOutputBuffer' in one line.
     * @param genEventType event type from 'WinBufEventType'
     * @param genEventFlags any states or conditions in integer
     * @param genEventText extra free text in event
     */
    public void generateEvent(final WinBufEventType genEventType,
                                final int genEventFlags,
                                final String genEventText) {
        WinBufEvent genEvent = new WinBufEvent(this,
                                                genEventType,
                                                genEventFlags,
                                                genEventText);
        this.notifyEventListeners(genEvent);
    }
    public void generateEvent(final WinBufEventType genEventType,
                                final int genEventFlags) {
        this.generateEvent(genEventType, genEventFlags, "");
    }
    public void generateEvent(final WinBufEventType genEventType,
                                final String genEventText) {
        this.generateEvent(genEventType, 0, genEventText);
    }
    public void generateEvent(final WinBufEventType genEventType) {
        this.generateEvent(genEventType, 0, "");
    }
    
    
    /////////////
    
    
    // banned
    @Override
    public void setAutoFlush(final boolean autoFlushMode)
                    throws IllegalCallerException {
        String excMsg = "Cannot change autoflush mode for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    /*
    // banned
    @Override
    public void sliceOut(final int sliceSize, final int startSliceIndex)
                    throws IllegalCallerException {
        String excMsg = "Cannot slice part of buffer for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    */
    // banned
    @Override
    public void setStrictSizeControlMode(final boolean bufferStrictMode)
                    throws IllegalCallerException {
        String excMsg = "Cannot change strict control mode for window's output buffer";
        throw new IllegalCallerException(excMsg);
    }
    
    
    
    /**
     * Send pre-event, do the flush, clear visual length, and send post-event.
     */
    @Override
    public void flush() {
        final int keptBeforeFlushLength = this.getBufferLength();
        final String keptBeforeFlushStr = this.getBufferStr();
        //
        // know via event everything are ready to output
        this.generateEvent(WinBufEventType.ON_BEFORE_FLUSH,
                            keptBeforeFlushLength,  // _our_ data about line length (can be zero for commands)
                            keptBeforeFlushStr);    // current buffer string line
        //
        // store buffer's string and length after 'before'-event
        final int keptBehindBeforeFlushLength = this.getBufferLength();
        final String keptBehindBeforeFlushStr = this.getBufferStr();
        //
        super.flush();
        //
        // 'after'-events recieves buffer data after 'before'-event
        this.generateEvent(WinBufEventType.ON_AFTER_FLUSH, keptBehindBeforeFlushLength, keptBehindBeforeFlushStr);
    }
    
    /**
     * Send pre-event, do auto-flush, and send post-event.
     */
    @Override
    protected void autoflush() {
        final String keptStr = this.getBufferStr();
        final int calculatedKeptStrLength;
        if ( this.isCmdStr(keptStr) ) {
            calculatedKeptStrLength = 0;
        } else {
            calculatedKeptStrLength = keptStr.length();
        }
        // So, if 'calculatedKeptStrLength' is zero we got line with some sort of command.
        //
        this.generateEvent(WinBufEventType.ON_BEFORE_AUTOFLUSH,
                            calculatedKeptStrLength,    // length of string which we want to consider
                            keptStr);                   // current buffer string line
        //
        // store buffer's string and length after 'before'-event
        final int keptBehindBeforeAutoflushLength = this.getBufferLength();
        final String keptBehindBeforeAutoflushStr = this.getBufferStr();
        //
        super.autoflush();
        //
        // 'after'-events recieves buffer data after 'before'-event
        this.generateEvent(WinBufEventType.ON_AFTER_AUTOFLUSH,
                            keptBehindBeforeAutoflushLength,
                            keptBehindBeforeAutoflushStr);
    }
    
    
    
    /**
     * Here in 'WindowOutputBuffer' we need carefully check whether it is a command or not.
     * @param strToCheck text to buffer we must to analyze
     * @return 'true' in case we consider the line to be a command (escape sequence or special character)
     */
    @Override
    protected boolean isCmdStr(final String strToCheck) {
        boolean isCmdStatus = false;
        if ( strToCheck.length() <= 0 ) {
            return isCmdStatus;
        }
        if ( WindowOutputBuffer.cmdCharacters.isEmpty() ) {
            return isCmdStatus;
        }
        //
        for ( int i = 0; i < WindowOutputBuffer.cmdCharacters.size(); i++ ) {
            String currentCharCheck = WindowOutputBuffer.cmdCharacters.get(i);
            if ( strToCheck.contains(currentCharCheck) ) {
                isCmdStatus = true;
                break;
            }
        }
        //
        return isCmdStatus;
    }
    
    
    
    /**
     * Suppose to add common non-command visual text via 'add'.
     * @param newCharsToBuffer 
     */
    @Override
    protected void addText(final String newCharsToBuffer) {
        //
        super.addText(newCharsToBuffer);
        //
    }
    
    /**
     * Special chars or escape sequence - to buffer.
     * Only whole-adding -> This is a clone of the 'addCmdWhole'-method.
     * @param newCmdCharsToBuffer
     */
    @Override
    protected void addCmd(final String newCmdCharsToBuffer) {
        //
        // Always add commands (escape sequences, special symbols) as whole word.
        // * * *
        this.addCmdWhole(newCmdCharsToBuffer);
        // * * *
        //
    }
    
    /**
     * Suppose to add common non-command visual text.
     * @param wholeCharsToBuffer 
     */
    @Override
    protected void addTextWhole(final String wholeCharsToBuffer) {
        //
        super.addTextWhole(wholeCharsToBuffer);
        //
    }
    
    /**
     * Suppose to add only command text,
     * enlargement of buffer visual size is prohibited ('WINDOW_ANY_CMD_LENGTH').
     * We assume that any special char can be added, because such characters
     * do not have length in window's output buffer ('WINDOW_ANY_CMD_LENGTH').
     * Pre- and post-events included to work with zero-size strings of commands.
     * @param wholeCmdCharsToBuffer 
     */
    @Override
    protected void addCmdWhole(final String wholeCmdCharsToBuffer) {
        //
        this.generateEvent(WinBufEventType.ON_BEFORE_CMD_SENT,
                            wholeCmdCharsToBuffer); // our command
        //
        // second arguments is crucial (as empty string with zero-length):
        this.doAddWhole(wholeCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
        //
        this.generateEvent(WinBufEventType.ON_AFTER_CMD_SENT,
                            wholeCmdCharsToBuffer);
    }
    
    
    
}
