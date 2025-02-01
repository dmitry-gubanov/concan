package me.dmitrygubanov40.concan.utility;


/**
 * All possible 'style' Esc-sequences with opposition of some kind.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum ConStyles
{
    
    CURSOR_BLINKING_ON  (ConUt.CURSOR_BLINKING_ON,  "CURSOR_BLINKING_ON",   "CURSOR_BLINKING_OFF"),
    CURSOR_BLINKING_OFF (ConUt.CURSOR_BLINKING_OFF, "CURSOR_BLINKING_OFF",  "CURSOR_BLINKING_ON"),
    //
    CURSOR_ON       (ConUt.CURSOR_ON,       "CURSOR_ON",        "CURSOR_OFF"),
    CURSOR_OFF      (ConUt.CURSOR_OFF,      "CURSOR_OFF",       "CURSOR_ON"),
    //
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
    INVERSE         (ConUt.INVERSE,         "INVERSE",          "INVERSE_OFF"),
    INVERSE_OFF     (ConUt.INVERSE_OFF,     "INVERSE_OFF",      "INVERSE"),
    HIDDEN          (ConUt.HIDDEN,          "HIDDEN",           "HIDDEN_OFF"),
    HIDDEN_OFF      (ConUt.HIDDEN_OFF,      "HIDDEN_OFF",       "HIDDEN"),
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
    ConStyles(final String initStyleCmd,
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
    public ConStyles getOppositeStyleOrNull() {
        final String oppStyleName = this.styleOppositeName;
        final int ord = this.ordinal();
        final ConStyles[] allEnums = ConStyles.values();
        final int minOrd = 0;
        final int maxOrd = allEnums.length - 1;
        //
        // rapid search - among the nearest indexes:
        // (suppose opposition is somwhere here)
        final int rapidStartIndex = (minOrd == ord) ? ord : (ord - 1);
        final int rapidEndIndex = (maxOrd == ord) ? maxOrd : (ord + 1);
        for ( int i = rapidStartIndex; i <= rapidEndIndex; i++) {
            if ( oppStyleName.equals(allEnums[ i ].getStyleName()) ) {
                //
                return allEnums[ i ];
                //
            }
        }
        //
        // something had happened - total search loop (takes longer):
        for ( ConStyles currentEnum : allEnums) {
            if ( oppStyleName.equals(currentEnum.getStyleName()) ) {
                //
                return currentEnum;
                //
            }
        }
        //
        // no match
        return null;
    }
    
    
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        String str = className + ": " + this.styleName;
        return str;
    }
    
    
    
}
