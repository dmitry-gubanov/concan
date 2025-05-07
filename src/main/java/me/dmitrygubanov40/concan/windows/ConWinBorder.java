package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.paint.ConBorderRectType;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Border aggregator for final window itself.
 * Just a structure for comfortable usage.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinBorder
{
    
    // "Width" in characters of border
    public static final int BORDER_NUMBER_OF_CHARS;
    
    private static final int INIT_ILLEGAL_BORDER_WIDTH;
    private static final int MIN_BORDER_WIDTH;
    private static final int MAX_BORDER_WIDTH;
    
    private static final int DEFAULT_WIDTH_TOP;
    private static final int DEFAULT_WIDTH_RIGHT;
    private static final int DEFAULT_WIDTH_BOTTOM;
    private static final int DEFAULT_WIDTH_LEFT;
    
    private static final ConBorderRectType DEFAULT_BORDER_TYPE;
    private static final ConDrawFill DEFAULT_BORDER_FILLING;
    private static final ConDrawFill DEFAULT_MARGIN_FILLING;
    
    static {
        BORDER_NUMBER_OF_CHARS = 1;
        //
        INIT_ILLEGAL_BORDER_WIDTH = -1;
        MIN_BORDER_WIDTH = 0;
        MAX_BORDER_WIDTH = 250;
        //
        DEFAULT_WIDTH_TOP = 0;
        DEFAULT_WIDTH_RIGHT = 0;
        DEFAULT_WIDTH_BOTTOM = 0;
        DEFAULT_WIDTH_LEFT = 0;
        //
        DEFAULT_BORDER_TYPE = ConBorderRectType.NONE;
        DEFAULT_BORDER_FILLING = new ConDrawFill(Term.EMPTY_CHAR);
        DEFAULT_MARGIN_FILLING = new ConDrawFill(Term.EMPTY_CHAR);
    }
    
    
    //////////////
    
    
    // border type to draw (if width of each line is at least one)
    // is used by rectangle
    private ConBorderRectType type;
    
    // brush and filling for our border
    private ConDrawFill filling;
    
    // brush and filling for our margin area
    // Imporntant! Is the same as 'filling' if was not given specially.
    private ConDrawFill marginFilling;
    
    // all window border widths
    // Important: border char is always one, but border width can be used
    // to make window output zone less
    private int leftWidth;
    private int topWidth;
    private int rightWidth;
    private int bottomWidth;
    
    //////////////
    
    private ConWinBorder() {
        this.type = null;
        this.filling = null;
        this.marginFilling = null;
        //
        this.topWidth       = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.rightWidth     = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.bottomWidth    = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.leftWidth      = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
    }
    
    
    
    /**
     * Checks width of all sides and apply it.
     * @param setTop border width of the top
     * @param setRight border width of the right side
     * @param setBottom border width of the bottom
     * @param setLeft border width of the left side
     * @throws IllegalArgumentException when width is incorrect
     */
    private void setBorderWidth(final int setLeft, final int setTop,
                                    final int setRight, final int setBottom)
                    throws IllegalArgumentException {
        //
        final int[] widths = {setLeft, setTop, setRight, setBottom};
        //
        for ( int curBorderWidth : widths ) {
            if ( curBorderWidth < ConWinBorder.MIN_BORDER_WIDTH
                    || curBorderWidth > ConWinBorder.MAX_BORDER_WIDTH ) {
                String excMsg = "One of window borders has inappropriate value: '" + curBorderWidth + "'."
                                + " Must be in range: " + ConWinBorder.MIN_BORDER_WIDTH
                                + " ... " + ConWinBorder.MAX_BORDER_WIDTH;
                throw new IllegalArgumentException(excMsg);
            }
        }
        //
        this.leftWidth      = setLeft;
        this.topWidth       = setTop;
        this.rightWidth     = setRight;
        this.bottomWidth    = setBottom;
    }
    
    
    
    /**
     * Check border and apply it.
     * @param setType border enum type
     * @throws NullPointerException when border type is incorrect
     */
    private void setBorderType(final ConBorderRectType setType)
                    throws NullPointerException {
        if ( null == setType ) {
            String excMsg = "Where is no border type to implement";
            throw new NullPointerException(excMsg);
        }
        //
        this.type = setType;
    }
    
    
    
    /**
     * Check filling settings and apply them.
     * @param setFilling border filling object (full parameters set)
     * @throws NullPointerException when filling was not given
     */
    private void setBorderFilling(final ConDrawFill setFilling)
                    throws NullPointerException {
        if ( null == setFilling ) {
            String excMsg = "Where is no border filling to implement";
            throw new NullPointerException(excMsg);
        }
        //
        this.filling = setFilling;
    }
    
    /**
     * Check filling settings for margin, and then apply them.
     * @param setMarginFilling margin filling object (full parameters set)
     * @throws NullPointerException when margin filling was not given
     */
    private void setMarginFilling(final ConDrawFill setMarginFilling)
                    throws NullPointerException {
        if ( null == setMarginFilling ) {
            String excMsg = "Where is no margin filling to implement";
            throw new NullPointerException(excMsg);
        }
        //
        this.marginFilling = setMarginFilling;
    }
    
    
    
    // block of getters:
    
    public ConBorderRectType getType() {
        return this.type;
    }
    
    public ConDrawFill getFilling() {
        return this.filling;
    }
    public ConDrawFill getMarginFilling() {
        return this.marginFilling;
    }
    
    public int getLeftWidth() {
        return this.leftWidth;
    }
    public int getTopWidth() {
        return this.topWidth;
    }
    public int getRightWidth() {
        return this.rightWidth;
    }
    public int getBottomWidth() {
        return this.bottomWidth;
    }
    
    /**
     * If we have at least one char of width - we can draw a border.
     * In other cases border can be presented, installed, but is invisible
     * (cannot be drawn).
     * @return 'true' when have enough space for drawing a border
     */
    public boolean canSeeBorder() {
        boolean result = false;
        //
        if ( this.getLeftWidth() > 0 && this.getRightWidth() > 0
                && this.getTopWidth() > 0 && this.getBottomWidth() > 0 ) {
            result = true;
        }
        //
        return result;
    }
    
    
    /////////////////////////////
    ///// Builder injection /////
    public static class Builder
    {
        
        private ConWinBorder container;
        
        //////////////////
        
        public Builder() {
            // only default values agregated
            this.container = new ConWinBorder();
        }
        
        //////////////////
        
        // sort of setters block
        
        /**
         * Setup via builder width size (in characters).
         * @param setLeft
         * @param setTop
         * @param setRight
         * @param setBottom
         * @return embedded builder for following methods
         */
        public Builder width(final int setLeft,
                                final int setTop,
                                final int setRight,
                                final int setBottom) {
            this.container.setBorderWidth(setLeft, setTop, setRight, setBottom);
            //
            return this;
        }
        
        /**
         * Setup via builder border type (visual style).
         * @param setType
         * @return embedded builder for following methods
         */
        public Builder type(final ConBorderRectType setType) {
            this.container.setBorderType(setType);
            //
            return this;
        }
        
        /**
         * Setup colors via builder.
         * @param setFilling
         * @return embedded builder for following methods
         */
        public Builder fill(final ConDrawFill setFilling) {
            this.container.setBorderFilling(setFilling);
            //
            return this;
        }
        
        /**
         * Setup colors for margin area via builder.
         * @param setMarginFilling
         * @return embedded builder for following methods
         */
        public Builder marginFill(final ConDrawFill setMarginFilling) {
            this.container.setMarginFilling(setMarginFilling);
            //
            return this;
        }
        
        /**
         * Check the border state before "building" it.
         * Everything can be created "by default", no extra parameters can be called.
         * @return link to the new created border
         */
        public ConWinBorder build() {
            // 1. Was the width applied?
            // If some area was not given - install 'zero'.
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.leftWidth )
                this.container.leftWidth = DEFAULT_WIDTH_LEFT;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.topWidth )
                this.container.topWidth = DEFAULT_WIDTH_TOP;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.rightWidth )
                this.container.rightWidth = DEFAULT_WIDTH_RIGHT;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.bottomWidth )
                this.container.bottomWidth = DEFAULT_WIDTH_BOTTOM;
            //
            // 2. Was the type applied?
            if ( null == this.container.type ) {
                // type was not given, so setup empty "none"-type
                this.container.setBorderType(ConWinBorder.DEFAULT_BORDER_TYPE);
            }
            //
            // 3. Was the filling applied?
            if ( null == this.container.filling ) {
                // filling from default terminal settings
                this.container.setBorderFilling(ConWinBorder.DEFAULT_BORDER_FILLING);
            }
            //
            // 4. Was the margin filling applied?
            if ( null == this.container.marginFilling ) {
                // margin filling from default terminal settings
                this.container.setMarginFilling(ConWinBorder.DEFAULT_MARGIN_FILLING);
            }
            //
            ConWinBorder result = this.container;
            this.container = new ConWinBorder();
            return result;
        }
        
        
        
    }
    ///// End of builder injection /////
    ////////////////////////////////////
    
    
}
