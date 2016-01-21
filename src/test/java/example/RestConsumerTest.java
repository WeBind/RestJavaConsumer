/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import junit.framework.TestCase;

/**
 *
 * @author Adirael
 */
public class RestConsumerTest extends TestCase {
    
    public RestConsumerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of run method, of class RestConsumer.
     *
    public void testRun() throws Exception {
        System.out.println("run");
        RestConsumer instance = null;
        instance.run();
    }

    /**
     * Test of doRequest method, of class RestConsumer.
     *
    public void testDoRequest() throws Exception {
        System.out.println("doRequest");
        RestConsumer instance = null;
        instance.doRequest();
    }

    /**
     * Test of main method, of class RestConsumer.
     */
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        RestConsumer.main(args);
    }

    /**
     * Test of getDuration method, of class RestConsumer.
     *
    public void testGetDuration() {
        System.out.println("getDuration");
        RestConsumer instance = null;
        int expResult = 0;
        int result = instance.getDuration();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDuration method, of class RestConsumer.
     *
    public void testSetDuration() {
        System.out.println("setDuration");
        int duration = 0;
        RestConsumer instance = null;
        instance.setDuration(duration);
    }

    /**
     * Test of getPeriod method, of class RestConsumer.
     *
    public void testGetPeriod() {
        System.out.println("getPeriod");
        RestConsumer instance = null;
        float expResult = 0.0F;
        float result = instance.getPeriod();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setPeriod method, of class RestConsumer.
     *
    public void testSetPeriod() {
        System.out.println("setPeriod");
        int period = 0;
        RestConsumer instance = null;
        instance.setPeriod(period);
    }

    /**
     * Test of getProvider method, of class RestConsumer.
     *
    public void testGetProvider() {
        System.out.println("getProvider");
        RestConsumer instance = null;
        String expResult = "";
        String result = instance.getProvider();
        assertEquals(expResult, result);
    }

    /**
     * Test of setProvider method, of class RestConsumer.
     *
    public void testSetProvider() {
        System.out.println("setProvider");
        String provider = "";
        RestConsumer instance = null;
        instance.setProvider(provider);
    }

    /**
     * Test of getSize method, of class RestConsumer.
     *
    public void testGetSize() {
        System.out.println("getSize");
        RestConsumer instance = null;
        int expResult = 0;
        int result = instance.getSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSize method, of class RestConsumer.
     *
    public void testSetSize() {
        System.out.println("setSize");
        int size = 0;
        RestConsumer instance = null;
        instance.setSize(size);
    }

    /**
     * Test of getStartingTime method, of class RestConsumer.
     *
    public void testGetStartingTime() {
        System.out.println("getStartingTime");
        RestConsumer instance = null;
        int expResult = 0;
        int result = instance.getStartingTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStartingTime method, of class RestConsumer.
     *
    public void testSetStartingTime() {
        System.out.println("setStartingTime");
        int startingTime = 0;
        RestConsumer instance = null;
        instance.setStartingTime(startingTime);
    }

    /**
     * Test of setEndReceived method, of class RestConsumer.
     *
    public void testSetEndReceived() {
        System.out.println("setEndReceived");
        Boolean endReceived = null;
        RestConsumer instance = null;
        instance.setEndReceived(endReceived);
    }

    /**
     * Test of putData method, of class RestConsumer.
     *
    public void testPutData() {
        System.out.println("putData");
        String key = "";
        Object value = null;
        RestConsumer instance = null;
        instance.putData(key, value);
    }
//*/
}
