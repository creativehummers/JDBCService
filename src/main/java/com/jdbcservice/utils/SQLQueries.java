/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.utils;

import com.jdbcservice.DatabaseTransactions;
import com.jdbcservice.dbexec.DefaultQueryExecutor;
import com.jdbcservice.enums.QueryType;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 *  Create a basic test queries map from file in default working directory
 *  Then on query execute request fetch the queries from DB cache
 */
@Log
public class SQLQueries {

    static HashMap<String, String> queriesMap = new HashMap<>() {{
        put("100", "Select 333");
    }};

    /*
     *  Read a queries file for test queries to validate the framework with
     *  CRUD ops and permutations thereof
     */
    public static void readQueriesFile() {
        try {
            List<String> lines = readAllLinesFromQueriesFile();
            for(String line: lines){
                String[] kvPair = line.split(":");
                queriesMap.put(kvPair[0].trim(), kvPair[1].trim());
            }
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            System.exit(-1);
        }
        log.info("queries map  " + queriesMap);
    }

    /*
     *  Read all lines of the file with exception thrown if it exceeds MAXQUERIESINFILE
     */
    private static List<String> readAllLinesFromQueriesFile() throws IOException {
        String filename = new File(".").getAbsolutePath().replace("." , "")+ ApplicationConstants.QueriesFile;
        final List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        if(lines.size() > ApplicationConstants.MAXQUERIESINFILE)
            throw new RuntimeException("Exceeded Max allowed number of queries in the test queries file: " + ApplicationConstants.MAXQUERIESINFILE);
        return lines;
    }

    /*
     *  PRIMARY API for fetching the parameterized query string from test map, transactions folder or Database cache
     */
    public static String getQuery(String tranID) {
        log.finest("" + tranID);
        String query = "";
        try {
            query = queriesMap.get(tranID);
            //TODO: better pattern to deal with strategy of picking query from needed source
            if (query == null)
                query = getQueryFromFile(tranID);
            if (query == null)
                query = getQueryStringFromDBCache(tranID);
        } catch (Exception ex) {
            log.severe("SQLQueries Error: " + ex.getMessage());
        }
        return query == null ? "" : query;
    }

    /*
     *  Large SQLs can be fetched from a transactions folder one tranID named file
     */
    private static String getQueryFromFile(String tranID) {
        String query = null;
        try {
            String filename = ApplicationConstants.SQLSubFolderName+ "/" + tranID;
            query = Files.readString(Paths.get(filename), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.severe("SQLQueries Error: " + ex.getMessage());
        }
        return query;
    }

    /*
     *  For many applications a DB cache based query fetch will be the default and last stop
     */
    private static String getQueryStringFromDBCache(String tranID) {
        //TODO: map from application.properties
        String dbCacheDBMS = DefaultQueryExecutor.class.getName();
        String dbPoolName = ApplicationConstants.DefaultDBPoolName;

        DatabaseTransactions instance = new DatabaseTransactions( dbCacheDBMS, dbPoolName);
        String fetchSQLQuery = ApplicationConstants.FetchSQLQuery;
        List params = Arrays.asList(new String[]{tranID});
        List<List<List>> resultList =  instance.genericQueryExecute( fetchSQLQuery, params);
        return (String)((List)resultList.get(1)).get(0);
    }

    /*
     *  basic logic to define the query type; can improve it with SQL features ?
     */
    public static QueryType getQueryType(String query) {
        if(query.toUpperCase().trim().startsWith("SELECT"))
            return QueryType.SELECT;
        else
            return QueryType.UPDATE;  //TODO: in StoredProc, Functions later..
    }


}

