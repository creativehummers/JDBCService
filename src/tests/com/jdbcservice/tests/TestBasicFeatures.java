/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.tests;

import com.jdbcservice.DatabaseTransactions;
import com.jdbcservice.transform.TransformToDefaultListOfStrings;
import com.jdbcservice.transform.TransformToJSONObjectsList;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 *  Basic testing for Database Transactions : select / updates..
 */
@Log
public class TestBasicFeatures {

    DatabaseTransactions instance = null;
    final int NumberOfRowsInsert = 10;

    @Before
    public void setUp(){
        BaseTestUtils.resetInstance();
        new BaseTestUtils(NumberOfRowsInsert).init();
        instance = BaseTestUtils.instance;
    }

    @Test
    public void testSimpleQuery() throws JSONException {

        String jsonResp = instance.genericQueryExecute("100001",  new ArrayList(), TransformToJSONObjectsList.class.getName()).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);

        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(2)).equals("pwd#1234"));
    }

    @Test
    public void testUserQuery() throws JSONException {

        String jsonResp = instance.genericQueryExecute("100010",Arrays.asList("skrish01"), TransformToJSONObjectsList.class.getName() ).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);

        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(2)).contains("pwd#123"));
    }

    @Test
    public void testUserQuery02() throws JSONException {

        String[] params = new String[] { "skrish01"};
        String jsonResp = instance.genericQueryExecute( "100110" , Arrays.asList(params) , TransformToJSONObjectsList.class.getName()).get(1);
        JSONArray dataRow = (JSONArray) new JSONArray(jsonResp).get(0);
        Assert.assertTrue( "Expected to contain : data_rows ",((String) dataRow.get(0)).contains("pwd#1234"));
        //Assert.assertTrue( "Expected to contain : data_rows ",resultArray[0][1].contains("ADMIN"));
    }

    @Test
    public void testMultipleQueryExecute() throws JSONException {

        String jsonResp = instance.genericQueryExecute("100100",  new ArrayList(), TransformToJSONObjectsList.class.getName()).get(1);
        JSONArray dataRows = (JSONArray) new JSONArray(jsonResp).get(0);
        Assert.assertTrue( "Expected to contain : data_rows ",( dataRows.length())> 1);
    }

    @Test
    public void testInsertQuery() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String userid =  "testuser"+user_random_suffix ;
        new BaseTestUtils().insertUser01(userid);
    }

    @Test
    public void testUpdateQuery() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String pwd = "pwd#" + user_random_suffix;
        String[] params = new String[] {  pwd , "skrish02" };
        List<String> updates = instance.genericQueryExecute( "100700" , Arrays.asList(params) , TransformToDefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ", (Integer.parseInt(updates.get(1).split(",")[0])==1 ));
    }

    @Test
    public void testUpdateUserError() throws JSONException {
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String pwd = "pwd#" + user_random_suffix;
        String[] params = new String[] {  pwd , "testuser2755" };
        List<String> updates = instance.genericQueryExecute( "100710" , Arrays.asList(params) , TransformToDefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ", updates.get(1).contains("no such column: userid") );
    }


}
