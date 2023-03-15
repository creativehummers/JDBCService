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
public class DefaultQueryExecutor extends AbstractJDBCQueryExecutor {

	public static AbstractJDBCQueryExecutor getInstance(String poolName){
		DefaultQueryExecutor instance =  new DefaultQueryExecutor();
		instance.createPooledConnection(poolName);
		return instance;
	}

	@Override
	public List<String> executeQuery(String queryIdentifier, List params, String formatType, QueryType queryType) {
		return super.executeQuery(queryIdentifier, params, formatType, queryType);
	}
}


