package me.dmitrygubanov40.concan.strain;



/**
 * Wrapper to measure benchmark execution from any class.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public interface Benchmarkable {
    
    /**
     * Will measure execution time of this method.
     */
    public void doBenchmark();
    
    
    
}
