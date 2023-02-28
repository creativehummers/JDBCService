/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions.transform;

import com.dbtransactions.enums.ColumnMetaData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JSONObjectsList extends AbstractResultTransformer< List<String>, ResultSet > {

    public static void init(){
        AbstractResultTransformer.register(JSONObjectsList.class.getName(), JSONObjectsList.class);
    }

    public List<String> transform(ResultSet rs){
        AbstractResultTransformer resultTransformer = null;
        try {
            resultTransformer = AbstractResultTransformer.getResultTransformer(ResultSetAsListOfHashMaps.class.getName());
        }  catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return transform((List<List<List>> )resultTransformer.transform(rs));
    }

    //TODO: Directly transform from ResultSet and use the ResultSetConvertor for performance on large datasets iteration
    public List<String> transform(List<List<List>> dbData){
        List<String> result = new ArrayList<String>();
        JSONObject resultSetAsJSONObj = new JSONObject();
        JSONArray columnsJSON = new JSONArray();
        List columns_metadata = (List) dbData.get(0);
        hydrateColumnsMetaData( columns_metadata, columnsJSON);
        result.add(columnsJSON.toString());
        JSONArray rowsJSON = new JSONArray();
        hydrateDataRows(dbData, rowsJSON);
        result.add( rowsJSON.toString());
        return result;
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
