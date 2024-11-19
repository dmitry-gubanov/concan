package me.dmitrygubanov40.concan.buffer;



/**
 * Automated buffer for direct console output.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class OutputBuffer
{
    
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
    
    
    private OutputBufferString buffer;
    protected int bufferSize;
    
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
        this.changeAutoFlush(autoFlushMode);
        this.changeStrictSizeControlMode(strictSizeControlMode);
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
     * @return the last string which was added to 'buffer'
     */
    protected String getLastAddedStr() {
        return this.buffer.getLastAddedStr();
    }
    
    
    
    /**
     * Wrapper to get the current size (in characters) of our buffer.
     * @return length in symbols, technical - with invisible letters and commands
     */
    protected int getBufferLength() {
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
     * For technical control of buffer state (in case when autoflush may be off).
     * @return whether our buffer is full (over its limit), or there is more space
     */
    public boolean isFull() {
        boolean result = (this.getBufferLength() >= this.bufferSize);
        //
        return result;
    }
    
    
    
    /**
     * Change buffer regime: auto-generate text in console or generate exceptions?
     * @param autoFlushMode do generate text when buffer is full
     */
    private void changeAutoFlush(final boolean autoFlushMode) {
        // de-facto mode will not be switched
        if ( this.autoFlush == autoFlushMode ) return;
        //
        // in case we currently have auto-flush - make output before the switch
        // (it will work only if buffer is not empty)
        if ( this.autoFlush ) this.flush();
        //
        this.autoFlush = autoFlushMode;
    }
    // out world wrapper
    public void setAutoFlush(final boolean autoFlushMode) {
        this.changeAutoFlush(autoFlushMode);
    }
    public void setAutoFlush() {
        this.setAutoFlush(true);
    }
    
    
    
    /**
     * Output to console all the buffer, then clear the buffer.
     * Do not work with an empty buffer.
     */
    public void flush() {
        // Do not use 'getBufferLength()' because
        // it can be re-defined in descendants.
        if ( this.buffer.length() <= 0 ) return;
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
    public void sliceOut(final int sliceSize) {
        this.sliceOut(sliceSize, 0);
    }
    public void sliceOut() {
        this.sliceOut(1);
    }
    
    
    
    /**
     * Change buffer regime: or rapid appending and flush over buffer size?
     * @param bufferStrictMode do ban to get over the buffer size limit for appending?
     */
    private void changeStrictSizeControlMode(final boolean bufferStrictMode) {
        this.strictSizeControl = bufferStrictMode;
    }
    // out world wrapper
    public void setStrictSizeControlMode(final boolean bufferStrictMode) {
        this.changeStrictSizeControlMode(bufferStrictMode);
    }
    public void setStrictSizeControlMode() {
        this.setStrictSizeControlMode(true);
    }
    
    
    
    /**
     * Checks whether with the 'newCharsToBuffer' we will exceed the buffer size.
     * @param newCharsToBufferLength estimate of length for the string we want to add to the buffer
     * @return true when we won't overstep buffer's size
     */
    private boolean isUnderBufferSizeLimit(final int newCharsToBufferLength) {
        boolean isUnderLimitRes = (this.getBufferLength() + newCharsToBufferLength) <= this.bufferSize;
        //
        return isUnderLimitRes;
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
     * Logic-holder of add-command.
     * Need it to implement into different wrappers because need distinguish
     * common text and commands.
     * @param newCharsToBuffer string to add to the buffer
     * @param newCharsToBufferLength our estimation of 'newCharsToBuffer' length (for buffer calculations)
     * @throws StringIndexOutOfBoundsException if string is too long and autoflush is off
     */
    protected void doAdd(final String newCharsToBuffer, int newCharsToBufferLength)
                    throws StringIndexOutOfBoundsException {
        // here we use '.length' because we need to catch real empty line
        if ( newCharsToBuffer.length() <= 0 ) return;
        //
        // Current state of buffer even with new chars is less buffer's size,
        // or it is an "empty" string (really is not, just checked before).
        // Here we use 'newCharsToBufferLength' because we need a relative length
        // the way it is calculated for buffer.
        if ( this.isUnderBufferSizeLimit(newCharsToBufferLength)
                || 0 == newCharsToBufferLength ) {
            this.buffer.append(newCharsToBuffer);
            return;
        }
        // Here we have the text which will exceed the buffer size.
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
        // Here we must keep buffer size
        // (and add new chars by pieces under strict buffer limit).
        // This block can not process "empty" (command) strings.
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
        String lastPieceStrAdded = newCharsToBufferOverSize.toString();
        // now we have a piece less then the size. Add it.
        this.buffer.append(lastPieceStrAdded);
    }
    // shot useful wrapper, where length is real length of a char/string
    protected void doAdd(final String newCharsToBuffer) {
        this.doAdd(newCharsToBuffer, newCharsToBuffer.length());
    }
    
    /**
     * Put new chars into the buffer.
     * @param newCharsToBuffer string to add to the buffer
     */
    public void add(final String newCharsToBuffer) {
        this.doAdd(newCharsToBuffer);
    }
    
    /**
     * Special chars or escape sequence - to buffer.
     * @param newCmdCharsToBuffer command string to add to the buffer
     */
    public void addCmd(final String newCmdCharsToBuffer) {
        this.doAdd(newCmdCharsToBuffer);
    }
    
    
    
    /**
     * Logic-holder of addWhole-command.
     * Need it to implement into different wrappers because need distinguish
     * common text and commands.
     * If new chars with current buffer are still less than buffer size limit - just add to buffer.
     * Don't work with an empty string.
     * @param wholeCharsToBuffer string to add to the buffer which can not be separated in any way
     * @param wholeCharsToBufferLength our estimation of 'wholeCharsToBuffer' length (for buffer calculations)
     * @throws StringIndexOutOfBoundsException if whole-string is too long and autoflush is off 
     */
    protected void doAddWhole(final String wholeCharsToBuffer, int wholeCharsToBufferLength)
                        throws StringIndexOutOfBoundsException {
        // here we use '.length' because we need to catch real empty line
        if ( wholeCharsToBuffer.length() <= 0 ) return;
        //
        // Whole-string must be less than the buffer size.
        // Here we use 'wholeCharsToBufferLength' because we need a relative length
        // the way it is calculated for buufer.
        if ( wholeCharsToBufferLength > this.bufferSize ) {
            String excMsg = "Whole string to add to the buffer at once is over of its boundaries";
            throw new StringIndexOutOfBoundsException(excMsg);
        }
        //
        // have enough space - 'wholeCharsToBuffer' will be added in whole
        if ( this.isUnderBufferSizeLimit(wholeCharsToBufferLength) ) {
            this.buffer.append(wholeCharsToBuffer);
            return;
        }
        // here and next 'wholeCharsToBuffer' will exceed the buffer
        //
        if ( !this.autoFlush ) {
            // We can do nothing without autoflush-mode (user must work himself).
            String excMsg = this.getAddExceptionMsg(wholeCharsToBuffer);
            throw new StringIndexOutOfBoundsException(excMsg);
        }
        //
        // we are allowed to overstep the limit for a step
        if ( !this.strictSizeControl ) {
            this.buffer.append(wholeCharsToBuffer);
            this.flush();
            return;
        }
        //
        // Now, we are in strict zone.
        // Make the buffer empty, then add the whole string
        this.flush();
        this.buffer.append(wholeCharsToBuffer);
    }
    // shot useful wrapper, where length is real length of a string
    protected void doAddWhole(final String wholeCharsToBuffer) {
        this.doAddWhole(wholeCharsToBuffer, wholeCharsToBuffer.length());
    }
    
    /**
     * Some string must be added to buffer simultaneously, as-is
     * (all 'wholeCharsToBuffer' will be outputted at one moment).
     * @param wholeCharsToBuffer string to add to the buffer which can not be separated in any way
     */
    public void addWhole(final String wholeCharsToBuffer) {
        this.doAddWhole(wholeCharsToBuffer);
    }
    
    /**
     * Special chars or escape sequence - to buffer in whole condition.
     * @param wholeCmdCharsToBuffer command string to add to the buffer which can not be separated in any way
     */
    public void addCmdWhole(final String wholeCmdCharsToBuffer) {
        this.doAddWhole(wholeCmdCharsToBuffer);
    }
    
    
    
    @Override
    public String toString() {
        String returnString = "size: " + this.bufferSize + ", multithread: " + this.buffer.isSafeAsync() + ", "
                                + "autoflush: " + this.autoFlush + ", strict size: " + this.strictSizeControl;
        //
        return returnString;
    }
    
    
    
}
