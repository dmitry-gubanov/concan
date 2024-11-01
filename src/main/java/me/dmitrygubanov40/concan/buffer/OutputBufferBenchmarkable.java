package me.dmitrygubanov40.concan.buffer;

import java.util.Random;

import me.dmitrygubanov40.concan.strain.Benchmarkable;



/**
 * Build-in console performance tester.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class OutputBufferBenchmarkable extends OutputBuffer implements Benchmarkable
{
    
    public OutputBufferBenchmarkable(final int initSize,
                                     final boolean isSafeAsync,
                                     final boolean autoFlushMode,
                                     final boolean strictSizeControlMode) {
        super(initSize, isSafeAsync, autoFlushMode, strictSizeControlMode);
    }
    
    
    
    /**
     * What we are going to measure with 'Benchmarkable'.
     */
    @Override
    public void doBenchmark() {
        final int minStrLength = 0;
        final int maxStrLength = 80;
        //
        for ( int i = 0; i < 100000; i++ ) {
            String outStr = this.getBenchmarkStr(minStrLength, maxStrLength, i);
            this.add(outStr);
        }
    }
    
    //////////
    
    /**
     * Get random console string just for system load purposes.
     * @param minLength string min length
     * @param maxLength string max length
     * @param randomParam randomizer for chars
     * @return 
     */
    public String getBenchmarkStr(final int minLength, final int maxLength, final int randomParam) {
        Random rnd = new Random(System.currentTimeMillis() + (long) randomParam);
        final int strLength = rnd.nextInt(minLength, maxLength);
        final String CHARS =     " ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                + "abcdefghijklmnopqrstuvwxyz"
                                + "1234567890"
                                + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                                + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
                                + "1234567890"// yes, numbers are given twice
                                + "!#$%&'()-@^_`{}~"
                                + "\"*+,/:;<=>?\\[]|";
        //
        StringBuilder salt = new StringBuilder();
        //
        while ( salt.length() < strLength ) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            salt.append(CHARS.charAt(index));
        }
        //
        String saltStr = salt.toString();
        return saltStr;
    }
    
    
    
}
