package com.nishant.customcache;

import com.nishant.customcache.common.Rectangle;
import com.nishant.customcache.common.Shape;
import com.nishant.customcache.common.ShapeKey;
import com.nishant.customcache.common.Square;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author cannot disclose
 * Problem description
 * An application needs a lightweight cache. For various reasons, reuse any of the readily available caching solutions is not an option.
 * Hence, we need to create a home-grown solution.
 * You have been tasked with creating this cache. Here are the acceptance criteria this cache solution.
 * <p>
 * Must Haves:
 * 1.	At the bare minimum the cache MUST be able to perform add(K key, V value) and get(Object) operations.
 * 2.	The cache has its own type safety rules.
 * i.	User can add key of any type and value as long as a key of that type is not already present.
 * ii.	Once a unique key type is added any subsequent entries should allow values which are of the same type or its subclass/subtype.
 * Example: Let’s say we have a class hierarchy of Shape -> Rectangle -> Square
 * The user should be able to add objects of all these classes to the cache against the same Key object say ShapeKey
 * but if he tries to add a string or any object not from the Shape hierarchy against the ShapeKey it should fail.
 * Once ShapeKey is removed from the cache then he should be able to add a String or any other object as value to ShapeKey.
 * There are unit test covering this scenario
 * (Eg: testSuperAndSubTypesTypes_RemoveAndAdd).
 * <p>
 * 3.	The caching solution should work in a single JVM multithreaded environment with no loss of functionality.
 * <p>
 * Nice to Haves:
 * 4.	Avoid using any of the readily available java Map implementations to build the cache or to store caching data.
 * Usage of List, Set, Arrays or any other Collection implementations is fine.
 * 5.	Solution allows for items to expire from cache at a preconfigured interval and this should be configurable at a key type level.
 * Once a key type expires the rule of type safety should get reset.
 * <p>
 * Getting Started:
 * A Maven based starter project file(s) have the unit tests and value objects which can be used as key and value for the cache.
 * The main class CustomCache.java has TODO where you can put your code.
 * Feel free to create additional classes both top level and nested for this implementation.
 * To test the solution, you can run the Unit test using the IDE or run the maven package target.
 */

public class CustomCacheTest {
    @Test
    public void testWrapperTypes() throws Exception {
        CustomCache customCache = new CustomCache();
        customCache.put("Hello", "World");
        customCache.put(20, 11);
        customCache.put(21, 2);
        customCache.put(21, 1);
        customCache.put(BigDecimal.ONE, BigDecimal.ZERO);
        Double aDouble = Double.valueOf(100);
        Double aDouble1 = Double.valueOf(101);
        customCache.put(aDouble, aDouble1);

        Assert.assertEquals(11, customCache.get(20));
        Assert.assertEquals(1, customCache.get(21));
        Assert.assertEquals("World", customCache.get("Hello"));
        Assert.assertEquals(aDouble1, customCache.get(aDouble));
    }

    @Test
    public void testSuperAndSubTypesTypes() throws Exception {
        CustomCache customCache = new CustomCache();
        ShapeKey keyOne = new ShapeKey(101);
        ShapeKey keyTwo = new ShapeKey(102);
        ShapeKey keyThree = new ShapeKey(103);
        Rectangle rectangle = new Rectangle(1, "Rectangle One", 5, 3);
        customCache.put(keyOne, rectangle);
        Shape shape = new Shape(0, "Generic Shape");
        customCache.put(keyTwo, shape);
        Square square = new Square(3, "Square One", 5);
        customCache.put(keyThree, square);

        Assert.assertEquals(rectangle, customCache.get(keyOne));
        Assert.assertEquals(shape, customCache.get(keyTwo));
        Assert.assertEquals(square, customCache.get(keyThree));
    }


    @Test
    public void testSuperAndSubTypesTypes_Fail() throws Exception {
        CustomCache customCache = new CustomCache();
        ShapeKey keyOne = new ShapeKey(101);
        ShapeKey keyTwo = new ShapeKey(102);
        ShapeKey keyThree = new ShapeKey(103);
        Rectangle rectangle = new Rectangle(1, "Rectangle One", 5, 3);
        customCache.put(keyOne, rectangle);
        Shape shape = new Shape(0, "Generic Shape");
        customCache.put(keyTwo, shape);
        Assert.assertEquals(rectangle, customCache.get(keyOne));
        Assert.assertEquals(shape, customCache.get(keyTwo));

        try {
            customCache.put(keyThree, "Not a Shape object should fail");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(null, customCache.get(keyThree));
            Assert.assertEquals("Object of class [class java.lang.String] not allowable for this Key Type [class com.nishant.customcache.common.ShapeKey]. " +
                    "Allowed types are [class com.nishant.customcache.common.Shape] or it sub and super types", e.getMessage());
        }
    }


    @Test
    public void testSuperAndSubTypesTypes_RemoveAndAdd() throws Exception {
        CustomCache customCache = new CustomCache();
        ShapeKey keyOne = new ShapeKey(101);
        ShapeKey keyTwo = new ShapeKey(102);
        ShapeKey keyThree = new ShapeKey(103);
        ShapeKey keyFour = new ShapeKey(104);
        Rectangle rectangle = new Rectangle(1, "Rectangle One", 5, 3);
        customCache.put(keyOne, rectangle);
        Shape shape = new Shape(0, "Generic Shape");
        customCache.put(keyTwo, shape);
        Assert.assertEquals(rectangle, customCache.get(keyOne));
        Assert.assertEquals(shape, customCache.get(keyTwo));

        try {
            customCache.put(keyFour, "Not a Shape object should fail");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(null, customCache.get(keyFour));
            Assert.assertEquals("Object of class [class java.lang.String] not allowable for this Key Type [class com.nishant.customcache.common.ShapeKey]. " +
                    "Allowed types are [class com.nishant.customcache.common.Shape] or it sub and super types", e.getMessage());
        }

        customCache.remove(keyOne);
        customCache.remove(keyTwo);

        customCache.put(keyFour, "Should work this time as all Shape's removed");
        Assert.assertEquals("Should work this time as all Shape's removed", customCache.get(keyFour));

    }


