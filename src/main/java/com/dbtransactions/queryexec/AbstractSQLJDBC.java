/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions.queryexec;

import com.dbtransactions.enums.QueryType;
import com.dbtransactions.transform.AbstractResultTransformer;
import com.dbtransactions.transform.ResultSetAsListOfHashMaps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*
*   Implement concrete classes for the databases target required
*   Implementation for common functionality can be provided
 */
@Log
public abstract class AbstractSQLJDBC {

    /*   connection jdbc connection and pool details from dbcp pooling */
    public DataSource jdbcPool;


    public List<String> executeSQL(String query, List<String> params, String formatType, QueryType queryType){

        List<String> resultList = new ArrayList<>();
        try {
            QueryExecutor queryExecutor = QueryExecutor.getQueryExecutor(queryType);
            queryExecutor.setSqljdbcInstance(this);
            resultList = queryExecutor.executeQuery(query, params, formatType);
        }  catch (Exception ex) {
            resultList.add("Error");
            resultList.add("Execute: " + ex.getMessage());
            log.severe("executeSQL Error: "+ ex.getMessage());
        }
        return resultList;
    }

    /*
    *   Returns result as enum resultFormat declares
    *   close stmt, return the connection to pool after execution
     */
    public List<List<List>> executeSelect(String query, List<String> params){
        List<List<List>> resultList = new ArrayList<>();
        Connection conn = connect();
        try{
            List<Object> result = new ArrayList<>();
            executeSelectWithConnection(conn, query, params, result, ResultSetAsListOfHashMaps.class.getName() );
            resultList.add( (List<List>) result.get(0));
            resultList.add( (List<List>) result.get(1));
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            fillInError( resultList,  ex.getMessage());
        }
        return resultList;
    }

    private void fillInError(List<List<List>> resultList, String errorMsg) {
        List row = new ArrayList();
        row.add( Arrays.asList(new String[]{"ERROR"}));
        row.add( Arrays.asList(new String[]{errorMsg})); //TODO: iterate if there are multiple
        resultList.add(row);
    }

    protected void executeSelectWithConnection(Connection conn, String query, List params, List<Object> resultList, String formatType) throws SQLException{
        PreparedStatement stmt = getPreparedStatement(  conn,  query,  params);
        stmt.setFetchSize(1000);
        ResultSet rs = stmt.executeQuery();
        try{
            AbstractResultTransformer resultTransformer = AbstractResultTransformer.getResultTransformer(formatType);
            List result = (List) resultTransformer.transform(rs);
            resultList.addAll(result);
        } catch(Exception ex){
            resultList.add("Error");
            resultList.add("Conversion: " + ex.getMessage());
        }
        stmt.close();
        rs.close();
        conn.close(); //HikariCP does not "close" but cleans up and returns to the pool
    }

    protected int[] executeUpdateWithConnection(Connection conn, String query, List params) throws SQLException{
        PreparedStatement stmt = getPreparedStatement(  conn,  query,  params);
        int[] resultCount = stmt.executeBatch();
        stmt.close();
        conn.close(); //HikariCP does not "close" but cleans up and returns to the pool
        return resultCount;
    }

    private PreparedStatement getPreparedStatement( Connection conn, String query, List params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        List<List> paramsList = new ArrayList();
        if(params.size() >  0 && params.get(0) instanceof List)
             paramsList = params;
        else
            paramsList.add(params);

        addToBatch(stmt, paramsList);
        return stmt;
    }

    private void addToBatch(PreparedStatement stmt, List<List> paramsList) throws SQLException{
        for(List params : paramsList ) {
            for (int idx = 0; idx < params.size(); idx++) {
                stmt.setObject(idx + 1, params.get(idx));
            }
            stmt.addBatch();
        }

    }

    public Connection connect() {
        try {
            return jdbcPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Implement Registry Pattern to instantiate the connection pool required : DBCP / Hikari / ... any others provided
    protected void createPooledConnection(String dbIdentifier){
        log.info("Opening database on createPooledConnection");
        HikariConfig config = new HikariConfig(dbIdentifier+".properties");
        jdbcPool = new HikariDataSource(config);
    }

    /*
     *   Connect using JNDI typically inside a container viz: Tomcat
     */
    public void connectJNDI(){
        log.info("Opening database on connectJNDI");
        InitialContext ctx;
        try {
            ctx = new InitialContext();
            jdbcPool = (DataSource) ctx.lookup("java:/comp/env/jdbc/db-transactions-server");
        } catch (Exception e) {
            log.severe(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        log.info("Opened database successfully");
    }

}