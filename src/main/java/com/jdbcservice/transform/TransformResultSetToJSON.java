/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.transform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class TransformResultSetToJSON {

  public static JSONObject convert( ResultSet rs )throws SQLException, JSONException
  {
    JSONObject resultSetAsJSONObj = new JSONObject();
    
    ResultSetMetaData rsmd = rs.getMetaData();
    int numColumns = rsmd.getColumnCount();
    String[] columnNames = new String[numColumns];
    int rowCount = 0;  

    JSONArray columnsJSON = new JSONArray(); 
    for (int col_idx=1; col_idx<numColumns+1; col_idx++) {
	JSONObject column = new JSONObject();
        column.put("column_name" , rsmd.getColumnName(col_idx));
	column.put("column_type" , rsmd.getColumnType(col_idx));
        column.put("column_type_name" , rsmd.getColumnTypeName(col_idx));
	column.put("column_display_size" , rsmd.getColumnDisplaySize(col_idx));
	column.put("precision" , rsmd.getPrecision(col_idx));
	column.put("scale" , rsmd.getScale(col_idx));

	columnsJSON.put(column);
	}

    resultSetAsJSONObj.put("columns_metadata" , columnsJSON);

    JSONArray rowsJSON = new JSONArray();

    while(rs.next()) {
      JSONArray rowValues = new JSONArray();
      for (int col_idx=1; col_idx<numColumns+1; col_idx++) {
	convertColumnToJSON( rsmd, rs, col_idx,  rowValues ); 
      }
      rowsJSON.put(rowValues);
    }
      resultSetAsJSONObj.put("data_rows" , rowsJSON);

    return resultSetAsJSONObj;
  }


  public static void convertColumnToJSON( ResultSetMetaData rsmd,ResultSet rs, int col_idx, JSONArray rowValues ) throws SQLException, JSONException{
	
	int columnType = rsmd.getColumnType(col_idx);
	
	switch( columnType ) {

	      case Types.ARRAY:
	         rowValues.put(rs.getArray(col_idx)); break;
          case Types.BIGINT:						//Should it be getLong ?
		     BigDecimal decimal = rs.getBigDecimal(col_idx);
		     BigInteger val = (decimal == null ? null : decimal.toBigInteger());
	         rowValues.put(val); 
		     break;
          case Types.BOOLEAN: 
	         rowValues.put(rs.getBoolean(col_idx)); break;
          case Types.BLOB: 
	         rowValues.put(rs.getBlob(col_idx)); break;
          case Types.BIT: 
	         rowValues.put(rs.getBoolean(col_idx)); break;
          case Types.BINARY: 
	        rowValues.put(rs.getBytes(col_idx)); break; 	 	//check.. if this holds BINARY -> getBytes..?
          case Types.CHAR: 
	        rowValues.put(rs.getByte(col_idx)); break; 
          case Types.CLOB: 
	        rowValues.put(rs.getClob(col_idx)); break; 
          case Types.DECIMAL: 
	        rowValues.put(rs.getBigDecimal(col_idx)); break; 	//convert this to the precision of the column
          case Types.DOUBLE: 
	         rowValues.put(rs.getDouble(col_idx)); break; 
          case Types.FLOAT: 
	         rowValues.put(rs.getFloat(col_idx)); break;
          case Types.INTEGER: 
	         rowValues.put(rs.getInt(col_idx)); break;
          case Types.LONGNVARCHAR: 
	         rowValues.put(rs.getNString(col_idx)); break;
          case Types.LONGVARCHAR: 
	         rowValues.put(rs.getString(col_idx)); break;
          case Types.NCHAR: 
	         rowValues.put(rs.getBytes(col_idx)); break;		//convert to unicode / UTF8 char..?
          case Types.NCLOB: 
	         rowValues.put(rs.getNClob(col_idx)); break;
          case Types.NVARCHAR: 
	         rowValues.put(rs.getNString(col_idx)); break;
          case Types.ROWID: 
	         rowValues.put(rs.getString(col_idx)); break;		//OR perhaps getObject(...).toString()..?
          case Types.REAL: 
	         rowValues.put(rs.getFloat(col_idx)); break;		//single precision type.. ?
          case Types.SMALLINT: 
	         rowValues.put(rs.getInt(col_idx)); break;
          case Types.SQLXML: 
	         rowValues.put(rs.getSQLXML(col_idx).getString()); break; //presume this is not huge 100MB+ XMLS..!..
          case Types.DATE: 
	         rowValues.put(rs.getDate(col_idx)); break;
          case Types.TIMESTAMP: 
	        rowValues.put(rs.getTimestamp(col_idx)); break; 
          case Types.VARBINARY: 
	        rowValues.put(rs.getBytes(col_idx)); break;   
	  case Types.VARCHAR: 
	         rowValues.put(rs.getString(col_idx)); break;
          default:
	        rowValues.put(rs.getObject(col_idx)); break;
		}
	  //Default caters for now to DATALINK, DISTINCT, NULL, REF, REF_CURSOR, TIME_WITH_TIMEZONE, TIMESTAMP_WITH_TIMEZONE,

	}
}
