/*
 * @author: Sudhakar Krishnamachari
 */
package com.dbtransactions.transform;

import com.dbtransactions.enums.ColumnMetaData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultListOfStrings extends AbstractResultTransformer< List<String>, ResultSet > {

    public static void init(){
        AbstractResultTransformer.register(DefaultListOfStrings.class.getName(), DefaultListOfStrings.class);
    }

    public List<String> transform(ResultSet rs){
        //Do Nothing Dummy
        List<String> resultList = new ArrayList<String>();
        StringBuffer buff = new StringBuffer();
        resultList.add("Default");
        long rowNum = 0;
        try {
            while (rs.next())
                buff.append(String.valueOf(rowNum) + ",");
        }catch(SQLException ex){}
        resultList.add(buff.toString()); //TODO: Detect Error and record
        return resultList;
    }

}
