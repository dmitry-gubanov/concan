package me.dmitrygubanov40.concan.utility;

import java.awt.Color;



/**
 * Extension over engine and ASCII classes with cover methods for Esc-sequences.
 * Has strong connection to 'escCommands' from engine-class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class UtilityEscCommands extends UtilityAscii
{
    
    // List of argumentless (simple) escape commands (sequences):
    public static final String HOME;
    public static final String CURSOR_ON;
    public static final String CURSOR_OFF;
    public static final String CURSOR_BLINKING_ON;
    public static final String CURSOR_BLINKING_OFF;
    public static final String CURSOR_REPORT;
    //
    public static final String ERASE_ALL_AFTER;
    public static final String ERASE_ALL_BEFORE;
    public static final String ERASE_ALL;
    public static final String ERASE_LINE_AFTER;
    public static final String ERASE_LINE_BEFORE;
    public static final String ERASE_LINE;
    //
    public static final String RESET;
    public static final String SAVE;
    public static final String RESTORE;
    //
    public static final String BOLD;
    public static final String BOLD_OFF;
    public static final String DIM;
    public static final String DIM_OFF;
    public static final String ITALIC;
    public static final String ITALIC_OFF;
    public static final String UNDERLINE;
    public static final String UNDERLINE_OFF;
    public static final String BLINK;
    public static final String BLINK_OFF;
    public static final String INVERSE;
    public static final String INVERSE_OFF;
    public static final String HIDDEN;
    public static final String HIDDEN_OFF;
    public static final String STRIKE;
    public static final String STRIKE_OFF;
    //
    public static final String COLOR_DEFAULT;
    public static final String BACKGROUND_DEFAULT;
    
    
    static {
        HOME                = UtilityEngine.getSimpleEscCmd("HOME");
        CURSOR_ON           = UtilityEngine.getSimpleEscCmd("CURSOR_ON");
        CURSOR_OFF          = UtilityEngine.getSimpleEscCmd("CURSOR_OFF");
        CURSOR_BLINKING_ON  = UtilityEngine.getSimpleEscCmd("CURSOR_BLINKING_ON");
        CURSOR_BLINKING_OFF = UtilityEngine.getSimpleEscCmd("CURSOR_BLINKING_OFF");
        CURSOR_REPORT       = UtilityEngine.getSimpleEscCmd("CURSOR_REPORT");
        //
        ERASE_ALL_AFTER     = UtilityEngine.getSimpleEscCmd("ERASE_ALL_AFTER");
        ERASE_ALL_BEFORE    = UtilityEngine.getSimpleEscCmd("ERASE_ALL_BEFORE");
        ERASE_ALL           = UtilityEngine.getSimpleEscCmd("ERASE_ALL");
        ERASE_LINE_AFTER    = UtilityEngine.getSimpleEscCmd("ERASE_LINE_AFTER");
        ERASE_LINE_BEFORE   = UtilityEngine.getSimpleEscCmd("ERASE_LINE_BEFORE");
        ERASE_LINE          = UtilityEngine.getSimpleEscCmd("ERASE_LINE");
        //
        RESET               = UtilityEngine.getSimpleEscCmd("RESET");
        SAVE                = UtilityEngine.getSimpleEscCmd("SAVE");
        RESTORE             = UtilityEngine.getSimpleEscCmd("RESTORE");
        //
        BOLD                = UtilityEngine.getSimpleEscCmd("BOLD");
        BOLD_OFF            = UtilityEngine.getSimpleEscCmd("BOLD_OFF");
        DIM                 = UtilityEngine.getSimpleEscCmd("DIM");
        DIM_OFF             = UtilityEngine.getSimpleEscCmd("DIM_OFF");
        ITALIC              = UtilityEngine.getSimpleEscCmd("ITALIC");
        ITALIC_OFF          = UtilityEngine.getSimpleEscCmd("ITALIC_OFF");
        UNDERLINE           = UtilityEngine.getSimpleEscCmd("UNDERLINE");
        UNDERLINE_OFF       = UtilityEngine.getSimpleEscCmd("UNDERLINE_OFF");
        BLINK               = UtilityEngine.getSimpleEscCmd("BLINK");
        BLINK_OFF           = UtilityEngine.getSimpleEscCmd("BLINK_OFF");
        INVERSE             = UtilityEngine.getSimpleEscCmd("INVERSE");
        INVERSE_OFF         = UtilityEngine.getSimpleEscCmd("INVERSE_OFF");
        HIDDEN              = UtilityEngine.getSimpleEscCmd("HIDDEN");
        HIDDEN_OFF          = UtilityEngine.getSimpleEscCmd("HIDDEN_OFF");
        STRIKE              = UtilityEngine.getSimpleEscCmd("STRIKE");
        STRIKE_OFF          = UtilityEngine.getSimpleEscCmd("STRIKE_OFF");
        //
        COLOR_DEFAULT       = UtilityEngine.getSimpleEscCmd("COLOR_DEFAULT");
        BACKGROUND_DEFAULT  = UtilityEngine.getSimpleEscCmd("BACKGROUND_DEFAULT");
    }
    
    
    /**
     * Checks math coordinates (console coordinates), not real console positions
     * @param coordinate coordinate to check
     * @param maxCoord maximum value we can allow
     * @throws IllegalArgumentException for illegal console position to move
     */
    private static void checkCord(final int coordinate, final int maxCoord)
                            throws IllegalArgumentException {
        if ( coordinate < 0 || coordinate > maxCoord ) {
            String excMsg = "Illegal console coordinate: " + coordinate
                            + ". Must be in range: [0, " + maxCoord + "]";
            throw new IllegalArgumentException(excMsg);
        }
    }
    private static void checkCordX(final int coordinate) {
        final int maxX = Term.get().maxX();
        UtilityEscCommands.checkCord(coordinate, maxX);
    }
    private static void checkCordY(final int coordinate) {
        final int maxY = Term.get().maxY();
        UtilityEscCommands.checkCord(coordinate, maxY);
    }
    
    
    
    /**
     * Get string with ready command to move to (X, Y)-coordinates of console.
     * Start point is [0, 0] but console consider [1, 1]. So, we need the Shift.
     * If coordinates are over terminal's window size, zero or maximum will be used.
     * @param columnNmb X-coordinate
     * @param lineNmb Y-coordinate
     * @return string with ready goto-command
     */
    public static String GOTO(final Integer columnNmb, final Integer lineNmb) {
        UtilityEscCommands.checkCordX(columnNmb);
        UtilityEscCommands.checkCordY(lineNmb);
        //
        // switch coordinates to have X-line at first
        return UtilityEscCommands.getEscCmd("GOTO", lineNmb + ConCord.SHIFT_Y, columnNmb + ConCord.SHIFT_X);
    }
    public static String GOTO(final ConCord position) {
        return UtilityEscCommands.GOTO(position.getX(), position.getY());
    }
    
    /**
     * Move cursor up for some number of lines (Y) up or down.
     * X position is kept.
     * Console must have enough empty lines, or cursor will move only available ones:
     * the first line for upper, the last line for lower.
     * @param lineNmb Y-step for movement
     * @return string with ready UP/DOWN command
     */
    public static String UP(final Integer lineNmb) {
        UtilityEscCommands.checkCordY(lineNmb);
        //
        return UtilityEscCommands.getEscCmd("UP", lineNmb);
    }
    public static String UP() {
        return UtilityEscCommands.UP(1);
    }
    public static String DOWN(final Integer lineNmb) {
        UtilityEscCommands.checkCordY(lineNmb);
        //
        return UtilityEscCommands.getEscCmd("DOWN", lineNmb);
    }
    public static String DOWN() {
        return UtilityEscCommands.DOWN(1);
    }
    
    /**
     * Move cursor up for some number of columns (X) left or right.
     * Y position is kept.
     * Console must have enough empty characters, or cursor will move only available ones.
     * @param columns X-step for movement
     * @return string with ready RIGHT/LEFT command
     */
    public static String RIGHT(final Integer columns) {
        UtilityEscCommands.checkCordX(columns);
        //
        return UtilityEscCommands.getEscCmd("RIGHT", columns);
    }
    public static String RIGHT() {
        return UtilityEscCommands.RIGHT(1);
    }
    public static String LEFT(final Integer columns) {
        UtilityEscCommands.checkCordX(columns);
        //
        return UtilityEscCommands.getEscCmd("LEFT", columns);
    }
    public static String LEFT() {
        return UtilityEscCommands.LEFT(1);
    }
    
     /**
      * Move cursor to the beginning of next or previous line.
      * X-coordinate will become '0' (at the beginning of the line),
      * Y-coordinate steps +/- number of 'lines'.
      * Console must have enough lines, or cursor will move only for available ones.
      * @param lines Y-step for movement
      * @return string with ready NEXT/PREVIOUS command
      */
    public static String NEXT(final Integer lines) {
        UtilityEscCommands.checkCordY(lines);
        //
        return UtilityEscCommands.getEscCmd("NEXT", lines);
    }
    public static String NEXT() {
        return UtilityEscCommands.NEXT(1);
    }
    public static String PREVIOUS(final Integer lines) {
        UtilityEscCommands.checkCordY(lines);
        //
        return UtilityEscCommands.getEscCmd("PREVIOUS", lines);
    }
    public static String PREVIOUS() {
        return UtilityEscCommands.PREVIOUS(1);
    }
    
     /**
      * Move cursor to the exact column (X).
      * Y position is kept.
      * Console must have enough width, or command will move cursor to the max possible coordinate.
      * Do no movement for column less than zero.
      * Start point is [0, 0] but console consider [1, 1]. So, we need the Shift.
      * @param column character position number in the current line we want to install cursor for
      * @return string with ready COLUMN command
      */
    public static String COLUMN(final Integer column) {
        UtilityEscCommands.checkCordX(column);
        //
        return UtilityEscCommands.getEscCmd("COLUMN", column + ConCord.SHIFT_X);
    }
    
    
    
    /**
     * Get string with ready command to install RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     * @return string with ready command with the color code
     */
    public static String COLOR(final Color color) {
        return UtilityEscCommands.getEscCmd("COLOR", color.getRed(), color.getGreen(), color.getBlue());
    }
    public static String COLOR(final ConCol color) {
        return UtilityEscCommands.COLOR(color.getTrueColor());
    }
    
    /**
     * Get string with ready command to install background RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     * @return string with ready command with the background code
     */
    public static String BACKGROUND(final Color color) {
        return UtilityEscCommands.getEscCmd("BACKGROUND", color.getRed(), color.getGreen(), color.getBlue());
    }
    public static String BACKGROUND(final ConCol color) {
        return UtilityEscCommands.BACKGROUND(color.getTrueColor());
    }
    
    
    /**
     * Get string with ready command to install color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     * @return string with ready command with the color code
     */
    public static String COLOR_8B(final ConCol color) {
        return UtilityEscCommands.getEscCmd("COLOR_8B", color.getColorCode());
    }
    /**
     * Get string with ready command to install background color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     * @return string with ready command with the background code
     */
    public static String BACKGROUND_8B(final ConCol color) {
        return UtilityEscCommands.getEscCmd("BACKGROUND_8B", color.getColorCode());
    }
    
    
    /**
     * Get string with ready command to install color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     * @throws IllegalArgumentException if color is out of 16 base colors range
     * @return string with ready command with the color code
     */
    public static String COLOR_4B(final ConCol color) {
        return UtilityEscCommands.getEscCmd("COLOR_4B", color.getColorCodeVGA());
    }
    /**
     * Get string with ready command to install background color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     * @return string with ready command with the background code
     */
    public static String BACKGROUND_4B(final ConCol color) {
        return UtilityEscCommands.getEscCmd("BACKGROUND_4B", color.getBackgroundCodeVGA());
    }
    
    
    ///////////////////////
    
    
    /**
     * Move to (0, 0) point of console.
     * (console consider this position as [1, 1])
     */
    public void sendHome() {
        this.sendEscCmd(UtilityEscCommands.HOME);
    }
    
    /**
     * Move to (X, Y)-coordinates of console.
     * Start point is [0, 0] but console consider [1, 1]. So, we need the Shift.
     * @param columnNmb X-coordinate
     * @param lineNmb Y-coordinate
     */
    public void sendGoto(final Integer columnNmb, final Integer lineNmb) {
        final String cmd = UtilityEscCommands.GOTO(columnNmb, lineNmb);
        this.sendEscCmd(cmd);
    }
    public void sendGoto(final ConCord position) {
        final String cmd = UtilityEscCommands.GOTO(position);
        this.sendEscCmd(cmd);
    }
    
    /**
     * Move cursor up for some number of lines (Y).
     * X position is kept.
     * @param lines how many lines we will step up
     */
    public void sendUp(final Integer lines) {
        final String cmd = UtilityEscCommands.UP(lines);
        this.sendEscCmd(cmd);
    }
    public void sendUp() {
        final String cmd = UtilityEscCommands.UP();
        this.sendEscCmd(cmd);
    }
    
    /**
     * Move cursor down for some number of lines (Y).
     * X position is kept.
     * @param lines how many lines we will step down
     */
    public void sendDown(final Integer lines) {
        final String cmd = UtilityEscCommands.DOWN(lines);
        this.sendEscCmd(cmd);
    }
    public void sendDown() {
        final String cmd = UtilityEscCommands.DOWN();
        this.sendEscCmd(cmd);
    }
    
    /**
     * Move cursor to the right for some char spaces (X).
     * Y position is kept.
     * Console must have enough empty positions, or cursor will move only available ones.
     * @param columns how many character positions we will step right
     */
    public void sendRight(final Integer columns) {
        final String cmd = UtilityEscCommands.RIGHT(columns);
        this.sendEscCmd(cmd);
    }
    public void sendRight() {
        this.sendRight(1);
    }
    
    /**
     * Move cursor to the left for some char spaces (X).
     * Y position is kept.
     * Console must have enough empty positions, or cursor will move only available ones.
     * @param columns how many character positions we will step left
     */
    public void sendLeft(final Integer columns) {
        final String cmd = UtilityEscCommands.LEFT(columns);
        this.sendEscCmd(cmd);
    }
    public void sendLeft() {
        this.sendLeft(1);
    }
    
    /**
     * Move cursor to the beginning of some next line down (X=0, Y plus 'lines').
     * Console must have enough lines, or cursor will move down only for available ones.
     * @param lines how many lines we will go down ('0' works the same way as '1')
     */
    public void sendNextLine(final Integer lines) {
        final String cmd = UtilityEscCommands.NEXT(lines);
        this.sendEscCmd(cmd);
    }
    public void sendNextLine() {
        this.sendNextLine(1);
    }
    
    /**
     * Move cursor to the beginning of some previous line up (X=0, Y minus 'lines').
     * Console must have enough lines, or cursor will move up only for available ones.
     * @param lines how many lines we will go up ('0' works the same way as '1')
     */
    public void sendPreviousLine(final Integer lines) {
        final String cmd = UtilityEscCommands.PREVIOUS(lines);
        this.sendEscCmd(cmd);
    }
    public void sendPreviousLine() {
        this.sendPreviousLine(1);
    }
    
    /**
     * Move cursor to the exact column (X).
     * Y position is kept.
     * Console must have enough width, or command will move cursor to the max possible coordinate.
     * Do no movement for column less than zero.
     * @param column character position number in the current line we want to install cursor for
     */
    public void sendColumn(final Integer column) {
        final String cmd = UtilityEscCommands.COLUMN(column);
        this.sendEscCmd(cmd);
    }
    
    /**
     * Console cursor mode on/off (visibility).
     */
    public void sendCursorOn() {
        this.sendEscCmd(UtilityEscCommands.CURSOR_ON);
    }
    public void sendCursorOff() {
        this.sendEscCmd(UtilityEscCommands.CURSOR_OFF);
    }
    
    /**
     * Console cursor blinking mode on/off.
     */
    public void sendCursorBlinkingOn() {
        this.sendEscCmd(UtilityEscCommands.CURSOR_BLINKING_ON);
    }
    public void sendCursorBlinkingOff() {
        this.sendEscCmd(UtilityEscCommands.CURSOR_BLINKING_OFF);
    }
    
    /**
     * Get console report of cursor position.
     * Something like '^[[32;5R' on display (as sequence of keys pressed).
     */
    public void sendCursorReport() {
        this.sendEscCmd(UtilityEscCommands.CURSOR_REPORT);
    }
    
    
    
    /**
     * Clear all from cursor to the end of console.
     * Cursor position is unchanged.
     */
    public void sendEraseAllAfter() {
        this.sendEscCmd(UtilityEscCommands.ERASE_ALL_AFTER);
    }
    
    /**
     * Clear all from cursor to the beginning of console.
     * Cursor position is unchanged.
     */
    public void sendEraseAllBefore() {
        this.sendEscCmd(UtilityEscCommands.ERASE_ALL_BEFORE);
    }
    
    /**
     * Clear all the console.
     * Cursor position is unchanged.
     */
    public void sendEraseAll() {
        this.sendEscCmd(UtilityEscCommands.ERASE_ALL);
    }
    
    /**
     * Clear all from cursor to the end of the line.
     * Cursor position is unchanged.
     */
    public void sendEraseLineAfter() {
        this.sendEscCmd(UtilityEscCommands.ERASE_LINE_AFTER);
    }
    
    /**
     * Clear all from cursor to the beginning of the line.
     * Cursor position is unchanged.
     */
    public void sendEraseLineBefore() {
        this.sendEscCmd(UtilityEscCommands.ERASE_LINE_BEFORE);
    }
    
    /**
     * Clear all the line where cursor is.
     * Cursor position is unchanged.
     */
    public void sendEraseLine() {
        this.sendEscCmd(UtilityEscCommands.ERASE_LINE);
    }
    
    
    
    /**
     * Reset all modes (styles and colors).
     */
    public void sendReset() {
        this.sendEscCmd(UtilityEscCommands.RESET);
    }
    
    /**
     * Save in terminal conditions current cursor position
     * and all style settings.
     */
    public void sendSave() {
        this.sendEscCmd(UtilityEscCommands.SAVE);
    }
    
    /**
     * Restore from terminal conditions earlier saved cursor position
     * and all style settings.
     */
    public void sendRestore() {
        this.sendEscCmd(UtilityEscCommands.RESTORE);
    }
    
    
    
    /**
     * Set bold mode on/off.
     * Off-mode also blocks dim-mode.
     */
    public void sendStyleBold() {
        this.sendEscCmd(UtilityEscCommands.BOLD);
    }
    public void sendStyleBoldOff() {
        this.sendEscCmd(UtilityEscCommands.BOLD_OFF);
    }
    
    /**
     * Set dim mode on/off.
     * Off-mode also blocks bold-mode.
     */
    public void sendStyleDim() {
        this.sendEscCmd(UtilityEscCommands.DIM);
    }
    public void sendStyleDimOff() {
        this.sendEscCmd(UtilityEscCommands.DIM_OFF);
    }
    
    /**
     * Set italic mode on/off.
     */
    public void sendStyleItalic() {
        this.sendEscCmd(UtilityEscCommands.ITALIC);
    }
    public void sendStyleItalicOff() {
        this.sendEscCmd(UtilityEscCommands.ITALIC_OFF);
    }
    
    /**
     * Set underline mode on/off.
     */
    public void sendStyleUnderline() {
        this.sendEscCmd(UtilityEscCommands.UNDERLINE);
    }
    public void sendStyleUnderlineOff() {
        this.sendEscCmd(UtilityEscCommands.UNDERLINE_OFF);
    }
    
    /**
     * Set blinking mode on/off.
     */
    public void sendStyleBlink() {
        this.sendEscCmd(UtilityEscCommands.BLINK);
    }
    public void sendStyleBlinkOff() {
        this.sendEscCmd(UtilityEscCommands.BLINK_OFF);
    }
    
    /**
     * Set inverse mode on/off.
     */
    public void sendStyleInverse() {
        this.sendEscCmd(UtilityEscCommands.INVERSE);
    }
    public void sendStyleInverseOff() {
        this.sendEscCmd(UtilityEscCommands.INVERSE_OFF);
    }
    
    /**
     * Set hidden mode on/off.
     */
    public void sendStyleHidden() {
        this.sendEscCmd(UtilityEscCommands.HIDDEN);
    }
    public void sendStyleHiddenOff() {
        this.sendEscCmd(UtilityEscCommands.HIDDEN_OFF);
    }
    
    /**
     * Set strikethrough mode on/off.
     */
    public void sendStyleStrike() {
        this.sendEscCmd(UtilityEscCommands.STRIKE);
    }
    public void sendStyleStrikeOff() {
        this.sendEscCmd(UtilityEscCommands.STRIKE_OFF);
    }
    
    
    
    /**
     * Make default color of characters.
     */
    public void sendColorDefault() {
        this.sendEscCmd(UtilityEscCommands.COLOR_DEFAULT);
    }
    /**
     * Make default background color in console.
     */
    public void sendBackgroundDefault() {
        this.sendEscCmd(UtilityEscCommands.BACKGROUND_DEFAULT);
    }
    
    /**
     * Install RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     */
    public void sendColor(final Color color) {
        final String cmd = UtilityEscCommands.COLOR(color);
        this.sendEscCmd(cmd);
    }
    public void sendColor(final ConCol color) {
        this.sendColor(color.getTrueColor());
    }
    /**
     * Install background RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     */
    public void sendBackground(final Color color) {
        final String cmd = UtilityEscCommands.BACKGROUND(color);
        this.sendEscCmd(cmd);
    }
    public void sendBackground(final ConCol color) {
        this.sendBackground(color.getTrueColor());
    }
    
    /**
     * Install color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     */
    public void sendColor8B(final ConCol color) {
        final String cmd = UtilityEscCommands.COLOR_8B(color);
        this.sendEscCmd(cmd);
    }
    /**
     * Install background color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     */
    public void sendBackground8B(final ConCol color) {
        final String cmd = UtilityEscCommands.BACKGROUND_8B(color);
        this.sendEscCmd(cmd);
    }
    
    /**
     * Install color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     * @throws IllegalArgumentException if color is out of 16 base colors range
     */
    public void sendColor4B(final ConCol color) {
        final String cmd = UtilityEscCommands.COLOR_4B(color);
        this.sendEscCmd(cmd);
    }
    /**
     * Install background color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     */
    public void sendBackground4B(final ConCol color) {
        final String cmd = UtilityEscCommands.BACKGROUND_4B(color);
        this.sendEscCmd(cmd);
    }
    
    
    
}
