package org.dataexporter.dao.impl;


import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
import org.dataexporter.dao.DataExportDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.*;

public class DataExportDaoImpl implements DataExportDao {

    private Session session;
    private List<String> header;
    private StatementResult result;
    private List<List<String>> dataList;

    public DataExportDaoImpl(){

        try {
            session = Neo4jConnectionManager.getSession();
            if(session ==null || !session.isOpen())
            {
                throw new ProjectCommonException(400,"Fail to connect to Database","Unable to create a session with the Neo4j Driver");
            }
        }
        catch (ProjectCommonException e)
        {
            throw e;
        }
        catch (Exception | Error e)
        {
            throw new ProjectCommonException(400,"Internal Server Error","Unable to get database connection. "+e);
        }
    }

    @Override
    public Response createNode(Map<String,Object> nodeData) {

        session = Neo4jConnectionManager.getSession();
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
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();
                    ProjectLogger.log("Node already exists : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                    response.addErrorData("Data Already Exists",dataCount+1);
                }
                else {

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
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }


    @Override
    public Response updateNode(Map<String,Object> nodeData) throws Exception{

        session = Neo4jConnectionManager.getSession();
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
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if(nodeCount > 1)
                    {
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
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }



    @Override
    public Response deleteNode(Map<String,Object> nodeData) throws Exception {

        session = Neo4jConnectionManager.getSession();
        return null;
    }


        @Override
    public Response createNodeRelation(Map<String,Object> relationData) throws Exception
    {

        session = Neo4jConnectionManager.getSession();
        Response response= new Response();
        response.setOperation("Create Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"})-[");
                query.append("r:`").append(relationDetailEach.get(4).trim()).append("`");
                query.append("]->(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"})");
                query.append(" CREATE (a)-[r:`").append(relationDetailEach.get(2).trim()).append("` {");
                ProjectLogger.log("Query generated to check if Node Relation already exists : " + query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if (result.hasNext()) {
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();
                    ProjectLogger.log("Relation already exists : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                    response.addErrorData("Data Already Exists",dataCount+1);
                } else {
                    query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"}),");
                    query.append("(b:`").append(relationDetailEach.get(3).trim()).append("` {`").append(header.get(4).trim()).append("`: \"").append(relationDetailEach.get(4).trim()).append("\"})");
                    query.append(" CREATE (a)-[r:`").append(relationDetailEach.get(2).trim()).append("` {");
                    boolean check = false;
                    for (int i = 5; i < header.size(); i++) {
                        if (!relationDetailEach.get(i).isEmpty()) {
                            query.append("`").append(header.get(i).trim()).append("`");
                            query.append(" : \"").append(relationDetailEach.get(i).trim()).append("\"");
                            query.append(",");
                            check = true;
                        }
                    }
                    int lastCommaIndex = query.toString().lastIndexOf(',');
                    if (lastCommaIndex > 0 && check) {
                        query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
                    }
                    query.append("}]->(b) RETURN r");

                    ProjectLogger.log("Query generated to Create Node Relation : " + query, LoggerEnum.INFO.name());
                    result = session.run(query.toString());
                    if (result.hasNext()) {
                        Record record = result.next();
                        RelationshipValue relation = (RelationshipValue) record.get(0);
                        ProjectLogger.log("Node Relation created successfully : "+relation.asMap(), LoggerEnum.INFO.name());
                    }

                    response.addSuccess();
                }
            }
            catch (Error |Exception e) {
                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data",dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;

    }

    @Override
    public Response updateNodeRelation(Map<String,Object> relationData) throws Exception{


        session = Neo4jConnectionManager.getSession();
        Response response= new Response();
        response.setOperation("Update Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");
        Record record;

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"})");
                query.append("-[r: `").append(relationDetailEach.get(4).trim()).append("`]-");
                query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"})");
                query.append(" RETURN r");
                ProjectLogger.log("Query generated to check Node Relation Existence : " + query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if(result.hasNext()) {
                    List<Record> recordList = result.list();
                    int relationCount = recordList.size();
                    if(relationCount > 1)
                    {
                        ProjectLogger.log("Duplicate Data Present : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present",dataCount+1);
                    }
                    else {

                        boolean check = false;
                        query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"})");
                        query.append("-[r: `").append(relationDetailEach.get(4).trim()).append("`]-");
                        query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(4).trim()).append("`: \"").append(relationDetailEach.get(4).trim()).append("\"})");
                        query.append(" SET ");
                        for (int i = 5; i < header.size(); i++) {
                            if (!relationDetailEach.get(i).isEmpty()) {
                                query.append("r.`").append(header.get(i).trim());
                                query.append("` = \"").append(relationDetailEach.get(i).trim()).append("\"");
                                query.append(",");
                                check = true;
                            }
                        }
                        int lastCommaIndex = query.toString().lastIndexOf(',');
                        if (lastCommaIndex > 0 && check) {
                            query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
                        }
                        query.append(" RETURN r");

                        ProjectLogger.log("Query generated to Update Node Relation : "+query, LoggerEnum.INFO.name());
                        result = session.run(query.toString());
                        if (result.hasNext()) {
                            record = result.next();
                            RelationshipValue relation = (RelationshipValue) record.get(0);
                            ProjectLogger.log("Node Relation updated successfully : "+relation.asMap(), LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }
                }
                else {
                    ProjectLogger.log("No Such Data Present", LoggerEnum.WARN.name());
                    response.addErrorData("No Such Data Present",dataCount+1);
                }

            }
            catch (Error |Exception e) {
                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data",dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }

    @Override
    public Response deleteNodeRelation(Map<String,Object> relationData) throws Exception {

        session = Neo4jConnectionManager.getSession();
        return null;
    }



}