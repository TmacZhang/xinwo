package com.cavalry.androidlib.toolbox.utils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class LibConvertNumberUtilsTest {

    private static int i;
    private static long l;
    private static float f;
    private static double d;
    private static String s1;
    private static String s2;
    private static String s3;
    private static String s4;
    private static String s5;
    private static String s6;

    @BeforeClass
    public static void beforeClass(){

        System.out.println("@BeforeClass-->不论同时执行几个方法,只在开始时执行一次");

        i = 6;
        l = 8l;
        f = 3.12f;
        d = 9.9998d;
        s1 = "sss";
        s2 = "10";
        s3 = "11.11";
        s4 = "20.01f";
        s5 = "30.3366d";
        s6 = "88.88m";

    }

    @AfterClass
    public static void afterClass(){
        System.out.println("@AfterClass-->不论同时执行几个方法,只在结束时执行一次");
    }

    @Before
    public void before(){
        System.out.println("@Before-->每执行一个方法,开始时都执行一次");
    }

    @After
    public void after(){
        System.out.println("@After-->每执行一个方法,结束时都执行一次");
    }

    @Test
    public void convert2Int(){
        System.out.println("START: convert2Int");

        Assert.assertEquals(6,LibConvertNumberUtils.convert2Int(i,0));
        Assert.assertEquals(8,LibConvertNumberUtils.convert2Int(l,0));
        Assert.assertEquals(3,LibConvertNumberUtils.convert2Int(f,0));
        Assert.assertEquals(9,LibConvertNumberUtils.convert2Int(d,0));
        Assert.assertEquals(0,LibConvertNumberUtils.convert2Int(s1,0));
        Assert.assertEquals(10,LibConvertNumberUtils.convert2Int(s2,0));
        Assert.assertEquals(11,LibConvertNumberUtils.convert2Int(s3,0));
        Assert.assertEquals(20,LibConvertNumberUtils.convert2Int(s4,0));
        Assert.assertEquals(30,LibConvertNumberUtils.convert2Int(s5,0));
        Assert.assertEquals(0,LibConvertNumberUtils.convert2Int(s6,0));

        System.out.println("END: convert2Int");
    }

    @Test
    public void convert2Double(){
        System.out.println("START: convert2Double");

        Assert.assertEquals(6.0d,LibConvertNumberUtils.convert2Double(i,0));
        Assert.assertEquals(8.0d,LibConvertNumberUtils.convert2Double(l,0));
        Assert.assertEquals(3.12d,LibConvertNumberUtils.convert2Double(f,0));
        Assert.assertEquals(9.9998d,LibConvertNumberUtils.convert2Double(d,0));
        Assert.assertEquals(0.00d,LibConvertNumberUtils.convert2Double(s1,0));
        Assert.assertEquals(10.0d,LibConvertNumberUtils.convert2Double(s2,0));
        Assert.assertEquals(11.11d,LibConvertNumberUtils.convert2Double(s3,0));
        Assert.assertEquals(20.01d,LibConvertNumberUtils.convert2Double(s4,0));
        Assert.assertEquals(30.3366d,LibConvertNumberUtils.convert2Double(s5,0));
        Assert.assertEquals(0.00d,LibConvertNumberUtils.convert2Double(s6,0));

        System.out.println("END: convert2Double");
    }

    @Test
    public void convert2Long(){
        System.out.println("START: convert2Long");

        Assert.assertEquals(6l,LibConvertNumberUtils.convert2Long(i,0l));
        Assert.assertEquals(8l,LibConvertNumberUtils.convert2Long(l,0l));
        Assert.assertEquals(3l,LibConvertNumberUtils.convert2Long(f,0l));
        Assert.assertEquals(9l,LibConvertNumberUtils.convert2Long(d,0l));
        Assert.assertEquals(0l,LibConvertNumberUtils.convert2Long(s1,0l));
        Assert.assertEquals(10l,LibConvertNumberUtils.convert2Long(s2,0l));
        Assert.assertEquals(11l,LibConvertNumberUtils.convert2Long(s3,0l));
        Assert.assertEquals(20l,LibConvertNumberUtils.convert2Long(s4,0l));
        Assert.assertEquals(30l,LibConvertNumberUtils.convert2Long(s5,0l));
        Assert.assertEquals(0l,LibConvertNumberUtils.convert2Long(s6,0l));

        System.out.println("END: convert2Long");
    }
}