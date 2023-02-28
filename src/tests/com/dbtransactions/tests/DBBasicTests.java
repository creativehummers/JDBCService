/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions.tests;

import com.dbtransactions.DatabaseTransactions;
import com.dbtransactions.dbms.DefaultJDBC;
import com.dbtransactions.transform.DefaultListOfStrings;
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
import java.util.stream.IntStream;


/*
 *  Basic testing for Database Transactions : select / updates..
 */
@Log
public class DBBasicTests {

    DatabaseTransactions instance = null;
    final int NumberOfRowsInsert = 10;

    @Before
    public void setUp(){
        CommonUtils.resetInstance();
        new CommonUtils(NumberOfRowsInsert).init();
        instance = CommonUtils.instance;
    }

    @Test
    public void testBasicQuery() throws JSONException {

        String jsonResp = instance.handleRequest("100001",  new ArrayList(), JSONObjectsList.class.getName()).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);

        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(2)).equals("pwd#1234"));
    }

    @Test
    public void testUserTestQuery() throws JSONException {

        String jsonResp = instance.handleRequest("100010",Arrays.asList("skrish01"),JSONObjectsList.class.getName() ).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);

        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(2)).contains("pwd#123"));
    }

    @Test
    public void testUserTest02Query() throws JSONException {

        String[] params = new String[] { "skrish01"};
        String jsonResp = instance.handleRequest( "100110" , Arrays.asList(params) ,JSONObjectsList.class.getName()).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);
        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(0)).contains("pwd#1234"));
        //Assert.assertTrue( "Expected to contain : data_rows ",resultArray[0][1].contains("ADMIN"));
    }

    @Test
    public void testMultipleRowsQuery() throws JSONException {

        String jsonResp = instance.handleRequest("100100",  new ArrayList(), JSONObjectsList.class.getName()).get(1);
        JSONArray dataRows = (JSONArray) new JSONArray(jsonResp).get(0);
        Assert.assertTrue( "Expected to contain : data_rows ",( dataRows.length())> 1);
    }

    @Test
    public void testInsertTestUser() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String userid =  "testuser"+user_random_suffix ;
        new CommonUtils().testInsertTestUser01(userid);
    }

    @Test
    public void testUpdateTestUser() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String pwd = "pwd#" + user_random_suffix;
        String[] params = new String[] {  pwd , "skrish02" };
        List<String> updates = instance.handleRequest( "100700" , Arrays.asList(params) , DefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ", (Integer.parseInt(updates.get(1).split(",")[0])==1 ));
    }

    @Test
    public void testUpdateTestUserError() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String pwd = "pwd#" + user_random_suffix;
        String[] params = new String[] {  pwd , "testuser2755" };
        List<String> updates = instance.handleRequest( "100710" , Arrays.asList(params) , DefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ", updates.get(1).contains("no such column: userid") );
    }


}