    @Test
    public void testWrapperTypesTypes_RemoveAndAdd() throws Exception {
        CustomCache customCache = new CustomCache();
        customCache.put("Hello", 1234);
        customCache.put(20, 11);
        customCache.put(BigDecimal.ONE, BigDecimal.ZERO);


        Assert.assertEquals(1234, customCache.get("Hello"));
        Assert.assertEquals(11, customCache.get(20));
        Assert.assertEquals(BigDecimal.ZERO, customCache.get(BigDecimal.ONE));

        try {
            customCache.put("Key", "Can I add this?");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(null, customCache.get("Key"));
            Assert.assertEquals("Object of class [class java.lang.String] not allowable for this Key Type [class java.lang.String]. " +
                    // TODO the validation should be for java.lang.Number instead of java.lang.Integer
                    //"Allowed types are [class java.lang.Integer] or it sub and super types", e.getMessage());
                    "Allowed types are [class java.lang.Number] or it sub and super types", e.getMessage());

        }

        customCache.remove("Hello");

        customCache.put("Key", "Can I add this?");
        Assert.assertEquals("Can I add this?", customCache.get("Key"));

    }

    @Test(timeout = 25000)
    public void testWrapperTypesThreaded() throws Exception {
        CustomCache customCache = new CustomCache();
        Double aDouble = Double.valueOf(100);
        Double aDouble1 = Double.valueOf(101);
        List<Thread> list = Arrays.asList(
                new Thread(() -> {
                    addSleep(1);
                    customCache.put("Hello", "World");
                }),
                new Thread(() -> {
                    addSleep(3);
                    customCache.put(20, 11);
                }),
                new Thread(() -> {
                    addSleep(2);
                    customCache.put(21, 1);
                }),
                new Thread(() -> {
                    addSleep(1);
                    customCache.put(BigDecimal.ONE, BigDecimal.ZERO);
                }),
                new Thread(() -> {
                    addSleep(3);
                    customCache.put(aDouble, aDouble1);
                })
        );

        list.forEach(Thread::start);
        list.forEach(thread -> {
            joinThreads(thread);
        });

        Assert.assertEquals(1, customCache.get(21));
        Assert.assertEquals(11, customCache.get(20));
        Assert.assertEquals(1, customCache.get(21));
        Assert.assertEquals("World", customCache.get("Hello"));
        Assert.assertEquals(aDouble1, customCache.get(aDouble));

        Thread.sleep(TimeUnit.SECONDS.toMillis(15));

        Assert.assertEquals(null, customCache.get(21));
        Assert.assertEquals(null, customCache.get(20));
        Assert.assertEquals(null, customCache.get(21));
        Assert.assertEquals(null, customCache.get("Hello"));
        Assert.assertEquals(null, customCache.get(aDouble));

        customCache.put(21, 1);
        Assert.assertEquals(1, customCache.get(21));

    }

    @Test(timeout = 7000)
    public void testWrapperTypesConcurrentReadAndWrite() throws Exception {
        CustomCache customCache = new CustomCache();
        List<Thread> writers = new ArrayList<>();
        IntStream.range(0, 10).forEach((idx) -> {
            writers.add(new Thread(() -> writerValidation(customCache, 5)));
        });
        List<Thread> readers = new ArrayList<>();
        IntStream.range(0, 10).forEach((idx) -> {
            readers.add(new Thread(() -> readerValidation(customCache, 3), String.valueOf(idx)));
        });
        writers.forEach(Thread::start);
        addSleep(1);
        readers.forEach(Thread::start);
        readers.forEach(thread -> joinThreads(thread));
        writers.forEach(thread -> joinThreads(thread));
        Assert.assertEquals(1, customCache.get(1));
    }

    private void writerValidation(CustomCache customCache, long runUntil) {
        Date end = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(runUntil));
        int i = 0;
        while (System.currentTimeMillis() < end.getTime()) {
            if (i == 15000) {
                i = 0;
            }
            customCache.put(i, i);
            customCache.put("" + i, "" + i);
            customCache.put(15000 + i, 15000 + i);
            customCache.remove(15000 + i);
            try {
                customCache.put(i, "" + i);
                Assert.fail();
            } catch (RuntimeException e) {
                Assert.assertEquals("Object of class [class java.lang.String] " +
                        "not allowable for this Key Type [class java.lang.Integer]. " +
                        "Allowed types are [class java.lang.Number] or it sub and super types", e.getMessage());
            }
            i++;
        }
    }

    private void readerValidation(CustomCache customCache, long runUntil) {
        Date end = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(runUntil));
        int i = 0;
        while (System.currentTimeMillis() < end.getTime()) {
            if (i == 15000) {
                i = 0;
            }
            Assert.assertEquals(i, customCache.get(i));
            Assert.assertEquals("" + i, customCache.get("" + i));
            i++;
        }
    }

    private void addSleep(long units) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(units));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void joinThreads(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}