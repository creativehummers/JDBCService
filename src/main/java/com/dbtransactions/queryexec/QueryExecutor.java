package com.dbtransactions.queryexec;

import com.dbtransactions.enums.QueryType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public abstract class QueryExecutor {

    private static HashMap<QueryType, Class<? extends QueryExecutor>> QueryExecutorRegistry = new HashMap<>();

    public static void register(QueryType queryType, Class<? extends QueryExecutor> queryExecutor){
        QueryExecutorRegistry.put(queryType, queryExecutor);
    }

    static{
        SelectTransaction.init();
        UpdateTransaction.init();
        MultipleTransaction.init();
    }

    public static QueryExecutor getQueryExecutor( QueryType queryType){
        QueryExecutor executor;
        try{
            executor =QueryExecutorRegistry.get(queryType).getDeclaredConstructor().newInstance();
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        return executor;
    }

    public abstract List<String> executeQuery(String query, List params, String formatType);

    public abstract String getQueryExecutorType();

    public abstract void setSqljdbcInstance(AbstractSQLJDBC sqljdbcInstance);
    
}
