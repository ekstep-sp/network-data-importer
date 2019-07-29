package org.commons.database;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Neo4jConnectionManager {

    private static Session session;
    private static Driver driver;

    static {
        Map<String,String> connectionData = getConnectionData();
        ProjectLogger.log("Creating connection with the Neo4j Database", LoggerEnum.DEBUG.name());
        if(connectionData.get("url")!=null) {
            try {
                try {
                    driver = GraphDatabase.driver(
                            connectionData.get("url"));
                    ProjectLogger.log("Connection created with the Neo4j Database successfully", LoggerEnum.INFO.name());
                } catch (AuthenticationException e) {
                    if(connectionData.get("username")!=null && connectionData.get("password")!=null) {
                        driver = GraphDatabase.driver(
                                connectionData.get("url"), AuthTokens.basic(connectionData.get("username"), connectionData.get("password")));
                        ProjectLogger.log("Connection created with the Neo4j Database successfully", LoggerEnum.INFO.name());
                    }
                else {
                        throw new ProjectCommonException(ResponseCode.databaseCredentialsMissingError);
                    }
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
        if(session == null || !session.isOpen()) {
            session = driver.session();
        }
        return session;
    }



    private static Map<String,String> getConnectionData() {

        Map<String,String> connectionDetails = new HashMap<>();
        Properties properties = new Properties();
        try {
            InputStream inputStream = Neo4jConnectionManager.class.getClass().getResourceAsStream("/database/neo4jdb.properties");
            properties.load(inputStream);

            byte[] defaultData = new byte[inputStream.available()];
            inputStream.read(defaultData);
//            System.out.println( new String(defaultData, StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            ProjectLogger.log("Unable to read neo4jdb.properties file",e, LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.fileMissingError,"neo4jdb.properties");
        }

        String url = System.getenv("neo4j_base_url");
        if(url==null || url.isEmpty())
            connectionDetails.put("url",properties.getProperty("neo4j_base_url"));
        else
            connectionDetails.put("url",url);

        String username = System.getenv("neo4j_username");
        if(url==null || url.isEmpty())
            connectionDetails.put("username",properties.getProperty("neo4j_username"));
        else
            connectionDetails.put("username",username);

        String password = System.getenv("neo4j_password");
        if(url==null || url.isEmpty())
            connectionDetails.put("password",properties.getProperty("neo4j_password"));
        else
            connectionDetails.put("password",password);

        return connectionDetails;
    }

    public static boolean checkDatabaseConnectionStatus() {

        Session session = driver.session();
        boolean check = false;
        StatementResult result = session.run("RETURN \"check\" ;");
        if(result.hasNext())
            check = true;

        session.close();
        return check;
    }
}
