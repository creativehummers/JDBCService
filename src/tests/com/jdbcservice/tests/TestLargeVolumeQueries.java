package com.jdbcservice.tests;

import com.jdbcservice.DatabaseTransactions;
import com.jdbcservice.transform.TransformToCSVData;
import com.jdbcservice.transform.TransformToDefaultListOfStrings;
import com.jdbcservice.transform.TransformToJSONObjectsList;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Log
public class TestLargeVolumeQueries {
    //https://github.com/mybatis/mybatis-3/issues/98

    static DatabaseTransactions instance = null;
    final long NumberOfRowsInsert = 10_000L;
    static boolean runOnce = false;

    @Before
    public void setUp(){
        BaseTestUtils.resetInstance();
        new BaseTestUtils(NumberOfRowsInsert).init();
        instance = BaseTestUtils.instance;
        if(!runOnce) {
            defaultFormatSetup(); //initial priming of the engine to get equitable perf reports later
            runOnce = true;
        }
    }

    @Test
    public void testDefaultJSONSetup(){
            log.info("*** Validate the tables and rows are correctly created");
            long startTime = System.nanoTime();
            List<String> resultList = instance.genericQueryExecute("111200",  new ArrayList(), TransformToJSONObjectsList.class.getName());
            JSONArray jsonColumns = new JSONArray(resultList.get(0));
            JSONArray jsonRows = new JSONArray(resultList.get(1));
            log.info("Cols: " + jsonColumns.length());
            int totalRecords = jsonRows.length();
            log.severe("Perf Select Records JSON Array: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

            Assert.assertTrue("Total Number of Records: " + totalRecords, (NumberOfRowsInsert+3)== totalRecords );
    }

    public void defaultFormatSetup(){
        log.info("*** Validate the tables and rows are correctly created");
        long startTime = System.nanoTime();
        List<String> resultList = instance.genericQueryExecute("111200",  new ArrayList(), TransformToDefaultListOfStrings.class.getName());
        int totalRecords = resultList.get(1).split(",").length;
        log.severe("Perf Select Default Format : "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

        Assert.assertTrue("Total Number of Records: " + totalRecords, totalRecords==NumberOfRowsInsert+3 );
    }

    @Test
    public void testCSVDataSetup(){
        log.info("*** Validate the tables and rows are correctly created");
        long startTime = System.nanoTime();
        List<String> resultList = instance.genericQueryExecute("111200",  new ArrayList(), TransformToCSVData.class.getName());
        String columns = resultList.get(0);
        String rows = resultList.get(1);
        log.info("Cols : " + columns.split(",").length);
        int totalRecords = rows.split(System.lineSeparator()).length;
        log.severe("Perf Select Records CSV Data: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

        Assert.assertTrue("Total Number of Records: " + totalRecords, (NumberOfRowsInsert+3)== totalRecords );
    }
}
