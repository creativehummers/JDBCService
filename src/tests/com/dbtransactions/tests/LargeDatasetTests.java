package com.dbtransactions.tests;

import com.dbtransactions.DatabaseTransactions;
import com.dbtransactions.transform.CSVData;
import com.dbtransactions.transform.DefaultListOfStrings;
import com.dbtransactions.transform.JSONObjectsList;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Log
public class LargeDatasetTests {

    static DatabaseTransactions instance = null;
    final long NumberOfRowsInsert = 100_000L;
    static boolean runOnce = false;

    @Before
    public void setUp(){
        CommonUtils.resetInstance();
        new CommonUtils(NumberOfRowsInsert).init();
        instance = CommonUtils.instance;
        if(!runOnce) {
            testDefaultFormatSetup(); //initial priming of the engine to get equitable perf reports later
            runOnce = true;
        }
    }


    @Test
    public void testBasicDefaultSetup(){
            log.info("*** Validate the tables and rows are correctly created");
            long startTime = System.nanoTime();
            List<String> resultList = instance.handleRequest("111200",  new ArrayList(), JSONObjectsList.class.getName());
            JSONArray jsonColumns = new JSONArray(resultList.get(0));
            JSONArray jsonRows = new JSONArray(resultList.get(1));
            log.info("Cols: " + jsonColumns.length());
            int totalRecords = jsonRows.length();
            log.severe("Perf Select Records JSON Array: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

            Assert.assertTrue("Total Number of Records: " + totalRecords, (NumberOfRowsInsert+3)== totalRecords );
    }

    public void testDefaultFormatSetup(){
        log.info("*** Validate the tables and rows are correctly created");
        long startTime = System.nanoTime();
        List<String> resultList = instance.handleRequest("111200",  new ArrayList(), DefaultListOfStrings.class.getName());
        int totalRecords = resultList.get(1).split(",").length;
        log.severe("Perf Select Default Format : "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

        Assert.assertTrue("Total Number of Records: " + totalRecords, totalRecords==NumberOfRowsInsert+3 );
    }

    @Test
    public void testCSVDataSetup(){
        log.info("*** Validate the tables and rows are correctly created");
        long startTime = System.nanoTime();
        List<String> resultList = instance.handleRequest("111200",  new ArrayList(), CSVData.class.getName());
        String columns = resultList.get(0);
        String rows = resultList.get(1);
        log.info("Cols : " + columns.split(",").length);
        int totalRecords = rows.split(System.lineSeparator()).length;
        log.severe("Perf Select Records CSV Data: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");

        Assert.assertTrue("Total Number of Records: " + totalRecords, (NumberOfRowsInsert+3)== totalRecords );
    }
}
