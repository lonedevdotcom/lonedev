package jme3.test01;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrencyTest implements Callable<String> {
    
    ExecutorService executor = Executors.newFixedThreadPool(5);
    
    public static void main(String[] args) {
        ConcurrencyTest ct = new ConcurrencyTest();
        ct.test();
    }
    
    public void test() {
        Future<String> testResult = executor.submit(this);
        while (!testResult.isDone()) {
            try { Thread.sleep(100); } catch (Exception ex) { }
            System.out.println("waiting...");
        }
        
        try {
            System.out.println("Result=" + testResult.get());
        } catch (Exception ex) {
            System.err.println("Error getting value: " + ex);
        }
        
        testResult = null;
        executor.shutdown(); // do this at the end of the session, not in this method!
    }

    @Override
    public String call() throws Exception {
        try { Thread.sleep(1000); } catch (Exception ex) { }
        return "DONE :-)";
    }
    
}