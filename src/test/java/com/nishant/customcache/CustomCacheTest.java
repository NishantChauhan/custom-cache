package com.nishant.customcache;

import com.nishant.customcache.common.Square;
import com.nishant.customcache.common.Rectangle;
import com.nishant.customcache.common.Shape;
import com.nishant.customcache.common.ShapeKey;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cannot disclose
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

    @Test
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

        list.forEach(thread -> {
            thread.start();
        });

        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Assert.assertEquals(1, customCache.get(21));
        Assert.assertEquals(11, customCache.get(20));
        Assert.assertEquals(1, customCache.get(21));
        Assert.assertEquals("World", customCache.get("Hello"));
        Assert.assertEquals(aDouble1, customCache.get(aDouble));

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));

        Assert.assertEquals(null, customCache.get(21));
        Assert.assertEquals(null, customCache.get(20));
        Assert.assertEquals(null, customCache.get(21));
        Assert.assertEquals(null, customCache.get("Hello"));
        Assert.assertEquals(null, customCache.get(aDouble));

        customCache.put(21,1);
        Assert.assertEquals(1, customCache.get(21));
    }

    private void addSleep(long units) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(units));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}