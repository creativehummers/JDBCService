/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.dbexec;

import com.jdbcservice.enums.QueryType;
import com.jdbcservice.exec.AbstractJDBCQueryExecutor;
import lombok.extern.java.Log;

import java.util.List;

/*
 *  Postgre specific features if required to be included here, overriding any of super class functions
 */
@Log
public class PostgreQueryExecutor extends AbstractJDBCQueryExecutor {

	public static AbstractJDBCQueryExecutor getInstance(String poolName){
		PostgreQueryExecutor instance =  new PostgreQueryExecutor();
		instance.createPooledConnection(poolName);
		return instance;
	}

	/*
	 * If there is a need to overwrite the implementation of executeSQL specific to PostGre
	 * modify the query in specific syntax / or replace the implementation of QueryExecutor subclasses..
	 */
	@Override
	public List<String> executeQuery(String queryIdentifier, List params, String formatType, QueryType queryType) {
		return super.executeQuery(queryIdentifier, params, formatType, queryType);
	}


}


