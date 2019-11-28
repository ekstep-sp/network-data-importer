package org.commons.database;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.commons.util.PropertiesCache;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;

import java.util.Map;

public class Neo4jConnectionManager {

    private static Session session;
    private static Driver driver;
    private static Transaction transaction;
    private static PropertiesCache propertiesCache;

    static {

        Config config = Config.builder()
                .withTrustStrategy( Config.TrustStrategy.trustAllCertificates())
                .withoutEncryption()
                .build();
        propertiesCache = PropertiesCache.getInstance();
        ProjectLogger.log("Creating connection with the Neo4j Database", LoggerEnum.DEBUG.name());
        // Checking if the Neo4j Url is provided
        String url = null;
        if(propertiesCache.getProperty("neo4j_url")!=null) {
            try {
                try {
                    // Trying to create connection with an open Neo4j database
                    url = propertiesCache.getProperty("neo4j_url");
                    driver = GraphDatabase.driver(url,config);
                    ProjectLogger.log("Connection created with the Neo4j Database successfully", LoggerEnum.INFO.name());
                } catch (AuthenticationException e) {
                        driver = GraphDatabase.driver(
                                url, AuthTokens.basic(propertiesCache.getProperty("neo4j_username"), propertiesCache.getProperty("neo4j_password")),config);
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


    public static void getSession() throws ProjectCommonException {

            session = driver.session();
            if(session ==null || !session.isOpen()) {
                ProjectLogger.log("Error, session could not be instantiated in neo4j", LoggerEnum.ERROR.name());
                throw new ProjectCommonException(ResponseCode.databaseSessionCreationError);
            }
    }

    public static Transaction getSessionTransaction() {

        if(session == null || !session.isOpen()) {
            getSession();
        }
        if(transaction == null || !transaction.isOpen()) {
            transaction = session.beginTransaction();
        }

            return transaction;
    }




    public static boolean checkDatabaseConnectionStatus() {

        // To check if the database is connected or not
        Transaction transaction = getSessionTransaction();
        boolean check = false;
        StatementResult result = transaction.run("RETURN true ;");
        if(result.hasNext()) {
            Record record = result.next();
            Map<String, Object> data = record.asMap();
            Object flag = data.get("true");
            if(flag instanceof Boolean && (boolean)flag)
            check = true;
        }
        session.close();
        return check;
    }
}
