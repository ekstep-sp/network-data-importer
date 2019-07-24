package org.commons.logger;

import org.apache.log4j.Logger;

public class ProjectLogger {

    final static Logger logger;

    static {
        logger = Logger.getLogger(ProjectLogger.class);
    }

    public static Logger getProjectLogger() {
        return logger;
    }

    public static void log(String message, String logLevel) {


        switch(logLevel) {
            case "INFO":
                logger.info(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "DEBUG":
                logger.debug(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "FATAL":
                logger.fatal(message);
                break;
                default:
                logger.debug(message);
        }
    }

    public static void log(String message,Throwable t, String logLevel) {


        switch(logLevel) {
            case "INFO":
                logger.info(message,t);
                break;
            case "WARN":
                logger.warn(message,t);
                break;
            case "DEBUG":
                logger.debug(message,t);
                break;
            case "ERROR":
                logger.error(message,t);
                break;
            case "FATAL":
                logger.fatal(message);
                break;
            default:
                logger.debug(message,t);
        }
    }

}
