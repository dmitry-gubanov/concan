package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.paint.ConBorderRectType;



/**
 * Border aggregator for final window itself.
 * Just a structure for comfortable usage.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinBorder
{
    
    private static final int INIT_ILLEGAL_BORDER_WIDTH;
    private static final int MIN_BORDER_WIDTH;
    private static final int MAX_BORDER_WIDTH;
    
    private static final int DEFAULT_WIDTH_TOP;
    private static final int DEFAULT_WIDTH_RIGHT;
    private static final int DEFAULT_WIDTH_BOTTOM;
    private static final int DEFAULT_WIDTH_LEFT;
    
    static {
        INIT_ILLEGAL_BORDER_WIDTH = -1;
        MIN_BORDER_WIDTH = 0;
        MAX_BORDER_WIDTH = 250;
        //
        DEFAULT_WIDTH_TOP = 0;
        DEFAULT_WIDTH_RIGHT = 0;
        DEFAULT_WIDTH_BOTTOM = 0;
        DEFAULT_WIDTH_LEFT = 0;
    }
    
    
    //////////////
    
    
    // border type to draw (if width of each line is at least one)
    // is used by rectangle
    private ConBorderRectType winBorderType;
    
    // all window border widths
    // Important: border char is always one, but border width can be used
    // to make window output zone less
    private int topWidth;
    private int rightWidth;
    private int bottomWidth;
    private int leftWidth;
    
    //////////////
    
    private ConWinBorder() {
        this.winBorderType = null;
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
    private void setBorderWidth(final int setTop, final int setRight,
                                    final int setBottom, final int setLeft)
                    throws IllegalArgumentException {
        //
        final int[] widths = {setTop, setRight, setBottom, setLeft};
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
        this.topWidth       = setTop;
        this.rightWidth     = setRight;
        this.bottomWidth    = setBottom;
        this.leftWidth      = setLeft;
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
        this.winBorderType = setType;
    }
    
    
    
    // block of getters:
    
    public ConBorderRectType getType() {
        return this.winBorderType;
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
    public int getLeftWidth() {
        return this.leftWidth;
    }
    
    
    /////////////////////////////
    ///// Builder injection /////
    public static class Builder
    {
        
        private final ConWinBorder container;
        
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
            this.container.setBorderWidth(setTop, setRight, setBottom, setLeft);
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
         * Check the border state before "building" it.
         * Check adequacy of the new object.
         * @return link to the new created border
         */
        public ConWinBorder build() {
            // 1. Was the width applied?
            // If some area was not given - install 'zero'.
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.topWidth )
                this.container.topWidth = DEFAULT_WIDTH_TOP;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.rightWidth )
                this.container.rightWidth = DEFAULT_WIDTH_RIGHT;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.bottomWidth )
                this.container.bottomWidth = DEFAULT_WIDTH_BOTTOM;
            if ( ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH == this.container.leftWidth )
                this.container.leftWidth = DEFAULT_WIDTH_LEFT;
            //
            // 2. Was the type applied?
            if ( null == this.container.winBorderType ) {
                // type was not given, so setup empty "none"-type
                this.container.setBorderType(ConBorderRectType.NONE);
            }
            //
            return this.container;
        }
        
        
        
    }
    ///// End of builder injection /////
    ////////////////////////////////////
    
    
}
