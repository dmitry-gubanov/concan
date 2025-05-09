package me.dmitrygubanov40.concan.windows;

import java.awt.Color;

import me.dmitrygubanov40.concan.paint.ConBorderRectType;
import me.dmitrygubanov40.concan.paint.ConDraw;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
import me.dmitrygubanov40.concan.utility.ConCol;
import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Window in console with independent output zone, and border.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWin
{
    
    // all windows must have at least this position
    private static final ConCord minPosOrderedPair;
    
    // window's size limitations
    private static final int MIN_WIN_WIDTH;
    private static final int MAX_WIN_WIDTH;
    private static final int MIN_WIN_HEIGHT;
    private static final int MAX_WIN_HEIGHT;
    //
    private static final int INIT_ILLEGAL_WINDOW_SIZE;
    
    // window default properties:
    private static final ConCord DEFAULT_WINDOW_POSITION;
    private static final ConWinBorder DEFAULT_WINDOW_BORDER;
    private static final String DEFAULT_WINDOW_CAPTION;
    // in case we need to cut off some piece of caption:
    private static final String DEFAULT_WINDOW_CAPTION_CUT_SYMBOL;
    //
    private static final boolean DEFAULT_WINZONE_MULTITHREAD;
    private static final boolean DEFAULT_WINZONE_SCROLLABLE;
    private static final int DEFAULT_WINZONE_STORAGE_PAGES;
    
    static {
        minPosOrderedPair = new ConCord(0, 0);
        //
        MIN_WIN_WIDTH = 1;
        MAX_WIN_WIDTH = 500;
        MIN_WIN_HEIGHT = 1;
        MAX_WIN_HEIGHT = 500;
        //
        INIT_ILLEGAL_WINDOW_SIZE = -1;
        //
        DEFAULT_WINDOW_POSITION = new ConCord(0, 0);
        // default border is enough for non-specified border
        // (zero-size template)
        DEFAULT_WINDOW_BORDER = new ConWinBorder.Builder().build();
        //
        DEFAULT_WINDOW_CAPTION = "";
        DEFAULT_WINDOW_CAPTION_CUT_SYMBOL = "...";
        //
        DEFAULT_WINZONE_MULTITHREAD = false;
        DEFAULT_WINZONE_SCROLLABLE = false;
        DEFAULT_WINZONE_STORAGE_PAGES = 100;
    }
    
    ////////////
    
    // window output area itslef (we put chars here)
    private ConWinOut zone;
    
    // do we suppose window will work in multithread mode?
    private boolean isMultithread;
    // should we scroll slowly one-by-one row when output in the last console row?
    private boolean isScrollable;
    // how many pages will be stored in memory for autoscrolling?
    private int storagePages;
    
    // 'start' point of window (in coordinates, start at [0, 0])
    private ConCord position;
    
    // X-width and Y-height of window, including possible border
    private int width;
    private int height;
    
    // all window's border data
    private ConWinBorder border;
    
    // top window label
    private String caption;
    
    ////////////////////////
    
    // Blank constrcutor for inner builder (see end of class).
    private ConWin() {
        // can be re-declared in builder:
        this.isMultithread = ConWin.DEFAULT_WINZONE_MULTITHREAD;
        this.isScrollable = ConWin.DEFAULT_WINZONE_SCROLLABLE;
        this.storagePages = ConWin.DEFAULT_WINZONE_STORAGE_PAGES;
        //
        this.width = ConWin.INIT_ILLEGAL_WINDOW_SIZE;
        this.height = ConWin.INIT_ILLEGAL_WINDOW_SIZE;
        //
        this.position = null;
        this.zone = null;
        this.border = null;
        this.caption = null;
    }
    
    
    ////////////////////////
    
    
    
    /**
     * To get a filling object to fill some console area with the current
     * terminal filling parameters.
     * @return ready filling object
     */
    private ConDrawFill getTerminalFilling() {
        final ConDrawFill result = new ConDrawFill(Term.get().background(),
                                                    Term.EMPTY_CHAR,
                                                    Term.get().color());
        //
        return result;
    }
    
    
    
    /**
     * Guarantee console place to be clean for the window.
     */
    private void clearPlaceForWindow() {
        final ConCord leftTop = this.position;
        //
        // linear sizes are "less" in coordinates:
        final ConCord addForPlaceRightBottom = new ConCord(this.width, this.height).removeConsoleShift();
        final ConCord rightBottom = this.position.plus(addForPlaceRightBottom);
        //
        final ConDrawFill clearFill = this.getTerminalFilling();
        //
        ConDraw.bar(leftTop, rightBottom, clearFill);
    }
    
    
    
    /**
     * Redraw border if it is set and possible to draw.
     */
    private void refreshBorder() {
        if ( this.border.canSeeBorder() ) {
            this.border.drawBorder(this.position, this.width, this.height);
        }
    }
    
    
    
    /**
     * Calculate output zone parameters (border parameters influence) and init them,
     * draw the necessary border.
     */
    private void initWindow() {
        final int winZoneWidth = this.getZoneWidth();
        final int winZoneHeight = this.getZoneHeight();
        final ConCord winZonePos = this.getZonePos();
        //
        // We do not need inbuild zone cleaner - clear the whole window area yourself.
        // But if window is non-scrollable - we need this regime for output zone autocleaning.
        final boolean WINZONE_CLEAR_STATE = !this.isScrollable;
        //
        this.zone = ConWinOut.startNewZone(winZoneWidth, winZoneHeight,
                                            winZonePos,
                                            WINZONE_CLEAR_STATE,
                                            this.isMultithread);
        this.zone.setScrollable(this.isScrollable);
        final int winZoneBuffSize = this.storagePages * winZoneWidth * winZoneHeight;
        this.zone.startNewStorage(winZoneBuffSize);// memory for each window output zone
    }
    
    /**
     * Get left top coordinate fo output text zone in the window.
     * @return ConCord zone start position
     */
    private ConCord getZonePos() {
        final ConCord zonePosShift = new ConCord(this.border.getLeftWidth(), this.border.getTopWidth());
        final ConCord winZonePos = this.position.plus(zonePosShift);
        //
        return winZonePos;
    }
    /**
     * Calculate real width for text zone (output area).
     * @return width (in characters)
     */
    private int getZoneWidth() {
        int zoneWidth = this.width - this.border.getLeftWidth() - this.border.getRightWidth();
        //
        return zoneWidth;
    }
    /**
     * Calculate real height for text zone (output area).
     * @return height (in characters)
     */
    private int getZoneHeight() {
        int zoneHeight = this.height - this.border.getTopWidth() - this.border.getBottomWidth();
        //
        return zoneHeight;
    }
    
    
    
    /**
     * Checks the point and install it as the object's (0,0)-point.
     * @param setPos start point of the window to set
     * @throws IllegalArgumentException when point is incorrect for the position
     */
    private void setPos(final ConCord setPos) 
                            throws NullPointerException, IllegalArgumentException {
        if ( null == setPos ) {
            String excMsg = "No window start position were given";
            throw new NullPointerException(excMsg);
        }
        //
        if ( setPos.getX() < ConWin.minPosOrderedPair.getX()
                || setPos.getY() < ConWin.minPosOrderedPair.getY() ) {
            String excMsg = "Window's inappropriate start position (left top corner): " + setPos + "."
                            + " Must be in positive quadrant of the coordinate plane"
                            + " (to the right and down) from: " + ConWin.minPosOrderedPair;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.position = setPos;
    }
    
    /**
     * Checks width and height and install them into the object.
     * @param setWidth width (x-coordinate) to install
     * @param setHeight height (y-coordinate) to install
     * @throws IllegalArgumentException when width or length are incorrect
     */
    private void setSize(final int setWidth, final int setHeight)
                    throws IllegalArgumentException {
        if ( setWidth < ConWin.MIN_WIN_WIDTH || setWidth > ConWin.MAX_WIN_WIDTH ) {
            String excMsg = "Window's inappropriate width: " + setWidth + "."
                            + " Must be in range: " + ConWin.MIN_WIN_WIDTH + " ... " + ConWin.MAX_WIN_WIDTH;
            throw new IllegalArgumentException(excMsg);
        }
        if ( setHeight < ConWin.MIN_WIN_HEIGHT || setHeight > ConWin.MAX_WIN_HEIGHT ) {
            String excMsg = "Window's inappropriate height: " + setHeight + "."
                            + " Must be in range: " + ConWin.MIN_WIN_HEIGHT + " ... " + ConWin.MAX_WIN_HEIGHT;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.width = setWidth;
        this.height = setHeight;
    }
    
    /**
     * Set windows border style and (possible) padding areas via the object.
     * @param setBorder border data to install
     * @throws NullPointerException if no border is given
     * @throws IllegalArgumentException if we cannot install such border size
     */
    private void setBorder(final ConWinBorder setBorder)
                        throws NullPointerException, IllegalArgumentException {
        if ( null == setBorder ) {
            String excMsg = "No border were given for window's initialization";
            throw new NullPointerException(excMsg);
        }
        //
        boolean sizeChanged = true;// for potential future, flags of change
        boolean styleChanged = true;
        if ( null != this.border ) {
            if ( setBorder.getTopWidth() == this.border.getTopWidth()
                    && setBorder.getRightWidth() == this.border.getRightWidth()
                    && setBorder.getBottomWidth() == this.border.getBottomWidth()
                    && setBorder.getLeftWidth() == this.border.getLeftWidth() ) {
                // no changes
                sizeChanged = false;
            }
            //
            if ( setBorder.getType() == this.border.getType() ) {
                styleChanged = false;
            }
        }
        //
        if ( (setBorder.getTopWidth() + setBorder.getBottomWidth()) >= this.height ) {
            String excMsg = "Window borders make no output zone height (Y). "
                            + "Top: " + setBorder.getTopWidth()
                            + ", bottom: " + setBorder.getBottomWidth()
                            + ", window height: " + this.height;
            throw new IllegalArgumentException(excMsg);
        }
        if ( (setBorder.getRightWidth() + setBorder.getLeftWidth()) >= this.width ) {
            String excMsg = "Window borders make no output zone width (X). "
                            + "Right: " + setBorder.getRightWidth()
                            + ", left: " + setBorder.getLeftWidth()
                            + ", window width: " + this.width;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.border = setBorder;
    }
    
    /**
     * Setter of multi-thread flag of window and its output.
     * @param setAsync the flag we install for multi-thread readiness
     */
    private void setMultithread(final boolean setAsync) {
        this.isMultithread = setAsync;
    }
    
    /**
     * On/off-setter for auto-scroll in window output area.
     * @param setScroll is scroll regime on?
     */
    private void setScrollable(final boolean setScroll) {
        this.isScrollable = setScroll;
    }
    
    /**
     * Output in window is saved, number of such saved "pages".
     * "Page" means block of characters in size of width x height
     * @param setPages number of pages to keep in memory
     */
    private void setStoragePages(final int setPages) {
        this.storagePages = setPages;
    }
    
    
    
    /**
     * This text will be put into window (exactly to the end of current text).
     * After text is added buffer is flushed (to wee the result immediately).
     * @param strData text line we send to output in window output zone
     * @throws NullPointerException if there is no string to output
     */
    public void print(final String strData) throws NullPointerException {
        if ( null == strData ) {
            String excMsg = "There is no string to print in window";
            throw new NullPointerException(excMsg);
        }
        //
        if ( strData.length() <= 0 ) {
            // no real job
            return;
        }
        //
        this.zone.addToZone(strData);
        this.zone.flush();
    }
    /**
     * New line version of 'print'.
     * @param strData text line we send to output in window output zone with '\n'
     */
    public void println(final String strData) {
        final String strDataNewLine = strData + "\n";
        //
        this.print(strDataNewLine);
    }
    
    
    
    /**
     * Setter for window's caption.
     * @param setCaption new caption for the window
     * @throws NullPointerException if there is no new caption
     * @throws IllegalArgumentException if caption has a new line of some kind
     */
    private void setCaption(final String setCaption) {
        if ( null == setCaption ) {
            String excMsg = "There is no string for window's caption";
            throw new NullPointerException(excMsg);
        }
        //
        if ( setCaption.indexOf('\n') != -1
                || setCaption.indexOf('\r') != -1 ) {
            String excMsg = "Window's caption must be in single line";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.caption = setCaption;
    }
    
    /**
     * @return just this window current caption
     */
    private String getCaption() {
        return this.caption;
    }
    
    /**
     * Analyze window state and the caption to generate text to place on the top of window.
     * @return line to top of window
     */
    private String getCaptionToDraw() {
        if ( this.getZoneWidth() >= this.getCaption().length() ) {
            // caption "as-is" can be placed on the top of 
            //
            return this.getCaption();
        }
        //
        final int diffLength = this.getCaption().length() - this.getZoneWidth();
        final int cutLegnth = ConWin.DEFAULT_WINDOW_CAPTION_CUT_SYMBOL.length();
        final int finalCutPos = this.getCaption().length() - diffLength - cutLegnth;
        final String captionToDraw = this.getCaption().substring(0, finalCutPos)
                                        + ConWin.DEFAULT_WINDOW_CAPTION_CUT_SYMBOL;
        //
        return captionToDraw;
    }
    
    /**
     * Analyze whether the window can visually have the caption.
     * @return allowance to have the visual caption
     */
    private boolean canSeeCaption() {
        boolean result = true;
        //
        if ( this.border.getTopWidth() <= 1 ) {
            // no top border to insert label
            result = false;
        }
        //
        if ( this.getCaptionToDraw().length() <= 0 ) {
            // where is no caption to draw
            result = false;
        }
        //
        if ( this.getCaptionToDraw().length() > this.getZoneWidth() ) {
            // no space for even shortned caption
            result = false;
        }
        //
        return result;
    }
    
    /**
     * Draw text (one single line) at the top of window.
     */
    private void drawCaption() {
        final String labelText = this.getCaptionToDraw();
        final ConDrawFill labelFill = this.getTerminalFilling();
        //
        final int alignCenterShift = (labelText.length() < this.getZoneWidth() )
                                    ? (int) ((this.getZoneWidth() - labelText.length()) / 2)
                                    : 0;
        //
        final int xLabelCoord = this.getZonePos().getX() + alignCenterShift;
        final int yLabelCoord = this.getZonePos().getY() - 1;
        final ConCord labelPos = new ConCord(xLabelCoord, yLabelCoord);
        //
        ConDraw.label(labelPos, labelText, labelFill);
    }
    
    /**
     * Redraw window's caption if it is set and possible to print.
     */
    private void refreshCaption() {
        if ( this.canSeeCaption() ) this.drawCaption();
    }
    
    
    
    /**
     * Make guarantee clear free window zones near border lines (if any).
     * Is necessary if wide/high fields could be occasionally printed.
     */
    private void refreshPaddingArea() {
        // once memorize potential shift of bars (in console symbols):
        final int BORDER_EXTRA = this.border.canSeeBorder()
                                    ? ConWinBorder.VISUAL_NUMBER_OF_CHARS
                                    : 0;
        final ConDraw painter = new ConDraw();
        painter.setCurrentFill( this.border.getPaddingFilling() );
        //
        // when we should start not at the end of previous area, but in the next "zone"
        final int ADD_FOR_NEW_AREA = 1;
        //
        // 1. left area:
        final ConCord leftPoint1 = this.position;
        //
        final int l2ToAddX = this.border.getLeftWidth() - BORDER_EXTRA;
        final int l2ToAddY = this.height;
        ConCord leftPaddingAreaSizeToAdd = new ConCord(l2ToAddX, l2ToAddY).removeConsoleShift();
        final ConCord leftPoint2 = leftPoint1.plus(leftPaddingAreaSizeToAdd);
        //
        if ( leftPoint2.getX() >= leftPoint1.getX() ) {
            // only if really have some padding area width
            painter.drawBar(leftPoint1, leftPoint2);
        }
        //
        // 2. right area:
        final int r1x = this.getZonePos().getX()
                        + (this.getZoneWidth() - ConCord.SHIFT_X)
                        + BORDER_EXTRA
                        + ADD_FOR_NEW_AREA;// always away from output zone characters
        final int r1y = this.position.getY();
        final ConCord rightPoint1 = new ConCord(r1x, r1y);
        //
        final ConCord winRightBottomAdd = new ConCord(this.width, this.height).removeConsoleShift();
        // calculate simple right-bottom corner coordinates of window:
        final ConCord rightPoint2 = this.position.plus(winRightBottomAdd);
        //
        if ( rightPoint2.getX() >= rightPoint1.getX() ) {
            // when have width in right area
            painter.drawBar(rightPoint1, rightPoint2);
        }
        //
        // 3. top area:
        final int t1x = (leftPoint2.getX() >= leftPoint1.getX())
                            ? leftPoint2.getX() + ADD_FOR_NEW_AREA// left area was brushed, no overcovering
                            : this.position.getX();// no left area, cover first characters colummn
        final int t1y = this.position.getY();// just window position
        // this point will start cover strcitly out of left area boundaries
        final ConCord topPoint1 = new ConCord(t1x, t1y);
        //
        final int t2ToAddX = this.getZoneWidth() + 2 * BORDER_EXTRA;// when have border - should be twice wider
        final int t2ToAddY = this.border.getTopWidth() - BORDER_EXTRA;
        ConCord topPaddingAreaSizeToAdd = new ConCord(t2ToAddX, t2ToAddY).removeConsoleShift();
        final ConCord topPoint2 = topPoint1.plus(topPaddingAreaSizeToAdd);
        //
        if ( topPoint2.getY() >= topPoint1.getY() ) {
            // when we have some real height for the area
            painter.drawBar(topPoint1, topPoint2);
        }
        //
        // 4. bottom area:
        final int b1y = this.getZonePos().getY()
                         + this.getZoneHeight()
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
    
    
    
    /**
     * Public access re-drawer of border lines and the caption.
     */
    public void redrawFrame() {
        this.refreshPaddingArea();
        this.refreshBorder();
        this.refreshCaption();
    }
    
    
    
    /////////////////////////////
    ///// Builder injection /////
    public static class Builder
    {
        
        private ConWin container;
        
        private int presetPaddingLeft;
        private int presetPaddingTop;
        private int presetPaddingRight;
        private int presetPaddingBottom;
        
        private ConDrawFill presetPaddingFilling;
        
        //////////////////
        
        public Builder() {
            // install empty values into aggregator
            this.container = new ConWin();
            //
            this.presetPaddingLeft = 0;
            this.presetPaddingTop = 0;
            this.presetPaddingRight = 0;
            this.presetPaddingBottom = 0;
            this.presetPaddingFilling = null;
        }
        
        //////////////////
        
        // builder setters
        
        /**
         * General window size (output zone, and border, including padding areas, if any).
         * Important: border size, and padding areas are included in window size.
         * @param setWidth total length in symbols
         * @param setHeight total char rows
         * @return embedded builder for following methods
         */
        public Builder size(final int setWidth, final int setHeight) {
            this.container.setSize(setWidth, setHeight);
            //
            return this;
        }
        
        /**
         * Location of top-left corner of the window.
         * @param setPos window coordinates to install
         * @return embedded builder for following methods
         */
        public Builder pos(final ConCord setPos) {
            this.container.setPos(setPos);
            //
            return this;
        }
        public Builder pos(final int x, final int y) {
            ConCord posCoord = new ConCord(x, y);
            //
            return this.pos(posCoord);
        }
        // set of border initializator
        
        /**
         * Set border for the window  w/o extra padding of specific type and colors.
         * Remember: padding is included into all window width.
         * @param setType type of the line around the window
         * @param setBorderColor the color of lines themselves
         * @param setBorderBgColor specific background color
         * @return embedded builder for following methods
         */
        public Builder border(final ConBorderRectType setType,
                                final Color setBorderColor,
                                final Color setBorderBgColor) {
            // filling properties object
            ConDrawFill setFilling = new ConDrawFill(setBorderBgColor, setBorderColor);
            //
            ConWinBorder setBorder = new ConWinBorder.Builder()
                                            .width(ConWinBorder.VISUAL_NUMBER_OF_CHARS)
                                            .type(setType)
                                            .fill(setFilling)
                                            .build();
            this.container.setBorder(setBorder);
            //
            return this;
        }
        // Only font (brush) color option method:
        // (if we want to set color in border presume it would be font color)
        public Builder border(final ConBorderRectType setType,
                                final Color setBorderColor) {
            final Color NO_BG_COLOR = null;
            return this.border(setType, setBorderColor, NO_BG_COLOR);
        }
        /**
         * Set border for the window  w/o extra padding,
         * with specific type and colors (ConCol version).
         * @param setType type of the line around the window
         * @param setBorderConCol the color of lines themselves
         * @param setBorderBgConCol specific background color
         * @return embedded builder for following methods
         */
        public Builder border(final ConBorderRectType setType,
                                final ConCol setBorderConCol,
                                final ConCol setBorderBgConCol) {
            // get true colors:
            final Color setBorderColor = setBorderConCol.getTrueColor();
            final Color setBorderBgColor = setBorderBgConCol.getTrueColor();
            //
            // use default method:
            return this.border(setType, setBorderColor, setBorderBgColor);
        }
        // Only font (brush) color option method (ConCol version):
        // (if we want to set color in border presume it would be font color)
        public Builder border(final ConBorderRectType setType,
                                final ConCol setBorderConCol) {
            final Color NO_BG_COLOR = null;
            return this.border(setType,
                                setBorderConCol.getTrueColor(),
                                NO_BG_COLOR);
        }
        /**
         * Set specific type border for the window w/o extra padding.
         * Use default terminal color and background.
         * @param setType type of the line around the window
         * @return embedded builder for following methods
         */
        public Builder border(final ConBorderRectType setType) {
            // filling properties object
            ConDrawFill setFilling = new ConDrawFill();
            //
            ConWinBorder setBorder = new ConWinBorder.Builder()
                                            .width(ConWinBorder.VISUAL_NUMBER_OF_CHARS)
                                            .type(setType)
                                            .fill(setFilling)
                                            .build();
            this.container.setBorder(setBorder);
            //
            return this;
        }
        
        /**
         * Inner paddings checker.
         * @param paddings set of four paddings to check
         * @throws IllegalArgumentException if these paddings cannot be applied
         */
        private void checkPaddings(final int[] paddings) {
            final int PADDINGS_SET_LENGTH = 4;
            final int MIN_PADDING = 0;
            final int MAX_PADDING = 100;
            //
            if ( paddings.length != PADDINGS_SET_LENGTH ) {
                String excMsg = "Incorrect paddings set of length: " + paddings.length;
                throw new IllegalArgumentException(excMsg);
            }
            for ( int curPadding : paddings ) {
                if ( curPadding < MIN_PADDING || curPadding > MAX_PADDING ) {
                    String excMsg = "Inappropriate padding value: '" + curPadding + "'."
                                    + " Must be in range: " + MIN_PADDING
                                    + " ... " + MAX_PADDING;
                    throw new IllegalArgumentException(excMsg);
                }
            }
        }
        
        /**
         * Inner padding appliment (both size and filling).
         * Saved 'padding' values must be integrated into border object.
         * @throws NullPointerException if we still do not have border
         */
        private void addPadding() {
            if ( null == this.container.border ) {
                String excMsg = "Cannot apply padding values - border is absent";
                throw new NullPointerException(excMsg);
            }
            //
            final int allPaddings = this.presetPaddingLeft + this.presetPaddingTop
                                    + this.presetPaddingRight + this.presetPaddingBottom;
            if ( allPaddings <= 0 ) return;// no paddings (it is not validating)
            //
            final int newLeft = this.container.border.getLeftWidth() + this.presetPaddingLeft;
            final int newTop = this.container.border.getTopWidth() + this.presetPaddingTop;
            final int newRight = this.container.border.getRightWidth() + this.presetPaddingRight;
            final int newBottom = this.container.border.getBottomWidth() + this.presetPaddingBottom;
            //
            ConWinBorder.Builder newBorderBuilder = new ConWinBorder.Builder();
            newBorderBuilder
                    .copy(this.container.border)
                    .width(newLeft, newTop, newRight, newBottom);
            if ( null != this.presetPaddingFilling ) {
                newBorderBuilder.paddingFill(this.presetPaddingFilling);
            }
            ConWinBorder newBorder = newBorderBuilder.build();
            //
            this.container.setBorder(newBorder);
        }
        
        /**
         * Do preset padding values before window building.
         * Important: padding is included in total window area.
         * @param setLeft
         * @param setTop
         * @param setRight
         * @param setBottom
         * @return embedded builder for following methods
         */
        public Builder padding(final int setLeft,
                                final int setTop,
                                final int setRight,
                                final int setBottom) {
            final int[] paddings = {setLeft, setTop, setRight, setBottom};
            this.checkPaddings(paddings);// they are correct (no exceptions), continue...
            //
            this.presetPaddingLeft = setLeft;
            this.presetPaddingTop = setTop;
            this.presetPaddingRight = setRight;
            this.presetPaddingBottom = setBottom;
            //
            return this;
        }
        
        /**
         * Do preset simple padding - all sides with same value.
         * @param setPadding
         * @return embedded builder for following methods
         */
        public Builder padding(final int setPadding) {
            this.padding(setPadding, setPadding, setPadding, setPadding);
            //
            return this;
        }
        
        /**
         * Do preset easy padding - top and bottom, left and right.
         * @param setLeftAndRight such padding for left and right borders
         * @param setTopAndBottom such padding for top and bottom borders
         * @return embedded builder for following methods
         */
        public Builder padding(final int setLeftAndRight,
                                final int setTopAndBottom) {
            this.padding(setLeftAndRight,
                            setTopAndBottom,
                            setLeftAndRight,
                            setTopAndBottom);
            //
            return this;
        }
        
        /**
         * Customize padding filling color (background).
         * There is no need of brush and/or direct (brush) color in padding.
         * @param setPaddingColor
         * @return embedded builder for following methods
         */
        public Builder paddingColor(final Color setPaddingColor) {
            // padding filling properties object
            ConDrawFill setPaddingFilling = new ConDrawFill(setPaddingColor);
            //
            this.presetPaddingFilling = setPaddingFilling;
            //
            return this;
        }
        /**
         * Customize padding filling color (background), ConCol version.
         * There is no need of brush and/or direct (brush) color in padding.
         * @param setPaddingConCol
         * @return embedded builder for following methods
         */
        public Builder paddingColor(final ConCol setPaddingConCol) {
            final Color setPaddingTrueColor = setPaddingConCol.getTrueColor();
            //
            this.paddingColor(setPaddingTrueColor);
            //
            return this;
        }
        
        // end of border initializators
        
        /**
         * Status of readiness to work in many threads.
         * @param setForAsync ready or not?
         * @return embedded builder for following methods
         */
        public Builder multithread(final boolean setForAsync) {
            this.container.setMultithread(setForAsync);
            //
            return this;
        }
        public Builder multithread() {
            // flag preset
            return this.multithread(true);
        }
        
        /**
         * Will the window output "auto-scrolling" text,
         * or put the text "page-by-page".
         * @param setScrollUsage use or not?
         * @return embedded builder for following methods
         */
        public Builder scrollable(final boolean setScrollUsage) {
            this.container.setScrollable(setScrollUsage);
            //
            return this;
        }
        public Builder scrollable() {
            // flag preset
            return this.scrollable(true);
        }
        
        /**
         * @param setPages the number of "pages" to be kept in memory
         * @return embedded builder for following methods
         */
        public Builder pages(final int setPages) {
            this.container.setStoragePages(setPages);
            //
            return this;
        }
        
        
        
        /**
         * Checks the window status and finally creates it (as entity).
         * @param showAfterCreation whether or not show an empty window with the frame
         * @return new created window
         * @throws IllegalStateException in case we failed to create a window
         */
        private ConWin create(final boolean showAfterCreation) {
            // 1. check linear size:
            if ( ConWin.INIT_ILLEGAL_WINDOW_SIZE == this.container.height
                    || ConWin.INIT_ILLEGAL_WINDOW_SIZE == this.container.width ) {
                String excMsg = "Attempt to create window without scalar size"
                                + " (height or/and width are not given)";
                throw new IllegalStateException(excMsg);
            }
            //
            // 2. check window start position:
            if ( null == this.container.position ) {
                // window location was not set - use default
                this.container.setPos(ConWin.DEFAULT_WINDOW_POSITION);
            }
            //
            // 3. check window border status:
            if ( null == this.container.border ) {
                this.container.setBorder(ConWin.DEFAULT_WINDOW_BORDER);
            }
            // if we have some extra padding - them must be added into the border
            // (simple enlarging by 'presetPadding'-values)
            this.addPadding();
            //
            // 4. check caption status:
            if ( null == this.container.caption ) {
                this.container.setCaption(ConWin.DEFAULT_WINDOW_CAPTION);
            }
            //
            // Now need to install output area:
            this.container.initWindow();
            if ( showAfterCreation ) {
                this.container.clearPlaceForWindow();
                this.container.redrawFrame();
            }
            //
            ConWin ourNewWindow = this.container;
            this.container = new ConWin();
            return ourNewWindow;
        }
        public ConWin build() {
            // create window and immediatelly show it
            return this.create(true);
        }
        public ConWin ready() {
            // create window, but do not show it immediatelly
            return this.create(false);
        }
        
        
    }
    ///// End of builder injection /////
    ////////////////////////////////////
    
    
    
}
