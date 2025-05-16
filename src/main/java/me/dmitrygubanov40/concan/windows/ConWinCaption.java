package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.paint.ConDraw;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * Caption aggregator for final window itself.
 * Just a structure for comfortable usage.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinCaption
{
    
    private static final int INIT_ILLEGAL_SIZE;
    // in case we need to cut off some piece of caption
    private static final String DEFAULT_CUT_SYMBOL;
    private static final String NONE_BRUSH_CHAR;// flag not to print
    
    private static final String DEFAULT_LABEL;
    private static final ConDrawFill DEFAULT_FILL;
    private static final ConCord DEFAULT_COORDS;
    private static final int DEFAULT_HEIGHT;
    private static final int DEFAULT_LENGTH;
    
    static {
        INIT_ILLEGAL_SIZE = -1;
        DEFAULT_CUT_SYMBOL = "...";
        //
        DEFAULT_LABEL = "";
        NONE_BRUSH_CHAR = ConUt.ESC;
        DEFAULT_FILL = new ConDrawFill(NONE_BRUSH_CHAR);
        DEFAULT_COORDS = new ConCord();
        DEFAULT_HEIGHT = 1;
        DEFAULT_LENGTH = 15;
    }
    
    
    ////////////////////////
    
    
    // text of the caption
    private String label;
    
    // caption colors
    private ConDrawFill filling;
    
    // determetated width the label must be drawn
    // (i.e. window output zone X-size)
    private int allowedLength;
    
    // determenated height for the label
    // (i.e. top border width - height)
    private int allowedHeight;
    
    // where the whole place starts
    // (i.e. window output zone)
    private ConCord areaLeftTop;
    
    
    ////////////////////////
    
    
    /**
     *  Base empty constructor.
     */
    private ConWinCaption() {
        this.label = null;
        this.filling = null;
        this.areaLeftTop = null;
        this.allowedLength = ConWinCaption.INIT_ILLEGAL_SIZE;
        this.allowedHeight = ConWinCaption.INIT_ILLEGAL_SIZE;
    }
    
    /**
     * Independent copy constructor.
     * @param origCaption original set we are coping
     * @throws NullPointerException if there is no 'origCaption'
     */
    private ConWinCaption(final ConWinCaption origCaption)
                    throws NullPointerException {
        if ( null == origCaption ) {
            String excMsg = "Where is no original window caption to copy";
            throw new NullPointerException(excMsg);
        }
        //
        this.allowedHeight = origCaption.getAllowedHeight();
        this.allowedLength = origCaption.getAllowedLength();
        this.areaLeftTop = origCaption.getAreaLeftTop();
        this.label = origCaption.getLabel();
        this.filling = origCaption.getFilling();
    }
    
    
    ////////////////////////
    
    
    // setters block:
    
    /**
     * Check string and apply it.
     * @param setLabel
     * @throws NullPointerException when label is not given
     */
    private void setLabel(final String setLabel)
                    throws NullPointerException {
        if ( null == setLabel ) {
            String excMsg = "There is no label to assign";
            throw new NullPointerException(excMsg);
        }
        //
        this.label = setLabel;
    }
    
    /**
     * Check filling object and apply it.
     * @param setFill
     * @throws NullPointerException when filling parameters were not given
     */
    private void setFilling(final ConDrawFill setFill)
                    throws NullPointerException {
        if ( null == setFill ) {
            String excMsg = "There are no filling parameters to assign";
            throw new NullPointerException(excMsg);
        }
        //
        this.filling = setFill;
    }
    
    /**
     * Check coordinates and apply them.
     * @param setCoords
     * @throws NullPointerException when coordinates were not given
     */
    private void setAreaLeftTop(final ConCord setCoords)
                    throws NullPointerException {
        if ( null == setCoords ) {
            String excMsg = "There are no left-top coordinates to assign";
            throw new NullPointerException(excMsg);
        }
        //
        this.areaLeftTop = setCoords;
    }
    
    /**
     * Check allowed height for caption, and assign it.
     * I.e. top border width, usually we need at least one char.
     * @param setHeight
     * @throws IllegalArgumentException when height is incorrect
     */
    private void setAllowedHeight(final int setHeight)
                    throws IllegalArgumentException {
        if ( setHeight < 0 ) {
            String excMsg = "Incorrect height to assign";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.allowedHeight = setHeight;
    }
    
    /**
     * Check allowed length for caption, and assign it.
     * I.e. window output zone width.
     * @param setLength
     * @throws IllegalArgumentException when length is incorrect
     */
    private void setAllowedLength(final int setLength)
                    throws IllegalArgumentException {
        if ( setLength <= 0 ) {
            String excMsg = "Incorrect length to assign";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.allowedLength = setLength;
    }
    
    
    // getters block:
    
    public String getLabel() {
        return this.label;
    }
    
    public ConDrawFill getFilling() {
        return this.filling;
    }
    
    public int getAllowedLength() {
        return this.allowedLength;
    }
    
    public int getAllowedHeight() {
        return this.allowedHeight;
    }
    
    public ConCord getAreaLeftTop() {
        return this.areaLeftTop;
    }
    
    // end of getters block
    
    
    /**
     * Analyze caption area and the label to generate text to place on the top of window.
     * @return line to top of window
     */
    private String getLabelToDraw() {
        if ( this.getAllowedLength() >= this.getLabel().length() ) {
            // caption "as-is" can be placed on the top of area
            return this.getLabel();
        }
        //
        final int diffLength = this.getLabel().length() - this.getAllowedLength();
        final int cutLegnth = ConWinCaption.DEFAULT_CUT_SYMBOL.length();
        final int finalCutPos = this.getLabel().length() - diffLength - cutLegnth;
        final String captionToDraw = this.getLabel().substring(0, finalCutPos)
                                        + ConWinCaption.DEFAULT_CUT_SYMBOL;
        //
        return captionToDraw;
    }
    
    /**
     * Analyze whether the caption can be shown.
     * @return allowance to have the visual caption
     */
    public boolean canSeeCaption() {
        boolean result = true;
        //
        if ( this.getAllowedHeight() <= 0 ) {
            // no top border to insert the label
            result = false;
        }
        //
        if ( this.getLabelToDraw().length() <= 0 ) {
            // where is no caption to draw
            result = false;
        }
        //
        if ( this.getLabelToDraw().length() > this.getAllowedLength() ) {
            // no space for even shortned caption
            result = false;
        }
        //
        return result;
    }
    
    /**
     * Draw text (one single line) above the area.
     */
    private void drawLabel() {
        // we cannot see the label by some reason...
        if ( !this.canSeeCaption() ) return;
        //
        final String labelText = this.getLabelToDraw();
        final ConDrawFill labelFill = this.getFilling();
        //
        final int alignCenterShift = (labelText.length() < this.getAllowedLength() )
                                    ? (int) ((this.getAllowedLength() - labelText.length()) / 2)
                                    : 0;
        //
        final int xLabelCoord = this.getAreaLeftTop().getX() + alignCenterShift;
        final int yLabelCoord = this.getAreaLeftTop().getY() - 1;
        final ConCord labelPos = new ConCord(xLabelCoord, yLabelCoord);
        //
        ConDraw painter = new ConDraw(labelFill);
        painter.setTermSaveStatus(false)
                .drawLabel(labelPos, labelText);
    }
    
    /**
     * Draw filled line above the area
     * in case some non-empty brush was applied.
     * For instance, if brush is '*', all the line will
     * be filled with the char of filling colors.
     */
    private void drawFillBar() {
        // there is no space for filling bar
        if ( this.getAllowedHeight() <= 0 ) return;
        if ( this.getFilling().getBrush().equals(ConWinCaption.NONE_BRUSH_CHAR) ) {
            // Brush was initialized not to fill all the space above area
            return;
        }
        //
        final int PREV_CHAR_LINE_POS = 1;
        final ConCord rbToAdd = new ConCord( this.getAllowedLength() - ConCord.SHIFT_X , 0);
        //
        ConCord leftTop = this.getAreaLeftTop()
                            .minus( new ConCord(0, PREV_CHAR_LINE_POS) );
        ConCord rightBottom = leftTop
                            .plus(rbToAdd);
        //
        ConDraw painter = new ConDraw(this.getFilling());
        painter.setTermSaveStatus(false)
                .drawBar(leftTop, rightBottom);
    }
    
    /**
     * Draw filling single-line bar (if set),
     * and then draw text (one single line)
     * at the top of window.
     */
    public void drawCaption() {
        this.drawFillBar();
        this.drawLabel();
    }
    
    
    
    /////////////////////////////
    ///// Builder injection /////
    public static class Builder
    {
        
        private ConWinCaption container;
        
        //////////////////
        
        public Builder() {
            // only default values agregated
            this.container = new ConWinCaption();
        }
        
        public Builder(final ConWinCaption initCaption) {
            this();
            // only default values agregated
            this.copy(initCaption);
        }
        
        //////////////////
        
        // sort of setters block:
        
        
        /**
         * Setup via builder the caption's area location.
         * Measured in real console coordinates.
         * @param setPos top-left coordinates of the area of caption
         * @return embedded builder for following methods
         */
        public Builder pos(final ConCord setPos) {
            this.container.setAreaLeftTop(setPos);
            //
            return this;
        }
        
        /**
         * Setup via builder the caption's allowed height.
         * I.e. do we have top border or not?
         * @param setHeight
         * @return embedded builder for following methods
         */
        public Builder height(final int setHeight) {
            this.container.setAllowedHeight(setHeight);
            //
            return this;
        }
        
        /**
         * Setup via builder the caption's max length.
         * @param setLength
         * @return embedded builder for following methods
         */
        public Builder length(final int setLength) {
            this.container.setAllowedLength(setLength);
            //
            return this;
        }
        
        /**
         * Setup via builder the caption's label.
         * @param setLabel
         * @return embedded builder for following methods
         */
        public Builder label(final String setLabel) {
            this.container.setLabel(setLabel);
            //
            return this;
        }
        
        /**
         * Setup via builder the caption's filling parameters.
         * @param setFill
         * @return embedded builder for following methods
         */
        public Builder fill(final ConDrawFill setFill) {
            this.container.setFilling(setFill);
            //
            return this;
        }
        
        /**
         * Turn off brush output.
         * No symbols/texture will be printed.
         * @return embedded builder for following methods
         */
        public Builder nobrush() {
            this.container.getFilling().setBrush(ConWinCaption.NONE_BRUSH_CHAR);
            //
            return this;
        }
        
        
        
        /**
         * Copier of caption for builder.
         * @param example caption all properties of each will be used
         * @return embedded builder for following methods
         * @throws NullPointerException if nothing to copy
         */
        public final Builder copy(final ConWinCaption example) throws NullPointerException {
            if ( null == example ) {
                String excMsg = "There is nothing to copy to caption properties";
                throw new NullPointerException(excMsg);
            }
            //
            this.container = new ConWinCaption(example);
            //
            return this;
        }
        
        
        
        /**
         * Check the caption state before "building" it.
         * Everything can be created "by default", no extra parameters can be called.
         * @return link to the new created caption
         */
        public ConWinCaption build() {
            // 1. Was the caption given?
            if ( null == this.container.label ) {
                // label was not assigned
                this.container.setLabel(ConWinCaption.DEFAULT_LABEL);
            }
            // 2. Were the filling paramerters given?
            if ( null == this.container.filling ) {
                // filling paramerters were not assigned
                this.container.setFilling(ConWinCaption.DEFAULT_FILL);
            }
            // 3. Were coordinates given?
            if ( null == this.container.areaLeftTop ) {
                // coordinates were not assigned
                this.container.setAreaLeftTop(ConWinCaption.DEFAULT_COORDS);
            }
            // 4. Do we know the height?
            if ( ConWinCaption.INIT_ILLEGAL_SIZE == this.container.allowedHeight ) {
                this.container.setAllowedHeight(ConWinCaption.DEFAULT_HEIGHT);
            }
            // 5. Do we know the length?
            if ( ConWinCaption.INIT_ILLEGAL_SIZE == this.container.allowedLength ) {
                this.container.setAllowedLength(ConWinCaption.DEFAULT_LENGTH);
            }
            //
            ConWinCaption result = this.container;
            this.container = new ConWinCaption();
            return result;
        }
        
        
        
    }
    ///// End of builder injection /////
    ////////////////////////////////////
    
    
}
