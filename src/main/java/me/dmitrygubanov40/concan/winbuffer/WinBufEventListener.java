package me.dmitrygubanov40.concan.winbuffer;

/**
 * Base event listener for console window's buffer.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public interface WinBufEventListener
{
    
    /**
     * Main generator of event impulses in 'WindowOutputBufferEvent'.
     * @param event structured data of the event
     */
    public void onWindowOutputBufferEvent(final WinBufEvent event);
    
}
