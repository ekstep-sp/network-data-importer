package org.dataexporter.dao.impl;


import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
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
                throw new ProjectCommonException(400,"Fail to connect to Database","Unable to establish connection with the database");
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

                String query = "CREATE (a:`" + nodeLabel + "` { ";

                boolean check = false;
                for (int i = 0; i < header.size(); i++) {
                    if (!nodeDetailsEach.get(i).isEmpty()) {
                        query += "`" + header.get(i).trim() + "`";
                        query += " : \"" + nodeDetailsEach.get(i).trim() + "\"";
                        query += ",";
                        check = true;
                    }
                }
                int lastCommaIndex = query.lastIndexOf(',');
                if (lastCommaIndex > 0 && check) {
                    query = query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1);
                }
                query += "}) RETURN a";

                System.out.println(query);
                result = session.run(query);
                if (result.hasNext()) {
                    Record record = result.next();
                    NodeValue node = (NodeValue) record.get(0);
                    System.out.println("CREATED NODE WITH DETAILS : " + node.asMap());
                }

                response.addSuccess();
            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
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

                String query = "MATCH (a:`" + nodeLabel + "` { `";
                query += header.get(0).trim() + "`: \"" + nodeDetailsEach.get(0).trim() + "\"";
                query += "}) RETURN a";
                System.out.println(query);

                result = session.run(query);
                if(result.hasNext()) {
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if(nodeCount > 1)
                    {
                        response.addDuplicateData(dataCount+1);
                    }

                    else {
                        boolean check = false;
                        query = "MATCH (a:`" + nodeLabel + "` { `";
                        query += header.get(0).trim() + "`: \"" + nodeDetailsEach.get(0).trim() + "\"";
                        query += "}) SET ";
                        for (int i = 1; i < header.size(); i++) {
                            if (!nodeDetailsEach.get(i).isEmpty()) {
                                query += "a.`" + header.get(i).trim();
                                query += "`=\"" + nodeDetailsEach.get(i).trim() + "\"";
                                query += ",";
                                check = true;
                            }
                        }
                        int lastCommaIndex = query.lastIndexOf(',');
                        if (lastCommaIndex > 0 && check) {
                            query = query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1);
                        }
                        query += " RETURN a";

                        System.out.println(query);
                        result = session.run(query);
                        if (result.hasNext()) {
                            record = result.next();
                            NodeValue node = (NodeValue) record.get(0);
                            System.out.println("UPDATED NODE WITH DETAILS : " + node.asMap());
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

                String query = "MATCH (a:`"+nodeSourceLabel+"` {`"+header.get(0).trim()+"`: \""+relationDetailEach.get(0).trim()+"\"}),";
                query+="(b:`"+nodeTargetLabel+"` {`"+header.get(1).trim()+"`: \""+relationDetailEach.get(1).trim()+"\"})";
                query+=" CREATE (a)-[r:`"+relationDetailEach.get(2).trim()+"` {";
                boolean check = false;
                for (int i = 3; i < header.size(); i++) {
                    if (!relationDetailEach.get(i).isEmpty()) {
                        query += "`" + header.get(i).trim() + "`";
                        query += " : \"" + relationDetailEach.get(i).trim() + "\"";
                        query += ",";
                        check = true;
                    }
                }
                int lastCommaIndex = query.lastIndexOf(',');
                if (lastCommaIndex > 0 && check) {
                    query = query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1);
                }
                query += "}]->(b) RETURN r";

                System.out.println(query);
                result = session.run(query);
                if (result.hasNext()) {
                    Record record = result.next();
                    RelationshipValue relation = (RelationshipValue) record.get(0);
                    System.out.println("CREATED RELATION WITH DETAILS : " + relation.asMap());
                }

                response.addSuccess();
            }
            catch (Exception e) {
                response.addError(dataCount+1);
                e.printStackTrace();
            }
        }
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

                String query = "MATCH (a:`"+nodeSourceLabel+"` {`"+header.get(0).trim()+"`: \""+relationDetailEach.get(0).trim()+"\"})";
                query += "-[r: `" + relationDetailEach.get(2).trim() + "`]-";
                query+="(b:`"+nodeTargetLabel+"` {`"+header.get(1).trim()+"`: \""+relationDetailEach.get(1).trim()+"\"})";
                query += " RETURN r";
                System.out.println(query);

                result = session.run(query);
                if(result.hasNext()) {
                    Record record = result.next();
                    int nodeCount = record.size();
                    if(nodeCount > 1)
                    {
                        response.addDuplicateData(dataCount+1);
                    }
                    else {

                        boolean check = false;
                        query = "MATCH (a:`"+nodeSourceLabel+"` {`"+header.get(0).trim()+"`: \""+relationDetailEach.get(0).trim()+"\"})";
                        query += "-[r: `" + relationDetailEach.get(2).trim() + "`]-";
                        query+="(b:`"+nodeTargetLabel+"` {`"+header.get(1).trim()+"`: \""+relationDetailEach.get(1).trim()+"\"})";
                        query += " SET ";
                        for (int i = 3; i < header.size(); i++) {
                            if (!relationDetailEach.get(i).isEmpty()) {
                                query += "r.`" + header.get(i).trim();
                                query += "` = \"" + relationDetailEach.get(i).trim() + "\"";
                                query += ",";
                                check = true;
                            }
                        }
                        int lastCommaIndex = query.lastIndexOf(',');
                        if (lastCommaIndex > 0 && check) {
                            query = query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1);
                        }
                        query += " RETURN r";

                        System.out.println(query);
                        result = session.run(query);
                        if (result.hasNext()) {
                            record = result.next();
                            RelationshipValue relation = (RelationshipValue) record.get(0);
                            System.out.println("UPDATED RELATION WITH DETAILS : " + relation.asMap());
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
        return response;
    }

}