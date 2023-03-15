/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.transform;

import com.jdbcservice.transform.utils.TransformResultSetToStringArray;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransformToCSVData extends AbstractResponseDataTransformer<List<String>, ResultSet> {

    public static void init(){
        AbstractResponseDataTransformer.register(TransformToCSVData.class.getName(), TransformToCSVData.class);
    }

    TransformResultSetToStringArray resultSetConvertToString = new TransformResultSetToStringArray();
    StringBuffer rowsBuf = new StringBuffer();
    List cols = new ArrayList<String>();
    List rows = new ArrayList<String>();
    private String separator = "," ;

    public List<String> transform(ResultSet rs){
        List<String> result = new ArrayList<String>();
        String[] columnList = new String[0];
        try {
            columnList = resultSetConvertToString.getColumnNames(rs);
            result.add(String.join(separator,columnList));
            StringBuilder rowBuffer = new StringBuilder();
            while (rs.next()) {
                String[] rowValues =resultSetConvertToString.getColumnValues(rs);
                rowBuffer.append(String.join( separator, rowValues));
                rowBuffer.append(System.lineSeparator());
             }
            result.add(rowBuffer.toString());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }


}
