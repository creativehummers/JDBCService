/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.transform;

import com.jdbcservice.enums.ColumnMetaData;
import com.jdbcservice.transform.utils.TransformResultSetToStringArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransformToJSONObjectsList extends AbstractResponseDataTransformer< List<String>, ResultSet > {

    TransformResultSetToStringArray resultSetConvertToString = new TransformResultSetToStringArray();

    public static void init(){
        AbstractResponseDataTransformer.register(TransformToJSONObjectsList.class.getName(), TransformToJSONObjectsList.class);
    }

    public List<String> transform(ResultSet rs){
        List<String> result = new ArrayList<String>();
        try {
            JSONArray columnsJSON = new JSONArray();
            ResultSetMetaData columns_metadata = rs.getMetaData();
            hydrateColumnsMetaData(columns_metadata, columnsJSON);
            result.add(columnsJSON.toString());
            JSONArray rowsJSON = new JSONArray();
            hydrateDataRows(rs, rowsJSON);
            result.add( rowsJSON.toString());
        }  catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    //Legacy performs similar to transform(rs) above.. despite dual loop parsing
    @Deprecated
    public List<String> transformAsListOfHashMaps(ResultSet rs){
        AbstractResponseDataTransformer resultTransformer = null;
        try {
            resultTransformer = AbstractResponseDataTransformer.getResultTransformer(TransformResultSetAsListOfHashMaps.class.getName());
        }  catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return transform((List<List<List>> )resultTransformer.transform(rs));
    }

    //TODO: Directly transform from ResultSet and use the ResultSetConvertor for performance on large datasets iteration
    public List<String> transform(List<List<List>> dbData){
        List<String> result = new ArrayList<String>();
        JSONArray columnsJSON = new JSONArray();
        List columns_metadata = (List) dbData.get(0);
        hydrateColumnsMetaData( columns_metadata, columnsJSON);
        result.add(columnsJSON.toString());
        JSONArray rowsJSON = new JSONArray();
        hydrateDataRows(dbData, rowsJSON);
        result.add( rowsJSON.toString());
        return result;
    }

    private void hydrateColumnsMetaData(ResultSetMetaData columns_metadata, JSONArray columnsJSON) throws SQLException {
        int size =0;
        size = columns_metadata.getColumnCount();
        for( int idx=0; idx < size; idx++ ) {
            JSONObject column = new JSONObject();
            column.put(ColumnMetaData.name.name(),columns_metadata.getColumnName(idx+1));
            column.put(ColumnMetaData.type.name(),columns_metadata.getColumnType(idx+1));
            column.put(ColumnMetaData.type_name.name(), columns_metadata.getColumnTypeName(idx+1));
            column.put(ColumnMetaData.display_size.name(), columns_metadata.getColumnDisplaySize(idx+1));
            column.put(ColumnMetaData.precision.name(), columns_metadata.getPrecision(idx+1));
            column.put(ColumnMetaData.scale.name(), columns_metadata.getScale(idx+1));
            columnsJSON.put(column);
        }
    }

    private void hydrateDataRows( ResultSet rs, JSONArray rowsJSON) throws SQLException, IOException {
         resultSetConvertToString.getColumnObjectValues( rowsJSON, rs, true, TransformResultSetToStringArray.DEFAULT_DATE_FORMAT, TransformResultSetToStringArray.DEFAULT_TIMESTAMP_FORMAT);
    }

    private void hydrateColumnsMetaData(List<Object> columns_metadata, JSONArray columnsJSON){
        for( int idx=0; idx < columns_metadata.size(); idx++ ) {
            List col = (ArrayList) columns_metadata.get(idx);
            JSONObject column = new JSONObject();
            column.put(ColumnMetaData.name.name(),col.get(0));
            column.put(ColumnMetaData.type.name(),col.get(1));
            column.put(ColumnMetaData.type_name.name(), col.get(2));
            column.put(ColumnMetaData.display_size.name(), col.get(3));
            column.put(ColumnMetaData.precision.name(), col.get(4));
            column.put(ColumnMetaData.scale.name(), col.get(5));
            columnsJSON.put(column);
        }

    }

    private void hydrateDataRows(List<List<List>> dbData, JSONArray rowsJSON){
        for( int idx=1; idx < dbData.size(); idx++ ) {
            List<List> data_rows = dbData.get(idx);
            for( int row_idx=0; row_idx < data_rows.size(); row_idx++ ) {
                JSONArray data_row = new JSONArray();
                List<Object> each_row = (List)data_rows.get(row_idx);
                for( int col_idx=0; col_idx < each_row.size(); col_idx++ ) {
                    data_row.put( each_row.get(col_idx));
                }
                rowsJSON.put(data_row);
            }

        }
    }


}
