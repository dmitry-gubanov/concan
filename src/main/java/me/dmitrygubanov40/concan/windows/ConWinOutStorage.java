package me.dmitrygubanov40.concan.windows;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dmitrygubanov40.concan.buffer.OutputBufferString;

import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * Buffer (archive) of output lines, passed into the window.
 * Just collect data string data.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinOutStorage
{
    
    // how many lines will be in memory by default
    public final static int DEFAULT_LINES_LIMIT;
    // maximum of such lines in archive
    private final static int MAX_LINES_LIMIT;
    
    static {
        DEFAULT_LINES_LIMIT = 100;
        MAX_LINES_LIMIT = 10000;
    }
    
    
    ///////////////////////
    
    
    // Is the storage operational?
    private boolean isOff;
    
    // line-by-line (for the current width) all output strings in the zone
    private ConWinOutStorageList<OutputBufferString> savedLines;
    
    // line-by-line all current styles at the end of line
    // (synchronized by index with 'savedLines')
    private ConWinOutStorageList<ConWinOutBrush> savedBrushes;
    
    // window zone of the storage has such a height
    // (crucial for scrolling)
    private int windowHeight;
    
    // window zone of the storage has such a width
    private int linesWidth;
    
    // hold in memory not more than this number of symbols
    private int linesLimit;
    
    // safe mode for multi-threading
    private final boolean isAsyncSafe;
    
    
    ////////////////////////
    
    
    /**
     * @param initAsyncStatus ready or not to work with many threads
     * @param initWidth we start with such width of output
     * @param initHeight keep data of current window height
     * @param initLinesLimit length we keep
     * @throws IllegalArgumentException with incorrect parameters
     */
    public ConWinOutStorage(final boolean initAsyncStatus,
                            final int initWidth, final int initHeight,
                            final int initLinesLimit)
                        throws IllegalArgumentException {
        if ( initWidth <= 0 || initLinesLimit < 0
                || initLinesLimit > ConWinOutStorage.MAX_LINES_LIMIT ) {
            String excMsg = "Cannot initialize window storage:"
                        + " width: " + initWidth + ", number of lines in memory: "
                        + initLinesLimit;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.isAsyncSafe = initAsyncStatus;
        //
        this.isOff = false;// start ready to work
        //
        this.savedLines = new ConWinOutStorageList<>();
        // init list with empty string
        this.savedLines.add( new OutputBufferString(this.isAsyncSafe) );
        //
        this.savedBrushes = new ConWinOutStorageList<>();
        //
        this.windowHeight = initHeight;
        this.linesWidth = initWidth;
        this.linesLimit = initLinesLimit;
        //
        // storage supposed to be an "empty link"
        if ( 0 == initLinesLimit ) this.turnOff();
        //
    }
    public ConWinOutStorage(final boolean initAsyncStatus,
                            final int initWidth, final int initHeight) {
        this(initAsyncStatus, initWidth, initHeight, ConWinOutStorage.DEFAULT_LINES_LIMIT);
    }
    
    
    ////////////////////////
    
    
    /**
     * Getter of characters buffer size limit.
     * @return installed limit of chars to save
     */
    public int getLinesLimit() {
        return this.linesLimit;
    }
    
    
    
    /**
     * Get the brush which was saved for the line with such index.
     * @param index line index the brush we want
     * @return actual brush at the end of line with 'index'
     * @throws IndexOutOfBoundsException for incorrect index
     */
    public ConWinOutBrush getBrushOfLineByIndex(final int index) 
                                throws IndexOutOfBoundsException {
        final int MAX_INDEX = this.savedBrushes.size() - 1;
        if ( index < 0 || index > MAX_INDEX ) {
            String excMsg = "Incorrect index of line to get console brush:"
                            + " requested index is '" + index
                            + "', maximum is '" + MAX_INDEX + "'";
            throw new IndexOutOfBoundsException(excMsg);
        }
        //
        return this.savedBrushes.get(index);
    }
    
    
    
    /**
     * Make the storage to be an empty dead-end.
     */
    public final void turnOff() {
        this.isOff = true;
        //
        this.savedLines = null;
        this.savedBrushes = null;
    }
    
    /**
     * Builtin 'isOff' filter.
     * For class-private purposes use 'generateException'.
     * @param generateException do we generate exceptions?
     * @return 'true' if can work
     * @throws IllegalStateException in case 'isOff' is 'true'
     */
    private boolean checkOffStatus(final boolean generateException) 
                                throws IllegalStateException {
        if ( this.isOff && generateException ) {
            String excMsg = "Window output storage is 'off' but method is called";
            throw new IllegalStateException(excMsg);
        }
        //
        return this.isOff;
    }
    private boolean checkOffStatus() {
        // by default do not generate an exception
        return this.checkOffStatus(false);
    }
    private boolean checkOffStatusWithException() {
        // generate exception, interupt execution
        return this.checkOffStatus(true);
    }
    
    
    
    /**
     * Transfer regular text to the storage.
     * Automatically clear (cut off) previous symbols
     * out of storage size limit if necessary.
     * @param addedTxt text to save
     * @param addedTxtWidth width of the text we are saving
     * @throws NullPointerException when tries to save null-string  
     * @throws RuntimeException if text width has been changed
     */
    public void saveOutput(final String addedTxt, final int addedTxtWidth)
                    throws NullPointerException, RuntimeException {
        if ( this.checkOffStatus() ) return;
        //
        if ( null == addedTxt ) {
            String excMsg = "Tried to save null-pointer string as console output";
            throw new NullPointerException(excMsg);
        }
        final int addedTxtLength = addedTxt.length();
        if ( addedTxtLength <= 0 ) return;
        //
        String textToAppend = addedTxt;
        //
        // If the last symbol in new line - remove it:
        if ( textToAppend.substring(addedTxtLength - 1).equals(ConUt.LF) ) {
            textToAppend = textToAppend.substring(0, addedTxtLength - 1);
        }
        //
        // Must add new chars to the last line of 'savedOutputLines'.
        // First of all, check if the width has changed:
        if ( this.linesWidth != addedTxtWidth ) {
            String excMsg = "Text area width has been changed."
                            + " Was initially: " + this.linesWidth
                            + ", now is: " + addedTxtWidth;
            throw new RuntimeException(excMsg);
        }
        // last line is to append:
        final int lastLineIndex = this.savedLines.size() - 1;
        OutputBufferString lastLine = this.savedLines.get(lastLineIndex);
        lastLine.append(textToAppend);
    }
    
    /**
     * Transfer text of the command (special character or ESC-sequence)
     * to the storage.
     * Does not control text area width, command can be of any width.
     * May need different methods for text and commands for future purposes.
     * @param addedTxt command to save
     */
    public void saveOutputCmd(final String addedTxt) {
        // at the moment - simple translation into 'saveOutput' function
        this.saveOutput(addedTxt, this.linesWidth);
    }
    
    
    
    /**
     * Save (overwrite) brush state for the current line of storage.
     * Important: lines are save synchronized via index.
     * @param currentBrush current state of brush we want to save
     * @throws NullPointerException when brush was not given
     */
    public void saveLineBrush(final ConWinOutBrush currentBrush) {
        if ( this.checkOffStatus() ) return;
        //
        if ( null == currentBrush ) {
            String excMsg = "Brush was not given to be save in storage";
            throw new NullPointerException(excMsg);
        }
        // shallow copy of current brush
        ConWinOutBrush brushToCopy = new ConWinOutBrush(currentBrush);
        //
        final int lastLineIndex = this.savedLines.size() - 1;
        if ( this.savedBrushes.size() < this.savedLines.size() ) {
            // output brush was not saved for the line yet
            this.savedBrushes.add(brushToCopy);
        } else {
            // overwrite
            this.savedBrushes.set(lastLineIndex, brushToCopy);
        }
    }
    
    
    
    /**
     * Insert (start) new line in the lines archive.
     */
    public void storeNewLine() {
        if ( this.checkOffStatus() ) return;
        //
        // new element in the archive of lines
        this.savedLines.add( new OutputBufferString(this.isAsyncSafe) );
        //
        // If list is larger than limit - clear the list
        // (except the first hidden line):
        final boolean hasElementsToDelete = this.savedLines.size() > this.getLinesLimit();
        if ( hasElementsToDelete ) {
            final int screenAndLine = this.savedLines.size() - (this.windowHeight + 1);
            final int halfList = (int) this.savedLines.size() / 2;
            int elementsToDelete = (halfList < screenAndLine)
                                        ? halfList
                                        : screenAndLine;
            //
            this.savedLines.removeFirst(elementsToDelete);
            // also, synchroniously remove brushes in the archive
            this.savedBrushes.removeFirst(elementsToDelete);
        }
    }
    
    
    
    /**
     * @return list of all saved lines as separate Strings
     */
    public ArrayList<String> getSavedOutputLines() {
        // an exception will rise if we want to read data from off-archive
        this.checkOffStatusWithException();
        //
        // stream of all the lines (OutputBufferString) -> (String) to ArrayList:
        Stream linesConverter = this.savedLines.getArrayList().stream();
        List<String> savedStrings = (List<String>) linesConverter.map(
            (outputBufferString) -> outputBufferString.toString()
        ).collect(Collectors.toList());
        //
        return new ArrayList<>(savedStrings);
    }
    
    
    
}
