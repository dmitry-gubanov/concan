package me.dmitrygubanov40.concan.utility;


import java.util.HashMap;
import java.util.Set;

import me.dmitrygubanov40.concan.buffer.OutputBuffer;



/**
 * Buffer-integrated base engine utility without constructor.
 * Base staff to work with ASCII chars and Esc-commands (sequences).
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class UtilityEngine
{
    
    // 'Glue' for Esc-symbol and the command (sequence) itself.
    public final static String ESC_CMD_SEPARATOR;
    public final static String ESC_CMD_PARAM;// extra argument in command replacement
    
    // ASCII-codes table
    private final static HashMap<String, Character> charAsciiCodes;
    
    // Esc-commands table (Esc-sequences)
    private final static HashMap<String, String> escCommands;
    
    
    static {
        ESC_CMD_SEPARATOR = "[";
        ESC_CMD_PARAM = "#";
        //
        // ASCII-codes table initialization:
        charAsciiCodes = new HashMap<>();
        initCharAsciiCodes();
        //
        // Escape-commands (sequences) initialization:
        escCommands = new HashMap<>();
        initEscCommands();
    }
    
    
    // whether to use rapid output of all buffer or output each symbol or command
    protected OutputBuffer buffer;
    
    
    ////////////////////
    
    
    /**
     * @return do we currently use the buffer?
     */
    public boolean isBuffering() {
        return (null != this.buffer);
    }
    
    
    
    /**
     * To fill the ASCII-codes table.
     */
    private static void initCharAsciiCodes() {
        UtilityEngine.charAsciiCodes.put("ETX", (char) 0x03);// end of text
        UtilityEngine.charAsciiCodes.put("BEL", (char) 0x07);// (if supported) terminal bell
        UtilityEngine.charAsciiCodes.put("BS",  (char) 0x08);// '\b', backspace
        UtilityEngine.charAsciiCodes.put("HT",  (char) 0x09);// '\t', horizontal tab
        UtilityEngine.charAsciiCodes.put("LF",  (char) 0x0A);// '\n', "linefeed" (new line)
        UtilityEngine.charAsciiCodes.put("VT",  (char) 0x0B);// vertical TAB (new line, same horizontal cursor position)
        UtilityEngine.charAsciiCodes.put("FF",  (char) 0x0C);// '\f', (if supported) "formfeed", printers' page breaker
        UtilityEngine.charAsciiCodes.put("CR",  (char) 0x0D);// '\r', carriage return
        UtilityEngine.charAsciiCodes.put("ESC", (char) 0x1B);// escape character for escape-sequence commands
        UtilityEngine.charAsciiCodes.put("DEL", (char) 0x7F);// (if supported) "delete" (empty) no-character
    }
    
    /**
     * To fill the table with all commands utility can support.
     * Important: '#' in command means some argument (1, 2, or 3).
     */
    private static void initEscCommands() {
        // Cursor control functions:
        UtilityEngine.escCommands.put("HOME",        "H");      // move cursor to the home position (1, 1)
        UtilityEngine.escCommands.put("GOTO",        "#;#H");   // move cursor to the position (#1, #2) /Y, X/
        UtilityEngine.escCommands.put("UP",          "#A");     // move cursor up for # lines (Y) if possible, X is kept
        UtilityEngine.escCommands.put("DOWN",        "#B");     // move cursor down for # lines (Y) if possible, X is kept
        UtilityEngine.escCommands.put("RIGHT",       "#C");     // move cursor to the right for # columns (X) if possible, Y is kept
        UtilityEngine.escCommands.put("LEFT",        "#D");     // move cursor to the left for # columns (X) if possible, Y is kept
        UtilityEngine.escCommands.put("NEXT",        "#E");     // move cursor to the beginning of next line (X=0, '#'-Y to down)
        UtilityEngine.escCommands.put("PREVIOUS",    "#F");     // move cursor to the beginning of previous line (X=0, '#'-Y up)
        UtilityEngine.escCommands.put("COLUMN",      "#G");     // move cursor to the column number '#' if possible, Y (line) is kept
        //
        UtilityEngine.escCommands.put("CURSOR_ON",          "?25h");    // show console cursor
        UtilityEngine.escCommands.put("CURSOR_OFF",         "?25l");    // hide console cursor
        UtilityEngine.escCommands.put("CURSOR_BLINKING_ON", "?12h");    // console cursor blinking is on (if supported)
        UtilityEngine.escCommands.put("CURSOR_BLINKING_OFF","?12l");    // console cursor blinking is off (if supported)
        UtilityEngine.escCommands.put("CURSOR_REPORT",      "6n");      // get console report of cursor position
        //
        // Clear functions:
        UtilityEngine.escCommands.put("ERASE_ALL_AFTER",     "0J"); // erase from cursor until end of screen, cursor is kept
        UtilityEngine.escCommands.put("ERASE_ALL_BEFORE",    "1J"); // erase from cursor to beginning of screen, cursor is kept
        UtilityEngine.escCommands.put("ERASE_ALL",           "2J"); // clear all the console, cursor is kept
        UtilityEngine.escCommands.put("ERASE_LINE_AFTER",    "0K"); // erase from cursor until end of line, cursor is kept
        UtilityEngine.escCommands.put("ERASE_LINE_BEFORE",   "1K"); // erase from cursor to beginning of line, cursor is kept
        UtilityEngine.escCommands.put("ERASE_LINE",          "2K"); // clear all the current line, cursor is kept
        //
        // Style control functions:
        UtilityEngine.escCommands.put("RESET",      "0m");  // reset all - both styles and color
        UtilityEngine.escCommands.put("SAVE",       "s");   // save cursor position and all settings
        UtilityEngine.escCommands.put("RESTORE",    "u");   // restore earlier saved cursor position and all settings
        //
        UtilityEngine.escCommands.put("BOLD",           "1m");  // letters are twice bolder
        UtilityEngine.escCommands.put("BOLD_OFF",       "22m"); // bold is off (same for dim-mode)
        UtilityEngine.escCommands.put("DIM",            "2m");  // letters are twice less colorful (if support)
        UtilityEngine.escCommands.put("DIM_OFF",        "22m"); // dim is off (same for bold-mode)
        UtilityEngine.escCommands.put("ITALIC",         "3m");  // letters are semi-curve
        UtilityEngine.escCommands.put("ITALIC_OFF",     "23m"); // italic is off
        UtilityEngine.escCommands.put("UNDERLINE",      "4m");  // all letters (inc. empty) have underline
        UtilityEngine.escCommands.put("UNDERLINE_OFF",  "24m"); // underline if off
        UtilityEngine.escCommands.put("BLINK",          "5m");  // letters are blinking (frequency is OS based)
        UtilityEngine.escCommands.put("BLINK_OFF",      "25m"); // blinking is off
        UtilityEngine.escCommands.put("INVERSE",        "7m");  // chars get background color, background - color of chars
        UtilityEngine.escCommands.put("INVERSE_OFF",    "27m"); // inverse if off
        UtilityEngine.escCommands.put("HIDDEN",         "8m");  // chars get background color, are invisible but selectable
        UtilityEngine.escCommands.put("HIDDEN_OFF",     "28m"); // hidden is off
        UtilityEngine.escCommands.put("STRIKE",         "9m");  // vertical medium height line through all the chars
        UtilityEngine.escCommands.put("STRIKE_OFF",     "29m"); // strike is off
        //
        // Colors control function:
        UtilityEngine.escCommands.put("COLOR_DEFAULT",       "39m");// return default console letters color (style is kept)
        UtilityEngine.escCommands.put("BACKGROUND_DEFAULT",  "49m");// return default console background color (style is kept)
        // (TrueColor 24b regime)
        UtilityEngine.escCommands.put("COLOR",           "38;2;#;#;#m");// (R-index, G-index, B-index)-color for letters
        UtilityEngine.escCommands.put("BACKGROUND",      "48;2;#;#;#m");// (R-index, G-index, B-index)-color for background
        // (8b regime)
        UtilityEngine.escCommands.put("COLOR_8B",        "38;5;#m");// premade console 256 colors for letters
        UtilityEngine.escCommands.put("BACKGROUND_8B",   "48;5;#m");// premade console 256 colors for background
        // (4b regime)
        UtilityEngine.escCommands.put("COLOR_4B",        "#m");// premade console 16 colors for letters
        UtilityEngine.escCommands.put("BACKGROUND_4B",   "#m");// premade console 16 colors for background
    }
    
    
    
    /**
     * Getter from 'charAsciiCodes', char-format.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character to use in commands
     * @throws IllegalArgumentException if there is no such ASCII code
     */
    protected static char getCharByName(final String charName) throws IllegalArgumentException {
        if ( !UtilityEngine.charAsciiCodes.containsKey(charName) ) {
            Set<String> names = UtilityEngine.charAsciiCodes.keySet();
            String excMsg = "There is no '" + charName + "'-char in ASCII-codes table. "
                               + "Available chars: " + names;
            throw new IllegalArgumentException(excMsg);
        }
        //
        char controlChar = UtilityEngine.charAsciiCodes.get(charName);
        return controlChar;
    }
    
    /**
     * Getter from 'charAsciiCodes', int-format.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character as int to use in commands
     */
    protected static int getIntCharByName(final String charName) {
        return (int) UtilityEngine.getCharByName(charName);
    }
    
    /**
     * Getter from 'charAsciiCodes', String-format.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character as String to use in commands
     */
    protected static String getStrCharByName(final String charName) {
        Character controlChar = UtilityEngine.getCharByName(charName);
        return controlChar.toString();
    }
    
    
    
    /**
     * Getter from 'escCommands'.
     * @param cmdName codename of a command from the table (escCommands)
     * @return command representation (string may be necessary to replace the '#')
     * @throws IllegalArgumentException if there is no such command in the table
     */
    protected static String getStrEscCmdByName(final String cmdName) throws IllegalArgumentException {
        if ( !UtilityEngine.escCommands.containsKey(cmdName) ) {
            Set<String> commands = UtilityEngine.escCommands.keySet();
            String excMsg = "There is no '" + cmdName + "'-command in Esc-sequence table. "
                               + "Available commands: " + commands;
            throw new IllegalArgumentException(excMsg);
        }
        //
        String commandStr = UtilityEngine.escCommands.get(cmdName);
        return commandStr;
    }
    
    
    
    /**
     * @return array of characters considered as commands (special chars)
     */
    public static char[] getSpecialAsciiCodes() {
        final int codesNumber = UtilityEngine.charAsciiCodes.size();
        char[] codes = new char[ codesNumber ];
        //
        int i = 0;
        for ( char curAsciiCode : UtilityEngine.charAsciiCodes.values() ) {
            codes[ i ] = curAsciiCode;
            i++;
        }
        //
        return codes;
    }
    
    /**
     * @return array of strings considered as commands (escape sequences)
     */
    public static String[] getEscCmds() {
        final int cmdNumber = UtilityEngine.escCommands.size();
        String[] cmds = new String[ cmdNumber ];
        //
        int i = 0;
        for ( String curCmdTemplateStr : UtilityEngine.escCommands.values() ) {
            cmds[ i ] = curCmdTemplateStr;
            i++;
        }
        //
        return cmds;
    }
    
    
    ////////////////////
    
    
    /**
     * Simple output (when buffer is not used).
     * Ignores empty string.
     * @param charStr text to be simply printed in console
     */
    private void output(final String charStr) {
        if ( charStr.length() <= 0 ) {
            return;
        }
        //
        System.out.print(charStr);
    }
    
    /**
     * Send text to buffer (in case we are buffering) or directly to console output otherwise.
     * Ignores empty string.
     * @param txtToSend text ready to be outputted
     * @param wholeMode flag to send 'txtToSend' as-is, guaranteeing it would not be separated
     */
    private void send(final String txtToSend, final boolean wholeMode) {
        if ( txtToSend.length() <= 0 ) {
            return;
        }
        //
        if ( this.isBuffering() ) {
            // add to the initialized buffer
            if ( wholeMode )    this.buffer.addWhole(txtToSend);
            else                this.buffer.add(txtToSend);
            //
        } else {
            // direct output of the symbol, always - 'as-is' (whole mode)
            this.output(txtToSend);
        }
    }
    private void send(final String txtToSend) {
        this.send(txtToSend, false);
    }
    
    
    
    /**
     * Base wrapper for sending direct ASCII operations.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @param iterations how many times the command symbol will be sent
     * @throws IllegalArgumentException with incorrect number of iterations
     */
    protected void sendAsciiChar(final String charName, final int iterations) throws IllegalArgumentException {
        if ( iterations <= 0 ) {
            String excMsg = "Number of iteraions for '" + charName + "'-char must be positive,"
                               + " but is '" + iterations + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        for ( int i = 0; i < iterations; i++ ) {
            String charStr = UtilityEngine.getStrCharByName(charName);
            this.send(charStr);
        }
    }
    
    
    
    /**
     * @param cmd command (sequence) raw macros (like '38;2;#;#;#m').
     * @return number of hash symbols '#' (UtilityEngine.ESC_CMD_PARAM) in the 'cmd'-string 
     */
    private static int getNmbOfEscCmdParams(final String cmd) {
        int hashCount = cmd.length() - cmd.replace(ESC_CMD_PARAM, "").length();
        //
        return hashCount;
    }
    
    /**
     * @param cmd command (sequence) final macros with all arguments replaced (like '38;2;1;2;3m').
     * @return string full full composition of esc-command
     */
    private static String getFullStrEscCmd(String cmd) {
        // Here is 'magic' like "ESC" + "[" + "2J"
        String escCmd = UtilityEngine.getStrCharByName("ESC")
                        + ESC_CMD_SEPARATOR
                        + cmd;
        //
        return escCmd;
    }
    
    
    
    /**
     * Connect all the parts of simple (no arguments) command/sequence, checking its parameters.
     * @param cmdCode base part of a command (like "H" or "2J")
     * @throws IllegalArgumentException when escape command (sequence) has arguments (is not simple)
     */
    protected static String getSimpleEscCmd(final String cmdCode)
                                    throws IllegalArgumentException {
        int hashCount = UtilityEngine.getNmbOfEscCmdParams(cmdCode);
        if ( hashCount > 0 ) {
            String excMsg = "Command '" + cmdCode + "' is not simple: has " + hashCount + "arguments";
            throw new IllegalArgumentException(excMsg);
        }
        //
        String cmd = UtilityEngine.getStrEscCmdByName(cmdCode);
        //
        return UtilityEngine.getFullStrEscCmd(cmd);
    }
    
    /**
     * Connect all the parts of command/sequence and send it to the buffer/output.
     * @param cmdCode base part of a command (like "H" or "2J")
     * @param params possible extra arguments (to replace '#'-s)
     */
    protected void sendEscCmd(final String cmdCode, final Integer... params) {
        String cmd = UtilityEngine.getStrEscCmdByName(cmdCode);
        if ( !this.checkEscCmdArguments(cmd, params) ) return;// in case parameters are incorrect do nothing
        cmd = this.replaceEscCmdArguments(cmd, params);
        //
        String escCmd = UtilityEngine.getFullStrEscCmd(cmd);
        //
        // all the command (sequence) will be shown at a single moment
        final boolean sendEscCmdInWhole = true;
        this.send(escCmd, sendEscCmdInWhole);
    }
    
    /**
     * Are arguments in hash table and the method correct?
     * @param cmd real command string (from 'escCommands'-table)
     * @param params possible extra arguments (for '#'-s)
     * @return 'true' when arguments correspond each other ('#'-hashes and 'params')
     */
    private boolean checkEscCmdArguments(final String cmd, final Integer... params) {
        // count number of '#' in command string
        int hashCount = UtilityEngine.getNmbOfEscCmdParams(cmd);
        //
        return (hashCount == params.length);
    }
    
    /**
     * Support method for '#'-s to be replaced with the values of 'params'.
     * Corresponding via number of '#' and 'params' must be already checked
     * (via 'checkEscCmdArguments')
     * @param cmd real command string (from 'escCommands'-table)
     * @param params list of possible extra argument (for the '#'-s)
     * @return string with actual parameters (if any)
     */
    private String replaceEscCmdArguments(final String cmd, final Integer... params) {
        if ( 0 == params.length ) {
            return cmd;
        }
        //
        StringBuilder finalCmd = new StringBuilder(cmd);
        //
        for ( Integer currentParam : params ) {
            int currentHashIndex = finalCmd.indexOf(ESC_CMD_PARAM);
            finalCmd.deleteCharAt(currentHashIndex);
            finalCmd.insert(currentHashIndex, currentParam.toString());
        }
        //
        return finalCmd.toString();
    }
    
    
    
}
