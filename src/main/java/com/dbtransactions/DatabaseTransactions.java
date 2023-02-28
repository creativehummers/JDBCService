/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions;

import com.dbtransactions.dbms.DefaultJDBC;
import com.dbtransactions.dbms.OracleSQLJDBC;
import com.dbtransactions.dbms.PostgreSQLJDBC;
import com.dbtransactions.enums.QueryType;
import com.dbtransactions.queryexec.AbstractSQLJDBC;
import com.dbtransactions.transform.*;
import com.dbtransactions.utils.SQLQueries;

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

    private static Map<String , Class<? extends AbstractSQLJDBC>> DBJDBCImplementationMap = new HashMap();
    private static final Hashtable<String, DatabaseTransactions> DBPoolInstanceMap = new Hashtable<>();
    protected AbstractSQLJDBC sqljdbcInstance;    //Instantiated in the constructor

    public static void register(String dbname, Class<? extends AbstractSQLJDBC> sqljdbcClass){
        DBJDBCImplementationMap.put(dbname, sqljdbcClass);
    }

    static{
        register(DefaultJDBC.class.getName(), DefaultJDBC.class);
        register(PostgreSQLJDBC.class.getName(), PostgreSQLJDBC.class);
        register(OracleSQLJDBC.class.getName(), OracleSQLJDBC.class);
    }

    public AbstractSQLJDBC getDBMSInstance(String dbname, String poolName){
        AbstractSQLJDBC sqljdbc ;
        try {
            Class aClass = DBJDBCImplementationMap.get(dbname);
            Method aMethod = aClass.getMethod("getInstance", String.class);
            sqljdbc = (AbstractSQLJDBC) aMethod.invoke(null, poolName);
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
    public List<String> handleRequest(String queryIdentifier, List params, String formatType) {
        if (formatType.equals(ResultSetAsListOfHashMaps.class.getName()))
            return fillInError( "Call handleRequest( queryIdentifier, params)");
        String query = SQLQueries.getQuery(queryIdentifier);
        QueryType queryType = SQLQueries.getQueryType(query);

        return sqljdbcInstance.executeSQL(query, params, formatType, queryType);
    }

     /*
     *  For native java data structure of the jdbc result set
     *  For now only for SELECTS as they can have large data transfers
     *  can be protobuf compressed and transferred for efficient handling / performance
     *  @Return : List of column_meta_data and row_values as list of lists .. with java datatypes at the leaf level
     *  first row of column_meta_data provides the enum ColumnMetaData listed values..
     */
    public List<List<List>> handleRequest(String queryIdentifier, List params) {
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




