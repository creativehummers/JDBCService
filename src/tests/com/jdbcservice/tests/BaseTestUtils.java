package com.jdbcservice.tests;

import com.jdbcservice.DatabaseTransactions;
import com.jdbcservice.dbexec.DefaultQueryExecutor;
import com.jdbcservice.transform.TransformToDefaultListOfStrings;
import com.jdbcservice.transform.TransformToJSONObjectsList;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

@Log
public class BaseTestUtils {

    static DatabaseTransactions instance = null;
    private long NumberOfRowsInsert = 10;

    BaseTestUtils(){
    }

    BaseTestUtils(long numOfRows){
        NumberOfRowsInsert = numOfRows;
    }

    public static void resetInstance() {
        instance = null;
    }

    public void init(){
        String jsonResp="";
        if(instance != null) //Hacky, for some reason the database in memory vanishes.. for the latter couple of tests..
            jsonResp = instance.genericQueryExecute("100010",Arrays.asList("skrish01"), TransformToJSONObjectsList.class.getName() ).get(1);
        if( instance == null || jsonResp.contains("ERROR")) {
            instance = new DatabaseTransactions(DefaultQueryExecutor.class.getName(), "default");
            settingUpDatabase();
        }
    }

    void settingUpDatabase(){
        log.info("*** Setting up the sqllite database in memory for all the tests");
        log.info("*** CREATE TABLES test_user and user_profile");
        instance.genericQueryExecute( "900000" , new ArrayList(), TransformToDefaultListOfStrings.class.getName());
        instance.genericQueryExecute( "900001" , new ArrayList(), TransformToDefaultListOfStrings.class.getName());
        insertUser01("skrish01");
        insertUser01("skrish02");
        insertUser01("skrish03");
        insertUserProfile01();
        selectAll();
        long startTime = System.nanoTime();
        List<List> paramsList = new ArrayList<>();
        LongStream.range(0, NumberOfRowsInsert).forEach(idx ->paramsList.add(Arrays.asList(createInsertSQLArgs(null))));
        List<String> updates = instance.genericQueryExecute( "100501" , paramsList , TransformToDefaultListOfStrings.class.getName());
        log.severe("Perf Inserting Records: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");
        log.info("*** Database Setup Complete : Run All Tests ...");
    }

    private void selectAll(){
        log.info("*** Validate the tables and rows are correctly created");
        List<String> resultList = instance.genericQueryExecute("111100",  new ArrayList(), TransformToJSONObjectsList.class.getName());
        Assert.assertTrue("First record is for skrish02", "skrish03".equals(((JSONArray)new JSONArray(resultList.get(1)).get(2)).get(1)) );
        Assert.assertTrue( "Expects 3 record rows", new JSONArray(resultList.get(1)).length() == 3);
    }

    private void insertUserProfile01() {
        log.info("*** INSERT rows into user_profile table: connected through uuid : profile_code from test_user");
        String uuid = "USPR_2022_DEC_XRFE_A129_XE12";
        String profile_category="ADMIN";
        String[] params = new String[] { uuid, profile_category};
        List<String> updates = instance.genericQueryExecute( "100511" , Arrays.asList(params) , TransformToDefaultListOfStrings.class.getName());
        Assert.assertTrue("Expect 1 row with given uuid" , "1".equals(updates.get(1)));
    }

    void insertUser01(String uid) throws JSONException {
        String[] params = createInsertSQLArgs(uid);
        List<String> updates = instance.genericQueryExecute( "100501" , Arrays.asList(params) , TransformToDefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ",(Integer.parseInt(updates.get(1).split(",")[0])==1 ));
    }

    String[] createInsertSQLArgs(String uid) {
        if(uid != null) //avoid logging for bulk inserts
            log.info("*** INSERT rows into test_user table: " + uid);
        String uuid = "USER_2023_"+ new java.util.Random().nextInt(9999) +"_"+new java.util.Random().nextInt(9999);
        String phone = "+91-"+ new java.util.Random().nextInt(9999) +"-"+new java.util.Random().nextInt(999999);
        int user_random_suffix = new java.util.Random().nextInt(9999);
        String last_name = "User_"+ user_random_suffix;
        String userid = uid == null ? "testuser"+user_random_suffix : uid;
        //String profile_code = "USPR_2022_DEC_XRFE-"+user_random_suffix;
        String[] params = new String[] { uuid, "Test" , last_name , userid , "pwd#1234" , "adjda@gmail.com" , phone, "USPR_2022_DEC_XRFE_A129_XE12" , "Unit Test Data Creation 01" };
        return params;
    }


}

