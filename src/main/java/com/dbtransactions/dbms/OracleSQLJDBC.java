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
public class OracleSQLJDBC extends AbstractSQLJDBC {

	public static AbstractSQLJDBC getInstance(String poolName){
		OracleSQLJDBC instance =  new OracleSQLJDBC();
		instance.createPooledConnection(poolName);
		return instance;
	}

	/*
	 * If there is a need to overwrite the implementation of executeSQL specific to Oracle
	 */
	@Override
	public List<String> executeSQL(String queryIdentifier, List params, String formatType, QueryType queryType) {
		return super.executeSQL(queryIdentifier, params, formatType, queryType);
	}
}


