package com.dbtransactions.utils;

import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/*
 *  Retain for future use.. all application wide properties
 */
@Log
public class ReadApplicationProperties {

    private static final ReadApplicationProperties INSTANCE = new ReadApplicationProperties();

    Properties appProperties = null;
    private static String workingDirectory = "";

    public static ReadApplicationProperties getSingletonInstance(){
            return INSTANCE;
    }

    public String getPropertyValue(String propertyKey){
        return (String) appProperties.get(propertyKey);
    }

    private void readAppProperties(String dbIdentifier){
        dbIdentifier = "application"; //defaulting for now.. to be removed later
        try {
            appProperties = readPropertiesFile( new File(".").getCanonicalPath()+"/resources/"+ dbIdentifier+".properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private  Properties readPropertiesFile(String fileName) {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            log.severe("Cannot find file: " + fileName + " cwd: " + new File(".").getAbsolutePath());
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return prop;
    }
}
