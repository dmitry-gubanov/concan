package me.dmitrygubanov40.concan.buffer;



/**
 * A dual-faced class for buffering, which provides a single object
 * both for fast- OR multithread-safe buffering.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class OutputBufferString extends BufferString
{
    
    private StringBuilder bufferFast;   // is faster, but not for multithreading
    private StringBuffer  bufferSafe;   // safe for many threads, but about x2 slower
    // (only one var would be initialized)
    
    // keeps the last string which was added via 'append'
    private String lastStrAdded;
    
    
    /////
    
    /**
     * @param setSafeAsyncStatus readiness for async duties (which is slower)
     */
    public OutputBufferString(final boolean setSafeAsyncStatus) {
        //
        super(setSafeAsyncStatus);
        //
        if ( this.isSafeAsync() ) {
            this.bufferSafe = new StringBuffer();
        } else {
            this.bufferFast = new StringBuilder();
        }
        //
        this.lastStrAdded = "";
    }
    
    public String getLastAddedStr() {
        return this.lastStrAdded;
    }
    
    
    //////////
    
    @Override
    public int length() {
        int lengthResult;
        //
        if ( this.isSafeAsync() )   lengthResult = this.bufferSafe.length();
        else                        lengthResult = this.bufferFast.length();
        //
        return lengthResult;
    }
    
    @Override
    public String toString() {
        String stringResult;
        //
        if ( this.isSafeAsync() )   stringResult = this.bufferSafe.toString();
        else                        stringResult = this.bufferFast.toString();
        //
        return stringResult;
    }
    
    @Override
    public void append(final String newsChars) {
        if ( this.isSafeAsync() )   this.bufferSafe.append(newsChars);
        else                        this.bufferFast.append(newsChars);
        //
        // always know exactly what was added last
        this.lastStrAdded = newsChars;
    }
    
    @Override
    public String substring(final int start, final int end) {
        String stringResult;
        //
        if ( this.isSafeAsync() )   stringResult = this.bufferSafe.substring(start, end);
        else                        stringResult = this.bufferFast.substring(start, end);
        //
        return stringResult;
    }
    
    @Override
    public String substring(final int start) {
        String stringResult;
        //
        if ( this.isSafeAsync() )   stringResult = this.bufferSafe.substring(start);
        else                        stringResult = this.bufferFast.substring(start);
        //
        return stringResult;
    }
    
    @Override
    public void delete(final int start, final int end) {
        if ( this.isSafeAsync() )   this.bufferSafe.delete(start, end);
        else                        this.bufferFast.delete(start, end);
    }
    
    
    
}
