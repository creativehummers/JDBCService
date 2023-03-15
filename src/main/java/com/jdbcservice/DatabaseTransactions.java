/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice;

import com.jdbcservice.dbexec.DefaultQueryExecutor;
import com.jdbcservice.dbexec.PostgreQueryExecutor;
import com.jdbcservice.enums.QueryType;
import com.jdbcservice.exec.AbstractJDBCQueryExecutor;
import com.jdbcservice.transform.*;
import com.jdbcservice.utils.SQLQueries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/*
 *   Provide the JAVA Method APIs to instantiate and run CRUD queries
 *   Both:
 *       serverless capabilities of single execution call sequence
 *       connection pooling based interface through JNDI
 *   handleRequest() method with parameters qualifying it switch to: CRUD, SP, functions..
 */
public class DatabaseTransactions {

    private static Map<String , Class<? extends AbstractJDBCQueryExecutor>> DBJDBCImplementationMap = new HashMap();
    private static final Hashtable<String, DatabaseTransactions> DBPoolInstanceMap = new Hashtable<>();
    protected AbstractJDBCQueryExecutor sqljdbcInstance;    //Instantiated in the constructor

    public static void register(String dbname, Class<? extends AbstractJDBCQueryExecutor> sqljdbcClass){
        DBJDBCImplementationMap.put(dbname, sqljdbcClass);
    }

    static{
        register(DefaultQueryExecutor.class.getName(), DefaultQueryExecutor.class);
        register(PostgreQueryExecutor.class.getName(), PostgreQueryExecutor.class);
    }

    public AbstractJDBCQueryExecutor getDBMSInstance(String dbname, String poolName){
        AbstractJDBCQueryExecutor sqljdbc ;
        try {
            Class aClass = DBJDBCImplementationMap.get(dbname);
            Method aMethod = aClass.getMethod("getInstance", String.class);
            sqljdbc = (AbstractJDBCQueryExecutor) aMethod.invoke(null, poolName);
        } catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        return sqljdbc;
    }

    /*
     *   Create the instance like a factory
     *   @param: fqnDBMSClassName : "com.dbtransactions.dbms.SQLiteJDBC" Or SQLiteJDBC.class.getName()
     */
    public DatabaseTransactions(String fqnDBMSClassName, String poolName) {
        SQLQueries.readQueriesFile();
        sqljdbcInstance = getDBMSInstance(fqnDBMSClassName, poolName);
    }

    /*
     *     The api method to invoke and execute the query
     *     for transfer on tcp / http / other wire protocols.. String based..
     *      @Return : List of Strings viz: JSON or CSV or Plain String for ..
     */
    public List<String> genericQueryExecute(String queryIdentifier, List params, String formatType) {
        if (formatType.equals(TransformResultSetAsListOfHashMaps.class.getName()))
            return fillInError( "Call handleRequest( queryIdentifier, params)");
        String query = SQLQueries.getQuery(queryIdentifier);
        QueryType queryType = SQLQueries.getQueryType(query);

        return sqljdbcInstance.executeQuery(query, params, formatType, queryType);
    }

     /*
     *  For native java data structure of the jdbc result set
     *  For now only for SELECTS as they can have large data transfers
     *  can be protobuf compressed and transferred for efficient handling / performance
     *  @Return : List of column_meta_data and row_values as list of lists .. with java datatypes at the leaf level
     *  first row of column_meta_data provides the enum ColumnMetaData listed values..
     */
    public List<List<List>> genericQueryExecute(String queryIdentifier, List params) {
        String query = SQLQueries.getQuery(queryIdentifier);
        QueryType queryType = SQLQueries.getQueryType(query);
        if( !queryType.equals(QueryType.SELECT))
            return fillInErrorList("Only Selects for returning java nested data structure List<List<List>>");
        List<String> resultList = new ArrayList<>();
        //TODO: can use switch case OR pattern to delegate to executeXXXX method
        return sqljdbcInstance.executeSelect(query, params);
    }

    private List<String> fillInError( String errorMsg) {
        List<String> result = new ArrayList<>();
        result.add( "ERROR");
        result.add( errorMsg); //TODO: iterate if there are multiple
        return result;
    }

    private List<List<List>>  fillInErrorList( String errorMsg) {
        List<List<List>> resultList = new ArrayList<>();
        List row = new ArrayList();
        row.add( Arrays.asList(new String[]{"ERROR"}));
        row.add( Arrays.asList( new String[]{errorMsg})); //TODO: iterate if there are multiple
        resultList.add(row);
        return resultList;
    }
}




