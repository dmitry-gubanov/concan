package me.dmitrygubanov40.concan.windows;


import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.ConUt;



/**
 * All logic about processing special characters behavior in terms coordinates movements,
 * and other side effects - is kept here.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinOutSpecChars
{
    
    // tabulation column size
    private final static int TAB_SIZE;
    
    static {
        TAB_SIZE = 4;
    }
    
    /////////////////////////////////
    
    // data of the current cursor position
    private ConCord curCursor;
    
    // pointer to window zone lines storage
    private ConWinOutStorage curStorage;
    
    // such width is allowed to work (put characters) in the area
    private int allowedWidth;
    
    //
    
    /**
     * @param initCurCursor pointer to real coordinates
     * @param initAllowedWidth X-axis border
     * @throws NullPointerException when does not get real coordinates
     */
    public ConWinOutSpecChars(final ConCord initCurCursor, final int initAllowedWidth)
                    throws NullPointerException {
        if ( null == initCurCursor ) {
            String excMsg = "Cursor coordinates are null (not initialized)";
            throw new NullPointerException(excMsg);
        }
        //
        this.curCursor = initCurCursor;
        this.allowedWidth = initAllowedWidth;
        //
        this.curStorage = null;// need 'linkStorage()' to have storage pointer
    }
    
    /**
     * The processing gets its own strings archive to influence it
     * during characters processing.
     * @param initCurStorage 
     */
    public void linkStorage(final ConWinOutStorage initCurStorage)
                        throws NullPointerException {
        if ( null == initCurStorage ) {
            String excMsg = "Lines archive (storage) is not specified (is null)";
            throw new NullPointerException(excMsg);
        }
        //
        this.curStorage = initCurStorage;
    }
    
    //
    
    /**
     * Outer caller to recalculate position in the area
     * to set a new line.
     * LF (\n) analog, in coordinates.
     * Need the method to have the same code upper in ConCan-hierarchy.
     */
    public void callNewLine() {
        this.setNewLine();
    }
    
    
    
    /**
     * Move cursor on new line in the area.
     * Calculate coordinates, does not move cursor directly.
     * @param xToSet where cursor will appear via X-axis
     * @throws NullPointerException when pointer to strings archive (storage) is absent
     * @see linkStorage()
     */
    private void setNewLine(final int xToSet)
                        throws NullPointerException {
        this.curCursor.setX(xToSet);
        this.curCursor.setY(this.curCursor.getY() + 1);
        //
        if ( null == this.curStorage ) {
            // strings archive is necessary but is absent
            String excMsg = "Lines archive (storage) is null (not linked)";
            throw new NullPointerException(excMsg);
        }
        //
        this.curStorage.storeNewLine();// add new line in zone's data
    }
    private void setNewLine() {
        this.setNewLine(0);
    }
    
    /**
     * Move one char back while we are in the zone.
     * @param backSteps how many symbols back we will step
     */
    private void goBackspace(final int backSteps) {
        int curX = this.curCursor.getX();
        final int xToSet = ((curX - backSteps) > 0)
                                ? (curX - backSteps)
                                : 0;
        this.curCursor.setX(xToSet);
    }
    private void goBackspace() {
        this.goBackspace(1);
    }
    
    /**
     * Move cursor right on tabulation size in chars, while it is
     * not end of the zone.
     */
    private void addTab() {
        final int curX = this.curCursor.getX();
        int newX = curX;// will set it
        // how many times TAB can be placed in the zone:
        final int stepsNmb = this.allowedWidth / ConWinOutSpecChars.TAB_SIZE;
        //
        for ( int j = 1; j <= stepsNmb; j++ ) {
            final int curTabX = j * ConWinOutSpecChars.TAB_SIZE;
            if ( curTabX <= this.allowedWidth
                    && curTabX > curX ) {
                newX = curTabX;
                break;
            }
        }
        //
        this.curCursor.setX(newX);
    }
    
    /**
     * Produce PC-speaker sound.
     */
    private void doBeep() {
        ConUt.beep();
    }
    
    /**
     * Move cursor to next line, but keep current horizontal position.
     * Does nothing at the line's edges.
     */
    private void addVerticalTab() {
        final int curX = this.curCursor.getX();
        if ( curX > 0 && curX < this.allowedWidth ) this.setNewLine(curX);
    }
    
    /**
     * Move cursor to the beginning of current line.
     */
    private void goLineStart() {
        final int curX = this.curCursor.getX();
        this.goBackspace(curX);
    }
    
    
    
    /**
     * Real console behavior imitator logic after special char entered.
     * @param cmdChar special character to analyze
     * @throws IllegalArgumentException it is not a 'char'
     */
    public void process(final String cmdChar)
                    throws IllegalArgumentException {
        if ( cmdChar.length() != 1 ) {
            String excMsg = "Incorrect length of special ASCII character: '"
                                + cmdChar.length() + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        // '\n':
        if ( cmdChar.equals(ConUt.LF) ) {
            // just new line
            this.setNewLine();
        }
        //
        // '\b':
        if ( cmdChar.equals(ConUt.BS) ) {
            // just one char back if possible
            this.goBackspace();
        }
        //
        // '\t':
        if ( cmdChar.equals(ConUt.HT) ) {
            // add standart margin if zone allows
            this.addTab();
        }
        //
        // 0x07 - beep ('\a'):
        if ( cmdChar.equals(ConUt.BEL) ) {
            // do 'beep' from PC speaker
            this.doBeep();
        }
        //
        // 0x0B - vertical tabulation
        if ( cmdChar.equals(ConUt.VT) ) {
            // pass vertical tabulation
            this.addVerticalTab();
        }
        //
        // '\r':
        if ( cmdChar.equals(ConUt.CR) ) {
            // to the begining of the line
            this.goLineStart();
        }
    }
    
    
    
}
