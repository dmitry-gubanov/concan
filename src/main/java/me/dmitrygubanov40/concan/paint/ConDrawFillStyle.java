package me.dmitrygubanov40.concan.paint;

import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * All possible 'style' escape sequences for 'ConDrawFill'-class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum ConDrawFillStyle
{
    
    BOLD            (ConUt.BOLD,            "BOLD",             "BOLD_OFF"),
    BOLD_OFF        (ConUt.BOLD_OFF,        "BOLD_OFF",         "BOLD"),
    DIM             (ConUt.DIM,             "DIM",              "DIM_OFF"),
    DIM_OFF         (ConUt.DIM_OFF,         "DIM_OFF",          "DIM"),
    ITALIC          (ConUt.ITALIC,          "ITALIC",           "ITALIC_OFF"),
    ITALIC_OFF      (ConUt.ITALIC_OFF,      "ITALIC_OFF",       "ITALIC"),
    UNDERLINE       (ConUt.UNDERLINE,       "UNDERLINE",        "UNDERLINE_OFF"),
    UNDERLINE_OFF   (ConUt.UNDERLINE_OFF,   "UNDERLINE_OFF",    "UNDERLINE"),
    BLINK           (ConUt.BLINK,           "BLINK",            "BLINK_OFF"),
    BLINK_OFF       (ConUt.BLINK_OFF,       "BLINK_OFF",        "BLINK"),
    STRIKE          (ConUt.STRIKE,          "STRIKE",           "STRIKE_OFF"),
    STRIKE_OFF      (ConUt.STRIKE_OFF,      "STRIKE_OFF",       "STRIKE");
    
    ////////////////////////////
    
    private final String styleCmd;
    private final String styleName;
    private final String styleOppositeName;
    
    ////////////////////////////
    
    /**
     * @param initStyleCmd ESC-sequence of the style
     * @param initStyleName text name of style
     * @param initStyleOpposite opposite style (bold <-> bold_off)
     */
    ConDrawFillStyle(final String initStyleCmd,
                        final String initStyleName,
                        final String initStyleOppositeName) {
        this.styleCmd           = initStyleCmd;
        this.styleName          = initStyleName;
        this.styleOppositeName  = initStyleOppositeName;
    }
    
    
    
    /**
     * @return appropriate ESC-sequence (command) for style
     */
    public String getStyleCmd() {
        return this.styleCmd;
    }
    
    /**
     * @return appropriate ESC-sequence (command) for style
     */
    public String getStyleName() {
        return this.styleName;
    }
    
    /**
     * @return our enum object we consider to be the "opposite" the current
     */
    public ConDrawFillStyle getOppositeStyleOrNull() {
        ConDrawFillStyle[] allEnums = ConDrawFillStyle.class.getEnumConstants();
        for ( ConDrawFillStyle currentEnum : allEnums) {
            if ( this.styleOppositeName.equals(currentEnum.getStyleName()) ) {
                //
                return currentEnum;
                //
            }
        }
        return null;
    }
    
    
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        String str = className + ": " + this.styleName;
        return str;
    }
    
    
    
}
