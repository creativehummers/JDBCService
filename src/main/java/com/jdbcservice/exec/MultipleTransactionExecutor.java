package com.jdbcservice.exec;

import com.jdbcservice.enums.QueryType;
import com.jdbcservice.utils.SQLQueries;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class MultipleTransactionExecutor extends QueryExecutor{

    private AbstractJDBCQueryExecutor sqljdbcInstance = null;
    private static QueryExecReporter report = new QueryExecReporter();

    public static void init(){
        QueryExecutor.register(QueryType.MULTIPLE, MultipleTransactionExecutor.class);
    }
    /*
     *   Returns result as enum resultFormat declares
     *   close stmt, return the connection to pool after execution
     *   { "column_meta_data" : { "name" : "..." , "type" : ".." , ...} ,
     *     "data_rows" : [ [ ".." , nnn , nn.mm , bool, .. ], [ ...] ]
     *    }
     */
    @Override
    public List<String> executeQuery(String multiQueryString, List listOfParams, String formatType) {
        report.finest("Entering Multiple Query: " + formatType );
        List<String> resultList = new ArrayList<String>();
        String[] queries = splitQueries(multiQueryString);
        if(queries.length != listOfParams.size()) {//receive empty params list if no queries are parameterized
            report.severe("Queries and Params size should match" + queries.length + " : " + listOfParams.size());
            throw new RuntimeException("Queries and Params size should match");
        }
        for( int idx=0; idx < queries.length; idx++){
            QueryType queryType = SQLQueries.getQueryType(queries[0]);
            List<String> result = sqljdbcInstance.executeQuery(queries[idx], (List) listOfParams.get(0), formatType,queryType);
            report.info("Result: " + idx + " : " + result.get(0));
            resultList.addAll(result);
        }

        return resultList;
    }

    /*
     * Multiple multiline Queries expected format : querySeparator of the format: ";"+ System.lineSeparator()
     * Select | Update | Delete
     *         [ {queryString} ] ;\r\n windows OR ;\n in linux/others.
     */
    private String[] splitQueries(String multiQueryString) {
        String[] queries = new String[]{};
        if(!multiQueryString.endsWith(";"+System.lineSeparator()))
            throw new RuntimeException("Multiple Query String should end with ;\r\n");
        queries = multiQueryString.split(";"+ System.lineSeparator()); //windows : ";\r\n" linux: ";\n"

        List<String> queryList  = Arrays.stream(queries)
                .map( eachQuery -> extractQueryString(eachQuery) )
                .filter( ea -> !ea.isBlank())
                .collect(Collectors.toList());
        return queryList.toArray(new String[queryList.size()]);
    }

    private static final String QueryCommentPrefix  = "--";

    //handle "--" comments, and blank lines, other potential basic syntax mismatch..
    private static String extractQueryString(String baseQueryString){
        String[] queryLines = baseQueryString.split(System.lineSeparator());
        if(queryLines.length > 0 && queryLines[0].endsWith("\r") && System. getProperty("os.name").contains("Linux"))
            throw new RuntimeException("Query is ending with Windows lineSeparator \r\n");
        List<String> splitList = Arrays.stream(queryLines)
                .filter( eachLine -> !eachLine.isEmpty() )
                .filter( eachLine -> !eachLine.trim().startsWith(QueryCommentPrefix))
                .collect(Collectors.toList());
        return String.join("", splitList);
    }

    public String getQueryExecutorType(){
        return "MULTIPLE";
    }

    public void setSqljdbcInstance(AbstractJDBCQueryExecutor sqljdbcInstance) {
        this.sqljdbcInstance = sqljdbcInstance;
    }
}
