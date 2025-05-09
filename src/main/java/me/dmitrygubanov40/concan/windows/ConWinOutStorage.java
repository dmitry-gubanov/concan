package me.dmitrygubanov40.concan.windows;


import java.util.LinkedList;

import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * Buffer (archive) of output characters, passed in the window.
 * Just collect data.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinOutStorage
{
    
    // how many symbols will be in memory by default
    private final static int DEFAULT_SIZE_LIMIT;
    // maximum of such symbols in archive
    private final static int MAX_SIZE_LIMIT;
    
    // start and end of command symbols in archive
    private final static char CMD_START_FLAG;
    private final static char CMD_END_FLAG;
    
    static {
        DEFAULT_SIZE_LIMIT = 80 * 25 * 100;
        MAX_SIZE_LIMIT = 80 * 25 * 1000;
        //
        CMD_START_FLAG = ConUt.STX.charAt(0);
        CMD_END_FLAG = ConUt.ETX.charAt(0);
    }
    
    
    ///////////////////////
    
    
    // Is the storage operational?
    private boolean isOff;
    
    // all characters passed by console output saved in
    // this archive (list)
    private LinkedList<Character> savedOutput;
    
    // line-by-line (for the current width) all output strings in the zone
    private LinkedList<StringBuilder> savedOutputLines;
    
    // line-by-line all current styles at the end of line
    private LinkedList<ConWinOutBrush> savedLineBrushes;
    
    // 'savedOutput' was saved with this width
    private int savedOutputLinesWidth;
    
    // hold in memory not more than this number of symbols
    private int savedOutputSizeLimit;
    
    
    ////////////////////////
    
    
    /**
     * @param initWidth we start with this width of output
     * @param initLimit length we keep
     * @throws IllegalArgumentException with incorrect parameters
     */
    public ConWinOutStorage(final int initWidth, final int initLimit)
                        throws IllegalArgumentException {
        if ( initWidth <= 0 || initLimit <= 0
                || initLimit > ConWinOutStorage.MAX_SIZE_LIMIT ) {
            String excMsg = "Cannot initialize window storage:"
                        + " width: " + initWidth + ", size limit: " + initLimit;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.isOff = false;// start ready to work
        //
        this.savedOutput = new LinkedList<>();
        //
        this.savedOutputLines = new LinkedList<>();
        this.savedOutputLines.add(new StringBuilder());// init with empty string
        //
        this.savedLineBrushes = new LinkedList<>();
        //
        this.savedOutputLinesWidth = initWidth;
        this.savedOutputSizeLimit = initLimit;
    }
    public ConWinOutStorage(final int initWidth) {
        this(initWidth, ConWinOutStorage.DEFAULT_SIZE_LIMIT);
    }
    
    
    ////////////////////////
    
    
    /**
     * Get the brush which was saved for the line with such index.
     * @param index line index the brush we want
     * @return actual brush at the end of line with 'index'
     * @throws IndexOutOfBoundsException for incorrect index
     */
    public ConWinOutBrush getBrushOfLineByIndex(final int index) {
        final int MAX_INDEX = this.savedLineBrushes.size() - 1;
        if ( index < 0 || index > MAX_INDEX ) {
            String excMsg = "Incorrect index of line to get console brush:"
                            + " requested index is '" + index
                            + "', maximum is '" + MAX_INDEX + "'";
            throw new IndexOutOfBoundsException(excMsg);
        }
        //
        return this.savedLineBrushes.get(index);
    }
    
    
    
    /**
     * Make the archive (storage) to be an empty dead-end.
     */
    public void turnOff() {
        this.isOff = true;
        //
        this.savedOutput = null;
        this.savedOutputLines = null;
        this.savedLineBrushes = null;
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
    
    
    /**
     * Delete some number of characters from the 'char-by-char' storage.
     * Automatically rework lines in the storage (remove lines if necessary).
     * @param toCutOff how many symbols must be cut off (both 'savedOutput' and 'savedOutputLines')
     */
    private void reduceStorageData(final int toCutOff) {
        this.checkOffStatus(true);
        //
        if ( toCutOff <= 0 ) return;
        //
        // erase the number of chars from single-line storage
        for ( int i = 0; i < toCutOff; i++ ) {
            char chatToDelete = this.savedOutput.get(0);
            this.savedOutput.remove(0);
            if ( chatToDelete == ConWinOutStorage.CMD_START_FLAG ) {
                // we have started to delete command block (ESC-sequence)
                // delete all chars until delete all the command block
                while ( this.savedOutput.get(0) != ConWinOutStorage.CMD_END_FLAG ) {
                    this.savedOutput.remove(0);
                    i++;
                }
                // finally, remove command block border
                this.savedOutput.remove(0);
                i++;
            }
        }
        //
        // We should also delete the lines:
        // part of the line, and the line in general when it becomes empty.
        for ( int i = 0; i < toCutOff; i++ ) {
            //
            StringBuilder firstLine;
            //
            this.deleteFirstEmptyLines();// now first line is not empty
            firstLine = this.savedOutputLines.get(0);
            //
            char chatToDelete = firstLine.charAt(0);
            //
            firstLine.deleteCharAt(0);
            this.deleteFirstEmptyLines();
            firstLine = this.savedOutputLines.get(0);
            //
            if ( chatToDelete == ConWinOutStorage.CMD_START_FLAG ) {
                // we have started to delete command block (ESC-sequence)
                // delete all chars in this line, and further also
                // until delete all the command block is deleted
                while ( firstLine.charAt(0) != ConWinOutStorage.CMD_END_FLAG ) {
                    //
                    firstLine.deleteCharAt(0);
                    this.deleteFirstEmptyLines();
                    firstLine = this.savedOutputLines.get(0);
                    //
                    i++;
                }
                // deleting the end bracket of command block
                firstLine.deleteCharAt(0);
                this.deleteFirstEmptyLines();
                i++;
            }
        }
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
        // Calculate possible overflows.
        final int toCutOff;
        toCutOff = addedTxtLength + this.savedOutput.size() - this.savedOutputSizeLimit;
        // Symbols overflow, need to cut off some previous characters.
        if ( toCutOff > 0 ) {
            this.reduceStorageData(toCutOff);
        }
        //
        // must save everything passed in 'addedTxt' char-by-char
        char[] addedChars = addedTxt.toCharArray();
        for ( int i = 0; i < addedChars.length; i++ ) {
            this.savedOutput.add(addedChars[ i ]);// all symbols archive
        }
        //
        // Must add new chars to the last line of 'savedOutputLines'.
        // First of all, check if the width has changed
        if ( this.savedOutputLinesWidth != addedTxtWidth ) {
            String excMsg = "Text area width has been changed."
                            + " Was initially: " + this.savedOutputLinesWidth
                            + ", now is: " + addedTxtWidth;
            throw new RuntimeException(excMsg);
        }
        final int lastLineIndex = this.savedOutputLines.size() - 1;
        StringBuilder lastLine = this.savedOutputLines.get(lastLineIndex);
        lastLine.append(addedTxt);
    }
    
    /**
     * Transfer text of the command (special character or ESC-sequence)
     * to the storage.
     * Does not control text area width, command can be of any width.
     * @param addedTxt command to save
     * @throws NullPointerException when tries to save null-string command
     */
    public void saveOutputCmd(final String addedTxt)
                            throws NullPointerException {
        if ( this.checkOffStatus() ) return;
        //
        if ( null == addedTxt ) {
            String excMsg = "Tried to save null-pointer"
                                + " command string as console output";
            throw new NullPointerException(excMsg);
        }
        //
        final String cmdToSave;
        final int SPECIAL_CHAR_LENGTH = 1;
        if ( addedTxt.length() > SPECIAL_CHAR_LENGTH ) {
            // long commands (ESC-sequence)
            cmdToSave = ConWinOutStorage.CMD_START_FLAG
                            + addedTxt
                            + ConWinOutStorage.CMD_END_FLAG;
        } else {
            // short command char
            cmdToSave = addedTxt;
        }
        //
        this.saveOutput(cmdToSave, this.savedOutputLinesWidth);
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
        final int lastLineIndex = this.savedOutputLines.size() - 1;
        if ( this.savedLineBrushes.size() < this.savedOutputLines.size() ) {
            // output brush was not saved for the line yet
            this.savedLineBrushes.add(brushToCopy);
        } else {
            // overwrite
            this.savedLineBrushes.set(lastLineIndex, brushToCopy);
        }
    }
    
    
    
    /**
     * Insert (start) new line in the lines archive.
     */
    public void storeNewLine() {
        if ( this.checkOffStatus() ) return;
        //
        this.savedOutputLines.add(new StringBuilder());
    }
    
    /**
     * Delete the first line in the lines archive.
     * @throws IndexOutOfBoundsException if try to erase the last line
     * @throws UnsupportedOperationException if try to erase non-empty line
     */
    public void deleteFirstLine()
                    throws IndexOutOfBoundsException {
        if ( this.checkOffStatus() ) return;
        //
        if ( this.savedOutputLines.size() <= 1 ) {
            String excMsg = "Cannot delete the last line in the storage";
            throw new IndexOutOfBoundsException(excMsg);
        }
        StringBuilder firstLine = this.savedOutputLines.get(0);
        if ( firstLine.length() > 0 ) {
            String excMsg = "Cannot delete non-empty line in the storage";
            throw new UnsupportedOperationException(excMsg);
        }
        //
        this.savedOutputLines.remove(0);
        this.savedLineBrushes.remove(0);// !!! It is necessary to add all removed brushes into default set of starting brushes
    }
    
    /**
     * Delete all empty first lines in the storage.
     */
    private void deleteFirstEmptyLines() {
        this.checkOffStatus(true);
        //
        while ( this.savedOutputLines.get(0).length() <= 0 ) {
            // if some first storage rows are empty - just delete them
            try {
                // would no delete the last row
                this.deleteFirstLine();
            } catch ( IndexOutOfBoundsException e ) {
                // the last row is re-inited, and exit
                this.savedOutputLines = new LinkedList<>();
                this.savedOutputLines.add(new StringBuilder()); // init with empty string
                //
                this.savedLineBrushes = new LinkedList<>();      // no brush data also
                //
                break;
            }
        }    
    }
    
    
    
    /**
     * Give char-array with all kept in memory output symbols
     * from 'savedOutput' list.
     * Direct line of everything passed by output.
     * @param outputLine what line are we going to process?
     * @return array of all symbols passed through output
     * @throws RuntimeException if symbols' archive cannot be processed correctly
     */
    private char[] getSavedOutput(LinkedList<Character> outputLine)
                        throws RuntimeException {
        this.checkOffStatus(true);
        //
        LinkedList<Character> outputToGet = new LinkedList<>(outputLine);
        // remove inner 'ConWinOutStorage' func-symbols:
        outputToGet.removeIf(
            (symbol) -> {
                boolean toDelete = symbol.equals(ConWinOutStorage.CMD_START_FLAG)
                                    || symbol.equals(ConWinOutStorage.CMD_END_FLAG);
                return toDelete;
            }
        );
        int length = outputToGet.size();// length of archive without special symbols
        char[] result = new char[ length ];
        //
        for ( int i = 0; i < length; i++ ) {
            char curChar = outputToGet.get(i);
            if ( curChar == ConWinOutStorage.CMD_START_FLAG
                    || curChar == ConWinOutStorage.CMD_END_FLAG ) {
                //
                String excMsg = "Cannot process saved console output";
                throw new RuntimeException(excMsg);
                //
            } else {
                // regular symbol to add
                result[ i ] = curChar;
            }
        }
        return result; 
    }
    
    /**
     * Only the string-converter for 'getSavedOutput()'.
     * @return string with all symbols passed through output
     */
    public String getSavedOutputStr() {
        // an exception will rise if we want to read data from off-archive
        this.checkOffStatus(true);
        //
        return String.valueOf( this.getSavedOutput(this.savedOutput) );
    }
    
    /**
     * @return list of all saved lines as separate Strings
     */
    public LinkedList<String> getSavedOutputLines() {
        // an exception will rise if we want to read data from off-archive
        this.checkOffStatus(true);
        //
        LinkedList<String> linesResult = new LinkedList<>();
        //
        for ( StringBuilder curLine : this.savedOutputLines ) {
            // Check new-line characters at endings.
            // They must be removed as in 'savedOutputLines' we control new lines ourselves.
            // Also, any LF-char ('\n') must be the last.
            while ( curLine.indexOf(ConUt.LF) != -1 ) {
                final int s = curLine.indexOf(ConUt.LF);
                curLine.deleteCharAt(s);
            }
            // get chars of current line:
            char[] curLineChars = new char[ curLine.length() ];
            curLine.getChars(0, curLine.length(), curLineChars, 0);
            //
            // chars array - into list:
            LinkedList<Character> curLineLinkedList = new LinkedList<>();
            for ( char curChar : curLineChars ) {
                curLineLinkedList.add(curChar);
            }
            //
            linesResult.add(String.valueOf( this.getSavedOutput(curLineLinkedList) ));
        }
        //
        return linesResult;
    }
    
    
    
}
