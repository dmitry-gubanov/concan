package me.dmitrygubanov40.concan.winbuffer;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    // list of regex expressions of all valid (possible to be executed)
    // ESC-commands
    private static final List<String> validCommands;
    
    // list of regex expressions of all banned escape sequences,
    // special chars, and everything we want to filter.
    private static final List<String> bannedCommands;
    
    
    static {
        WINDOW_AUTOFLUSH_MODE = true;
        WINDOW_STRICT_SIZE_CONTROL_MODE = true;
        WINDOW_ANY_CMD_LENGTH = 0;
        //
        MIN_WINDOW_BUFFER_SIZE = 1;
        MAX_WINDOW_BUFFER_SIZE = 1000;
        //
        // All these characters we assume can be only in command, not regular text:
        cmdCharacters = new ArrayList<>();
        initCmdCharacters();
        //
        // All these escape sequences are valid to be executed in the buffer:
        validCommands = new ArrayList<>();
        initValidCommandsRegex();
        //
        // All we will filter (and block) before it is added to the buffer.
        bannedCommands = new ArrayList<>();
        initBannedCommandsRegex();
    }
    
    /**
     * Initialize symbols we consider as special (commands).
     * 'ESC' is added directly at first - to be checked the first too.
     * So, ignore 'ESC' from direct array from ConUt.
     */
    private static void initCmdCharacters() {
        cmdCharacters.add(ConUt.ESC);// first direct adding of primary 'ESC'
        //
        char[] specialChars = ConUt.getSpecialAsciiCodes();
        for ( char curSpecChar : specialChars ) {
            String curSpecCharStr = String.valueOf(curSpecChar);
            // ignore 'ESC' added first directly:
            if ( curSpecCharStr.equals(ConUt.ESC) ) continue;
            //
            cmdCharacters.add(curSpecCharStr);
        }
    }
    
    /**
     * Write down all regex expressions to cover all escape sequences
     * we consider to be valid and executable.
     */
    private static void initValidCommandsRegex() {
        String[] validCmds = {
            // all 'm'-based expressions:
            // (colors, styles...)
            "(([3-4]8;5;)?|([3-4]8;2;\\d+;\\d+;)?)\\d+m",
            // clear and cursor functions:
            "(s)|(u)|(\\?(12|25)[hl])"
        };
        //
        for ( String curExpression : validCmds ) {
            // all lines must start as 'ESC' + '[':
            String regexToAdd = "\033\\["
                                + curExpression;
            validCommands.add(regexToAdd);
        }
    }
    
    /**
     * Write down all regex expressions to cover everything which must
     * break the buffer operation: invalid characters, escape sequences, and
     * all we want not to be in the buffer.
     */
    private static void initBannedCommandsRegex() {
        String[] bannedCmds = {
            // all cursor control commands
            "(\\d+;\\d+)?[A-H]",
            "6n",
            "[012][JK]"
        };
        //
        for ( String curExpression : bannedCmds ) {
            // all banned commands must start with 'ESC' + '[':
            String regexToAdd = "\033\\["
                                + curExpression;
            bannedCommands.add(regexToAdd);
        }
        // add here banned characters if necessary
        // ...
    }
    
    
    ////////////////////////////////
    
    
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
        isCmdStatus = this.hasCmdChars(strToCheck);
        //
        return isCmdStatus;
    }
    
    /**
     * Check if the string has a special char or not.
     * @param strToCheck line we must to analyze
     * @return 'true' when has special symbols ('cmdCharacters'), or 'false'
     */
    private boolean hasCmdChars(final String strToCheck) {
        for ( int i = 0; i < WindowOutputBuffer.cmdCharacters.size(); i++ ) {
            String currentCharCheck = WindowOutputBuffer.cmdCharacters.get(i);
            if ( strToCheck.contains(currentCharCheck) ) {
                return true;
            }
        }
        return false;
    }
    
    
    
    /**
     * Check if the string is any ESC-sequence or not.
     * @param strToCheck line we must analyze
     * @return 'true' when is alone ESC-sequence, or 'false'
     */
    private boolean isSingleEscCommand(final String strToCheck) {
        Pattern pattern;
        Matcher matcher;
        //
        for ( String curValidCmd : WindowOutputBuffer.validCommands ) {
            // Here we need only whole sequences from start to end of line,
            // so we will use 'matches()'.
            pattern = Pattern.compile(curValidCmd);
            matcher = pattern.matcher(strToCheck);
            //
            if ( matcher.matches() ) {
                return true;// +
            }
        }
        //
        // here the match did not occur:
        return false;
    }
    
    /**
     * Pass the line through the list of all the banned commands.
     * @param strToCheck line we have to analyze
     * @return 'true' when the line contains any restricted commands, or 'false'
     */
    private boolean hasBannedCmd(final String strToCheck) {
        Pattern pattern;
        Matcher matcher;
        //
        for ( String curBannedCmd : WindowOutputBuffer.bannedCommands ) {
            pattern = Pattern.compile(curBannedCmd);
            matcher = pattern.matcher(strToCheck);
            if ( matcher.find() ) {
                // cought something we consider to be banned
                return true;// +
            }
        }
        // no match -> no illegal commands
        return false;
    }
    
    
    
    // banned
    @Override
    public void add(final String newCharsToBuffer) throws IllegalCallerException {
        String excMsg = "Does not allow to use method 'add', use 'addToWinBuf' instead.";
        throw new IllegalCallerException(excMsg);
    }
    
    /**
     * Real, hidden method to add something to the buffer.
     * @param strToBuf what we are going to add to buffer
     * @param iteration recursion controller
     */
    private void addToWinBuf(final String strToBuf, final int iteration) {
        if ( !this.isCmdStr(strToBuf) ) {
            // easy way - no commands in line: add and exit
            super.add(strToBuf);
            return;
        }
        //
        this.addCmdToWinBuf(strToBuf, iteration);
    }
    
    /**
     * Universal (text and commands) add-method for window buffer.
     * Directly add regular text, or use special functions
     * to add text with commands (special symbols, escape sequences).
     * Can be slow for long command combinations.
     * Is used instead of 'add'.
     * @param strToBuf what we are going to add to buffer
     */
    public void addToWinBuf(final String strToBuf) {
        this.addToWinBuf(strToBuf, 0);
    }
    
    
    /**
     * Full 'Add'-method, only for a command.
     * Has inner iterations controller.
     * @param strToBuf string with a command
     * @param iteration recursion counter
     * @throws IllegalArgumentException when not command is tried to be added, or command is banned
     * @throws Runtime­Exception in case we got infinite parsing loop
     */
    public void addCmdToWinBuf(final String strToBuf, final int iteration)
                        throws IllegalArgumentException, Runtime­Exception {
        final int MAX_RECUSION_CNT = 100;
        if ( iteration > MAX_RECUSION_CNT ) {
            String excMsg = "Cannot add command (escape sequence), fall into infinite loop";
            throw new Runtime­Exception(excMsg);
        }
        //
        if ( !this.isCmdStr(strToBuf) ) {
            String excMsg = "Command expected for buffer, got '" + strToBuf + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        // Prevent blocked special chars/ANSI escape sequences to be added/executed.
        // Here we know is some command.
        if ( this.hasBannedCmd(strToBuf) ) {
            String excMsg = "Command (special character or escape sequence) is banned to be added to the buffer";
            throw new IllegalArgumentException(excMsg);
        }
        //
        // Here we have at least one command in line (spec. char, escape sequence).
        //
        // Rapid processing for one-symbol cmd-string (special char):
        if ( strToBuf.length() <= 1 ) {
            this.addCmdWhole(strToBuf);
            return;
        }
        //
        // Rapid processing for single escape sequence
        if ( this.isSingleEscCommand(strToBuf) ) {
            this.addCmdWhole(strToBuf);
            return;
        }
        //
        // Get a set of strings we want to add sequentially.
        // Here we make a guarantee each command will be sent separately.
        List<String> toAdd = this.getStringsToAddFromCmdStr(strToBuf);
        for ( String currentStrToAdd : toAdd ) {
            this.addToWinBuf(currentStrToAdd, iteration);
        }
    }
    
    /**
     * Default short version without iterations.
     * @param strToBuf 
     */
    public void addCmdToWinBuf(final String strToBuf) {
        this.addCmdToWinBuf(strToBuf, 0);
    }
    
    
    /**
     * Make some string with special chars and commands to be a list of separate strings
     * with regular text and commands each.
     * "txt1\ntxt2\ntxt3\e[5mtxt4" => { "txt1", "\n", "txt2", "\n", "txt3", "\e[5m", "txt4" }
     * Important! Is not secure, '' must be checked before that doesn't have invalid commands.
     * @param strToParse string with a command
     * @return list of strings, each has a command or a block of regular text
     */
    private List<String> getStringsToAddFromCmdStr(final String strToParse) {
        List<String> strResult = new ArrayList<>();
        int curIndex;
        int lastAddedIndex;
        //
        for ( curIndex = 0, lastAddedIndex = 0; curIndex < strToParse.length(); curIndex++ ) {
            char curChar = strToParse.charAt(curIndex);
            // if current symbol is printable - we are not interested:
            if ( ConUt.isPrintableChar(curChar) ) continue;
            //
            String curSymbol = String.valueOf(curChar);
            //
            // pass through all special chars:
            if ( this.hasCmdChars(curSymbol) ) {
                // met command (special char/escape sequence)
                // add to result strings everything we passed before
                if ( curIndex > lastAddedIndex ) {
                    String strToAdd = strToParse.substring(lastAddedIndex, curIndex);
                    strResult.add(strToAdd);
                }
                //
                if ( curSymbol.equals(ConUt.ESC) ) {
                    // In case we met 'ESC' work with possible escape sequence.
                    Pattern pattern;
                    Matcher matcher;
                    String curSubstr = strToParse.substring(curIndex);
                    //
                    for ( String curValidCmd : WindowOutputBuffer.validCommands ) {
                        pattern = Pattern.compile(curValidCmd);
                        matcher = pattern.matcher(curSubstr);
                        if ( matcher.find() ) {
                            final int cmdLength = matcher.end();
                            //
                            String escCmdStrToAdd = curSubstr.substring(0, cmdLength);
                            strResult.add(escCmdStrToAdd);
                            //
                            // 'curIndex' will be incremented next loop iteration automatically
                            curIndex = (curIndex + cmdLength) - 1;
                            lastAddedIndex = curIndex + 1;
                            //
                            break;
                        }
                    }
                } else {
                    // Any command single char except 'ESC'
                    // must be added as separate string into the list.
                    strResult.add(curSymbol);
                    lastAddedIndex = curIndex + 1;
                }
            }
        }
        //
        // Add rest part of base string if has anything:
        if ( curIndex > lastAddedIndex ) {
            String strToAdd = strToParse.substring(lastAddedIndex, curIndex);
            strResult.add(strToAdd);
        }
        //
        return strResult;
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
     * Only whole-adding -> Here this is a clone of the 'addCmdWhole'-method.
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
     * Suppose to add common non-command, visual text.
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
        super.doAddWhole(wholeCmdCharsToBuffer, WINDOW_ANY_CMD_LENGTH);
        //
        this.generateEvent(WinBufEventType.ON_AFTER_CMD_SENT,
                            wholeCmdCharsToBuffer);
    }
    
    
    
    /**
     * Public method to erase the last symbol from the buffer.
     * Does not slice out the string, just erasing.
     */
    public void deleteLastChar() {
        final int bufLength = this.getBufferLength();
        final int bufStrSize = this.getBufferStr().length();
        //
        if ( bufStrSize <= 0 ) return;// real line length is already zero
        //
        this.deleteFromBuffer(bufStrSize - 1, bufStrSize, bufLength);
    }
    
    
    
}
