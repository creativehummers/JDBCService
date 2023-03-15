package com.jdbcservice.exec;

import com.jdbcservice.enums.QueryType;
import com.jdbcservice.transform.TransformToDefaultListOfStrings;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class UpdateTransactionExecutor extends QueryExecutor{

    private AbstractJDBCQueryExecutor sqljdbcInstance = null;

    public static void init(){
        QueryExecutor.register(QueryType.UPDATE, UpdateTransactionExecutor.class);
    }

    /*
     *   all insert, update and delete
     *   should implement a check from whitelist / blacklist set of endpoint DB or tables
     */
    @Override
    public List<String> executeQuery(String query, List params, String formatType) {
        List<String> resultList = new ArrayList<String>();
        if(!formatType.equals(TransformToDefaultListOfStrings.class.getName())){
            resultList.add("ERROR");
            resultList.add("Only format: DefaultListOfStrings is supported");
            return resultList;
        }
        Connection conn = sqljdbcInstance.connect();
        try{
            int[] resultCount = sqljdbcInstance.executeUpdateWithConnection(conn, query, params );
            resultList.add("ResultCount");
            String str = Arrays.toString(resultCount);
            resultList.add( str.substring(1,str.length()-1) );
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            resultList.add("ERROR");
            resultList.add("executeSelect: " + ex.getMessage());
        }
        return resultList;
    }

    public void setSqljdbcInstance(AbstractJDBCQueryExecutor sqljdbcInstance) {
        this.sqljdbcInstance = sqljdbcInstance;
    }

    public String getQueryExecutorType(){
        return "UPDATE";
    }

}
