package me.dmitrygubanov40.concan.utility;

import java.io.IOException;
import java.io.Reader;



/**
 * Heavy methods (functions) for utility.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class UtilityMethods extends UtilityEscCommands
{
    
    private final static String CONSOLE_REPORT_START_CHAR;
    private final static String CONSOLE_REPORT_SEPARATOR_CHAR;
    private final static String CONSOLE_REPORT_END_CHAR;
    
    // assume terminal can not be more than it,
    // try this point to get width and height of terminal
    private final static ConCord CONSOLE_MAX_SIZE;
    
    static {
        CONSOLE_REPORT_START_CHAR = "[";
        CONSOLE_REPORT_SEPARATOR_CHAR = ";";
        CONSOLE_REPORT_END_CHAR = "R";
        //
        CONSOLE_MAX_SIZE = new ConCord(1000, 1000);
    }
    
    //////////
    
    // get console cursor position //
    
    /**
     * ONLY FOR UNIX AND LINUX.
     * @return (X;Y) console coordinate, first column/line are '1' (not '0')
     * @throws RuntimeException if OS is not *nix, console manipulations failed,
     *          or console buffer reading was interrupted 
     */
    public static ConCord getCursorPosition() throws RuntimeException {
        if ( !Os.isNx() ) {
            // not Linux neither UNIX, method is unapplicable
            String excMsg = "Method is supported only by UNIX and Linux OS";
            throw new RuntimeException(excMsg);
        }
        //
        int byteBuffer;
        StringBuilder positionAnswer = new StringBuilder();
        Reader consoleKeyListener = System.console().reader();
        //
        if ( !UtilityMethods.isSttyRaw() ) {
            String excMsg = "Console's mode switch failed (to raw-mode)";
            throw new RuntimeException(excMsg);
        }
        //
        // base esc-command to get cursor-position respond
        System.out.print(UtilityEscCommands.CURSOR_REPORT);
        //
        final int endOfTextCharCode = UtilityEngine.getIntCharByName("ETX");
        final int escapeCharCode    = UtilityEngine.getIntCharByName("ESC");
        //
        try {
            while ( (byteBuffer = consoleKeyListener.read()) > -1 ) {
                if ( endOfTextCharCode == byteBuffer ) {
                    // command to stop listening
                    break;
                }
                if ( escapeCharCode == byteBuffer ) {
                    // we do not want Esc-char in result string
                    continue;
                }
                //
                char nextBufferChar = (char) byteBuffer;
                positionAnswer.append(nextBufferChar);
                if ( 'R' == nextBufferChar ) {
                    // 'R' is the end of text console self-answer
                    break;
                }
            }
        } catch ( IOException exc ) {
            String excMsg = "<IOException> Failed to read the console's buffer (key pressed)";
            throw new RuntimeException(excMsg);
        }
        //
        if ( !UtilityMethods.isSttyCoocked() ) {
            String excMsg = "Console's mode switch failed (to coocked-mode)";
            throw new RuntimeException(excMsg);
        }
        //
        ConCord result;
        result = UtilityMethods.parseConsoleReport(positionAnswer);
        // need a shift to have math coordinates not position:
        // ([coordinate] = [console position - 1])
        result.setX(result.getX() - ConCord.SHIFT_X);
        result.setY(result.getY() - ConCord.SHIFT_Y);
        //
        return result;
    }
    
    /**
     * @param answer buffer we got after escape command
     * @return ConCord object with coordinates answer (as-is)
     * @throws IllegalArgumentException when input string is out of format ("ESC[#;#R")
     */
    private static ConCord parseConsoleReport(final StringBuilder answer)
                    throws IllegalArgumentException {
        String excMsg = "Incorrect console position answer: " + answer;
        if ( answer.length() < 4 ) {
            throw new IllegalArgumentException(excMsg);
        }
        //
        int startIndex      = answer.indexOf(CONSOLE_REPORT_START_CHAR);
        int separatorIndex  = answer.indexOf(CONSOLE_REPORT_SEPARATOR_CHAR);
        int endIndex        = answer.indexOf(CONSOLE_REPORT_END_CHAR);
        //
        if ( (endIndex - separatorIndex) < 2 || (separatorIndex - startIndex) < 2 ) {
            throw new IllegalArgumentException(excMsg);
        }
        //
        String xCordStr = answer.substring(separatorIndex + 1, endIndex);
        String yCordStr = answer.substring(startIndex + 1, separatorIndex);
        int xCord = Integer.parseInt(xCordStr);
        int yCord = Integer.parseInt(yCordStr);
        //
        return new ConCord(xCord, yCord);
    }
    
    
    
    /**
     * Run some console command until successfully executed.
     * @param consoleCmd array with command elements (as in 'exec()')
     * @return 'true' when process was well done executed, 'false' in case of execution errors
     */
    protected static boolean isConsoleCmdExecuted(final String[] consoleCmd) {
        if ( consoleCmd.length <= 0 ) {
            // no command to execute
            return false;
        }
        //
        boolean cmdResult = true;
        //
        try {
            Runtime.getRuntime().exec(consoleCmd).waitFor();
        } catch ( IOException | InterruptedException ex ) {
            cmdResult = false;
        }
        //
        return cmdResult;
    }
    
    /**
     * @return 'true' for successful switch of console to awaiting mode
     */
    protected static boolean isSttyRaw() {
        String[] cmdarrRaw = new String[] {"/bin/sh", "-c", "stty raw -echo < /dev/tty"};
        return UtilityMethods.isConsoleCmdExecuted(cmdarrRaw);
    }
    /**
     * @return 'true' for successful switch of console to regular mode
     */
    protected static boolean isSttyCoocked() {
        String[] cmdarrCoocked = new String[] {"/bin/sh", "-c", "stty cooked echo < /dev/tty"};
        return UtilityMethods.isConsoleCmdExecuted(cmdarrCoocked);
    }
    
    // end console cursor position //
    
    
    /**
     * Get current max X and Y coordinates of any char in console window.
     * If maximum axis X is '10' than eleven characters can be place
     * from '0' to '10'.
     * @return (maxX, maxY) in 'ConCord'
     */
    public static ConCord getTerminalMaxCoord() {
        ConUt conTool = new ConUt();
        conTool.sendGoto(CONSOLE_MAX_SIZE);
        ConCord consoleMaxCoord = UtilityMethods.getCursorPosition();
        //
        return consoleMaxCoord;
    }
    
    /**
     * Calculate current width and height of console window.
     * In character's positions, e.g. width '10' means terminal
     * has 10 characters in axis X, coordinates on X are 0..9.
     * @return (width, height) in 'ConCord'
     */
    public static ConCord getTerminalSize() {
        ConCord consoleMaxCoord = UtilityMethods.getTerminalMaxCoord();
        //
        // need to shift becouse position is calculated in coordinates
        int width = consoleMaxCoord.getX() + ConCord.SHIFT_X;
        int height = consoleMaxCoord.getY() + ConCord.SHIFT_Y;
        //
        return new ConCord(width, height);
    }
    
    
    
}
