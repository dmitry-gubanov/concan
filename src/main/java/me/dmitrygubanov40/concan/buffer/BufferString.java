package me.dmitrygubanov40.concan.buffer;



/**
 * Base, must-have buffer features for console output.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
abstract class BufferString
{
    
    // if is ready to operate in multithread tasks
    private final boolean isSafeAsync;
    
    
    
    public BufferString(final boolean setSafeAsyncStatus) {
        this.isSafeAsync = setSafeAsyncStatus;
    }
    
    
    
    public boolean isSafeAsync() {
        return this.isSafeAsync;
    }
    
    
    
    /**
     * @return the buffer's length (in string characters)
     */
    abstract public int length();
    
    /**
     * @return content, transformed to regular String object
     */
    @Override
    abstract public String toString();
    
    /**
     * @param newsChars new characters to the end of buffer
     */
    abstract public void append(final String newsChars);
    
}
