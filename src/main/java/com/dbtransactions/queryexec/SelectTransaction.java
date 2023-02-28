package com.dbtransactions.queryexec;

import com.dbtransactions.enums.QueryType;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log
public class SelectTransaction extends QueryExecutor{

    private AbstractSQLJDBC sqljdbcInstance = null;

    public static void init(){
        QueryExecutor.register(QueryType.SELECT, SelectTransaction.class);
    }
    /*
     *   Returns result as enum resultFormat declares
     *   close stmt, return the connection to pool after execution
     *   { "column_meta_data" : { "name" : "..." , "type" : ".." , ...} ,
     *     "data_rows" : [ [ ".." , nnn , nn.mm , bool, .. ], [ ...] ]
     *    }
     */
    @Override
    public List<String> executeQuery(String query, List params, String formatType) {
        List<String> resultList = new ArrayList<String>();
        Connection conn = sqljdbcInstance.connect();
        try{
            List<Object> result = new ArrayList<Object>();
            sqljdbcInstance.executeSelectWithConnection(conn, query, params, result, formatType );
            resultList.add((String) result.get(0));
            resultList.add((String) result.get(1));
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            resultList.add("ERROR");
            resultList.add("executeSelect: " + ex.getMessage());
        }
        return resultList;
    }

    public void setSqljdbcInstance(AbstractSQLJDBC sqljdbcInstance) {
        this.sqljdbcInstance = sqljdbcInstance;
    }

    public String getQueryExecutorType(){
        return "SELECT";
    }
}
