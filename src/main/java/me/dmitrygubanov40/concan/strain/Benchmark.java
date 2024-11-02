package me.dmitrygubanov40.concan.strain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;



/**
 * Build-in console performance tester.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class Benchmark
{
    
    private static final long DEFAULT_MAX_BENCH_TIME;
    
    static {
        DEFAULT_MAX_BENCH_TIME = 60 * 1000;// ms
    }
    
    // created to measure custom method (or prepared from 'Benchmarkable')
    private final boolean isByMethod;
    
    // parameters for custom build-in method
    private Class theClass;
    private Object obj;
    private String methodName;
    private Object[] params;
    private Method highloadMethod;
    
    // 'Benchmarkable'-object to measure
    private final Benchmarkable theBenchmarkableObj;
    
    private long maxBenchTime;// ms
    
    
    
    /**
     * 'Benchmarkable' initialization via special object (build-in interface support).
     * @param benchObj 'doBenchmark'-method of this object will be under high load
     * @throws IllegalArgumentException when null-object is given to init
     */
    public Benchmark(final Benchmarkable benchObj) throws IllegalArgumentException {
        if ( null == benchObj ) {
            String excMsg = "[Benchmark] failed. The 'benchmarkable'-object is empty";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.isByMethod = false;
        this.theBenchmarkableObj = benchObj;
        this.setMaxBenchTime(DEFAULT_MAX_BENCH_TIME);
    }
    
    /**
     * Object-oriented initialization with all necessary data to make heavy load.
     * @param obj object which will be tested
     * @param methodName testing method of the object
     * @param params object method parameters
     */
    public Benchmark(final Object obj, final String methodName, final Object... params) {
        this.isByMethod = true;
        this.theBenchmarkableObj = null;// no use
        //
        this.BenchmarkInitByMethod(obj, obj.getClass(), methodName, params);
        //
        this.setMaxBenchTime(DEFAULT_MAX_BENCH_TIME);
    }
    
    /**
     * Class-oriented initialization (for static methods) with all necessary data to make heavy load.
     * @param thisClass method's class
     * @param methodName testing method of the object
     * @param params object method parameters
     */
    public Benchmark(final Class thisClass, final String methodName, final Object... params) {
        this.isByMethod = true;
        this.theBenchmarkableObj = null;// no use
        //
        this.BenchmarkInitByMethod(null, thisClass, methodName, params);
        //
        this.setMaxBenchTime(DEFAULT_MAX_BENCH_TIME);
    }
    
    /**
     * Initialization with all necessary data for method purposes.
     * @param obj object which will be tested (may be null)
     * @param thisClass method's class (may be null)
     * @param methodName testing method of the object
     * @param params object method parameters
     * @throws IllegalArgumentException when method's name is incorrect
     */
    private void BenchmarkInitByMethod( final Object obj,
                                        final Class thisClass,
                                        final String methodName,
                                        final Object[] params)
                    throws IllegalArgumentException {
        if  ( methodName.length() <= 0 ) {
            String excMsg = "[Benchmark] failed. The method to execute was not given";
            throw new IllegalArgumentException(excMsg);
        }
        if ( null == obj && null == thisClass ) {
            String excMsg = "[Benchmark] failed. The class or the object must be given, both are empty";
            throw new IllegalArgumentException(excMsg);
        }
        //
        // 'theClass' will be initialized next block
        this.obj = obj;
        this.methodName = methodName;
        this.params = params;
        //
        if ( null == this.obj ) {
            this.theClass = thisClass;
        } else {
            this.theClass = obj.getClass();
        }
        //
        try {
            this.highloadMethod = this.getMethodByName();
            //
            if ( null == this.obj && !Modifier.isStatic(this.highloadMethod.getModifiers()) ) {
                throw new NoSuchMethodException();
            }
            //
        } catch ( NoSuchMethodException | SecurityException ex ) {
            System.out.println("\n\n[Benchmark] failed. There is no object's '" + this.methodName + "'-method.");
            System.out.println("Context: " + ex.getMessage());
            throw new IllegalArgumentException();
        }
    }
    
    
    
    /**
     * @param msMaxTime how long all the benchmark can be executed (all iterations)
     * @throws IllegalArgumentException in case of incorrect time maximum
     */
    public final void setMaxBenchTime(final long msMaxTime) throws IllegalArgumentException {
        if ( msMaxTime <= 0 ) {
            String excMsg = "[Benchmark] failed. Incorrect max execution time given: '" + msMaxTime + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.maxBenchTime = msMaxTime;
    }
    
    
    
    /**
     * @return the method to be executed in benchmark tests
     * @throws NoSuchMethodException, SecurityException when method was not found
     */
    private Method getMethodByName() throws NoSuchMethodException, SecurityException {
        Method methodResult = null;
        Class[] paramTypes = new Class[ this.params.length ];
        //
        for ( int i = 0; i < paramTypes.length; i++ ) {
            if ( null == this.params[ i ] ) {
                paramTypes[ i ] = null;
                continue;
            }
            paramTypes[ i ] = this.params[ i ].getClass();
        }
        //
        try {
            methodResult = this.theClass.getMethod(this.methodName, paramTypes);
        } catch ( NoSuchMethodException | SecurityException ex ) {
            throw ex; 
        }
        //
        return methodResult;
    }
    
    
    
    /**
     * Run necessary the method (both by method or from a Benchmarkable-obj,
     * run it number of times, calculate medium time of execution.
     * @param iterations number of attempts
     * @return medium on iterations execution time of the method
     * @throws IllegalArgumentException for less than one iteration
     */
    public double runHighload(final int iterations) throws IllegalArgumentException {
        if ( iterations <= 0 ) {
            String excMsg = "[Benchmark] failed."
                                + " There number of iterations must be more than zero, is: " + iterations;
            throw new IllegalArgumentException(excMsg);
        }
        //
        System.out.println("\n[Benchmark] started.");
        if ( this.isByMethod ) {
            String objectUsage = (null == this.obj) ? " (static method)" : " (object is used)";
            System.out.println("Type: BY METHOD");
            System.out.println("Class name: " + this.theClass + ", method: " + this.methodName + objectUsage);
        } else {
            System.out.println("Type: BENCHMARKABLE");
        }
        System.out.println("\n");
        //
        long startTimeStep, benchmarkStep;
        long benchmarkAll = 0;
        double benchmarkResult = 0.0;
        //
        int i;
        for ( i = 0; i < iterations; ) {
            i++;
            startTimeStep = System.nanoTime();
            ////////////////////////////////////
            if ( this.isByMethod ) {
                // created to execute some custom method
                try {
                    //
                    highloadMethod.invoke(this.obj, this.params);
                    //
                } catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
                    System.out.println("\n\n[Benchmark] failed. '" + this.methodName + "'-method execution issues.");
                    System.out.println("Context: " + ex.getMessage());
                    ex.printStackTrace(System.out);
                    //
                    return 0.0;
                    //
                }
            } else {
                //
                this.theBenchmarkableObj.doBenchmark();
                //
            }
            ////////////////////////////////////
            benchmarkStep = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeStep);
            benchmarkAll += benchmarkStep;
            //
            // medium iteration result
            if ( 1 == i ) benchmarkResult = benchmarkStep;
            else benchmarkResult = (benchmarkResult + (double) benchmarkStep) / 2.0;
            //
            System.out.println("\n\n[Benchmark] current iteration: " + i 
                          + ", current result: " + benchmarkStep + " ms\n");
            //
            // in case too long benchmark duration (all iterations)
            if ( benchmarkAll > this.maxBenchTime ) {
                System.out.println("\n\n[Benchmark] iterations were stopped."
                                        + " Too long awaiting, limit is " + this.maxBenchTime + " ms");
                break;
            }
        }
        //
        System.out.println("\n[Benchmark] medium result: " + benchmarkResult + " ms"
                                + " (" + i + " iterations)");
        //
        return benchmarkResult;
        //
    }
    public double runHighload() {
        return this.runHighload(1);
    }
    
    
    
}
