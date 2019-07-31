package org.commons.database;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.commons.util.PropertiesCache;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;

public class Neo4jConnectionManager {

    private static Session session;
    private static Driver driver;
    private static PropertiesCache propertiesCache;

    static {
        propertiesCache = PropertiesCache.getInstance();
        ProjectLogger.log("Creating connection with the Neo4j Database", LoggerEnum.DEBUG.name());
        // Checking if the Neo4j Url is provided
        String url = null;
        if(propertiesCache.getProperty("neo4j_url")!=null) {
            try {
                try {
                    // Trying to create connection with an open Neo4j database
                    url = propertiesCache.getProperty("neo4j_url");
                    driver = GraphDatabase.driver(url);
                    ProjectLogger.log("Connection created with the Neo4j Database successfully", LoggerEnum.INFO.name());
                } catch (AuthenticationException e) {
                        driver = GraphDatabase.driver(
                                url, AuthTokens.basic(propertiesCache.getProperty("neo4j_username"), propertiesCache.getProperty("neo4j_password")));
                        ProjectLogger.log("Connection created with the Neo4j Database successfully", LoggerEnum.INFO.name());

                }
            } catch (AuthenticationException e) {
                ProjectLogger.log("Unable to connect to Neo4j Database due to Authentication Failure",e, LoggerEnum.FATAL.name());
                throw new ProjectCommonException(ResponseCode.databaseAuthenticationError);
            } catch (Exception e) {
                ProjectLogger.log("Unable to connect to Neo4j Database",e, LoggerEnum.FATAL.name());
                throw new ProjectCommonException(ResponseCode.databaseConnectionError);
            }
        }
        else
        {
            throw new ProjectCommonException(ResponseCode.databaseUrlMissingError);
        }
    }

    public static Session getSession() {

        try {
            session = driver.session();
            if(session ==null || !session.isOpen())
            {
                ProjectLogger.log("Error, session could not be instantiated in neo4j", LoggerEnum.ERROR.name());
                throw new ProjectCommonException(ResponseCode.databaseSessionCreationError);
            }
        }
        catch (ProjectCommonException e)
        {
            throw e;
        }
        catch (Exception | Error e)
        {
            ProjectLogger.log("Error in database while creating session", LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.databaseConnectionError);
        }
        return session;
    }





    public static boolean checkDatabaseConnectionStatus() {

        // To check if the database is connected or not
        Session session = driver.session();
        boolean check = false;
        StatementResult result = session.run("RETURN \"check\" ;");
        if(result.hasNext())
            check = true;

        session.close();
        return check;
    }
}
