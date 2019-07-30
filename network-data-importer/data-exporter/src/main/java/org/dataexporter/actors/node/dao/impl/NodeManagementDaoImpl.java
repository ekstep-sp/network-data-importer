package org.dataexporter.actors.node.dao.impl;


import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.dataexporter.actors.node.dao.NodeManagementDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.*;

public class NodeManagementDaoImpl implements NodeManagementDao {

    private Session session;
    private List<String> header;
    private StatementResult result;
    private List<List<String>> dataList;

    public NodeManagementDaoImpl(){

        try {
            session = Neo4jConnectionManager.getSession();
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
    }

    @Override
    public Response createNode(Map<String,Object> nodeData) {

        // To create a Node
        Response response= new Response();
        response.setOperation("Create Node");
        int dataCount=0;
        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            if(nodeDetailsEach.get(0).trim().isEmpty())
            {
                continue;
            }
            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeDetailsEach.get(0).trim() + "` { `");
                query.append(header.get(1).trim()).append("`: \"").append(nodeDetailsEach.get(1).trim()).append("\"");
                query.append("}) RETURN a");
                ProjectLogger.log("Query generated to check if Node already exists : " + query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if (result.hasNext()) {
                    // Check if Node already Exists
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();
                    ProjectLogger.log("Node already exists : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                    response.addErrorData("Data Already Exists",dataCount+1);
                }
                else {

                    //Creating a Node
                    query = new StringBuilder("CREATE (a:`" + nodeDetailsEach.get(0).trim() + "` { ");

                    boolean check = false;
                    for (int i = 1; i < header.size(); i++) {
                        if (!nodeDetailsEach.get(i).isEmpty()) {
                            query.append("`").append(header.get(i).trim()).append("`");
                            query.append(" : \"").append(nodeDetailsEach.get(i).trim()).append("\"");
                            query.append(",");
                            check = true;
                        }
                    }
                    int lastCommaIndex = query.toString().lastIndexOf(',');
                    if (lastCommaIndex > 0 && check) {
                        query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
                    }
                    query.append("}) RETURN a");

                    ProjectLogger.log("Query generated to Create Node : " + query, LoggerEnum.INFO.name());
                    result = session.run(query.toString());
                    if (result.hasNext()) {
                        Record record = result.next();
                        NodeValue node = (NodeValue) record.get(0);
                        ProjectLogger.log("Node created Successfully : "+node.asMap(), LoggerEnum.INFO.name());
                    }

                    response.addSuccess();
                }
            }
            catch (Error | Exception e) {
                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data",dataCount+1);
            }
        }
        session.close();
        return response;
    }


    @Override
    public Response updateNode(Map<String,Object> nodeData) throws Exception{

        // Update a Node
        Response response= new Response();
        response.setOperation("Update Node");
        int dataCount=0;

        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        Record record;
        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeDetailsEach.get(0).trim() + "` { `");
                query.append(header.get(1).trim()).append("`: \"").append(nodeDetailsEach.get(1).trim()).append("\"");
                query.append("}) RETURN a");
                ProjectLogger.log("Query generated to Create Node : "+query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if(result.hasNext()) {
                    // Check if the Node Exists
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if(nodeCount > 1)
                    {
                        // update the Node
                        ProjectLogger.log("Duplicate Data Present : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present",dataCount+1);                    }

                    else {
                        boolean check = false;
                        query = new StringBuilder("MATCH (a:`" + nodeDetailsEach.get(0).trim() + "` { `");
                        query.append(header.get(1).trim()).append("`: \"").append(nodeDetailsEach.get(1).trim()).append("\"");
                        query.append("}) SET ");
                        for (int i = 2; i < header.size(); i++) {
                            if (!nodeDetailsEach.get(i).isEmpty()) {
                                query.append("a.`").append(header.get(i).trim());
                                query.append("`=\"").append(nodeDetailsEach.get(i).trim()).append("\"");
                                query.append(",");
                                check = true;
                            }
                        }
                        int lastCommaIndex = query.toString().lastIndexOf(',');
                        if (lastCommaIndex > 0 && check) {
                            query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
                        }
                        query.append(" RETURN a");

                        ProjectLogger.log("Query generated to Update Node : "+query, LoggerEnum.INFO.name());
                        result = session.run(query.toString());
                        if (result.hasNext()) {
                            record = result.next();
                            NodeValue node = (NodeValue) record.get(0);
                            ProjectLogger.log("Node updated successfully : "+node.asMap(), LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }
                }
                else
                {
                    ProjectLogger.log("No Such Data Present", LoggerEnum.WARN.name());
                    response.addErrorData("No Such Data Present",dataCount+1);
                }

            }
            catch (Error |Exception e) {
                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data",dataCount+1);
            }
        }
        session.close();
        return response;
    }



    @Override
    public Response deleteNode(Map<String,Object> nodeData) throws Exception {

        return null;
    }


}