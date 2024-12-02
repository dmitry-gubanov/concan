package me.dmitrygubanov40.concan.winbuffer;

import java.util.EventObject;

/**
 * Events for window's buffer have such structure
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class WinBufEvent extends EventObject
{
    
    /*** have as 'EventObject' descendant
    protected Object source;
    ***/
    
    // Text ID of the exact type of event: "beforeFlush", "onAdd"...
    private final WinBufEventType eventType;
    
    // possible extra flags
    private final int eventFlags;
    
    // extra comment or text data
    private final String eventText;
    
    // local time moment of the event
    private final long eventTimeMs;    
    
    ///////////////
    
    
    /**
     * Full constructor with all parameters.
     * @param initSource something that initiated the event
     * @param initEventType event type from 'WinBufEventType'
     * @param initEventFlags to pass any states or conditions
     * @param initEventText free-to-write text
     * @param initEventTimeMs current time in milliseconds (see 'currentTimeMillis')
     */
    public WinBufEvent(final Object initSource,
                        final WinBufEventType initEventType,
                        final int initEventFlags,
                        final String initEventText,
                        final long initEventTimeMs) {
        super(initSource);
        this.eventType      = initEventType;
        this.eventFlags     = initEventFlags;
        this.eventText      = initEventText;
        this.eventTimeMs    = initEventTimeMs;
    }
    //
    // sub-constructor of any kind
    public WinBufEvent(final Object initSource,
                        final WinBufEventType initEventType,
                        final int initEventFlags,
                        final String initEventText) {
        super(initSource);
        //
        // stam the time ourselves:
        final long initEventTimeMs = System.currentTimeMillis();
        //
        this.eventType      = initEventType;
        this.eventFlags     = initEventFlags;
        this.eventText      = initEventText;
        this.eventTimeMs    = initEventTimeMs;  // (auto)
    }
    //
    public WinBufEvent(final Object initSource,
                        final WinBufEventType initEventType,
                        final String initEventText) {
        super(initSource);
        //
        final int initEventFlags = 0;
        // stam the time ourselves:
        final long initEventTimeMs = System.currentTimeMillis();
        //
        this.eventType      = initEventType;
        this.eventFlags     = initEventFlags;   // (auto)
        this.eventText      = initEventText;
        this.eventTimeMs    = initEventTimeMs;  // (auto)
    }
    //
    public WinBufEvent(final Object initSource,
                        final WinBufEventType initEventType,
                        final int initEventFlags) {
        super(initSource);
        //
        final String initEventText = "";
        // stam the time ourselves:
        final long initEventTimeMs = System.currentTimeMillis();
        //
        this.eventType      = initEventType;
        this.eventFlags     = initEventFlags;
        this.eventText      = initEventText;    // (auto)
        this.eventTimeMs    = initEventTimeMs;  // (auto)
    }
    //
    public WinBufEvent(final Object initSource,
                        final WinBufEventType initEventType) {
        super(initSource);
        //
        final int initEventFlags = 0;
        final String initEventText = "";
        // stam the time ourselves:
        final long initEventTimeMs = System.currentTimeMillis();
        //
        this.eventType      = initEventType;
        this.eventFlags     = initEventFlags;   // (auto)
        this.eventText      = initEventText;    // (auto)
        this.eventTimeMs    = initEventTimeMs;  // (auto)
    }
    
    
    ///////////////
    
    /*** have as 'EventObject' descendant
    public Object getSource();
    ***/
    
    /**
     * @return text code of occur event
     */
    public WinBufEventType getEventType() {
        return this.eventType;
    }
    
    /**
     * @return special integer/bit flags of the event
     */
    public int getEventFlags() {
        return this.eventFlags;
    }
    
    /**
     * @return extra text of occur event
     */
    public String getEventText() {
        return this.eventText;
    }
    
    /**
     * @return time of the event, milliseconds after 01.01.1970
     */
    public long getEventTimeMs() {
        return this.eventTimeMs;
    }
    
    
    
    @Override
    public String toString() {
        String str = this.getClass().getSimpleName()
                    + ", " + this.eventType.getTypeName()
                    + ": [timestamp: " + this.eventTimeMs
                    + ", flags: " + this.eventFlags
                    + ", text: '" + this.eventText + "']";
        return str;
    }
    
    
    
}
