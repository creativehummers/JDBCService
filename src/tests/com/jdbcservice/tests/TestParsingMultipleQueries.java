package com.jdbcservice.tests;

import com.jdbcservice.exec.MultipleTransactionExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestParsingMultipleQueries {

    private Method aMethod;
    private MultipleTransactionExecutor instance = new MultipleTransactionExecutor();

    private static final String SampleQuery01 =
            " -- Comment Line 01"+System.lineSeparator()+
            "Select * from test_user;"+System.lineSeparator()+"\t"+System.lineSeparator()+
            " -- Comment Line 02"+System.lineSeparator()+
            "Select * from user_profile;"+System.lineSeparator();

    private static final String ErrorSampleQuery02 =
            " -- Comment Line 01"+System.lineSeparator()+
                    "Select * from test_user;"+System.lineSeparator()+
                    " -- Comment Line 02"+System.lineSeparator()+
                    "Select * from user_profile;"+System.lineSeparator()+"\t"+System.lineSeparator()+
            "ABCDM"+System.lineSeparator();

    @Before
    public void setUp(){
        try{
            Class aClass = MultipleTransactionExecutor.class;
            aMethod = aClass.getDeclaredMethod("splitQueries", String.class);
            aMethod.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testSplitQueries(){
        try{
            String[] queries = (String[]) aMethod.invoke(instance,SampleQuery01);
            Assert.assertTrue("Expect 2 queries: "+queries.length , queries.length==2);
        } catch ( IllegalAccessException | InvocationTargetException ex) {
            Assert.assertTrue("Exception thrown: " + ex.getMessage(), false);
        }
    }

    @Test
    public void testSplitQueriesEndsWithSemiColonLineSeparator(){
        try {
            String[] queries = (String[]) aMethod.invoke(instance, ErrorSampleQuery02);
        } catch ( IllegalAccessException | InvocationTargetException ex) {
            if(((InvocationTargetException) ex).getTargetException().getMessage().contains("Multiple Query String should end with ;\r\n"))
                return ;
            Assert.assertTrue("Exception thrown: " + ex.getMessage(), false);
        }
        Assert.assertTrue("Runtime Exception expected to be caught in the first block", true);
    }
}
