package me.dmitrygubanov40.concan.winbuffer;

/**
 * Base event generator for console window's buffer.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public interface WinBufEventImpulser
{
    
    /**
     * @param listener to add
     */
    public void addEventListener(final WinBufEventListener listener);
    
    /**
     * @param listener to remove
     */
    public void removeEventListener(final WinBufEventListener listener);
    
    /**
     * @param event the thing which has happened
     */
    public void notifyEventListeners(final WinBufEvent event);
    
}
