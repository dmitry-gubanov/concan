package me.dmitrygubanov40.concan.windows;

import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.ConUt;
import me.dmitrygubanov40.concan.winbuffer.*;



/**
 * Output console zone of window.
 * Essential part of any window with inner refreshment.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinOut implements WinBufEventListener
{
    
    // tabulation column size
    private final static int TAB_SIZE;
    
    
    static {
        TAB_SIZE = 4;
    }
    
    /////////////////////////////////
    
    // console manipulation helper
    // (no window's buffer connection)
    private final ConUt consoleTool;
    
    // outout console buffer for this window
    private final WindowOutputBuffer zoneBuf;
    
    // start point of output zone
    private ConCord zonePosition;
    
    // current cursor position in zone
    private ConCord zoneCursorPos;
    
    // width and height of buffer output zone
    // must be less than parent window
    private int zoneWidth;
    private int zoneHeight;
    
    // maximum in coordinates terminal
    // allows us to put chars
    private ConCord terminalMaxCoords;
    
    
    ////////////
    
    /**
     * Closed constructor of a output window zone.
     * See 'startNewZone'.
     * @param setWidth width
     * @param setHeight height
     * @param setPos first-point coordinate of the zone
     * @param isSetSafeAsync is our buffer ready to work in async-regime?
     */
    private ConWinOut(final int setWidth, final int setHeight,
                        final ConCord setPos,
                        final boolean isSetSafeAsync) {
        this.consoleTool = new ConUt();
        //
        // buffer size is the same as zone's width:
        this.zoneBuf = new WindowOutputBuffer(setWidth, isSetSafeAsync);
        this.installEvents();
        //
        this.zonePosition = setPos;
        this.zoneCursorPos = new ConCord(0, 0);
        //
        this.zoneWidth = setWidth;
        this.zoneHeight = setHeight;
        //
        this.terminalMaxCoords = ConUt.getTerminalMaxCoord();
    }
    
    /**
     * Notice-free installation of a listener in the constructor.
     */
    private void installEvents() {
        if ( this.zoneBuf.isAlreadyListener(this) ) {
            // is already a listener
            return;
        }
        //
        this.zoneBuf.addEventListener(this);
    }
    
    /**
     * Static constructor of a output window zone.
     * @param setWidth width
     * @param setHeight height
     * @param setPos first-point coordinate of the zone
     * @param isSetSafeAsync is our buffer ready to work in async-regime?
     * @return pointer toward a new window output zone
     */
    public static ConWinOut startNewZone(final int setWidth, final int setHeight,
                        final ConCord setPos,
                        final boolean isSetSafeAsync) {
        return new ConWinOut(setWidth, setHeight, setPos, isSetSafeAsync);
    }
    
    
    ////////////////////////////
    // Events
    ////////////////////////////
    
    /**
     * Head caller of all events.
     * @param event which we got from buffer
     */
    @Override
    public void onWindowOutputBufferEvent(final WinBufEvent event) {
        WinBufEventType eventType = event.getEventType();
        //
        if ( WinBufEventType.ON_BEFORE_FLUSH == eventType ) {
            this.onBeforeFlush(event);
        }
        /* do not need now
        if ( WinBufEventType.ON_AFTER_FLUSH == eventType ) {
            this.onAfterFlush(event);
        }
        */
        //
        /* do not need now
        if ( WinBufEventType.ON_BEFORE_AUTOFLUSH == eventType ) {
            this.OnBeforeAutoflush(event);
        }
        if ( WinBufEventType.ON_AFTER_AUTOFLUSH == eventType ) {
            this.OnAfterAutoflush(event);
        }
        */
        //
        if ( WinBufEventType.ON_BEFORE_CMD_SENT == eventType ) {
            this.OnBeforeCmdSent(event);
        }
        if ( WinBufEventType.ON_AFTER_CMD_SENT == eventType ) {
            this.OnAfterCmdSent(event);
        }
        //
        if ( WinBufEventType.ON_BEFORE_OUTPUT_CHAR == eventType ) {
            this.onBeforeOutputChar(event);
        }
        if ( WinBufEventType.ON_AFTER_OUTPUT_CHAR == eventType ) {
            this.OnAfterOutputChar(event);
        }
        if ( WinBufEventType.ON_BEFORE_OUTPUT_CMD == eventType ) {
            this.OnBeforeOutputCmd(event);
        }
        if ( WinBufEventType.ON_AFTER_OUTPUT_CMD == eventType ) {
            this.OnAfterOutputCmd(event);
        }
    }
    
    
    
    /**
     * Reaction at the beginning of any command to be executed.
     * Find mother-buffer and flush it.
     * @param event 
     */
    private void OnBeforeCmdSent(final WinBufEvent event) {
        final WindowOutputBuffer eventBuffer = (WindowOutputBuffer) event.getSource();
        //
        // everything before the command will be output:
        eventBuffer.flush();
    }
    
    /**
     * Reaction after the the command was added.
     * Any single special char must be processed (including cursor behavior),
     * and non-blocked escape sequences (checked by winbuf earlier) must be immediately printed.
     * @param event 
     */
    private void OnAfterCmdSent(final WinBufEvent event) {
        final WindowOutputBuffer eventBuffer = (WindowOutputBuffer) event.getSource();
        final String outStr = event.getEventText();
        //
        if ( outStr.length() == 1 ) {
            // case of single special character - 
            // synchronize de-facto behavior and console condition
            this.processSpecialChar(outStr, eventBuffer);
        } else if ( outStr.length() > 1 ) {
            // case of escape sequence - immediately flush it to be shown
            eventBuffer.flush();
        }
    }
    
    
    
    /**
     * Reaction at the beginning of any 'flush' operation.
     * Process the case when the flushing string will overcome the possible width.
     * @param event 
     */
    private void onBeforeFlush(final WinBufEvent event) {
        // here in the flag we have string length evaluation
        final int outStrLength = event.getEventFlags();
        //
        final int alreadyPrintedLength = this.zoneCursorPos.getX();
        //
        if ( alreadyPrintedLength + outStrLength > this.zoneWidth ) {
            // output of the current buffer will overstep window's width
            // slice out possible symbols, and go next line
            final WindowOutputBuffer eventBuffer = (WindowOutputBuffer) event.getSource();
            int sliceLength = this.zoneWidth - alreadyPrintedLength;
            //
            if ( sliceLength > 0 ) {
                eventBuffer.sliceOut(sliceLength);
            }
            this.setNewLine();
        }
    }
    
    /* do not need now
    private void onAfterFlush(final WinBufEvent event) {
        final String outStr = event.getEventText();
        final int outStrLength = event.getEventFlags();
    }
    */
    
    
    
    /* do not need now
    private void OnBeforeAutoflush(final WinBufEvent event) {
        final String outStr = event.getEventText();
        final int outStrLength = event.getEventFlags();
    }
    */
    
    /* do not need now
    private void OnAfterAutoflush(final WinBufEvent event) {
        final String outStr = event.getEventText();
        final int outStrLength = event.getEventFlags();
    }
    */
    
    
    
    /**
     * Reaction at the beginning of any visual symbol putting.
     * Have to prepare cursor position.
     * @param event 
     */
    private void onBeforeOutputChar(final WinBufEvent event) {
        // before output to zone must move console cursor
        this.takeTerminalCursorPosition();
    }
    
    /**
     * Reaction after the character was put into console.
     * Must recalculate current cursor position in the zone.
     * @param event 
     */
    private void OnAfterOutputChar(final WinBufEvent event) {
        final int outputLength = event.getEventFlags();
        this.zoneCursorPos.setX(this.zoneCursorPos.getX() + outputLength);
    }
    
    /**
     * Reaction at the beginning of command output.
     * Also, it better to prepare cursor position.
     * @param event 
     */
    private void OnBeforeOutputCmd(final WinBufEvent event) {
        this.takeTerminalCursorPosition();
    }
    
    /**
     * Reaction after the command (special char/ESC-sequence)
     * was put into console.
     * After command we do not need to move cursor in the zone.
     * @param event 
     */
    private void OnAfterOutputCmd(final WinBufEvent event) {
        // ...
    }
    
    ////////////////////////////
    // end of events block
    ////////////////////////////
    
    
    /**
     * Call the zone to add this string to output zone.
     * @param str text line we will add to the zone
     */
    public void addToZone(final String str) {
        if ( str.length() <= 0 ) {
            // no empty strings
            return;
        }
        //
        this.zoneBuf.addToWinBuf(str);
    }
    
    /**
     * Output everything we already have in buffer.
     */
    public void flush() {
        //
        this.zoneBuf.flush();
        //
    }
    
    /**
     * Move cursor to the position, where cursor must be in the zone
     * for the output.
     */
    private void takeTerminalCursorPosition() {
        ConCord curTerminalPos = this.getTerminalZonePos();
        this.consoleTool.sendGoto(curTerminalPos);
    }
    
    
     
    ///////////////////////////////
    // special characters:
    
    /**
     * Get actual coordinates in terminal's console.
     * @return point in the terminal
     */
    private ConCord getTerminalZonePos() {
        ConCord curZonePos = this.zonePosition.plus(this.zoneCursorPos);
        return curZonePos;
    }
    
    /**
     * Move cursor on new line of the zone.
     * Output to the line is finished.
     * Calculate coordinates, does not move cursor directly.
     * @param xToSet where cursor will appear via X-axis
     */
    private void setNewLine(final int xToSet) {
        this.zoneCursorPos.setX(xToSet);
        this.zoneCursorPos.setY(this.zoneCursorPos.getY() + 1);
    }
    private void setNewLine() {
        this.setNewLine(0);
    }
    
    /**
     * Move one char back while we are in the zone.
     */
    private void goBackspace(final int backSteps) {
        int curX = this.zoneCursorPos.getX();
        final int xToSet = ((curX - backSteps) > 0)
                                ? (curX - backSteps)
                                : 0;
        this.zoneCursorPos.setX(xToSet);
    }
    private void goBackspace() {
        this.goBackspace(1);
    }
    
    /**
     * Move cursor right on tabulation size in chars, while it is
     * not end of the zone.
     */
    private void addTab() {
        final int curX = this.zoneCursorPos.getX();
        int newX = curX;// will set it
        // how many times TAB can be placed in the zone:
        final int stepsNmb = this.zoneWidth / ConWinOut.TAB_SIZE;
        //
        for ( int j = 1; j <= stepsNmb; j++ ) {
            final int curTabX = j * ConWinOut.TAB_SIZE;
            if ( curTabX <= this.zoneWidth
                    && curTabX > curX ) {
                newX = curTabX;
                break;
            }
        }
        //
        this.zoneCursorPos.setX(newX);
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
        final int curX = this.zoneCursorPos.getX();
        if ( curX > 0 && curX < this.zoneWidth ) this.setNewLine(curX);
    }
    
    /**
     * Move cursor to the beginning of current line.
     */
    private void goLineStart() {
        final int curX = this.zoneCursorPos.getX();
        this.goBackspace(curX);
    }
    
    
    
    /**
     * Imitate real console behavior in the zone by ASCII control chars.
     * Is called after command character was flushed in buffer.
     * Only for number of special ASCII symbols.
     * @param cmdChar special character we got
     * @param buf operated zone's buffer
     * @throws IllegalArgumentException it is not a 'char'
     */
    private void processSpecialChar(final String cmdChar,
                                    final WindowOutputBuffer buf)
                        throws IllegalArgumentException {
        if ( cmdChar.length() != 1 ) {
            String excMsg = "Incorrect length of special ASCII character: '"
                                + cmdChar.length() + "'";
            throw new IllegalArgumentException(excMsg);
        }
        if ( cmdChar.equals(ConUt.ESC) ) return;// 'ESC' is not processed by the method
        //
        // delete just added single character
        buf.deleteLastChar();
        //
        // '\n':
        if ( cmdChar.equals(ConUt.LF) ) {
            // just new line
            this.setNewLine();
        }
        // '\b':
        if ( cmdChar.equals(ConUt.BS) ) {
            // just one char back if possible
            this.goBackspace();
        }
        // '\t':
        if ( cmdChar.equals(ConUt.HT) ) {
            // add standart margin if zone allows
            this.addTab();
        }
        // 0x07 - beep ('\a'):
        if ( cmdChar.equals(ConUt.BEL) ) {
            // add standart margin if zone allows
            this.doBeep();
        }
        // 0x0B - vertical tabulation
        if ( cmdChar.equals(ConUt.VT) ) {
            // add standart margin if zone allows
            this.addVerticalTab();
        }
        // '\r':
        if ( cmdChar.equals(ConUt.CR) ) {
            // to the begining of the line
            this.goLineStart();
        }
        //
        // need 'flush' to recalculate buffer length and zone status
        buf.flush();
    }
    
    // end of special chars block
    ///////////////////////////////
    
    
    
}
