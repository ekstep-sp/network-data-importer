package org.commons.database;

import org.commons.exception.ProjectCommonException;
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
        if(connectionData.get("url")!=null) {
            try {
                try {
                    driver = GraphDatabase.driver(
                            connectionData.get("url"));
                } catch (AuthenticationException e) {
                    if(connectionData.get("username")!=null && connectionData.get("password")!=null)
                    driver = GraphDatabase.driver(
                            connectionData.get("url"), AuthTokens.basic(connectionData.get("username"), connectionData.get("password")));
                else {
                        throw new ProjectCommonException(400, "Missing Neo4j Database Credentials", "Unable to get Neo4j database username or password");
                    }
                }
            } catch (AuthenticationException e) {
                throw new ProjectCommonException(400, "Neo4j Connection Failed", "Unable to connect to Neo4j due to " + e.toString());
            } catch (Exception e) {
                throw new ProjectCommonException(400, "Internal Server Error", "Failed to establish connection with Neo4j due to " + e.toString());
            }
        }
        else
        {
            throw new ProjectCommonException(400,"Missing Neo4j Database Credentials","Unable to get Neo4j database connection url");
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
            throw new ProjectCommonException(400,"Internal Server Error","Unable to read the neo4jdb.properties file. "+e);
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
