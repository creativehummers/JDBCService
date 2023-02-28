package com.dbtransactions.transform.helper;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Objects;

    public class ResultSetToStringArray implements ResultSetConverter{

        protected static final int CLOBBUFFERSIZE = 2048;

        static final String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";
        static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yyyy HH:mm:ss";

        private String dateFormat = DEFAULT_DATE_FORMAT;
        private String dateTimeFormat = DEFAULT_TIMESTAMP_FORMAT;

        public ResultSetToStringArray() {
        }


        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public void setDateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
        }

        @Override
        public String[] getColumnNames(ResultSet rs) throws SQLException {
            ResultSetMetaData metadata = rs.getMetaData();
            String[] nameArray = new String[metadata.getColumnCount()];
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                nameArray[i] = metadata.getColumnLabel(i+1);
            }
            return nameArray;
        }

        @Override
        public String[] getColumnValues(ResultSet rs) throws SQLException, IOException {
            return this.getColumnValues(rs, false, dateFormat, dateTimeFormat);
        }

        @Override
        public String[] getColumnValues(ResultSet rs, boolean trim) throws SQLException, IOException {
            return this.getColumnValues(rs, trim, dateFormat, dateTimeFormat);
        }

        @Override
        public String[] getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString) throws SQLException, IOException {
            ResultSetMetaData metadata = rs.getMetaData();
            String[] valueArray = new String[metadata.getColumnCount()];
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                valueArray[i-1] = getColumnValue(rs, metadata.getColumnType(i), i,
                        trim, dateFormatString, timeFormatString);
            }
            return valueArray;
        }

        /**
         * The formatted timestamp.
         */
        protected String handleTimestamp(Timestamp timestamp, String timestampFormatString) {
            SimpleDateFormat timeFormat = new SimpleDateFormat(timestampFormatString);
            return timestamp == null ? null : timeFormat.format(timestamp);
        }

        private String getColumnValue(ResultSet rs, int colType, int colIndex, boolean trim, String dateFormatString, String timestampFormatString)
                throws SQLException, IOException {

            String value = "";

            switch (colType) {
                case Types.BIT:
                case Types.JAVA_OBJECT:
                    value = Objects.toString(rs.getObject(colIndex), "");
                    break;
                case Types.BOOLEAN:
                    value = Objects.toString(rs.getBoolean(colIndex));
                    break;
                case Types.NCLOB: // todo : use rs.getNClob
                case Types.CLOB:
                    Clob c = rs.getClob(colIndex);
                    value = readClob(c);
                    break;
                case Types.BIGINT:
                    value = Objects.toString(rs.getLong(colIndex));
                    break;
                case Types.DECIMAL:
                case Types.REAL:
                case Types.NUMERIC:
                    value = Objects.toString(rs.getBigDecimal(colIndex), "");
                    break;
                case Types.DOUBLE:
                    value = Objects.toString(rs.getDouble(colIndex));
                    break;
                case Types.FLOAT:
                    value = Objects.toString(rs.getFloat(colIndex));
                    break;
                case Types.INTEGER:
                case Types.TINYINT:
                case Types.SMALLINT:
                    value = Objects.toString(rs.getInt(colIndex));
                    break;
                case Types.DATE:
                    Date date = rs.getDate(colIndex);
                    if (date != null) {
                        SimpleDateFormat df = new SimpleDateFormat(dateFormatString);
                        value = df.format(date);
                    }
                    break;
                case Types.TIME:
                    value = Objects.toString(rs.getTime(colIndex), "");
                    break;
                case Types.TIMESTAMP:
                    value = handleTimestamp(rs.getTimestamp(colIndex), timestampFormatString);
                    break;
                case Types.NVARCHAR: //TODO:
                case Types.NCHAR: //TODO:
                case Types.LONGNVARCHAR: //TODO:
                case Types.LONGVARCHAR:
                case Types.VARCHAR:
                case Types.CHAR:
                    String columnValue = rs.getString(colIndex);
                    if (trim && columnValue != null) {
                        value = columnValue.trim();
                    } else {
                        value = columnValue;
                    }
                    break;
                default:
                    value = "";
            }

            if (rs.wasNull() || value == null) {
                value = "";
            }

            return value;
        }

    public static String readClob(Clob clob) throws SQLException, IOException {
        StringBuffer sb = new StringBuffer((int) clob.length());
        Reader r = clob.getCharacterStream();
        char[] cbuf = new char[2048];
        int n;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            sb.append(cbuf, 0, n);
        }
        return sb.toString();
    }
}
