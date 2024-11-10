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
        RESET = UtilityEngine.getSimpleEscCmd("RESET");
        //
        BOLD            = UtilityEngine.getSimpleEscCmd("BOLD");
        BOLD_OFF        = UtilityEngine.getSimpleEscCmd("BOLD_OFF");
        DIM             = UtilityEngine.getSimpleEscCmd("DIM");
        DIM_OFF         = UtilityEngine.getSimpleEscCmd("DIM_OFF");
        ITALIC          = UtilityEngine.getSimpleEscCmd("ITALIC");
        ITALIC_OFF      = UtilityEngine.getSimpleEscCmd("ITALIC_OFF");
        UNDERLINE       = UtilityEngine.getSimpleEscCmd("UNDERLINE");
        UNDERLINE_OFF   = UtilityEngine.getSimpleEscCmd("UNDERLINE_OFF");
        BLINK           = UtilityEngine.getSimpleEscCmd("BLINK");
        BLINK_OFF       = UtilityEngine.getSimpleEscCmd("BLINK_OFF");
        INVERSE         = UtilityEngine.getSimpleEscCmd("INVERSE");
        INVERSE_OFF     = UtilityEngine.getSimpleEscCmd("INVERSE_OFF");
        HIDDEN          = UtilityEngine.getSimpleEscCmd("HIDDEN");
        HIDDEN_OFF      = UtilityEngine.getSimpleEscCmd("HIDDEN_OFF");
        STRIKE          = UtilityEngine.getSimpleEscCmd("STRIKE");
        STRIKE_OFF      = UtilityEngine.getSimpleEscCmd("STRIKE_OFF");
        //
        COLOR_DEFAULT       = UtilityEngine.getSimpleEscCmd("COLOR_DEFAULT");
        BACKGROUND_DEFAULT  = UtilityEngine.getSimpleEscCmd("BACKGROUND_DEFAULT");
    }
    
    
    /**
     * Move to (0, 0) point of console.
     */
    public void sendHome() {
        this.sendEscCmd("HOME");
    }
    
    /**
     * Move to (X, Y) point of console.
     * @param columnNmb X-coordinate
     * @param lineNmb Y-coordinate
     */
    public void sendGoto(final Integer columnNmb, final Integer lineNmb) {
        // switch coordinates to have X-line at first
        this.sendEscCmd("GOTO", lineNmb, columnNmb);
    }
    
    /**
     * Move cursor up for some lines (Y).
     * X position is kept.
     * @param lines how many lines we will step up
     */
    public void sendUp(final Integer lines) {
        this.sendEscCmd("UP", lines);
    }
    public void sendUp() {
        this.sendUp(1);
    }
    
    /**
     * Move cursor down for some lines (Y).
     * X position is kept.
     * Console must have enough empty lines, or cursor will move only available ones.
     * @param lines how many lines we will step down
     */
    public void sendDown(final Integer lines) {
        this.sendEscCmd("DOWN", lines);
    }
    public void sendDown() {
        this.sendDown(1);
    }
    
    /**
     * Move cursor to the right for some char spaces (X).
     * Y position is kept.
     * Console must have enough empty positions, or cursor will move only available ones.
     * @param columns how many character positions we will step right
     */
    public void sendRight(final Integer columns) {
        this.sendEscCmd("RIGHT", columns);
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
        this.sendEscCmd("LEFT", columns);
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
        this.sendEscCmd("NEXT", lines);
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
        this.sendEscCmd("PREVIOUS", lines);
    }
    public void sendPreviousLine() {
        this.sendPreviousLine(1);
    }
    
    /**
     * Move cursor to the exact column (X).
     * First position is '1'.
     * Y position is kept.
     * Console must have enough empty positions, 
     * @param column character position number in the current line we want to install cursor for
     */
    public void sendColumn(final Integer column) {
        this.sendEscCmd("COLUMN", column);
    }
    
    /**
     * Console cursor mode on/off (visibility).
     */
    public void sendCursorOn() {
        this.sendEscCmd("CURSOR_ON");
    }
    public void sendCursorOff() {
        this.sendEscCmd("CURSOR_OFF");
    }
    
    /**
     * Console cursor blinking mode on/off.
     */
    public void sendCursorBlinkingOn() {
        this.sendEscCmd("CURSOR_BLINKING_ON");
    }
    public void sendCursorBlinkingOff() {
        this.sendEscCmd("CURSOR_BLINKING_OFF");
    }
    
    /**
     * Get console report of cursor position.
     * Something like '^[[32;5R' on display (as sequence of keys pressed).
     */
    public void sendCursorReport() {
        this.sendEscCmd("CURSOR_REPORT");
    }
    
    
    
    /**
     * Clear all from cursor to the end of console.
     * Cursor position is unchanged.
     */
    public void sendEraseAllAfter() {
        this.sendEscCmd("ERASE_ALL_AFTER");
    }
    
    /**
     * Clear all from cursor to the beginning of console.
     * Cursor position is unchanged.
     */
    public void sendEraseAllBefore() {
        this.sendEscCmd("ERASE_ALL_BEFORE");
    }
    
    /**
     * Clear all the console.
     * Cursor position is unchanged.
     */
    public void sendEraseAll() {
        this.sendEscCmd("ERASE_ALL");
    }
    
    /**
     * Clear all from cursor to the end of the line.
     * Cursor position is unchanged.
     */
    public void sendEraseLineAfter() {
        this.sendEscCmd("ERASE_LINE_AFTER");
    }
    
    /**
     * Clear all from cursor to the beginning of the line.
     * Cursor position is unchanged.
     */
    public void sendEraseLineBefore() {
        this.sendEscCmd("ERASE_LINE_BEFORE");
    }
    
    /**
     * Clear all the line where cursor is.
     * Cursor position is unchanged.
     */
    public void sendEraseLine() {
        this.sendEscCmd("ERASE_LINE");
    }
    
    
    
    /**
     * Reset all modes (styles and colors).
     */
    public void sendReset() {
        this.sendEscCmd("RESET");
    }
    
    
    
    /**
     * Set bold mode on/off.
     * Off-mode also blocks dim-mode.
     */
    public void sendStyleBold() {
        this.sendEscCmd("BOLD");
    }
    public void sendStyleBoldOff() {
        this.sendEscCmd("BOLD_OFF");
    }
    
    /**
     * Set dim mode on/off.
     * Off-mode also blocks bold-mode.
     */
    public void sendStyleDim() {
        this.sendEscCmd("DIM");
    }
    public void sendStyleDimOff() {
        this.sendEscCmd("DIM_OFF");
    }
    
    /**
     * Set italic mode on/off.
     */
    public void sendStyleItalic() {
        this.sendEscCmd("ITALIC");
    }
    public void sendStyleItalicOff() {
        this.sendEscCmd("ITALIC_OFF");
    }
    
    /**
     * Set underline mode on/off.
     */
    public void sendStyleUnderline() {
        this.sendEscCmd("UNDERLINE");
    }
    public void sendStyleUnderlineOff() {
        this.sendEscCmd("UNDERLINE_OFF");
    }
    
    /**
     * Set blinking mode on/off.
     */
    public void sendStyleBlink() {
        this.sendEscCmd("BLINK");
    }
    public void sendStyleBlinkOff() {
        this.sendEscCmd("BLINK_OFF");
    }
    
    /**
     * Set inverse mode on/off.
     */
    public void sendStyleInverse() {
        this.sendEscCmd("INVERSE");
    }
    public void sendStyleInverseOff() {
        this.sendEscCmd("INVERSE_OFF");
    }
    
    /**
     * Set hidden mode on/off.
     */
    public void sendStyleHidden() {
        this.sendEscCmd("HIDDEN");
    }
    public void sendStyleHiddenOff() {
        this.sendEscCmd("HIDDEN_OFF");
    }
    
    /**
     * Set strikethrough mode on/off.
     */
    public void sendStyleStrike() {
        this.sendEscCmd("STRIKE");
    }
    public void sendStyleStrikeOff() {
        this.sendEscCmd("STRIKE_OFF");
    }
    
    
    
    /**
     * Make default color of characters.
     */
    public void sendColorDefault() {
        this.sendEscCmd("COLOR_DEFAULT");
    }
    /**
     * Make default background color in console.
     */
    public void sendBackgroundDefault() {
        this.sendEscCmd("BACKGROUND_DEFAULT");
    }
    
    /**
     * Install RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     */
    public void sendColor(final Color color) {
        this.sendEscCmd("COLOR", color.getRed(), color.getGreen(), color.getBlue());
    }
    public void sendColor(final ConCol color) {
        this.sendColor(color.getTrueColor());
    }
    /**
     * Install background RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     */
    public void sendBackground(final Color color) {
        this.sendEscCmd("BACKGROUND", color.getRed(), color.getGreen(), color.getBlue());
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
        this.sendEscCmd("COLOR_8B", color.getColorCode());
    }
    /**
     * Install background color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     */
    public void sendBackground8B(final ConCol color) {
        this.sendEscCmd("BACKGROUND_8B", color.getColorCode());
    }
    
    /**
     * Install color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     * @throws IllegalArgumentException if color is out of 16 base colors range
     */
    public void sendColor4B(final ConCol color) {
        this.sendEscCmd("COLOR_4B", color.getColorCodeVGA());
    }
    /**
     * Install background color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     */
    public void sendBackground4B(final ConCol color) {
        this.sendEscCmd("BACKGROUND_4B", color.getBackgroundCodeVGA());
    }
    
    
    
}
