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
    private final int allowedWidth;
    
    //
    
    /**
     * @param initCurCursor pointer to real coordinates
     * @param initAllowedWidth X-axis border
     * @throws NullPointerException when does not get real coordinates
     */
    public ConWinOutSpecChars(final int initAllowedWidth)
                                            throws NullPointerException {
        this.curCursor = null;
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
    
    /**
     * We need actual cursor state before every operation.
     * Update it manually via parameter.
     * @param cursorToInstall
     * @throws NullPointerException when cursor position was not given
     */
    private void updateCursor(final ConCord cursorToInstall)
                                        throws NullPointerException {
        if ( null == cursorToInstall ) {
            String excMsg = "Console cursor position is not specified (is null)";
            throw new NullPointerException(excMsg);
        }
        //
        this.curCursor = new ConCord(cursorToInstall);
    }
    
    //
    
    /**
     * Outer caller to recalculate position in the area
     * to set a new line.
     * LF (\n) analog, in coordinates.
     * Need the method to have the same code upper in ConCan-hierarchy.
     * @param curCursor current cursor position
     * @return new console coordinates position
     */
    public ConCord callNewLine(final ConCord curCursor) {
        this.updateCursor(curCursor);
        //
        return this.setNewLine();
    }
    
    
    
    /**
     * Move cursor on new line in the area.
     * Calculate coordinates, does not move cursor directly.
     * Special case for new lines with direct return value 
     * because of possible out calling ('callNewLine').
     * @param xToSet where cursor will appear via X-axis
     * @return new console coordinates position
     * @throws NullPointerException when pointer to strings archive (storage) is absent
     * @see linkStorage()
     */
    private ConCord setNewLine(final int xToSet)
                        throws NullPointerException {
        if ( null == this.curStorage ) {
            // strings archive is necessary but is absent
            String excMsg = "Lines archive (storage) is null (not linked)";
            throw new NullPointerException(excMsg);
        }
        //
        final int yToSet = this.curCursor.getY() + 1;
        this.curCursor = new ConCord(xToSet, yToSet);
        //
        this.curStorage.storeNewLine();// add new line in zone's data
        //
        return this.curCursor;
    }
    private ConCord setNewLine() {
        return this.setNewLine(0);
    }
    
    /**
     * Move one char back while we are in the zone.
     * @param backSteps how many symbols back we will step
     */
    private void goBackspace(final int backSteps) {
        final int curX = this.curCursor.getX();
        final int xToSet = ((curX - backSteps) > 0)
                                ? (curX - backSteps)
                                : 0;
        final int yToSet = this.curCursor.getY();
        this.curCursor = new ConCord(xToSet, yToSet);
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
        final int oldY = this.curCursor.getY();
        this.curCursor = new ConCord(newX, oldY);
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
     * @param curCursor current cursor position
     * @return new console coordinate position
     * @throws IllegalArgumentException it is not a 'char'
     */
    public ConCord process(final String cmdChar,
                            final ConCord curCursor)
                                throws IllegalArgumentException {
        if ( cmdChar.length() != 1 ) {
            String excMsg = "Incorrect length of special ASCII character: '"
                                + cmdChar.length() + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.updateCursor(curCursor);
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
        //
        return this.curCursor;
    }
    
    
    
}
