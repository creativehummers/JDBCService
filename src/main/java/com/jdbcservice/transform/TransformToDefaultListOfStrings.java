/*
 * @author: Sudhakar Krishnamachari
 */
package com.jdbcservice.transform;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransformToDefaultListOfStrings extends AbstractResponseDataTransformer< List<String>, ResultSet > {

    public static void init(){
        AbstractResponseDataTransformer.register(TransformToDefaultListOfStrings.class.getName(), TransformToDefaultListOfStrings.class);
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
