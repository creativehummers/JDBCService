package com.dbtransactions.tests;

import com.dbtransactions.DatabaseTransactions;
import com.dbtransactions.dbms.DefaultJDBC;
import com.dbtransactions.transform.CSVData;
import com.dbtransactions.transform.JSONObjectsList;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class CSVDataTests {

    static DatabaseTransactions instance = null;
    final int NumberOfRowsInsert = 10;

    @Before
    public void setUp(){
        CommonUtils.resetInstance();
        new CommonUtils(NumberOfRowsInsert).init();
        instance = CommonUtils.instance;
    }

    @Test
    public void testBasicQuery() throws JSONException {
        List<String> csvResp = instance.handleRequest("100001",  new ArrayList(), CSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ",(rows.split(",")[2]).equals("pwd#1234"));
    }

    @Test
    public void testUserTestQuery() throws JSONException {
        List<String> csvResp = instance.handleRequest( "100110" , Arrays.asList("skrish01") ,CSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ", rows.split(",")[0].equals("pwd#1234"));
    }

    @Test
    public void testUserTest02Query() throws JSONException {

        String[] params = new String[] { "skrish01"};
        List<String> csvResp = instance.handleRequest( "100110" , Arrays.asList(params) ,CSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        Assert.assertTrue( "Expected to contain : data_rows ", rows.split(",")[0].equals("pwd#1234"));
    }

    @Test
    public void testMultipleRowsQuery() throws JSONException {

        List<String> csvResp = instance.handleRequest("100100",  new ArrayList(), CSVData.class.getName());
        log.info("Column Names : "+ csvResp.get(0));
        String rows = csvResp.get(1);
        String[] dataRows = rows.split("\r\n")[0].split(",");
        Assert.assertTrue( "Expected to contain : data_rows ", dataRows.length> 1);
    }



}
