package me.dmitrygubanov40.concan.windows;

import me.dmitrygubanov40.concan.paint.ConDraw;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
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
    
    // window default properties:
    private static final String DEFAULT_WINDOW_CAPTION;
    // in case we need to cut off some piece of caption:
    private static final String DEFAULT_WINDOW_CAPTION_CUT_SYMBOL;
    //
    private static final boolean DEFAULT_WINZONE_CLEAR_STATE;
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
        DEFAULT_WINDOW_CAPTION = "";
        DEFAULT_WINDOW_CAPTION_CUT_SYMBOL = "...";
        //
        DEFAULT_WINZONE_CLEAR_STATE = true;
        DEFAULT_WINZONE_SCROLLABLE = false;
        DEFAULT_WINZONE_STORAGE_PAGES = 100;
    }
    
    ////////////
    
    // window output area itslef (we put chars here)
    private ConWinOut zone;
    
    // do we suppose window will work in multithread mode?
    private final boolean isMultithread;
    
    // 'start' point of window (in coordinates, start at [0, 0])
    private ConCord winPosition;
    
    // X-width and Y-height of window, including possible border
    private int winWidth;
    private int winHeight;
    
    // all window's border data
    private ConWinBorder winBorder;
    
    // top window label
    private String winCaption;
    
    ////////////////////////
    
    /**
     * Full constructor of a console window.
     * 
     * @param setPos first-point coordinate of window
     * @param setWidth width of window
     * @param setHeight height of window
     * @param setBorder border customization for the window
     * @param isSetMultithread is safe for multi-thread application?
     */
    public ConWin(final ConCord setPos,
                    final int setWidth, final int setHeight,
                    final ConWinBorder setBorder,
                    final boolean isSetMultithread) {
        this.setWinPos(setPos);                 // whole window's begin-point (corner)
        this.setWinSize(setWidth, setHeight);   // whole window's size
        this.setWinBorder(setBorder);           // apply window borders
        this.isMultithread = isSetMultithread;
        this.setCaption(ConWin.DEFAULT_WINDOW_CAPTION);
        //
        // place for window must be clean, with possible border:
        this.clearPlaceForWindow();
        this.refreshBorder();
        this.refreshCaption();
        // zone initialization:
        this.initWindow();
    }
    
    
    ////////////////////////
    
    
    
    /**
     * To get a filling object to fill some console area with the current
     * terminal filling color.
     * @return ready object
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
        final ConCord leftTop = this.winPosition;
        //
        // linear sizes are "less" in coordinates:
        final ConCord addForPlaceRightBottom = new ConCord(this.winWidth, this.winHeight).removeConsoleShift();
        final ConCord rightBottom = this.winPosition.plus(addForPlaceRightBottom);
        //
        final ConDrawFill clearFill = this.getTerminalFilling();
        //
        ConDraw.bar(leftTop, rightBottom, clearFill);
    }
    
    
    
    /**
     * If we have at least one char of width with every line around the window -
     * we can draw a border.
     * @return 'true' when have enough space for drawing a border
     */
    private boolean canSeeBorder() {
        boolean result = false;
        //
        if ( this.winBorder.getLeftWidth() > 0 && this.winBorder.getRightWidth() > 0
                && this.winBorder.getTopWidth() > 0 && this.winBorder.getBottomWidth() > 0 ) {
            result = true;
        }
        //
        return result;
    }
    
    /**
     * Draw the specific rectangle around output zone of the window.
     */
    private void drawBorder() {
        // Need a "-1" shift to put the cursor out of printing zone:
        final int left = this.winPosition.getX() + this.winBorder.getLeftWidth() - 1;
        final int top = this.winPosition.getY() + this.winBorder.getTopWidth() - 1;
        // The calculations give correct cursor position w/o shifts:
        final int right = this.winPosition.getX() + this.winWidth - this.winBorder.getRightWidth();
        final int bottom = this.winPosition.getY() + this.winHeight - this.winBorder.getBottomWidth();
        //
        final ConCord borderLeftTop = new ConCord(left, top);
        final ConCord borderRightBottom = new ConCord(right, bottom);
        final ConDrawFill borderFill = this.winBorder.getFilling();
        //
        ConDraw.border(this.winBorder.getType(), borderLeftTop, borderRightBottom, borderFill);
    }
    /**
     * Redraw border if it is set and possible to draw.
     */
    private void refreshBorder() {
        if ( this.canSeeBorder() ) this.drawBorder();
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
        this.zone = ConWinOut.startNewZone(winZoneWidth, winZoneHeight,
                                            winZonePos,
                                            ConWin.DEFAULT_WINZONE_CLEAR_STATE,
                                            this.isMultithread);
        this.zone.setScrollable(ConWin.DEFAULT_WINZONE_SCROLLABLE);
        final int winZoneBuffSize = ConWin.DEFAULT_WINZONE_STORAGE_PAGES * winZoneWidth * winZoneHeight;
        this.zone.startNewStorage(winZoneBuffSize);// default memory for each window
    }
    
    /**
     * Get left top coordinate fo output text zone in the window.
     * @return ConCord zone start position
     */
    private ConCord getZonePos() {
        final ConCord zonePosShift = new ConCord(this.winBorder.getLeftWidth(), this.winBorder.getTopWidth());
        final ConCord winZonePos = this.winPosition.plus(zonePosShift);
        //
        return winZonePos;
    }
    /**
     * Calculate real width for text zone (output area).
     * @return width (in characters)
     */
    private int getZoneWidth() {
        int width = this.winWidth - this.winBorder.getLeftWidth() - this.winBorder.getRightWidth();
        //
        return width;
    }
    /**
     * Calculate real height for text zone (output area).
     * @return height (in characters)
     */
    private int getZoneHeight() {
        int height = this.winHeight - this.winBorder.getTopWidth() - this.winBorder.getBottomWidth();
        //
        return height;
    }
    
    
    
    /**
     * Checks the point and install it as the object's (0,0)-point.
     * @param setPos start point of the window to set
     * @throws IllegalArgumentException when point is incorrect for the position
     */
    private void setWinPos(final ConCord setPos) throws IllegalArgumentException {
        if ( setPos.getX() < ConWin.minPosOrderedPair.getX()
                || setPos.getY() < ConWin.minPosOrderedPair.getY() ) {
            String excMsg = "Window's inappropriate start position (left top corner): " + setPos + "."
                            + " Must be in positive quadrant of the coordinate plane"
                            + " (to the right and down) from: " + ConWin.minPosOrderedPair;
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.winPosition = setPos;
    }
    
    /**
     * Checks width and height and install them into the object.
     * @param setWidth width (x-coordinate) to install
     * @param setHeight height (y-coordinate) to install
     * @throws IllegalArgumentException when width or length are incorrect
     */
    private void setWinSize(final int setWidth, final int setHeight)
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
        this.winWidth = setWidth;
        this.winHeight = setHeight;
    }
    
    /**
     * Set width of each window's border.
     * @param setBorder border data to install
     * @throws NullPointerException if no border is given
     * @throws IllegalArgumentException if we cannot install such border size
     */
    private void setWinBorder(final ConWinBorder setBorder)
                        throws NullPointerException, IllegalArgumentException {
        if ( null == setBorder ) {
            String excMsg = "No border were given for window's initialization";
            throw new NullPointerException(excMsg);
        }
        //
        boolean sizeChanged = true;
        boolean styleChanged = true;
        if ( null != this.winBorder ) {
            if ( setBorder.getTopWidth() == this.winBorder.getTopWidth()
                    && setBorder.getRightWidth() == this.winBorder.getRightWidth()
                    && setBorder.getBottomWidth() == this.winBorder.getBottomWidth()
                    && setBorder.getLeftWidth() == this.winBorder.getLeftWidth() ) {
                // no changes
                sizeChanged = false;
            }
            //
            if ( setBorder.getType() == this.winBorder.getType() ) {
                styleChanged = false;
            }
        }
        //
        if ( (setBorder.getTopWidth() + setBorder.getBottomWidth()) >= this.winHeight ) {
            String excMsg = "Window borders make no output zone height (Y)";
            throw new IllegalArgumentException(excMsg);
        }
        if ( (setBorder.getRightWidth() + setBorder.getLeftWidth()) >= this.winWidth ) {
            String excMsg = "Window borders make no output zone width (X)";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.winBorder = setBorder;
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
        this.winCaption = setCaption;
    }
    
    /**
     * @return just this window current caption
     */
    private String getCaption() {
        return this.winCaption;
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
        if ( this.winBorder.getTopWidth() <= 1 ) {
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
    private void refreshMarginArea() {
        final int borderExtra = this.canSeeBorder() ? 1 : 0;// once memorize potential shift of bars
        final ConDraw painter = new ConDraw();
        painter.setCurrentFill( this.getTerminalFilling() );
        //
        // when we should start not at the end of previous area, but in the next "zone"
        final int addForNewArea = 1;
        //
        // 1. left area:
        final ConCord leftPoint1 = this.winPosition;
        //
        final int l2ToAddX = this.winBorder.getLeftWidth() - borderExtra;
        final int l2ToAddY = this.winHeight;
        ConCord leftMarginAreaSizeToAdd = new ConCord(l2ToAddX, l2ToAddY).removeConsoleShift();
        final ConCord leftPoint2 = leftPoint1.plus(leftMarginAreaSizeToAdd);
        //
        if ( leftPoint2.getX() >= leftPoint1.getX() ) {
            // only if really have some margin area width
            painter.drawBar(leftPoint1, leftPoint2);
        }
        //
        // 2. right area:
        final int r1x = this.getZonePos().getX()
                        + (this.getZoneWidth() - ConCord.SHIFT_X)
                        + borderExtra
                        + addForNewArea;// always away from output zone characters
        final int r1y = this.winPosition.getY();
        final ConCord rightPoint1 = new ConCord(r1x, r1y);
        //
        final ConCord winRightBottomAdd = new ConCord(this.winWidth, this.winHeight).removeConsoleShift();
        // calculate simple right-bottom corner coordinates of window:
        final ConCord rightPoint2 = this.winPosition.plus(winRightBottomAdd);
        //
        if ( rightPoint2.getX() >= rightPoint1.getX() ) {
            // when have width in right area
            painter.drawBar(rightPoint1, rightPoint2);
        }
        //
        // 3. top area:
        final int t1x = (leftPoint2.getX() >= leftPoint1.getX())
                            ? leftPoint2.getX() + addForNewArea// left area was brushed, no overcovering
                            : this.winPosition.getX();// no left area, cover first characters colummn
        final int t1y = this.winPosition.getY();// just window position
        // this point will start cover strcitly out of left area boundaries
        final ConCord topPoint1 = new ConCord(t1x, t1y);
        //
        final int t2ToAddX = this.getZoneWidth() + 2 * borderExtra;// when have border - should be twice wider
        final int t2ToAddY = this.winBorder.getTopWidth() - borderExtra;
        ConCord topMarginAreaSizeToAdd = new ConCord(t2ToAddX, t2ToAddY).removeConsoleShift();
        final ConCord topPoint2 = topPoint1.plus(topMarginAreaSizeToAdd);
        //
        if ( topPoint2.getY() >= topPoint1.getY() ) {
            // when we have some real height for the area
            painter.drawBar(topPoint1, topPoint2);
        }
        //
        // 4. bottom area:
        final int b1y = this.getZonePos().getY()
                         + this.getZoneHeight()
                         + addForNewArea
                         + borderExtra
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
     * Public access re-drawer of border lines and the caption for some cases.
     */
    public void redrawFrame() {
        this.refreshMarginArea();
        this.refreshBorder();
        this.refreshCaption();
    }
    
    
    
}
