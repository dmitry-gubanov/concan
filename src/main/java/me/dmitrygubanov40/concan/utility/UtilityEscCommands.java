package me.dmitrygubanov40.concan.utility;

import java.awt.Color;



/**
 * Extension over engine and ASCII classes with cover methods for Esc-sequences.
 * Has strong connection to 'escCommands' from engine-class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class UtilityEscCommands extends UtilityAscii
{
    
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
     * Blinking console cursor mode on/off.
     */
    public void sendCursorOn() {
        this.sendEscCmd("CURSOR_ON");
    }
    public void sendCursorOff() {
        this.sendEscCmd("CURSOR_OFF");
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
        this.sendEscCmd("STYLE_BOLD");
    }
    public void sendStyleBoldOff() {
        this.sendEscCmd("STYLE_BOLD_OFF");
    }
    
    /**
     * Set dim mode on/off.
     * Off-mode also blocks bold-mode.
     */
    public void sendStyleDim() {
        this.sendEscCmd("STYLE_DIM");
    }
    public void sendStyleDimOff() {
        this.sendEscCmd("STYLE_DIM_OFF");
    }
    
    /**
     * Set italic mode on/off.
     */
    public void sendStyleItalic() {
        this.sendEscCmd("STYLE_ITALIC");
    }
    public void sendStyleItalicOff() {
        this.sendEscCmd("STYLE_ITALIC_OFF");
    }
    
    /**
     * Set underline mode on/off.
     */
    public void sendStyleUnderline() {
        this.sendEscCmd("STYLE_UNDERLINE");
    }
    public void sendStyleUnderlineOff() {
        this.sendEscCmd("STYLE_UNDERLINE_OFF");
    }
    
    /**
     * Set blinking mode on/off.
     */
    public void sendStyleBlink() {
        this.sendEscCmd("STYLE_BLINK");
    }
    public void sendStyleBlinkOff() {
        this.sendEscCmd("STYLE_BLINK_OFF");
    }
    
    /**
     * Set inverse mode on/off.
     */
    public void sendStyleInverse() {
        this.sendEscCmd("STYLE_INVERSE");
    }
    public void sendStyleInverseOff() {
        this.sendEscCmd("STYLE_INVERSE_OFF");
    }
    
    /**
     * Set hidden mode on/off.
     */
    public void sendStyleHidden() {
        this.sendEscCmd("STYLE_HIDDEN");
    }
    public void sendStyleHiddenOff() {
        this.sendEscCmd("STYLE_HIDDEN_OFF");
    }
    
    /**
     * Set strikethrough mode on/off.
     */
    public void sendStyleStrike() {
        this.sendEscCmd("STYLE_STRIKE");
    }
    public void sendStyleStrikeOff() {
        this.sendEscCmd("STYLE_STRIKE_OFF");
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
    public void sendColor(final ConsoleColor color) {
        this.sendColor(color.getTrueColor());
    }
    /**
     * Install background RGB-color for letters, TrueColor 24b mode.
     * @param color RGB-color to install
     */
    public void sendBackground(final Color color) {
        this.sendEscCmd("BACKGROUND", color.getRed(), color.getGreen(), color.getBlue());
    }
    public void sendBackground(final ConsoleColor color) {
        this.sendBackground(color.getTrueColor());
    }
    
    /**
     * Install color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     */
    public void sendColor8B(final ConsoleColor color) {
        this.sendEscCmd("COLOR_8B", color.getColorCode());
    }
    /**
     * Install background color for letters, 8b mode.
     * Same as for TrueColor, only argument differs.
     * @param color one of premade 256 colors to install
     */
    public void sendBackground8B(final ConsoleColor color) {
        this.sendEscCmd("BACKGROUND_8B", color.getColorCode());
    }
    
    /**
     * Install color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     * @throws IllegalArgumentException if color is out of 16 base colors range
     */
    public void sendColor4B(final ConsoleColor color) {
        this.sendEscCmd("COLOR_4B", color.getColorCodeVGA());
    }
    /**
     * Install background color for letters, 4b mode (16 colors).
     * @param color one of premade 16 colors to install
     */
    public void sendBackground4B(final ConsoleColor color) {
        this.sendEscCmd("BACKGROUND_4B", color.getBackgroundCodeVGA());
    }
    
    
    
}
