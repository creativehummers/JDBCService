package com.dbtransactions.utils;

import java.io.File;

public class ApplicationConstants {

    static final String QueriesFile = "queries.txt";
    static final int MAXQUERIESINFILE = 20;
    static final String DefaultDBPoolName = "default";
    static final String FetchSQLQuery = "select sql_text from sql_cache where sql_name=?";
    static final String SQLSubFolderName = new File(".").getAbsolutePath().replace("." , "")+"transactions";

}
