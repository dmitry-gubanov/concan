package me.dmitrygubanov40.concan.winbuffer;

/**
 * All possible events for 'WindowOutputBuffer' are registered here.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum WinBufEventType
{
    
    ON_BEFORE_FLUSH         ("onBeforeFlush"),
    ON_AFTER_FLUSH          ("onAfterFlush"),
    ON_BEFORE_AUTOFLUSH     ("onBeforeAutoflush"),
    ON_AFTER_AUTOFLUSH      ("onAfterAutoflush");
    
    
    ////////////////////////////
    
    private final String typeName;
    
    ////////////////////////////
    
    
    /**
     * @param initTypeName text code of event type
     */
    WinBufEventType(final String initTypeName) {
        this.typeName = initTypeName;
    }
    
    
    
    /**
     * @return string of type name
     */
    public String getTypeName() {
        return this.typeName;
    }
    
    
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        String str = className + ": " + this.typeName;
        return str;
    }
    
    
    
}
