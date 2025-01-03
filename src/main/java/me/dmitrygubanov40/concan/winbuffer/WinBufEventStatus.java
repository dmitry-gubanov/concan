package me.dmitrygubanov40.concan.winbuffer;

/**
 * All possible event status for 'WindowOutputBuffer'.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum WinBufEventStatus
{
    
    WB_EVENT_OK,        // do the job, and the event has prolongation
    WB_EVENT_IGNORE,    // STOP - do not react the event, but the event has prolongation
    WB_EVENT_ABORT;     // STOP - do not react the event, and stop the prolongation
    
}
