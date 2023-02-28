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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Log
public class CommonUtils {

    static DatabaseTransactions instance = null;
    private long NumberOfRowsInsert = 10;

    CommonUtils(){
    }

    CommonUtils(long numOfRows){
        NumberOfRowsInsert = numOfRows;
    }

    public static void resetInstance() {
        instance = null;
    }

    public void init(){
        String jsonResp="";
        if(instance != null) //Hacky, for some reason the database in memory vanishes.. for the latter couple of tests..
            jsonResp = instance.handleRequest("100010",Arrays.asList("skrish01"),JSONObjectsList.class.getName() ).get(1);
        if( instance == null || jsonResp.contains("ERROR")) {
            instance = new DatabaseTransactions(DefaultJDBC.class.getName(), "default");
            testSettingUpDatabase();
        }
    }

    void testSettingUpDatabase(){
        log.info("*** Setting up the sqllite database in memory for all the tests");
        log.info("*** CREATE TABLES test_user and user_profile");
        instance.handleRequest( "900000" , new ArrayList(), DefaultListOfStrings.class.getName());
        instance.handleRequest( "900001" , new ArrayList(), DefaultListOfStrings.class.getName());
        testInsertTestUser01("skrish01");
        testInsertTestUser01("skrish02");
        testInsertTestUser01("skrish03");
        testInsertTestUserProfile01();
        testSelectAll();
        long startTime = System.nanoTime();
        List<List> paramsList = new ArrayList<>();
        LongStream.range(0, NumberOfRowsInsert).forEach(idx ->paramsList.add(Arrays.asList(createInsertTestParams(null))));
        List<String> updates = instance.handleRequest( "100501" , paramsList , DefaultListOfStrings.class.getName());
        log.severe("Perf Inserting Records: "+ NumberOfRowsInsert + " : " + (System.nanoTime()-startTime)/1_000_000L + " ms");
        log.info("*** Database Setup Complete : Run All Tests ...");
    }

    private void testSelectAll(){
        log.info("*** Validate the tables and rows are correctly created");
        List<String> resultList = instance.handleRequest("111100",  new ArrayList(), JSONObjectsList.class.getName());
        Assert.assertTrue("First record is for skrish02", "skrish03".equals(((JSONArray)new JSONArray(resultList.get(1)).get(2)).get(1)) );
        Assert.assertTrue( "Expects 3 record rows", new JSONArray(resultList.get(1)).length() == 3);
    }

    private void testInsertTestUserProfile01() {
        log.info("*** INSERT rows into user_profile table: connected through uuid : profile_code from test_user");
        String uuid = "USPR_2022_DEC_XRFE_A129_XE12";
        String profile_category="ADMIN";
        String[] params = new String[] { uuid, profile_category};
        List<String> updates = instance.handleRequest( "100511" , Arrays.asList(params) , DefaultListOfStrings.class.getName());
        Assert.assertTrue("Expect 1 row with given uuid" , "1".equals(updates.get(1)));
    }

    void testInsertTestUser01(String uid) throws JSONException {
        String[] params = createInsertTestParams(uid);
        List<String> updates = instance.handleRequest( "100501" , Arrays.asList(params) , DefaultListOfStrings.class.getName());
        Assert.assertTrue( "Expected to contain : data_rows ",(Integer.parseInt(updates.get(1).split(",")[0])==1 ));
    }

    String[] createInsertTestParams(String uid) {
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

    private String myBatisJdbcCompare(){
        //https://github.com/mybatis/mybatis-3/issues/98
        String result =
            "Mybatis Jdbc"+System.lineSeparator()+
            "Data quantity 100000 rows 100000 rows"+System.lineSeparator()+
            "batch size/fetch size 1000 rows 1000 rows"+System.lineSeparator()+
            "Insert cost 2841 ms 1843 ms"+System.lineSeparator()+
            "Select cost 2345 ms 1328 ms"+System.lineSeparator();

        result = result+System.lineSeparator()+
                "version                insert cost/ms select cost/ms"+System.lineSeparator()+
                "jdbc                   8721           6603"+System.lineSeparator()+
                "ibatis2.3.5            11268          15153"+System.lineSeparator()+
                "mybatis3.2.3           23790"+System.lineSeparator()+
                "mybatis3.2.3(with raw) 11796          14061"+System.lineSeparator();

        return result;
    }
}

