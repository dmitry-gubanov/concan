package me.dmitrygubanov40.concan.buffer;



/**
 * Automated buffer for direct console output.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class OutputBuffer {
    
    // max length, chars
    private final static int DEFAULT_BUFFER_SIZE;
    // less or larger buffer is not efficient
    private final static int MIN_BUFFER_SIZE;
    private final static int MAX_BUFFER_SIZE;
    
    // if buffer is ready to work with many threads (in cost of performance)
    private final static boolean DEFAULT_SAFE_ASYNC_STATUS;
    // autoflush-mode at initialization
    private final static boolean DEFAULT_AUTOFLUSH_MODE;
    // autoflush-mode at initialization
    private final static boolean DEFAULT_STRICT_SIZE_CONTROL_MODE;
    
    // this part of long strings can be displayed
    private final static int LENGTH_TO_SHOW;
    
    static {
        DEFAULT_BUFFER_SIZE = 120;
        MIN_BUFFER_SIZE = 10;
        MAX_BUFFER_SIZE = 100000;
        //
        DEFAULT_SAFE_ASYNC_STATUS = false;
        DEFAULT_AUTOFLUSH_MODE = true;
        DEFAULT_STRICT_SIZE_CONTROL_MODE = false;
        //
        LENGTH_TO_SHOW = 40;
    }
    
    
    private BufferString buffer;
    private int bufferSize;
    
    // Make auto-output when buffer is full, or generate an exception?
    private boolean autoFlush;
    
    // Whether it is possible to get over the buffer size limit
    // for appending purposes?
    private boolean strictSizeControl;
    
    
    
    /**
     * Most general full constructor.
     * @param initSize max length (in chars)
     * @param isSafeAsync created for many threads(or for only one thread)?
     * @param autoFlushMode output automatically
     * @param strictSizeControlMode disallow to get over buffer size limit at the moment of appending
     * @throws IllegalArgumentException with incorrect buffer size
     */
    public OutputBuffer(final int initSize,
                        final boolean isSafeAsync,
                        final boolean autoFlushMode,
                        final boolean strictSizeControlMode)
            throws IllegalArgumentException {
        if ( initSize < MIN_BUFFER_SIZE || initSize > MAX_BUFFER_SIZE ) {
            String excMsg = "Output buffer inappropriate size: " + initSize + "."
                            + " Must be in range: " + MIN_BUFFER_SIZE + " ... " + MAX_BUFFER_SIZE;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.bufferSize = initSize;
        //
        this.initBuffer(isSafeAsync);
        this.setAutoFlush(autoFlushMode);
        this.setStrictSizeControlMode(strictSizeControlMode);
    }
    public OutputBuffer(final int initSize, final boolean isSafeAsync, final boolean autoFlushMode) {
        this(initSize, isSafeAsync, autoFlushMode, DEFAULT_STRICT_SIZE_CONTROL_MODE);
    }
    public OutputBuffer(final int initSize) {
        this(initSize, DEFAULT_SAFE_ASYNC_STATUS, DEFAULT_AUTOFLUSH_MODE);
    }
    public OutputBuffer(final boolean isSafeAsync) {
        this(DEFAULT_BUFFER_SIZE, isSafeAsync, DEFAULT_AUTOFLUSH_MODE);
    }
    public OutputBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    
    
    /**
     * Create base data storage element for chars.
     * @param isSafeAsync in case we need safety with many threads (in cost of speed)
     * @throws IllegalStateException when buffer is already initialized
     */
    private void initBuffer(final boolean isSafeAsync) throws IllegalStateException {
        if ( null != this.buffer ) {
            String excMsg = "Cannot reinitialize already initialized buffer";
            throw new IllegalStateException(excMsg);
        }
        this.buffer = new OutputBufferString(isSafeAsync);
    }
    
    /**
     * To clear all the string in buffer.
     * Information may be lost, do flush if necessary.
     * @throws NullPointerException in case buffer was not initialized
     */
    private void clearBuffer() throws NullPointerException {
        if ( null == this.buffer ) {
            String excMsg = "Cannot clear not initialized buffer";
            throw new NullPointerException(excMsg);
        }
        //
        // buffer was initialized, and it knows status itself
        // re-new with the same one
        boolean wasSafeAsyncStatus = this.buffer.isSafeAsync();
        this.buffer = new OutputBufferString(wasSafeAsyncStatus);
    }
    
    
    
    /**
     * Wrapper to get the current size (in characters) of our buffer.
     * @return length in symbols
     */
    private int getBufferLength() {
        return this.buffer.length();
    }
    
    
    
    /**
     * Print current state of the buffer to console.
     * Has the exact mechanism to draw to the console.
     */
    private void output() {
        //
        System.out.print(this.buffer.toString());
        //
    }
    
    /**
     * Print the part of buffer (when we slice it).
     * @param slice part of buffer to be shown
     */
    private void outputSlice(final String slice) {
        //
        System.out.print(slice);
        //
    }
    
    
    
    /**
     * @return whether our buffer is full (via its limit), or there is more space
     */
    public boolean isFull() {
        boolean result = (this.getBufferLength() > this.bufferSize);
        //
        return result;
    }
    
    
    
    /**
     * Change buffer regime: auto-generate text in console or generate exceptions?
     * @param autoFlushMode do generate text when buffer is full
     */
    public final void setAutoFlush(final boolean autoFlushMode) {
        // de-facto mode will not be switched
        if ( this.autoFlush == autoFlushMode ) return;
        //
        // in case we currently have auto-flush - make output before the switch
        // (it will work only if buffer is not empty)
        if ( this.autoFlush ) this.flush();
        //
        this.autoFlush = autoFlushMode;
    }
    public void setAutoFlush() {
        this.setAutoFlush(true);
    }
    
    
    
    /**
     * Output to console all the buffer, then clear the buffer.
     * Do not work with an empty buffer.
     */
    public void flush() {
        if ( this.getBufferLength() <= 0 ) return;
        //
        this.output();
        this.clearBuffer();
    }
    
    
    /**
     * Get slice from 'startSliceIndex' with size of 'sliceSize', show in console,
     * and then remove the slice from the buffer.
     * Do not work with an empty buffer.
     * If buffer is less or equal than 'sliceSize' (and  'startSliceIndex' is zero), we will get the 'flush'-method.
     * @param sliceSize number of chars in buffer's slice (string) to cut
     * @param startSliceIndex first character index of the slice
     * @throws IllegalArgumentException sliceSize > 0, startSliceIndex >= 0
     */
    public void sliceOut(final int sliceSize, final int startSliceIndex) throws IllegalArgumentException {
        if ( sliceSize <= 0 ) {
            String excMsg = "Cannot slice-out buffer with the slice length: '" + sliceSize + "'";
            throw new IllegalArgumentException(excMsg);
        }
        if ( startSliceIndex < 0 ) {
            String excMsg = "Cannot slice-out buffer with the start char at index: '" + startSliceIndex + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        int buffLength = this.getBufferLength();
        final int endSliceIndex = startSliceIndex + sliceSize;
        //
        // we must have buffer:
        if ( buffLength <= 0 ) return;
        //
        // the buffer must have the tail we want to slice:
        if ( buffLength < endSliceIndex ) return;
        // 
        // with this 'slice' it will be identical to 'flush':
        if ( 0 == startSliceIndex && buffLength <= sliceSize ) {
            this.flush();
            return;
        }
        //
        // need to get the slice from buffer
        String slice = this.buffer.substring(startSliceIndex, endSliceIndex);
        // show
        this.outputSlice(slice);
        // removed from the buffer
        this.buffer.delete(startSliceIndex, endSliceIndex);
    }
    public void sliceOut(final int sliceSize) throws IllegalArgumentException {
        this.sliceOut(sliceSize, 0);
    }
    public void sliceOut() {
        this.sliceOut(1);
    }
    
    
    
    /**
     * Change buffer regime: or rapid appending and flush over buffer size?
     * @param bufferStrictMode do ban to get over the buffer size limit for appending?
     */
    public final void setStrictSizeControlMode(final boolean bufferStrictMode) {
        this.strictSizeControl = bufferStrictMode;
    }
    public final void setStrictSizeControlMode() {
        this.setStrictSizeControlMode(true);
    }
    
    
    
    /**
     * Need a string not more than LENGTH_TO_SHOW,
     * and with <...>-marker if it was cut.
     * @param str
     * @return the string ready to insert
     */
    private String getStrWithLengthToShow(final String str) {
        StringBuilder result = new StringBuilder();
        //
        int end = str.length();
        int start = (str.length() > LENGTH_TO_SHOW)
                        ? (str.length() - LENGTH_TO_SHOW)
                        : 0;
        result.append(str.substring(start, end));
        if ( 0 != start ) result.insert(0, "<...>");
        //
        return result.toString();
    }
    
    /**
     * @param newCharsToBuffer addable characters to the buffer
     * @return ready string for the exception about necessary manual flush 
     */
    private String getAddExceptionMsg(final String newCharsToBuffer) {
        String buffToShow = this.getStrWithLengthToShow(this.buffer.toString());
        String addendumToShow = this.getStrWithLengthToShow(newCharsToBuffer);
        //
        String excMsg = "The buffer will overflow, and autoflush mode is off."
                + " Buffer: " + buffToShow + "."
                + " String to add: " + addendumToShow;
        //
        return excMsg;
    }
    
    /**
     * Put new chars into the buffer.
     * @param newCharsToBuffer string to add to the buffer
     * @throws StringIndexOutOfBoundsException if string is too long and autoflush is off
     */
    public void add(final String newCharsToBuffer) throws StringIndexOutOfBoundsException {
        if ( newCharsToBuffer.length() <= 0 ) return;
        //
        // current state of buffer even with new chars is less buffer's size
        boolean isUnderBuffSize = (this.getBufferLength() + newCharsToBuffer.length()) <= this.bufferSize;
        if ( isUnderBuffSize ) {
            this.buffer.append(newCharsToBuffer);
            return;
        }
        // Here we have a text which will exceed the buffer size.
        //
        if ( !this.autoFlush ) {
            // No way without auto mode (user must flush themselves).
            String excMsg = this.getAddExceptionMsg(newCharsToBuffer);
            throw new StringIndexOutOfBoundsException(excMsg);
        }
        //
        // Here we must add text over buffer size, and it is legal.
        // It might be twice, triple and more longer than buffer size.
        // Also, it is gurantted that autoflush is on.
        //
        // quick appending over buffer size is allowed:
        if ( !this.strictSizeControl ) {
            this.buffer.append(newCharsToBuffer);
            this.flush();
            return;
        }
        //
        // we must keep buffer size:
        // (and add new chars by pieces under strict buffer limit)
        StringBuilder newCharsToBufferOverSize = new StringBuilder(newCharsToBuffer);
        while ( (this.getBufferLength() + newCharsToBufferOverSize.length()) > this.bufferSize ) {
            int cutStart = 0;
            int cutEnd = this.bufferSize - this.getBufferLength();
            //
            String pieceOfNewCharsToAdd = newCharsToBufferOverSize.substring(cutStart, cutEnd);
            this.buffer.append(pieceOfNewCharsToAdd);
            //
            newCharsToBufferOverSize = newCharsToBufferOverSize.delete(cutStart, cutEnd);
            //
            this.flush();
        }
        // now we have a piece less then the size. Add it.
        this.buffer.append(newCharsToBufferOverSize.toString());
    }
    
    
    
    @Override
    public String toString() {
        String returnString = "size: " + this.bufferSize + ", multithread: " + this.buffer.isSafeAsync() + ", "
                                + "autoflush: " + this.autoFlush + ", strict size: " + this.strictSizeControl;
        //
        return returnString;
    }
    
    
    
}
