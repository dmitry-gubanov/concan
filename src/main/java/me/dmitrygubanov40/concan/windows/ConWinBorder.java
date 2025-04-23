package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.paint.ConBorderRectType;



/**
 * Border aggregator for final window itself.
 * Just a structure for comfortable usage.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinBorder
{
    
    private static final int MIN_BORDER_WIDTH;
    private static final int MAX_BORDER_WIDTH;
    
    static {
        MIN_BORDER_WIDTH = 0;
        MAX_BORDER_WIDTH = 250;
    }
    
    
    //////////////
    
    
    // border type to draw (if width of each line is at least one)
    // is used by rectangle
    private ConBorderRectType winBorderType;
    
    // all window border widths
    // Important: border char is always one, but border width can be used
    // to make window output zone less
    private int winBorderTop;
    private int winBorderRight;
    private int winBorderBottom;
    private int winBorderLeft;
    
    //////////////
    
    /**
     * Base 'wide' constructor.
     * @param setType single, double or even none border rectangle visualization
     * @param setLeft border width of the left side
     * @param setTop border width of the top
     * @param setRight border width of the right side
     * @param setBottom border width of the bottom
     * @throws NullPointerException if there is no border type
     * @throws IllegalArgumentException when real border is requested by any side border width is zero
     */
    public ConWinBorder(final ConBorderRectType setType,
                            final int setLeft, final int setTop,
                            final int setRight, final int setBottom) {
        if ( null == setType ) {
            String excMsg = "Where is no border type to implement";
            throw new NullPointerException(excMsg);
        }
        this.winBorderType = setType;
        this.setBorderWidth(setTop, setRight, setBottom, setLeft);
    }
    // default no-border-symbols, need only margin for some sides
    public ConWinBorder(final int setLeft, final int setTop,
                            final int setRight, final int setBottom) {
        this(ConBorderRectType.NONE, setTop, setRight, setBottom, setLeft);
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
        this.winBorderTop       = setTop;
        this.winBorderRight     = setRight;
        this.winBorderBottom    = setBottom;
        this.winBorderLeft      = setLeft;
    }
    
    
    
    // block of getters:
    
    public ConBorderRectType getType() {
        return this.winBorderType;
    }
    
    public int getTopWidth() {
        return this.winBorderTop;
    }
    public int getRightWidth() {
        return this.winBorderRight;
    }
    public int getBottomWidth() {
        return this.winBorderBottom;
    }
    public int getLeftWidth() {
        return this.winBorderLeft;
    }
    
    
    
}
