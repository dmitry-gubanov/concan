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
    private int bufferLength;
    
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
     * @param minSize minimal buffer size we are allowed to create
     * @param maxSize maximum buffer size we are allowed to create
     */
    public OutputBuffer(final int initSize,
                        final boolean isSafeAsync,
                        final boolean autoFlushMode,
                        final boolean strictSizeControlMode,
                        final int minSize,
                        final int maxSize) {
        //
        this.checkBufferSize(initSize, minSize, maxSize);
        this.bufferSize = initSize;
        //
        this.initBuffer(isSafeAsync);
        this.changeAutoFlush(autoFlushMode);
        this.changeStrictSizeControlMode(strictSizeControlMode);
    }
    public OutputBuffer(final int initSize,
                        final boolean isSafeAsync,
                        final boolean autoFlushMode,
                        final boolean strictSizeControlMode) {
        this(initSize, isSafeAsync, autoFlushMode, strictSizeControlMode, MIN_BUFFER_SIZE, MAX_BUFFER_SIZE);
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
     * @param newSize desired buffer size > 0
     * @param minSize min buffer size we can accept, '0' - do not check
     * @param maxSize max buffer size we can accept, '0' - do not check
     * @throws IllegalArgumentException with incorrect buffer size
     */
    private void checkBufferSize(final int newSize, final int minSize, final int maxSize)
                    throws IllegalArgumentException {
        String excMsg = "Output buffer inappropriate size: '" + newSize + "'.";
        if ( newSize <= 0 ) {
            excMsg += " Must be positive.";
            throw new IllegalArgumentException(excMsg);
        }
        if ( minSize > 0 && newSize < minSize ) {
            excMsg += " Must be " + minSize + " or greater";
            throw new IllegalArgumentException(excMsg);
        }
        if ( maxSize > 0 && newSize > maxSize ) {
            excMsg += " Must be less or equal to " + maxSize;
            throw new IllegalArgumentException(excMsg);
        }
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
        this.setBufferLength(0);
    }
    
    /**
     * To clear all the string in buffer.
     * Information may be lost, do flush if necessary.
     * @throws NullPointerException in case buffer was not initialized
     */
    protected void clearBuffer() throws NullPointerException {
        if ( null == this.buffer ) {
            String excMsg = "Cannot clear not initialized buffer";
            throw new NullPointerException(excMsg);
        }
        //
        // buffer was initialized, and it knows status itself
        // re-new with the same one
        boolean wasSafeAsyncStatus = this.buffer.isSafeAsync();
        this.buffer = new OutputBufferString(wasSafeAsyncStatus);
        this.setBufferLength(0);
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
        return this.bufferLength;
    }
    
    /**
     * Setter of buffer string length.
     * Not always we calculate buffer's length via simple string length,
     * possible to change with direct installation.
     * @param newLength all new length we will install
     * @throws IllegalArgumentException for negative values
     */
    protected void setBufferLength(final int newLength)
                        throws IllegalArgumentException {
        if ( newLength < 0 ) {
            String excMsg = "Buffer's cannot have negative length,"
                            + " replaced by '" + newLength + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.bufferLength = newLength;
    }
    
    /**
     * Add/Subtract some value of buffer's length.
     * Works with 'OutputBuffer' system methods 'getBufferLength' and 'setBufferLength'.
     * @param lengthChange the number we will plus (or minus)
     * @throws IllegalArgumentException in case buffer's length becomes negative
     */
    private void changeBufferLength(final int lengthChange)
                                throws IllegalArgumentException {
        if ( 0 == lengthChange ) return;// do nothing
        //
        int curLength = this.getBufferLength();
        int newLength = curLength + lengthChange;
        if ( newLength < 0 ) {
            String excMsg = "Buffer's cannot have negative length:"
                            + " current length is'" + curLength + "',"
                            + " change by '" + lengthChange + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.setBufferLength(newLength);
    }
    
    /**
     * @return string line of current buffer
     * @throws NullPointerException in case buffer is not initialized
     */
    protected String getBufferStr() throws NullPointerException {
        if ( null == this.buffer ) {
            String excMsg = "Buffer is not initialized";
            throw new NullPointerException(excMsg);
        }
        //
        return this.buffer.toString();
    }
    
    /**
     * Extract some substring from the buffer line.
     * @param start char index to start removing
     * @param end char index to stop removing
     * @param bufLength such we consider the current buffer length *before* deleting
     * @throws IllegalArgumentException when 'start' or 'end' less zero, or start > end
     * @throws StringIndexOutOfBoundsException when indexes are out of string
     */
    protected void deleteFromBuffer(final int start, final int end, final int bufLength)
                        throws IllegalArgumentException, StringIndexOutOfBoundsException {
        if ( start < 0 || end <= 0 || start >= end ) {
            String excMsg = "Incorrect parameters to delete a substring from the buffer,"
                                + " start: '" + start + "', end: '" + end + "'";
            throw new IllegalArgumentException(excMsg);
        }
        if ( bufLength < 0 ) {
            String excMsg = "Cannot work with buffer, considered to be less than zero,"
                                + " buffer length: '" + bufLength + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        final int realBufLengthBefore = this.buffer.length();
        if ( start > realBufLengthBefore || end > realBufLengthBefore ) {
            String excMsg = "Parameters to delete a substring from the buffer exceed the string length,"
                                + " start: '" + start + "', end: '" + end
                                + "', buffer length: '" + realBufLengthBefore + "'";
            throw new StringIndexOutOfBoundsException(excMsg);
        }
        //
        //
        // removed from the buffer
        this.buffer.delete(start, end);
        //
        final int newBufLength;
        if ( 0 == bufLength ) {
            // at the point we could have really empty buffer, so the zero length
            // was made intentionally, we must keep it
            newBufLength = 0;
        } else {
            // in case we had real length, in it just calculated
            final int sliceLength = end - start;
            final int realBufLengthAfter = bufLength - sliceLength;
            newBufLength = realBufLengthAfter;
        }
        this.setBufferLength(newBufLength);
    }
    
    /**
     * Add some characters to the buffer string.
     * Does not control buffer size limit, only add and calculate new length.
     * Must be used in upper level methods.
     * @param newChars string we want to add
     * @param newCharsLength we presume the string we are adding has such length
     * @throws IllegalArgumentException when 'newCharsLength' less than zero
     */
    private void appendToBuffer(final String newChars, final int newCharsLength)
                        throws IllegalArgumentException {
        if ( newCharsLength < 0 ) {
            String excMsg = "Cannot work with the string, which length considered to be less than zero,"
                                + " string length: '" + newCharsLength + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        if ( newChars.length() <= 0 ) return;// no real characters to add
        //
        // and chars and correct the length:
        this.buffer.append(newChars);
        this.changeBufferLength(newCharsLength);
    }
    
    /**
     * Add string to the buffer without direct size.
     * Appropriate for regular text, not commands.
     * @param newChars what to add
     * @throws IllegalArgumentException when command line is given
     */
    private void appendToBuffer(final String newChars) {
        if ( this.isCmdStr(newChars) ) {
            String excMsg = "Cannot append the command to the buffer";
            throw new IllegalArgumentException(excMsg);
        }
        //
        final int newCharsLength = newChars.length();
        this.appendToBuffer(newChars, newCharsLength);
    }
    
    
    
    /**
     * Print current state of the buffer to console.
     * Has the exact mechanism to draw to the console.
     * All must be calculated before 'output()'. The method only placing the chars.
     * @param outputStr final sting to place into console.
     */
    protected void output(final String outputStr) {
        //
        System.out.print(outputStr);
        //
    }
    
    /**
     * Print the part of buffer (when we slice it).
     * @param slice part of buffer to be shown
     */
    protected void outputSlice(final String slice) {
        //
        this.output(slice);
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
        if ( this.autoFlush ) this.autoflush();
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
     * Can be used any time by outsource.
     */
    public void flush() {
        this.output(this.buffer.toString());
        this.clearBuffer();
    }
    
    /**
     * Flush by 'OutputBuffer' itself when buffer is full automatically.
     * @throws RuntimeException if is run without auto-flush regime
     */
    protected void autoflush() throws RuntimeException {
        if ( !this.autoFlush ) {
            String excMsg = "Buffer called autoflush, but autoflush mode is off";
            throw new RuntimeException(excMsg);
        }
        //
        // do regular flush
        this.flush();
    }
    
    
    
    /**
     * Get slice from 'startSliceIndex' with size of 'sliceSize', show in console,
     * and then remove the slice from the buffer.
     * Do not work with an empty buffer.
     * If buffer is less or equal than 'sliceSize' (and  'startSliceIndex' is zero),
     * we will get the 'flush'-method.
     * @param sliceSize number of chars in buffer's slice (string) to cut
     * @param startSliceIndex first character index of the slice
     * @throws IllegalArgumentException sliceSize > 0, startSliceIndex >= 0
     */
    public void sliceOut(final int sliceSize, final int startSliceIndex)
                    throws IllegalArgumentException {
        if ( sliceSize <= 0 ) {
            String excMsg = "Cannot slice-out buffer with the slice length: '"
                                + sliceSize + "'";
            throw new IllegalArgumentException(excMsg);
        }
        if ( startSliceIndex < 0 ) {
            String excMsg = "Cannot slice-out buffer with the start char at index: '"
                                + startSliceIndex + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        final int calculatedBufLength = this.getBufferLength();
        //
        // here works with real chars positions:
        final int buffLengthReal = this.buffer.length();
        final int endSliceIndex = startSliceIndex + sliceSize;
        //
        // we must have buffer:
        if ( buffLengthReal <= 0 ) return;
        //
        // the buffer must have the tail we want to slice:
        if ( buffLengthReal < endSliceIndex ) return;
        // 
        // with this 'slice' it will be identical to 'flush':
        // output all, clear all the buffer
        if ( 0 == startSliceIndex && buffLengthReal <= sliceSize ) {
            this.flush();
            return;
        }
        //
        // show the part we are removing:
        String slice = this.buffer.substring(startSliceIndex, endSliceIndex);
        this.outputSlice(slice);
        //
        // make the buffer shorter:
        this.deleteFromBuffer(startSliceIndex, endSliceIndex, calculatedBufLength);
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
     * @param strToCheck
     * @return 'true' in case we consider the line to be a command (escape sequence, special character)
     */
    protected boolean isCmdStr(final String strToCheck) {
        // Here in 'OutputBuffer' we do not need
        // a real separation between visual text and a command.
        // Done to be overridden in more complex buffers.
        //
        // * * * * *
        return false;
        // * * * * *
    }
    
    
    
    /**
     * Logic-holder of add-command.
     * Need it to implement into different wrappers because need distinguish
     * common text and commands.
     * @param newCharsToBuffer string to add to the buffer
     * @param newCharsToBufferLength our estimation of 'newCharsToBuffer' length (for buffer calculations)
     * @throws StringIndexOutOfBoundsException if string is too long and auto-flush is off
     */
    protected void doAdd(final String newCharsToBuffer, int newCharsToBufferLength)
                    throws StringIndexOutOfBoundsException {
        // here we use 'length()' because we need to pass by real empty line
        if ( newCharsToBuffer.length() <= 0 ) return;
        //
        // Current state of buffer even with new chars is less buffer's size,
        // or it is an "empty" string (really is not, just checked before).
        // Here we use 'newCharsToBufferLength' because we need a relative length
        // the way it is calculated for buffer.
        if ( this.isUnderBufferSizeLimit(newCharsToBufferLength) ) {
            this.appendToBuffer(newCharsToBuffer, newCharsToBufferLength);
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
            this.appendToBuffer(newCharsToBuffer, newCharsToBufferLength);
            this.autoflush();
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
            this.appendToBuffer(pieceOfNewCharsToAdd);
            //
            newCharsToBufferOverSize = newCharsToBufferOverSize.delete(cutStart, cutEnd);
            //
            this.autoflush();
        }
        String lastPieceStrAdded = newCharsToBufferOverSize.toString();
        // Now we have a piece less then the size. Add it.
        this.appendToBuffer(lastPieceStrAdded);
    }
    // shot useful wrapper, where length is real length of a char/string
    protected void doAdd(final String newCharsToBuffer) {
        this.doAdd(newCharsToBuffer, newCharsToBuffer.length());
    }
    
    /**
     * Put any new chars into the buffer.
     * Auto-detection whether it is a command or a visual text.
     * @param newCharsToBuffer string to add to the buffer
     */
    public void add(final String newCharsToBuffer) {
        // special case if we detected command
        if ( this.isCmdStr(newCharsToBuffer) ) {
            this.addCmd(newCharsToBuffer);
            return;
            // that is all for command
        }
        //
        // only for the case of regular visual text
        this.addText(newCharsToBuffer);
    }
    
    /**
     * Regular, visual user's text - to buffer.
     * @param newTextCharsToBuffer visual text string to add to the buffer
     */
    protected void addText(final String newTextCharsToBuffer) {
        this.doAdd(newTextCharsToBuffer);
    }
    
    /**
     * Special chars or escape sequence - to buffer.
     * @param newCmdCharsToBuffer command string to add to the buffer
     */
    protected void addCmd(final String newCmdCharsToBuffer) {
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
     * @throws StringIndexOutOfBoundsException if whole-string is too long and auto-flush is off 
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
            this.appendToBuffer(wholeCharsToBuffer, wholeCharsToBufferLength);
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
            this.appendToBuffer(wholeCharsToBuffer, wholeCharsToBufferLength);
            this.autoflush();
            return;
        }
        //
        // Now, we are in strict zone.
        // Make the buffer empty, then add the whole string
        this.autoflush();
        this.appendToBuffer(wholeCharsToBuffer, wholeCharsToBufferLength);
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
        // special case if we detected command-in-whole
        if ( this.isCmdStr(wholeCharsToBuffer) ) {
            this.addCmdWhole(wholeCharsToBuffer);
            return;
            // that is all for command
        }
        //
        // only for the case of regular visual text-in-whole
        this.addTextWhole(wholeCharsToBuffer);
    }
    
    /**
     * Regular, visual user's text - to buffer in whole condition.
     * @param wholeTextCharsToBuffer string to add to the buffer which can not be separated in any way
     */
    protected void addTextWhole(final String wholeTextCharsToBuffer) {
        this.doAddWhole(wholeTextCharsToBuffer);
    }
    
    /**
     * Special chars or escape sequence - to buffer in whole condition.
     * @param wholeCmdCharsToBuffer command string to add to the buffer which can not be separated in any way
     */
    protected void addCmdWhole(final String wholeCmdCharsToBuffer) {
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
