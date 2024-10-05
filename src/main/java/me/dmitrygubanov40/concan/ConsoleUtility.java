package me.dmitrygubanov40.concan;

import java.util.HashMap;
import java.util.Set;



public class ConsoleUtility
{
    
    private HashMap<String, Character> charAsciiCodes;
    
    ////////////////////
    
    public ConsoleUtility() {
        this.initCharAsciiCodes();
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
        this.charAsciiCodes.put("DEL", (char) 0x7F);// "Delete" (empty) no-character
    }
    
    /**
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @return ASCII character as String to use in commands
     * @throws IllegalArgumentException 
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
     * Base wrapper for sending direct ACSII operations.
     * @param charName codename of character from ASCII table (charAsciiCodes)
     * @param iterations how many times the command symbol will be sent
     * @throws IllegalArgumentException 
     */
    private void sendCommandChar(final String charName, final int iterations) throws IllegalArgumentException {
        if ( iterations <= 0 ) {
            String excMsg = "Number of iteraions for '" + charName + "'-char must be positive,"
                               + " but is '" + iterations + "'";
            throw new IllegalArgumentException(excMsg);
        }
        for ( int i = 0; i < iterations; i++ ) {
            System.out.print(this.getStrCharByName(charName));
        }
    }
    
    
    
    /**
     * Send "carriage-return" command
     * (the very beginning of the line), \r.
     */
    public void CR() {
        this.sendCommandChar("CR", 1);
    }
    
    /**
     * Send "BackSpace" command (move one symbol back), \b.
     * @param steps number of chars to left from the cursor
     */
    public void BS(final int steps) throws IllegalArgumentException {
        this.sendCommandChar("BS", steps);
    }
    public void BS() {
        this.BS(1);
    }
    
    /**
     * Move to new line, cursor is at the beginning, \n.
     * @param lines number of lines to move down
     */
    public void LF(final int lines) {
        this.sendCommandChar("LF", lines);
    }
    public void LF() {
        this.LF(1);
    }
    
    /**
     * Make one 8-characters tabulation step to the right, \t.
     * At the end of line the output will continue at the beginning of new line.
     * @param columns number of 8-symbol steps to move right
     */
    public void HT(final int columns) {
        this.sendCommandChar("HT", columns);
    }
    public void HT() {
        this.HT(1);
    }
    
    /**
     * Keep current horizontal cursor position and then move down, \v.
     * @param lines number of lines to move down
     */
    public void VT(final int lines) {
        this.sendCommandChar("VT", lines);
    }
    public void VT() {
        this.VT(1);
    }
    
    
    
}
