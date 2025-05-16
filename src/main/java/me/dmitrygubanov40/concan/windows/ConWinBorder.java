package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.paint.ConBorderRectType;
import me.dmitrygubanov40.concan.paint.ConDraw;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Border aggregator for final window itself.
 * Just a structure for comfortable usage.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinBorder
{
    
    // "Width" in characters of border
    public static final int VISUAL_NUMBER_OF_CHARS;
    
    private static final int INIT_ILLEGAL_BORDER_WIDTH;
    private static final int MIN_BORDER_WIDTH;
    private static final int MAX_BORDER_WIDTH;
    
    private static final int DEFAULT_WIDTH_TOP;
    private static final int DEFAULT_WIDTH_RIGHT;
    private static final int DEFAULT_WIDTH_BOTTOM;
    private static final int DEFAULT_WIDTH_LEFT;
    
    private static final ConBorderRectType DEFAULT_BORDER_TYPE;
    private static final ConDrawFill DEFAULT_BORDER_FILLING;
    private static final ConDrawFill DEFAULT_PADDING_FILLING;
    
    static {
        VISUAL_NUMBER_OF_CHARS = 1;
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
        DEFAULT_BORDER_FILLING = new ConDrawFill();
        DEFAULT_PADDING_FILLING = new ConDrawFill();
    }
    
    
    //////////////
    
    
    // border type to draw (if width of each line is at least one)
    // is used by rectangle
    private ConBorderRectType type;
    
    // brush and filling for our border
    private ConDrawFill filling;
    
    // brush and filling for our padding area
    // Imporntant! Is the same as 'filling' if was not given specially.
    private ConDrawFill paddingFilling;
    
    // all window border widths
    // Important: border char is always one, but border width can be used
    // to make window output zone less
    private int leftWidth;
    private int topWidth;
    private int rightWidth;
    private int bottomWidth;
    
    //////////////
    
    /**
     *  Base empty constructor.
     */
    private ConWinBorder() {
        this.type = null;
        this.filling = null;
        this.paddingFilling = null;
        //
        this.leftWidth      = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.topWidth       = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.rightWidth     = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
        this.bottomWidth    = ConWinBorder.INIT_ILLEGAL_BORDER_WIDTH;
    }
    
    /**
     * Independent copy constructor.
     * @param origBorder original set of filling parameters we will copy
     * @throws NullPointerException if there is no 'origBorder'
     */
    private ConWinBorder(final ConWinBorder origBorder) {
        if ( null == origBorder ) {
            String excMsg = "Where is no original border parameters to copy";
            throw new NullPointerException(excMsg);
        }
        //
        this.type = origBorder.getType();
        this.filling = new ConDrawFill( origBorder.getFilling() );
        this.paddingFilling = new ConDrawFill( origBorder.getPaddingFilling() );
        //
        this.leftWidth      = origBorder.getLeftWidth();
        this.topWidth       = origBorder.getTopWidth();
        this.rightWidth     = origBorder.getRightWidth();
        this.bottomWidth    = origBorder.getBottomWidth();
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
     * Check filling settings for padding, and then apply them.
     * @param setPaddingFilling padding filling object (full parameters set)
     * @throws NullPointerException when padding filling was not given
     */
    private void setPaddingFilling(final ConDrawFill setPaddingFilling)
                    throws NullPointerException {
        if ( null == setPaddingFilling ) {
            String excMsg = "Where is no padding filling to implement";
            throw new NullPointerException(excMsg);
        }
        //
        this.paddingFilling = setPaddingFilling;
    }
    
    
    
    // block of getters:
    
    public ConBorderRectType getType() {
        return this.type;
    }
    
    public ConDrawFill getFilling() {
        return this.filling;
    }
    public ConDrawFill getPaddingFilling() {
        return this.paddingFilling;
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
     * 'NONE' type of border also blocks visual border.
     * @return 'true' when have enough space for drawing a border
     */
    public boolean canSeeBorder() {
        boolean result = false;
        //
        if ( this.getLeftWidth() > 0 && this.getRightWidth() > 0
                && this.getTopWidth() > 0 && this.getBottomWidth() > 0
                && ConBorderRectType.NONE != this.getType() ) {
            result = true;
        }
        //
        return result;
    }
    
    /**
     * Draw the specific rectangle around given bar (pos, width, height).
     * @param pos start position (i.e. left top corner)
     * @param width all area (border) width
     * @param height all area (border) height
     * @throws NullPointerException if position was not given
     * @throws IllegalArgumentException if no linear size were given
     */
    public void drawBorder(final ConCord pos, final int width, final int height)
                        throws NullPointerException, IllegalArgumentException {
        if ( null == pos ) {
            String excMsg = "There is no start corner coordinates to draw the border";
            throw new NullPointerException(excMsg);
        }
        if ( width <= 0 || height <= 0 ) {
            String excMsg = "Incorrect size to draw the border";
            throw new IllegalArgumentException(excMsg);
        }
        //
        final int left = pos.getX() + this.getLeftWidth();
        final int top = pos.getY() + this.getTopWidth();
        // The calculations give correct cursor position w/o shifts.
        // We add scalar width, and after - remove scalar width.
        final int right = pos.getX() + width - this.getRightWidth();
        final int bottom = pos.getY() + height - this.getBottomWidth();
        //
        // Use 'removeConsoleShift' to scale linear size (widths) into coordinates:
        final ConCord borderLeftTop = new ConCord(left, top).removeConsoleShift();
        final ConCord borderRightBottom = new ConCord(right, bottom);
        final ConDrawFill borderFill = this.getFilling();
        //
        ConDraw painter = new ConDraw(borderFill);
        // paint the border without inner console status restoration
        painter.setTermSaveStatus(false)
                .drawBorderRect(this.getType(), borderLeftTop, borderRightBottom);
    }
    
    /**
     * Fill specific margin/padding areas in out zone (outPos, outWidth, outHeight),
     * ignoring inner bar (innerPos, innerWidth, innerHeight).
     * If visual border is set - do not fill the sides of the border.
     * If there is no real padding - fill nothing.
     * @param outPos
     * @param outWidth
     * @param outHeight
     * @param innerPos
     * @param innerWidth
     * @param innerHeight
     * @throws NullPointerException when positions for area are empty
     * @throws IllegalArgumentException in case of incorrect linear sizes
     */
    public void fillPadding(final ConCord outPos, final int outWidth, final int outHeight,
                        final ConCord innerPos, final int innerWidth, final int innerHeight)
                    throws NullPointerException, IllegalArgumentException {
        if ( null == outPos || null == innerPos ) {
            String excMsg = "There is no coordinates to fill the padding area";
            throw new NullPointerException(excMsg);
        }
        if ( outWidth < 0 || outHeight < 0 || innerWidth <= 0 || innerHeight <= 0 ) {
            String excMsg = "Incorrect size of outter or inner bar for padding filling";
            throw new IllegalArgumentException(excMsg);
        }
        //
        // once memorize potential shift of bars (in console symbols):
        final int BORDER_EXTRA = this.canSeeBorder()
                                    ? ConWinBorder.VISUAL_NUMBER_OF_CHARS
                                    : 0;
        final ConDraw painter = new ConDraw();
        painter.setCurrentFill( this.getPaddingFilling() );
        //
        // when we should start not at the end of previous area, but in the next "zone"
        final int ADD_FOR_NEW_AREA = 1;
        //
        // 1. left area:
        final ConCord leftPoint1 = outPos;
        //
        final int l2ToAddX = this.getLeftWidth() - BORDER_EXTRA;
        final int l2ToAddY = outHeight;
        ConCord leftPaddingAreaSizeToAdd = new ConCord(l2ToAddX, l2ToAddY).removeConsoleShift();
        final ConCord leftPoint2 = leftPoint1.plus(leftPaddingAreaSizeToAdd);
        //
        if ( leftPoint2.getX() >= leftPoint1.getX() ) {
            // only if really have some padding area width
            painter.drawBar(leftPoint1, leftPoint2);
        }
        //
        // 2. right area:
        final int r1x = innerPos.getX()
                        + (innerWidth - ConCord.SHIFT_X)
                        + BORDER_EXTRA
                        + ADD_FOR_NEW_AREA;// always away from output zone characters
        final int r1y = outPos.getY();
        final ConCord rightPoint1 = new ConCord(r1x, r1y);
        //
        final ConCord winRightBottomAdd = new ConCord(outWidth, outHeight).removeConsoleShift();
        // calculate simple right-bottom corner coordinates of window:
        final ConCord rightPoint2 = outPos.plus(winRightBottomAdd);
        //
        if ( rightPoint2.getX() >= rightPoint1.getX() ) {
            // when have width in right area
            painter.drawBar(rightPoint1, rightPoint2);
        }
        //
        // 3. top area:
        final int t1x = (leftPoint2.getX() >= leftPoint1.getX())
                            ? leftPoint2.getX() + ADD_FOR_NEW_AREA// left area was brushed, no overcovering
                            : outPos.getX();// no left area, cover first characters colummn
        final int t1y = outPos.getY();// just window position
        // this point will start cover strcitly out of left area boundaries
        final ConCord topPoint1 = new ConCord(t1x, t1y);
        //
        final int t2ToAddX = innerWidth + 2 * BORDER_EXTRA;// when have border - should be twice wider
        final int t2ToAddY = this.getTopWidth() - BORDER_EXTRA;
        ConCord topPaddingAreaSizeToAdd = new ConCord(t2ToAddX, t2ToAddY).removeConsoleShift();
        final ConCord topPoint2 = topPoint1.plus(topPaddingAreaSizeToAdd);
        //
        
        if ( topPoint2.getY() >= topPoint1.getY() ) {
            // when we have some real height for the area
            painter.drawBar(topPoint1, topPoint2);
        }
        //
        // 4. bottom area:
        final int b1y = innerPos.getY()
                         + innerHeight
                         + ADD_FOR_NEW_AREA
                         + BORDER_EXTRA
                         - ConCord.SHIFT_Y;
        final ConCord bottomPoint1 = new ConCord(topPoint1.getX(), b1y);
        //
        // 'X' as in top area, 'Y' as in right area:
        final ConCord bottomPoint2 = new ConCord(topPoint2.getX(), rightPoint2.getY());
        //
        if ( bottomPoint2.getY() >= bottomPoint1.getY() ) {
            // when we have some real height for the area
            painter.drawBar(bottomPoint1, bottomPoint2);
        }
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
         * Setup via builder width size (in characters) for each side.
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
         * Setup via builder width size (in characters) for all sides at once.
         * @param setWidth
         * @return embedded builder for following methods
         */
        public Builder width(final int setWidth) {
            return this.width(setWidth, setWidth, setWidth, setWidth);
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
         * Setup colors for padding area via builder.
         * @param setPaddingFilling
         * @return embedded builder for following methods
         */
        public Builder paddingFill(final ConDrawFill setPaddingFilling) {
            this.container.setPaddingFilling(setPaddingFilling);
            //
            return this;
        }
        
        /**
         * Copier of border for builder.
         * @param example border all properties of each will be used
         * @return embedded builder for following methods
         * @throws NullPointerException if nothing to copy
         */
        public Builder copy(final ConWinBorder example) throws NullPointerException {
            if ( null == example ) {
                String excMsg = "There is nothing to copy to border properties";
                throw new NullPointerException(excMsg);
            }
            //
            this.container = new ConWinBorder(example);
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
            // 3. Was the padding filling applied?
            if ( null == this.container.paddingFilling ) {
                // There is no padding filling...
                if ( null == this.container.filling ) {
                    // ... and also there is no filling at all!
                    // So, use default 'terminal' filling.
                    this.container.setPaddingFilling(ConWinBorder.DEFAULT_PADDING_FILLING);
                }
                else {
                    // When padding filling was not applied, but general filling was.
                    // In such a case we will use the same filling
                    // for padding areas too.
                    this.container.setPaddingFilling(this.container.filling);
                }
            }
            // 4. Was the filling applied?
            if ( null == this.container.filling ) {
                // filling from default terminal settings
                this.container.setBorderFilling(ConWinBorder.DEFAULT_BORDER_FILLING);
            }
            //
            // 4. Was the padding filling applied?
            if ( null == this.container.paddingFilling ) {
                // padding filling from default terminal settings
                this.container.setPaddingFilling(ConWinBorder.DEFAULT_PADDING_FILLING);
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
