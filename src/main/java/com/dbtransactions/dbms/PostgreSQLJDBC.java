/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions.dbms;

import com.dbtransactions.enums.QueryType;
import com.dbtransactions.queryexec.AbstractSQLJDBC;
import lombok.extern.java.Log;

import java.util.List;

/*
 *  Postgre specific features if required to be included here, overriding any of super class functions
 */
@Log
public class PostgreSQLJDBC extends AbstractSQLJDBC {

	public static AbstractSQLJDBC getInstance(String poolName){
		PostgreSQLJDBC instance =  new PostgreSQLJDBC();
		instance.createPooledConnection(poolName);
		return instance;
	}

	/*
	 * If there is a need to overwrite the implementation of executeSQL specific to PostGre
	 * modify the query in specific syntax / or replace the implementation of QueryExecutor subclasses..
	 */
	@Override
	public List<String> executeSQL(String queryIdentifier, List params, String formatType, QueryType queryType) {
		return super.executeSQL(queryIdentifier, params, formatType, queryType);
	}


}


