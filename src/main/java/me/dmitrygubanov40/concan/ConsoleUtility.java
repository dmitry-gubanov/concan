package me.dmitrygubanov40.concan;


import java.util.HashMap;
import java.util.Set;

import me.dmitrygubanov40.concan.buffer.OutputBuffer;



public class ConsoleUtility
{
    
    private HashMap<String, Character> charAsciiCodes;// ASCII-codes table
    
    // Whether to use rapid output of all buffer or output each symbol or command.
    private final OutputBuffer buffer;
    
    
    
    ////////////////////
    
    /**
     * Default (empty) constructor for direct console output.
     * Operating with standard 'println', or do not need at all.
     */
    public ConsoleUtility() {
        this.buffer = null;
        //
        this.initCharAsciiCodes();
    }
    
    /**
     * To send characters right ti the buffer.
     * @param initBuffer buffer the utility will work with
     * @throws NullPointerException when there is no real buffer to use
     */
    public ConsoleUtility(final OutputBuffer initBuffer) throws NullPointerException {
        //
        if ( null == initBuffer ) {
            String excMsg = "Cannot initialize buffer for Console Utility";
            throw new NullPointerException(excMsg);
        }
        //
        this.buffer = initBuffer;
        //
        this.initCharAsciiCodes();
    }
    
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
    private void initCharAsciiCodes() {
        this.charAsciiCodes = new HashMap<>();
        // General ASCII codes:
        this.charAsciiCodes.put("BEL", (char) 0x07);// Terminal bell (if supported)
        this.charAsciiCodes.put("BS",  (char) 0x08);// Backspace
        this.charAsciiCodes.put("HT",  (char) 0x09);// Horizontal Tab
        this.charAsciiCodes.put("LF",  (char) 0x0A);// "Linefeed" (new line)
        this.charAsciiCodes.put("VT",  (char) 0x0B);// Vertical TAB (new line with the same horizontal cursor position)
        this.charAsciiCodes.put("FF",  (char) 0x0C);// "Formfeed", printers' page breaker (if supported)
        this.charAsciiCodes.put("CR",  (char) 0x0D);// Carriage return
        this.charAsciiCodes.put("ESC", (char) 0x1B);// Escape character
        this.charAsciiCodes.put("DEL", (char) 0x7F);// "Delete" (empty) no-character (if supported)
    }
    
    /**
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character as String to use in commands
     * @throws IllegalArgumentException if there is no such ASCII code
     */
    private String getStrCharByName(final String charName) throws IllegalArgumentException {
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
     * Simple output (when buffer is not used).
     * @param charStr text to be simply printed in console
     */
    private void output(final String charStr) {
        //
        System.out.print(charStr);
        //
    }
    
    
    
    /**
     * Base wrapper for sending direct ASCII operations.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @param iterations how many times the command symbol will be sent
     * @throws IllegalArgumentException 
     */
    private void sendAsciiChar(final String charName, final int iterations) throws IllegalArgumentException {
        if ( iterations <= 0 ) {
            String excMsg = "Number of iteraions for '" + charName + "'-char must be positive,"
                               + " but is '" + iterations + "'";
            throw new IllegalArgumentException(excMsg);
        }
        for ( int i = 0; i < iterations; i++ ) {
            String charStr = this.getStrCharByName(charName);
            if ( this.isBuffering() ) {
                // add to a buffer
                this.buffer.add(charStr);
            } else {
                // direct output of the symbol
                this.output(charStr);
            }
        }
    }
    
    
    
    /**
     * "To the beginning of the line"
     * Send "carriage-return" command
     * (the very beginning of the line), \r.
     */
    public void sendCR() {
        this.sendAsciiChar("CR", 1);
    }
    public String getCR() {
        return this.getStrCharByName("CR");
    }
    
    /**
     * "One symbol back"
     * Send "BackSpace" command (move one symbol back), \b.
     * @param steps number of chars to left from the cursor
     */
    public void sendBS(final int steps) {
        this.sendAsciiChar("BS", steps);
    }
    public void sendBS() {
        this.sendBS(1);
    }
    public String getBS() {
        return this.getStrCharByName("BS");
    }
    
    /**
     * "New line"
     * Move to new line, cursor is at the beginning, \n.
     * @param lines number of lines to move down
     */
    public void sendLF(final int lines) {
        this.sendAsciiChar("LF", lines);
    }
    public void sendLF() {
        this.sendLF(1);
    }
    public String getLF() {
        return this.getStrCharByName("LF");
    }
    
    /**
     * "Horizontal tabulation"
     * Make one 8-characters tabulation step to the right, \t. 
     * At the end of line the output will continue at the beginning of new line.
     * @param columns number of 8-symbol steps to move right
     */
    public void sendHT(final int columns) {
        this.sendAsciiChar("HT", columns);
    }
    public void sendHT() {
        this.sendHT(1);
    }
    public String getHT() {
        return this.getStrCharByName("HT");
    }
    
    /**
     * "Vertical tabulation"
     * Keep current horizontal cursor position and then move down, \v.
     * @param lines number of lines to move down
     */
    public void sendVT(final int lines) {
        this.sendAsciiChar("VT", lines);
    }
    public void sendVT() {
        this.sendVT(1);
    }
    public String getVT() {
        return this.getStrCharByName("VT");
    }
    
    /**
     * "Escape character"
     * Essential part for escape sequences.
     */
    public void sendESC() {
        this.sendAsciiChar("ESC", 1);
    }
    public String getESC() {
        return this.getStrCharByName("ESC");
    }
    
    
    
}
