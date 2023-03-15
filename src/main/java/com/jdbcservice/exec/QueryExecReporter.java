package com.jdbcservice.exec;

import lombok.extern.log4j.Log4j2;

/*
 *  Default it for now to Logger implementation and use that capability
 *  Allow for customization / replacement to a custom logging mechanism
 *  Log4J2 is good enough to customize the logging through log4j.xml/properties file..
 */
@Log4j2(topic = "QueryExecutionReporter")
public class QueryExecReporter {

    public void severe( String logText){
        log.error(logText);
    }

    public void info( String logText){
        log.info(logText); //log4jLoger.info(logText);
    }

    public void finest( String logText){
        log.trace(logText);
    }

}
