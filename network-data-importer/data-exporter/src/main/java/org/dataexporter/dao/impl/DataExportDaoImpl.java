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
            if(session ==null)
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
    public Response createNode(String nodeLabel, Map<String,Object> nodeData) {

        Response response= new Response();
        response.setOperation("Create Node");
        int dataCount=0;
        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("CREATE (a:`" + nodeLabel + "` { ");

                boolean check = false;
                for (int i = 0; i < header.size(); i++) {
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

                ProjectLogger.log("Query generated to Create Node : "+query, LoggerEnum.INFO.name());
                result = session.run(query.toString());
                if (result.hasNext()) {
                    Record record = result.next();
                    NodeValue node = (NodeValue) record.get(0);
                    ProjectLogger.log("Node created Successfully", LoggerEnum.INFO.name());
                }

                response.addSuccess();
            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }


    @Override
    public Response updateNode(String nodeLabel, Map<String,Object> nodeData) throws Exception{

        Response response= new Response();
        response.setOperation("Update Node");
        int dataCount=0;

        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        Record record;
        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeLabel + "` { `");
                query.append(header.get(0).trim()).append("`: \"").append(nodeDetailsEach.get(0).trim()).append("\"");
                query.append("}) RETURN a");
                ProjectLogger.log("Query generated to Create Node : "+query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if(result.hasNext()) {
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if(nodeCount > 1)
                    {
                        response.addDuplicateData(dataCount+1);
                    }

                    else {
                        boolean check = false;
                        query = new StringBuilder("MATCH (a:`" + nodeLabel + "` { `");
                        query.append(header.get(0).trim()).append("`: \"").append(nodeDetailsEach.get(0).trim()).append("\"");
                        query.append("}) SET ");
                        for (int i = 1; i < header.size(); i++) {
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

                        System.out.println(query);
                        result = session.run(query.toString());
                        if (result.hasNext()) {
                            record = result.next();
                            NodeValue node = (NodeValue) record.get(0);
                            ProjectLogger.log("Node updated successfully", LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }
                }
                else
                {
                    response.addNoSuchData(dataCount+1);
                }

            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }


    @Override
    public Response createNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception {

        Response response= new Response();
        response.setOperation("Create Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeSourceLabel + "` {`" + header.get(0).trim() + "`: \"" + relationDetailEach.get(0).trim() + "\"}),");
                query.append("(b:`").append(nodeTargetLabel).append("` {`").append(header.get(1).trim()).append("`: \"").append(relationDetailEach.get(1).trim()).append("\"})");
                query.append(" CREATE (a)-[r:`").append(relationDetailEach.get(2).trim()).append("` {");
                boolean check = false;
                for (int i = 3; i < header.size(); i++) {
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

                ProjectLogger.log("Query generated to Create Node Relation : "+query, LoggerEnum.INFO.name());
                result = session.run(query.toString());
                if (result.hasNext()) {
                    Record record = result.next();
                    RelationshipValue relation = (RelationshipValue) record.get(0);
                    ProjectLogger.log("Node Relation created successfully", LoggerEnum.INFO.name());
                }

                response.addSuccess();
            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;

    }

    @Override
    public Response updateNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception{


        Response response= new Response();
        response.setOperation("Update Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeSourceLabel + "` {`" + header.get(0).trim() + "`: \"" + relationDetailEach.get(0).trim() + "\"})");
                query.append("-[r: `").append(relationDetailEach.get(2).trim()).append("`]-");
                query.append("(b:`").append(nodeTargetLabel).append("` {`").append(header.get(1).trim()).append("`: \"").append(relationDetailEach.get(1).trim()).append("\"})");
                query.append(" RETURN r");
                System.out.println(query);

                result = session.run(query.toString());
                if(result.hasNext()) {
                    Record record = result.next();
                    int nodeCount = record.size();
                    if(nodeCount > 1)
                    {
                        response.addDuplicateData(dataCount+1);
                    }
                    else {

                        boolean check = false;
                        query = new StringBuilder("MATCH (a:`" + nodeSourceLabel + "` {`" + header.get(0).trim() + "`: \"" + relationDetailEach.get(0).trim() + "\"})");
                        query.append("-[r: `").append(relationDetailEach.get(2).trim()).append("`]-");
                        query.append("(b:`").append(nodeTargetLabel).append("` {`").append(header.get(1).trim()).append("`: \"").append(relationDetailEach.get(1).trim()).append("\"})");
                        query.append(" SET ");
                        for (int i = 3; i < header.size(); i++) {
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
                            ProjectLogger.log("Node Relation updated successfully", LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }
                }
                else {
                    response.addNoSuchData(dataCount+1);
                }

            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
        session.close();
        return response;
    }

}