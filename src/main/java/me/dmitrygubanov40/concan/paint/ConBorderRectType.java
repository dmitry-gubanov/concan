package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

/**
 * All possible types of border are collected here.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum ConBorderRectType
{
    
    NONE    ("none"),
    SPACE   ("space"),
    SINGLE  ("single"),
    DOUBLE  ("double"),
    BOLD    ("bold");
    
    //////////////////
    
    // NONE-option can be used for 'no-border' (not to draw anything at all)
    private static final Character[] NONE_BORDER_LINES;
    private static final Character[] SPACE_BORDER_LINES;
    private static final Character[] SINGLE_BORDER_LINES;
    private static final Character[] DOUBLE_BORDER_LINES;
    private static final Character[] BOLD_BORDER_LINES;
    
    private static final ArrayList< Character[] > BORDER_LINES;
    
    static {
        /**
         * 0 - top left char
         * 1 - top right char
         * 2 - bottom left char
         * 3 - bottom right char
         * 4 - horizontal top line char
         * 5 - horizontal bottom line char
         * 6 - vertical top line char
         * 7 - vertical bottom line char
         */
        final Character NIL = Character.MIN_VALUE;// no-character no-border
        NONE_BORDER_LINES       = new Character[] { NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL };
        SPACE_BORDER_LINES      = new Character[] { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
        SINGLE_BORDER_LINES     = new Character[] { '┌', '┐', '└', '┘', '─', '─', '│', '│' };
        DOUBLE_BORDER_LINES     = new Character[] { '╔', '╗', '╚', '╝', '═', '═', '║', '║' };
        BOLD_BORDER_LINES       = new Character[] { '┏', '┓', '┗', '┛', '━', '━', '┃', '┃' };
        //
        BORDER_LINES = new ArrayList<>();
        // borders are added the same order as they appeared in static area
        BORDER_LINES.add(NONE_BORDER_LINES);
        BORDER_LINES.add(SPACE_BORDER_LINES);
        BORDER_LINES.add(SINGLE_BORDER_LINES);
        BORDER_LINES.add(DOUBLE_BORDER_LINES);
        BORDER_LINES.add(BOLD_BORDER_LINES);
    }
    
    
    /////////////////////////
    
    
    final private String type;
    
    
    /**
     * Enum constructor.
     * @param initType one of possible types (styles) of border
     */
    ConBorderRectType(final String initType) {
        this.type = initType;
    }
    
    
    
    /**
     * Extract necessary symbol, basing on its type.
     * @param borderRectLineType type of the border
     * @return necessary character for the border
     */
    public Character getBorderSymbol(ConBorderRectLineType borderRectLineType) {
        Character[] outBorderSymbols = ConBorderRectType.BORDER_LINES.get( this.ordinal() );
        Character outBorderChar = outBorderSymbols[ borderRectLineType.ordinal() ];
        //
        return outBorderChar;
    }
    
    
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        String str = className + ": " + this.type;
        return str;
    }
    
    
    
}
