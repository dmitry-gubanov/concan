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
    private final static String ESC_CMD_SEPARATOR;
    private final static String ESC_CMD_PARAM;
    
    static {
        ESC_CMD_SEPARATOR = "[";
        ESC_CMD_PARAM = "#";
    }
    
    
    // ASCII-codes table
    private HashMap<String, Character> charAsciiCodes;
    
    // Esc-commands table (Esc-sequences)
    private HashMap<String, String> escCommands;
    
    // whether to use rapid output of all buffer or output each symbol or command
    protected OutputBuffer buffer;
    
    
    ////////////////////
    
    
    /**
     * Fill the tables to operate.
     * Need it for final class initialization.
     */
    protected void initHashTables() {
        this.initCharAsciiCodes();
        this.initEscCommands();
    }
    
    
    
    /**
     * @return do we currently use the buffer?
     */
    public boolean isBuffering() {
        return (null != this.buffer);
    }
    
    
    
    /**
     * To fill the ASCII-codes table.
     */
    private void initCharAsciiCodes() {
        this.charAsciiCodes = new HashMap<>();
        // General ASCII codes:
        this.charAsciiCodes.put("BEL", (char) 0x07);// '\a', (if supported) terminal bell
        this.charAsciiCodes.put("BS",  (char) 0x08);// '\b', backspace
        this.charAsciiCodes.put("HT",  (char) 0x09);// '\t', horizontal tab
        this.charAsciiCodes.put("LF",  (char) 0x0A);// '\n', "linefeed" (new line)
        this.charAsciiCodes.put("VT",  (char) 0x0B);// '\v', vertical TAB (new line, same horizontal cursor position)
        this.charAsciiCodes.put("FF",  (char) 0x0C);// '\f', (if supported) "formfeed", printers' page breaker
        this.charAsciiCodes.put("CR",  (char) 0x0D);// '\r', carriage return
        this.charAsciiCodes.put("ESC", (char) 0x1B);// '\e', escape character
        this.charAsciiCodes.put("DEL", (char) 0x7F);// (if supported) "delete" (empty) no-character
    }
    
    
    
    /**
     * To fill the table with all commands utility can support.
     * Important: '#' in command means 'i' (or 'j' for the second one) argument.
     */
    private void initEscCommands() {
        this.escCommands = new HashMap<>();
        //
        // List of Esc-commands.
        //
        // Cursor control functions:
        this.escCommands.put("HOME",        "H");   // move cursor to the home position (0, 0)
        this.escCommands.put("GOTO",        "#;#H");// move cursor to the position (#1, #2) /Y, X/
        this.escCommands.put("UP",          "#A");  // move cursor up for # lines (Y) if possible, X is kept
        this.escCommands.put("DOWN",        "#B");  // move cursor down for # lines (Y) if possible, X is kept
        this.escCommands.put("RIGHT",       "#C");  // move cursor to the right for # columns (X) if possible, Y is kept
        this.escCommands.put("LEFT",        "#D");  // move cursor to the left for # columns (X) if possible, Y is kept
        this.escCommands.put("NEXT",        "#E");  // move cursor to the beginning of next line (X=0, '#'-Y to down)
        this.escCommands.put("PREVIOUS",    "#F");  // move cursor to the beginning of previous line (X=0, '#'-Y up)
        this.escCommands.put("COLUMN",      "#G");  // move cursor to the column number '#' if possible, Y (line) is kept
        this.escCommands.put("CURSOR_ON",   "?25h");// show console cursor
        this.escCommands.put("CURSOR_OFF",  "?25l");// hide console cursor
        //
        // Clear functions:
        this.escCommands.put("ERASE_ALL_AFTER",     "0J");  // erase from cursor until end of screen, cursor is kept
        this.escCommands.put("ERASE_ALL_BEFORE",    "1J");  // erase from cursor to beginning of screen, cursor is kept
        this.escCommands.put("ERASE_ALL",           "2J");  // clear all the console, cursor is kept
        this.escCommands.put("ERASE_LINE_AFTER",    "0K");  // erase from cursor until end of line, cursor is kept
        this.escCommands.put("ERASE_LINE_BEFORE",   "1K");  // erase from cursor to beginning of line, cursor is kept
        this.escCommands.put("ERASE_LINE",          "2K");  // clear all the current line, cursor is kept
        //
        // Style control functions:
        this.escCommands.put("RESET",               "0m");  // reset all - both styles and color
        //
        this.escCommands.put("STYLE_BOLD",          "1m");  // letters are twice bolder
        this.escCommands.put("STYLE_BOLD_OFF",      "22m"); // bold is off (same for dim-mode)
        this.escCommands.put("STYLE_DIM",           "2m");  // letters are twice less colorful (if support)
        this.escCommands.put("STYLE_DIM_OFF",       "22m"); // dim is off (same for bold-mode)
        this.escCommands.put("STYLE_ITALIC",        "3m");  // letters are semi-curve
        this.escCommands.put("STYLE_ITALIC_OFF",    "23m"); // italic is off
        this.escCommands.put("STYLE_UNDERLINE",     "4m");  // all letters (inc. empty) have underline
        this.escCommands.put("STYLE_UNDERLINE_OFF", "24m"); // underline if off
        this.escCommands.put("STYLE_BLINK",         "5m");  // letters are blinking (frequency is OS based)
        this.escCommands.put("STYLE_BLINK_OFF",     "25m"); // blinking is off
        this.escCommands.put("STYLE_INVERSE",       "7m");  // chars get background color, background - color of chars
        this.escCommands.put("STYLE_INVERSE_OFF",   "27m"); // inverse if off
        this.escCommands.put("STYLE_HIDDEN",        "8m");  // chars get background color, are invisible but selectable
        this.escCommands.put("STYLE_HIDDEN_OFF",    "28m"); // hidden is off
        this.escCommands.put("STYLE_STRIKE",        "9m");  // vertical medium height line through all the chars
        this.escCommands.put("STYLE_STRIKE_OFF",    "29m"); // strike is off
        //
        // Colors control function:
        this.escCommands.put("COLOR_DEFAULT",       "39m"); // return default console letters color (style is kept)
        this.escCommands.put("BACKGROUND_DEFAULT",  "49m"); // return default console background color (style is kept)
        // (TrueColor 24b regime)
        this.escCommands.put("COLOR",           "38;2;#;#;#m"); // (R-index, G-index, B-index)-color for letters
        this.escCommands.put("BACKGROUND",      "48;2;#;#;#m"); // (R-index, G-index, B-index)-color for background
        // (8b regime)
        this.escCommands.put("COLOR_8B",        "38;5;#m"); // premade console 256 colors for letters
        this.escCommands.put("BACKGROUND_8B",   "48;5;#m"); // premade console 256 colors for background
        // (4b regime)
        this.escCommands.put("COLOR_4B",        "#m");  // premade console 16 colors for letters
        this.escCommands.put("BACKGROUND_4B",   "#m");  // premade console 16 colors for background
    }
    
    
    
    /**
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character as String to use in commands
     * @throws IllegalArgumentException if there is no such ASCII code
     */
    protected String getStrCharByName(final String charName) throws IllegalArgumentException {
        if ( !this.charAsciiCodes.containsKey(charName) ) {
            Set<String> names = this.charAsciiCodes.keySet();
            String excMsg = "There is no '" + charName + "'-char in ASCII-codes table. "
                               + "Available chars: " + names;
            throw new IllegalArgumentException(excMsg);
        }
        //
        Character controlChar = this.charAsciiCodes.get(charName);
        //
        return controlChar.toString();
    }
    
    /**
     * @param cmdName codename of a command from the table (escCommands)
     * @return command representation (string may be necessary to replace the '#')
     * @throws IllegalArgumentException if there is no such command in the table
     */
    protected String getStrCmdByName(final String cmdName) throws IllegalArgumentException {
        if ( !this.escCommands.containsKey(cmdName) ) {
            Set<String> commands = this.escCommands.keySet();
            String excMsg = "There is no '" + cmdName + "'-command in Esc-sequence table. "
                               + "Available commands: " + commands;
            throw new IllegalArgumentException(excMsg);
        }
        //
        String commandStr = this.escCommands.get(cmdName);
        return commandStr;
    }
    
    
    
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
            //
            // add to the initialized buffer
            if ( wholeMode )    this.buffer.addWhole(txtToSend);
            else                this.buffer.add(txtToSend);
            //
        } else {
            // direct output of the symbol
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
            String charStr = this.getStrCharByName(charName);
            this.send(charStr);
        }
    }
    
    
    
    /**
     * Connect all the parts of command/sequence and send it to the buffer/output.
     * @param cmdCode base part of a command (like "H" or "2J")
     * @param params possible extra arguments (to replace '#'-s)
     */
    protected void sendEscCmd(final String cmdCode, final Integer... params) {
        String cmd = this.getStrCmdByName(cmdCode);
        if ( !this.checkEscCmdArguments(cmd, params) ) return;// in case parameters are incorrect
        cmd = this.replaceEscCmdArguments(cmd, params);
        //
        // Here is 'magic' like "ESC" + "[" + "2J"
        String escCmd = this.getStrCharByName("ESC")
                        + UtilityEngine.ESC_CMD_SEPARATOR
                        + cmd;
        //
        // all the command (sequence) will be shown at a single moment
        final boolean sendEscCmdInWhole = true;
        this.send(escCmd, sendEscCmdInWhole);
    }
    
    /**
     * Are arguments in hash table and the method correct?
     * @param cmd real command string (from 'escCommands'-table)
     * @param params possible extra arguments (for '#'-s)
     * @return 'true' when arguments correspond each other
     * @throws IllegalArgumentException when number of '#' in command string don't correspond 'params'
     */
    private boolean checkEscCmdArguments(final String cmd, final Integer... params)
                        throws IllegalArgumentException {
        // count number of '#' in command string
        int hashCount = cmd.length() - cmd.replace(UtilityEngine.ESC_CMD_PARAM, "").length();
        //
        if ( hashCount != params.length ) {
            String excMsg = "'" + cmd + "'-command requires " + hashCount + " arguments. "
                            + "Number of arguments: '" + params.length + "'.";
            throw new IllegalArgumentException(excMsg);
        }
        //
        return true;
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
            int currentHashIndex = finalCmd.indexOf(UtilityEngine.ESC_CMD_PARAM);
            finalCmd.deleteCharAt(currentHashIndex);
            finalCmd.insert(currentHashIndex, currentParam.toString());
        }
        //
        return finalCmd.toString();
    }
    
    
    
}
