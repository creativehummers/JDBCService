package com.jdbcservice.tests;

import com.jdbcservice.DatabaseTransactions;
import com.jdbcservice.transform.TransformToCSVData;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class TestResultsAsCSV {

    static DatabaseTransactions instance = null;
    final int NumberOfRowsInsert = 10;

    @Before
    public void setUp(){
        BaseTestUtils.resetInstance();
        new BaseTestUtils(NumberOfRowsInsert).init();
        instance = BaseTestUtils.instance;
    }

    @Test
    public void testBasicQuery() throws JSONException {
        List<String> csvResp = instance.genericQueryExecute("100001",  new ArrayList(), TransformToCSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ",(rows.split(",")[2]).equals("pwd#1234"));
    }

    @Test
    public void testUserTestQuery() throws JSONException {
        List<String> csvResp = instance.genericQueryExecute( "100110" , Arrays.asList("skrish01") , TransformToCSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ", rows.split(",")[0].equals("pwd#1234"));
    }

    @Test
    public void testUserTest02Query() throws JSONException {

        String[] params = new String[] { "skrish01"};
        List<String> csvResp = instance.genericQueryExecute( "100110" , Arrays.asList(params) , TransformToCSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ", rows.split(",")[0].equals("pwd#1234"));
    }

    @Test
    public void testMultipleRowsQuery() throws JSONException {

        List<String> csvResp = instance.genericQueryExecute("100100",  new ArrayList(), TransformToCSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        String[] dataRows = rows.split("\r\n")[0].split(",");
        Assert.assertTrue( "Expected to contain : data_rows ", dataRows.length> 1);
    }



}
