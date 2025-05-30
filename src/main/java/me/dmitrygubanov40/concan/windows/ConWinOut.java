package me.dmitrygubanov40.concan.windows;


import java.util.ArrayList;

import me.dmitrygubanov40.concan.paint.ConDraw;
import me.dmitrygubanov40.concan.paint.ConDrawFill;
import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.ConUt;
import me.dmitrygubanov40.concan.utility.Term;
import me.dmitrygubanov40.concan.winbuffer.*;



/**
 * Output console zone of window.
 * Essential part of any window with inner refreshment.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinOut implements WinBufEventListener
{
    
    // console manipulation helper
    // (no window's buffer connection)
    private final ConUt consoleTool;
    
    // outout console buffer for this window
    private final WindowOutputBuffer zoneBuf;
    private final boolean isAsyncSafe;
    
    // start point of output zone
    private ConCord zonePosition;
    
    // current cursor position in the zone
    // i.e. can have height over 'zoneHeight'
    private ConCord zoneCursorPos;
    
    // number of lines has passed up into 'invisibility'
    // (if it is a scrollable zone)
    private int zoneCursorScrolledDown;
    
    // width and height of buffer output zone
    // must be less than parent window
    private int zoneWidth;
    private int zoneHeight;
    
    
    // special chars manipulator helper (for console)
    private ConWinOutSpecChars specialCharProcessor;
    
    
    // 'archive' of data has been output
    private ConWinOutStorage storage;
    
    
    // Should the output scroll down as in real terminal,
    // or attempt to step over vertical border
    // put cursor on the first line?
    private boolean isZoneScrollable;
    
    
    // clear everything in the zone before output
    private boolean isZoneToClear;
    
    // helper to keep all the styles in the zone
    private ConWinOutBrush zoneBrush;
    
    // use of SAVE/RESTORE every time we use (auto-) flush
    private boolean useTermRestore;
    
    
    ////////////
    
    /**
     * Closed constructor of a output window zone.
     * @see 'startNewZone()'.
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
        this.isAsyncSafe = isSetSafeAsync;
        //
        this.zoneWidth = setWidth;
        this.zoneHeight = setHeight;
        //
        this.zonePosition = setPos;
        this.zoneCursorPos = new ConCord(0, 0);
        this.zoneCursorScrolledDown = 0;
        this.isZoneScrollable = this.canBeScrollable();// default zone will automatically scroll down if can
        //
        this.isZoneToClear = true;
        //
        this.specialCharProcessor = new ConWinOutSpecChars(this.zoneWidth);
        //
        this.zoneBrush = new ConWinOutBrush();
        //
        this.useTermRestore = true;
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
     * Static constructor of the basic output window zone.
     * Has regular storage (default size), and is scrollable.
     * @param setWidth width
     * @param setHeight height
     * @param setPos first-point coordinate of the zone
     * @param setClearState regime of clearing before output
     * @param isSetSafeAsync is our buffer ready to work in async-regime?
     * @param setTermSaveStatus use or not SAVE/RESTORE
     * @return pointer toward a new window output zone
     */
    public static ConWinOut startNewZone(final int setWidth, final int setHeight,
                        final ConCord setPos,
                        final boolean setClearState,
                        final boolean isSetSafeAsync,
                        final boolean setTermSaveStatus) {
        ConWinOut newZone = new ConWinOut(setWidth, setHeight, setPos, isSetSafeAsync);
        newZone.setTermSaveStatus(setTermSaveStatus);
        newZone.startNewStorage();
        newZone.setClearZoneRegime(setClearState);
        if ( newZone.getClearZoneRegime() ) {
            // when created with 'clear' mode - clear the zone
            newZone.initClearZone();
        }
        //
        return newZone;
    }
    // clear mode - on, and async mode - off - creator
    public static ConWinOut startNewZone(final int setWidth,
                                            final int setHeight,
                                            final ConCord setPos) {
        final boolean DEFAULT_CLEAR_STATUS = true;
        final boolean DEFAULT_ASYNC_STATUS = false;
        final boolean DEFAULT_TERM_SAVE_STATUS = true;
        return ConWinOut.startNewZone(setWidth, setHeight,setPos,
                                        DEFAULT_CLEAR_STATUS,
                                        DEFAULT_ASYNC_STATUS,
                                        DEFAULT_TERM_SAVE_STATUS);
    }
    
    /**
     * Restart storage.
     * All previous data will be erased.
     * Also, links new storage to special symbols processor.
     * @param initLimit number of lines in storage to be saved
     */
    public void startNewStorage(final int initLimit) {
        this.storage = new ConWinOutStorage(this.isAsyncSafe,
                                            this.zoneWidth,
                                            this.zoneHeight,
                                            initLimit);
        this.specialCharProcessor.linkStorage(this.storage);
    }
    public void startNewStorage() {
        this.storage = new ConWinOutStorage(this.isAsyncSafe,
                                            this.zoneWidth,
                                            this.zoneHeight);
        this.specialCharProcessor.linkStorage(this.storage);
    }
    
    /**
     * Make the zone to work without strings archive.
     * Links off-storage to special symbols processor.
     * Also, scrolling is automatically off
     * (because it is impossible to scroll without buffer).
     */
    public void turnOffStorage() {
        this.storage = new ConWinOutStorage(this.isAsyncSafe,
                                            this.zoneWidth,
                                            this.zoneHeight);
        this.storage.turnOff();
        //
        this.setScrollable(false);// cannot scroll w/o the archive
        //
        this.specialCharProcessor.linkStorage(this.storage);
    }
    
    /**
     * Whether or not clear all the space before output.
     * Clearing is done by default, use to switch.
     * @param setToClear 
     */
    public void setClearZoneRegime(final boolean setToClear) {
        this.isZoneToClear = setToClear;
    }
    public boolean getClearZoneRegime() {
        return this.isZoneToClear;
    }
    
    /**
     * Control of save terminal condition, and restoring it (or not).
     * May be useful for temporary inner zones.
     * @param setStatus will we use SAVE/RESTORE during output in the zone?
     */
    private void setTermSaveStatus(final boolean setStatus) {
        this.useTermRestore = setStatus;
    }
    public boolean getTermSaveStatus() {
        return this.useTermRestore;
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
        if ( WinBufEventType.ON_AFTER_FLUSH == eventType ) {
            this.onAfterFlush(event);
        }
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
            // case of single special character,
            // synchronize de-facto behavior and console condition
            this.processSpecialChar(outStr, eventBuffer);
        } else if ( outStr.length() > 1 ) {
            // case of escape sequence - flush it to be shown
            eventBuffer.flush();
        }
        // Keep command output for history straight after it was added:
        // 'flush()' or processing at any case will immediatelly output it.
        this.storage.saveOutputCmd(outStr);
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
        // keep coordinates and style of output before output
        if ( this.getTermSaveStatus() ) {
            Term.get().save();
        }
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
            //
            // new line via 'ConWinOutSpecChars':
            // move one line down, save the new line in 'storage'-archive.
            this.zoneCursorPos = this.specialCharProcessor.callNewLine(this.zoneCursorPos);
            //
        }
    }
    
    /**
     * Reaction after the 'flush' operation.
     * Is called after every single portion of printing to the console.
     * @param event 
     */
    private void onAfterFlush(final WinBufEvent event) {
        //final String outStr = event.getEventText();
        //final int outStrLength = event.getEventFlags();
        //
        // Save current brush to the archive of this line (can be updated).
        // Crucial to do it here as long as 'flush' is used at each output.
        this.storage.saveLineBrush(this.zoneBrush);
        //
        // after the output we return to the style conditions
        // of terminal (it had been saved before output)
        if ( this.getTermSaveStatus() ) {
            Term.get().restore();
        }
    }
    
    
    
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
        try {
            //
            this.takeTerminalCursorPosition();
            //
        } catch ( OutOfTerminalWindowException termBorderExc ) {
            // failed to move cursor to the necessary position
            // the symbol will not be printed into console
            event.updateEventStatus(WinBufEventStatus.WB_EVENT_IGNORE);
            // now will be moved somewhere in the nearest position
            this.consoleTool.sendGoto( termBorderExc.getAllowedCoords() );
        }
        //
        // before symbols are printed proof we will not go over borders
        while ( this.isOverHeight() ) {
            // should scroll down when output is back in the zone
            if ( this.isScrollable() ) this.scrollDown();
            else this.moveCursorIntoBorder();
        }
        //
        // now restore brush settings - output necessary commands
        final String brushToRestore = this.zoneBrush.getBrush();
        System.out.print(brushToRestore);
    }
    
    /**
     * Reaction after the character was put into console.
     * Must recalculate current cursor position in the zone,
     * and then save the output which had happened.
     * @param event 
     */
    private void OnAfterOutputChar(final WinBufEvent event) {
        final String outStr = event.getEventText();
        final int outputLength = event.getEventFlags();
        //
        final int newX = this.zoneCursorPos.getX() + outputLength;
        final int oldY = this.zoneCursorPos.getY();
        this.zoneCursorPos = new ConCord(newX, oldY);
        // keep text output for history after de-facto printing
        this.storage.saveOutput(outStr, this.zoneWidth);
    }
    
    /**
     * Reaction at the beginning of command output.
     * Also, it better to prepare cursor position.
     * @param event 
     */
    private void OnBeforeOutputCmd(final WinBufEvent event) {
        try {
            //
            this.takeTerminalCursorPosition();
            //
        } catch ( OutOfTerminalWindowException termBorderExc ) {
            // event prolongations will not be stoped
            // move cursor to the nearest char's block
            this.consoleTool.sendGoto( termBorderExc.getAllowedCoords() );
        }
    }
    
    /**
     * Reaction after the command (special char/ESC-sequence)
     * was put into console.
     * After command we do not need to move cursor in the zone.
     * The last output will keep the style and color of output.
     * @param event 
     */
    private void OnAfterOutputCmd(final WinBufEvent event) {
        final String outStr = event.getEventText();
        //final int outStrLength = event.getEventFlags();
        //
        // update current brush (if it is brush command)
        // maybe it is a font color or a font background, etc.
        this.zoneBrush.analyseAndUpdateBrush(outStr);
    }
    
    
    ////////////////////////////
    // end of events block
    ////////////////////////////
    
    
    
    /**
     * Call the zone to add this string to output zone.
     * @param str text line we will add to the zone
     */
    private void addToZone(final String str) {
        if ( null == str || str.length() <= 0 ) {
            // no empty strings
            return;
        }
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
     * Add output from outter source.
     * @param str text line we will add to the zone
     */
    public void print(final String str) {
        if ( null == str || str.length() <= 0 ) {
            // no empty strings
            return;
        }
        //
        final String strToAdd = this.parseLFs(str);
        //
        this.addToZone(strToAdd);
    }
    
    /**
     * Directly print some text in the zone, borders-safe.
     * It is overlay output over already printed text from the buffer,
     * and it does not correspond with the buffer anyway.
     * 'str' will be just put over in the terminal's console,
     * using the zone's borders.
     * @param str what to print in the zone
     * @param clearBeforePrint do we clear the zone before printing?
     * @param coords where to start printing (counting from the zone's start point)
     * @param width text area we want width
     * @param height text area we want height
     * @throws IllegalArgumentException when requested to print out of zone borders
     */
    public void stamp(final String str,
                                final boolean clearBeforePrint,
                                final ConCord coords,
                                final int width,
                                final int height)
                    throws IllegalArgumentException {
        if ( null == str || null == coords ) {
            // incorrect arguments
            return;
        }
        //
        final ConCord printZoneCoords = this.zonePosition.plus(coords);
        //
        final int printZoneWidthMax = this.zoneWidth - coords.getX();
        final int printZoneHeightMax = this.zoneHeight - coords.getY();
        //
        if ( printZoneWidthMax <= 0 || printZoneHeightMax <= 0
                || width > printZoneWidthMax || height > printZoneHeightMax ) {
            // cannot start output away from the zone coordinates
            String excMsg = "Printing in the window zone initiated out of zone borders."
                                + " Requested coordinates: " + coords
                                + ", zone width: " + this.zoneWidth
                                + ", zone height: " + this.zoneHeight
                                + ", text area width: " + width
                                + ", text area height: " + height;
            throw new IllegalArgumentException(excMsg);
        }
        //
        final boolean USE_TERM_RESTORE = false;// do not need influence on terminal state savings
        ConWinOut printZone = ConWinOut.startNewZone(width,
                                                        height,
                                                        printZoneCoords,
                                                        clearBeforePrint,
                                                        this.isAsyncSafe,
                                                        USE_TERM_RESTORE);
        printZone.turnOffStorage();// unscrollable zone without buffer
        printZone.addToZone(str);
        printZone.flush();
    }
    public void stamp(final String str) {
        // text area w/o clearing in all the zone
        final boolean CLEAR_BEFORE_PRINT_STATUS = false;
        final ConCord POSITION = new ConCord(0, 0);
        this.stamp(str,
                    CLEAR_BEFORE_PRINT_STATUS, POSITION,
                    this.zoneWidth, this.zoneHeight);
    }
    public void stampClear(final String str) {
        // text area w/o clearing in all the zone
        final boolean CLEAR_BEFORE_PRINT_STATUS = true;
        final ConCord POSITION = new ConCord(0, 0);
        this.stamp(str,
                    CLEAR_BEFORE_PRINT_STATUS, POSITION,
                    this.zoneWidth, this.zoneHeight);
    }
    
    /**
     * Inner function to wipe away the last line in the output zone
     * (to make it 'clean' before new output).
     */
    private void clearLastLine() {
        ConCord lastLineStart = new ConCord(0, this.zoneHeight - ConCord.SHIFT_Y);
        ConCord lastLineEnd = new ConCord(this.zoneWidth - ConCord.SHIFT_X,
                                            this.zoneHeight - ConCord.SHIFT_Y);
        // transfer to all console coords:
        ConCord lastLineBarStart = this.zonePosition.plus(lastLineStart);
        ConCord lastLineBarEnd = this.zonePosition.plus(lastLineEnd);
        //
        ConDraw painter = new ConDraw( new ConDrawFill() );
        painter.setTermSaveStatus(false)
                .drawBar(lastLineBarStart, lastLineBarEnd);
    }
    
    
    
    /**
     * @return can the zone theoretically be scrollable or not?
     */
    private boolean canBeScrollable() {
        // no storage - no scrolling:
        if ( null == this.storage ) return false;
        //
        // only 2+ lines zone can be scrolled
        final boolean correctHeight = (this.zoneHeight > 1);
        // at least (N+1) full lines in memory
        final boolean enoughLines = (this.storage.getLinesLimit() >= this.zoneHeight);
        //
        final boolean res = correctHeight && enoughLines;
        //
        return res;
    }
    
    /**
     * Set new status, if zone is scrollable, or not.
     * @param setScrollable
     * @throws IllegalArgumentException when wants to install inappropriate status
     */
    public void setScrollable(final boolean setScrollable) {
        if ( !this.canBeScrollable() && setScrollable ) {
            // the zone cannot be scrollable, but user insist it to be so
            String excMsg = "Window zone cannot set up to be scrollable";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.isZoneScrollable = setScrollable;
    }
    
    /**
     * The window zone must be set up to be scrollable, and can be such.
     * @return window zone scrollable status
     */
    private boolean isScrollable() {
        return ( this.isZoneScrollable && this.canBeScrollable() );
    }
    
    
    
    /**
     * Get actual coordinates in terminal's console.
     * @return point in the terminal
     */
    private ConCord getTerminalZonePos() {
        ConCord curZonePos = this.zonePosition.plus(this.zoneCursorPos);
        //
        // take into account scrolled lines:
        final int yPosWoScrolled = curZonePos.getY() - this.zoneCursorScrolledDown;
        //
        return new ConCord(curZonePos.getX(), yPosWoScrolled);
    }
    
    /**
     * Move cursor to the position, where cursor must be in the zone
     * for the output.
     * @throws OutOfTerminalWindowException when gets out of terminal window
     */
    private void takeTerminalCursorPosition() throws OutOfTerminalWindowException {
        ConCord curTerminalPos = this.getTerminalZonePos();
        //
        if ( curTerminalPos.getX() > Term.get().maxX()
                || curTerminalPos.getY() > Term.get().maxY() ) {
            String excMsg = "Cannot move away from terminal window."
                                + " Need to move cursor to: " + curTerminalPos
                                + ", maximum coordinates are: " + Term.get().maxCoord();
            final int excX = (curTerminalPos.getX() > Term.get().maxX())
                                ? Term.get().maxX()
                                : curTerminalPos.getX();
            final int excY = (curTerminalPos.getY() > Term.get().maxY())
                                ? Term.get().maxY()
                                : curTerminalPos.getY();
            ConCord excCoords = new ConCord(excX, excY);
            //
            throw new OutOfTerminalWindowException(excMsg, excCoords);
        }
        //
        this.consoleTool.sendGoto(curTerminalPos);
    }
    
    
    
    /**
     * Checks the current cursor in the zone if it is necessary to scroll one line down.
     * Supposed to be called after cursor was moved to output, but
     * characters are not put yet.
     * @return 'true' when scroll is available and necessary
     */
    private boolean isOverHeight() {
        // shift-plus to switch coordinates to height scale
        final int curPosHeight = this.zoneCursorPos.getY() + ConCord.SHIFT_Y;
        final int maxHeightAllowed = this.zoneHeight + this.zoneCursorScrolledDown;
        //
        final int linesOver = curPosHeight - maxHeightAllowed;
        //
        return (linesOver > 0);
    }
    
    /**
     * Make cursor to jump if it is going to leave the zone.
     * After the last line cursor will move to the first to continue output.
     */
    private void moveCursorIntoBorder() {
        // zone can scroll - ignore:
        if ( this.isScrollable() ) return;
        //
        final int moveX = this.zoneCursorPos.getX();
        final int moveY = (this.zoneCursorPos.getY() % this.zoneHeight);
        if ( moveY != this.zoneCursorPos.getY() ) {
            // cursor moved up - clear the space for new output
            if ( true == this.getClearZoneRegime() ) this.clearZone();
        }
        this.zoneCursorPos = new ConCord(moveX, moveY);
        this.takeTerminalCursorPosition();// re-calculate cursor position
    }
    
    /**
     * Perform the scrolling of one line down.
     * Suppose cursor is already out of the zone's height.
     * Previous lines will be put other the zone to make the last line to look correct.
     * Base idea is to use non-scrollable zone to overlay print lines to imitate scrolling.
     * @throws IllegalStateException when one line zone is to scroll (use 'moveCursorIntoBorder()')
     */
    private void scrollDown() throws IllegalStateException {
        // zone cannot scroll - so, ignore:
        if ( !this.isScrollable() ) return;
        //
        ArrayList<String> prevLines = this.storage.getSavedOutputLines();
        //
        // Install new, temp zone for output of storage lines.
        // The temporary zone must be one line shoter:
        final int tempZoneHeight = this.zoneHeight - 1;
        final boolean CLEAR_SCROLL_ZONE = true;
        final boolean USE_TERM_RESTORE = false;// don't want to use terminal's SAVE/RESTORE
        ConWinOut tempScrollZone = ConWinOut.startNewZone(this.zoneWidth,
                                                            tempZoneHeight,
                                                            this.zonePosition,
                                                            CLEAR_SCROLL_ZONE,
                                                            this.isAsyncSafe,
                                                            USE_TERM_RESTORE);
        // It must be unscrollable zone without buffer
        // (only one output):
        tempScrollZone.turnOffStorage();
        tempScrollZone.setScrollable(false);
        //
        // We use the brush from the last hidden line.
        // And after output all stored lines one by one which can be seen
        // in the zone with height 'tempZoneHeight' (such number of lines).
        final int lastHiddenLineIndex = (prevLines.size() - 1) - tempZoneHeight - 1;
        //
        // Important: when 'lastHiddenLineIndex' is less than zero it means
        // output zone storage does not have enough saved lines.
        // It is a runtime issue because we cannot "build" smooth scrolling.
        if ( lastHiddenLineIndex < 0 ) {
            String excMsg = "Text in window output zone cannot be scrolled:"
                                + " not enough lines saved in output zone";
            throw new IllegalStateException(excMsg);
        }
        //
        final int firstShownLineIndex = lastHiddenLineIndex + 1;
        //
        // add previous brush (before real chars output):
        final ConWinOutBrush lastHiddenLineBrush = this.storage.getBrushOfLineByIndex(lastHiddenLineIndex);
        String lastHiddenLineBrushCmd = lastHiddenLineBrush.getBrush();
        tempScrollZone.addToZone(lastHiddenLineBrushCmd);
        //
        for ( int i = firstShownLineIndex; i < prevLines.size(); i++ ) {
            String curPrevLine = prevLines.get(i);
            //
            tempScrollZone.addToZone(curPrevLine);
            // if length of 'curPrevLine' less than zone's width it is necessary,
            // but if it fits the width - LF-char won't affect visualization.
            tempScrollZone.addToZone(ConUt.LF);
        }
        //
        tempScrollZone.flush();
        //
        this.clearLastLine();// letters will be put in clean space
        //
        //
        this.zoneCursorScrolledDown++;// remember how many lines have been scrolled
        //
        // re-calculate cursor position within the zone after scrolling
        this.takeTerminalCursorPosition();
    }
    
    
    
    /**
     * Cover all the zone with the filling parameter.
     * Printed as overlay. Does not interact with the buffer.
     * @param filling how to fill the entire zone
     * @param termRestStatus do we do save-restore commands?
     * @throws NullPointerException when filling parameters are empty
     */
    private void fillZone(final ConDrawFill filling, final boolean termRestStatus) {
        if ( null == filling ) {
            String excMsg = "Filling parameters for the zone are null";
            throw new NullPointerException(excMsg);
        }
        //
        final ConCord leftTop = this.zonePosition;
        // carefully re-calculate right-bottom coordinates via non-coordinates
        // (zone width and height are not given in math coordinates)
        final ConCord rightBottomCoordsToAdd = new ConCord(this.zoneWidth, this.zoneHeight)
                                                    .removeConsoleShift();
        final ConCord rightBottom = this.zonePosition.plus(rightBottomCoordsToAdd);
        // 'ConDraw' saves and restore output styles itself
        ConDraw painter = new ConDraw(filling);
        painter.setTermSaveStatus(termRestStatus)
                .drawBar(leftTop, rightBottom);
    }
    
    /**
     * Cover all the zone with the the default colors and an empty char.
     * Inner method during zone output (do not use Terminal restoration).
     */
    private void clearZone() {
        final boolean TERM_RESTORATION = false;
        final ConDrawFill clearFill = new ConDrawFill();
        this.fillZone(clearFill, TERM_RESTORATION);
    }
    /**
     * Cover all the zone with the the default colors and an empty char.
     * Initialization function just in the constructor.
     */
    private void initClearZone() {
        final boolean TERM_RESTORATION = this.getTermSaveStatus();
        final ConDrawFill clearFill = new ConDrawFill();
        this.fillZone(clearFill, TERM_RESTORATION);
    }
    
    
    
    /**
     * Imitate real console behavior in the zone by ASCII control chars.
     * Is called after command character was flushed in buffer.
     * Only for number of special ASCII symbols.
     * @param cmdChar special character we got
     * @param buf operated zone's buffer
     * @throws RuntimeException when character processing failed
     */
    private void processSpecialChar(final String cmdChar,
                                    final WindowOutputBuffer buf)
                    throws RuntimeException {
        // 'ESC' is never processed by the method:
        if ( cmdChar.equals(ConUt.ESC) ) return;
        //
        // delete just added single character
        buf.deleteLastChar();
        //
        try {
            // "library" class to process the character
            // (re-calculate coordinates, append side effects)
            this.zoneCursorPos = specialCharProcessor.process(cmdChar, this.zoneCursorPos);
        } catch ( RuntimeException charProcessExc ) {
            String excMsg = "Failed to procces special symbol. Reason: "
                                + charProcessExc.getMessage();
            throw new RuntimeException(excMsg);
        }
        //
        // need 'flush' to recalculate buffer length and zone status
        buf.flush();
    }
    
    
    
    /**
     * If the last char is new line - replace with BS-LF-combo.
     * Need for native autoscrolling.
     * @param in input string value
     * @return string with valid autoscrolling ending
     */
    private String parseLFs(final String in) {
        if ( !in.endsWith(ConUt.LF) ) return in;
        //
        final String out = in.substring(0, in.length() - 1) + ConUt.BLF;
        //
        return out;
    }
    
    
    
    /**
     * @return all strings data (for current zone width)
     */
    public ArrayList<String> getOutputLines() {
        return new ArrayList<>(this.storage.getSavedOutputLines());
    }
    
    
    
}
