package me.dmitrygubanov40.concan.utility;



/**
 * Extension for the engine-class with necessary cover methods.
 * Has strong connection to 'charAsciiCodes' from engine-class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class UtilityAscii extends UtilityEngine
{
    
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
