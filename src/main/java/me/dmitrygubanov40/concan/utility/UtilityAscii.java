package me.dmitrygubanov40.concan.utility;



/**
 * Extension for the engine-class with necessary cover methods.
 * Has strong connection to 'charAsciiCodes' from engine-class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class UtilityAscii extends UtilityEngine
{
    
    // block of static available ASCII shortcuts
    // (look for 'UtilityEngine::charAsciiCodes')
    public static final String CR;
    public static final String BS;
    public static final String LF;
    public static final String HT;
    public static final String VT;
    public static final String ESC;
    public static final String BEL;
    
    
    static {
        CR  = UtilityEngine.getStrCharByName("CR");
        BS  = UtilityEngine.getStrCharByName("BS");
        LF  = UtilityEngine.getStrCharByName("LF");
        HT  = UtilityEngine.getStrCharByName("HT");
        VT  = UtilityEngine.getStrCharByName("VT");
        ESC = UtilityEngine.getStrCharByName("ESC");
        BEL = UtilityEngine.getStrCharByName("BEL");
    }
    
    
    
    /**
     * @param symbol character we analyze
     * @return 'true' when it is regular visible char, 'false' otherwise
     */
    public static boolean isPrintableChar(final char symbol) {
        boolean result = true;
        //
        int symbolValue = (int) symbol;
        // first ASCII-table characters or 'Delete'-key:
        if ( symbolValue < 0x20 || symbolValue == 0x7F ) {
            result = false;
        }
        //
        return result;
    }
    
    /**
     * @param symbol string with a char we analyze
     * @return 'true' when it is regular visible char, 'false' otherwise
     * @throws IllegalArgumentException if symbol-string is not one-char string
     */
    public static boolean isPrintableChar(final String symbol)
                            throws IllegalArgumentException {
        if ( symbol.length() != 1 ) {
            String excMsg = "String with one symbol expected, got '" + symbol + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        return UtilityAscii.isPrintableChar(symbol.charAt(0));
    }
    
    
    
    /**
     * "To the beginning of the line"
     * Send "carriage-return" command
     * (the very beginning of the line), \r.
     */
    public void sendCR() {
        this.sendAsciiChar("CR", 1);
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
    
    /**
     * "Escape character"
     * Essential part for escape sequences.
     */
    public void sendESC() {
        this.sendAsciiChar("ESC", 1);
    }
    
    
    
}
