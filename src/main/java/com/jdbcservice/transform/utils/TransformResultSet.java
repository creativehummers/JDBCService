package com.jdbcservice.transform.utils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TransformResultSet {

        /**
         * Returns the column names from the ResultSet.
         */
        String[] getColumnNames(ResultSet rs) throws SQLException;

        /**
         */
        String[] getColumnValues(ResultSet rs) throws SQLException, IOException;

        /**
         */
        String[] getColumnValues(ResultSet rs, boolean trim) throws SQLException, IOException;

        /**
         */
        String[] getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString)
                throws SQLException, IOException;

}
