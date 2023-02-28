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
public class DefaultJDBC extends AbstractSQLJDBC {

	public static AbstractSQLJDBC getInstance(String poolName){
		DefaultJDBC instance =  new DefaultJDBC();
		instance.createPooledConnection(poolName);
		return instance;
	}

	@Override
	public List<String> executeSQL(String queryIdentifier, List params, String formatType, QueryType queryType) {
		return super.executeSQL(queryIdentifier, params, formatType, queryType);
	}
}


