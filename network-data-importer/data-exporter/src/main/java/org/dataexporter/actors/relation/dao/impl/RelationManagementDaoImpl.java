package org.dataexporter.actors.relation.dao.impl;


import org.commons.database.Neo4jConnectionManager;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
import org.commons.util.Constants;
import org.dataexporter.actors.relation.dao.RelationManagementDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.NullValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.*;

public class RelationManagementDaoImpl implements RelationManagementDao {

    private List<String> header;
    private StatementResult result;
    private List<List<String>> dataList;

    public RelationManagementDaoImpl() {

    }


    @Override
    public Response createNodeRelation(Map<String,Object> relationData)
    {

        // Create Node Relationship
        Response response= new Response();
        response.setOperation("Create Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");
        Session session = Neo4jConnectionManager.getSession();

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            if(relationDetailEach.get(0).trim().isEmpty())
            {
                continue;
            }

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("}),");
                query.append("(b:`" + relationDetailEach.get(2).trim() + "` {`" + header.get(3).trim() + "`: \"" + relationDetailEach.get(3).trim() + "\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("})");
                query.append("WITH a,b ");
                query.append("OPTIONAL MATCH (a)-[r:`");
                query.append(relationDetailEach.get(4).trim());
                query.append("` {").append(Constants.FLAG +":false").append("}");
                query.append("]-(b) RETURN a,b,r");
                ProjectLogger.log("Query generated to check if Node Relation already exists : " + query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                List<Record> recordList = result.list();
                Record record = null;
                if(recordList.size()==1)
                {
                    record = recordList.get(0);

                    if(record.get(0) instanceof NullValue || record.get(1) instanceof NullValue)
                    {
                        if(record.get(0) instanceof NullValue)
                        ProjectLogger.log("No such Node Exists : a", LoggerEnum.WARN.name());
                        if(record.get(1) instanceof NullValue)
                            ProjectLogger.log("No such Node Exists : b", LoggerEnum.WARN.name());
                        response.addErrorData("No such Node Exists",dataCount+1);
                    }
                    else if(!(record.get(2) instanceof NullValue))
                    {
                        ProjectLogger.log("Relation already exists : "+record.get(2).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Relationship Already Exists",dataCount+1);
                    }
                    else {
                        // Create Relationship Between Nodes
                        query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"}),");
                        query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"})");
                        query.append(" CREATE (a)-[r:`").append(relationDetailEach.get(4).trim()).append("` {");
                        for (int i = 5; i < header.size(); i++) {
                            if (!relationDetailEach.get(i).isEmpty()) {
                                query.append("`").append(header.get(i).trim()).append("`");
                                query.append(" : \"").append(relationDetailEach.get(i).trim()).append("\"");
                                query.append(",");
                            }
                        }
//                    int lastCommaIndex = query.toString().lastIndexOf(',');
//                    if (lastCommaIndex > 0 && check) {
//                        query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
//                    }
                        query.append(Constants.FLAG).append(": false");
                        query.append("}]->(b) RETURN r");

                        ProjectLogger.log("Query generated to Create Node Relation : " + query, LoggerEnum.INFO.name());
                        result = session.run(query.toString());
                        if (result.hasNext()) {
                            record = result.next();
                            RelationshipValue relation = (RelationshipValue) record.get(0);
                            ProjectLogger.log("Node Relation created successfully : "+relation.asMap(), LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }

                }
                else if(recordList.size()>1)
                {
                    ProjectLogger.log("Duplicate Node Exists", LoggerEnum.WARN.name());
                    response.addErrorData("Duplicate Node Exists",dataCount+1);
                }
                else
                {
                    ProjectLogger.log("No such Node Exists", LoggerEnum.WARN.name());
                    response.addErrorData("No such Node Exists",dataCount+1);
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
    public Response updateNodeRelation(Map<String,Object> relationData) {

        // To update Node Relationships
        Response response= new Response();
        response.setOperation("Update Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");
        Record record;
        Session session = Neo4jConnectionManager.getSession();

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("})");
                query.append("-[r: `").append(relationDetailEach.get(4).trim()).append("`").append("{").append(Constants.FLAG +":false").append("}").append("]-");
                query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("})");
                query.append(" RETURN r");
                ProjectLogger.log("Query generated to check Node Relation Existence : " + query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if(result.hasNext()) {
                    // Check if the relationship exists between Nodes
                    List<Record> recordList = result.list();
                    int relationCount = recordList.size();
                    if(relationCount > 1)
                    {
                        ProjectLogger.log("Duplicate Data Present : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present",dataCount+1);
                    }
                    else {
                        // Updating relationship between Node
                        boolean check = false;
                        query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"})");
                        query.append("-[r: `").append(relationDetailEach.get(4).trim()).append("`]-");
                        query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"})");
                        query.append(" SET ");
                        for (int i = 5; i < header.size(); i++) {
                            if (!relationDetailEach.get(i).isEmpty()) {
                                query.append("r.`").append(header.get(i).trim());
                                if(relationDetailEach.get(i).trim().toLowerCase().equals(Constants.DELETE_DATA))
                                    query.append("` = ").append("null");
                                else
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
            }
        }
        session.close();
        return response;
    }

    @Override
    public Response deleteNodeRelation(Map<String,Object> relationData) {

        // To update Node Relationships
        Response response= new Response();
        response.setOperation("Delete Node Relation");
        int dataCount=0;

        header = (List<String>) relationData.get("header");
        dataList = (List<List<String>>) relationData.get("data");
        Record record;
        Session session = Neo4jConnectionManager.getSession();

        for(List<String> relationDetailEach : dataList) {

            dataCount++;

            try {

                boolean check = false;
                StringBuilder query = new StringBuilder("MATCH (a:`" + relationDetailEach.get(0).trim() + "` {`" + header.get(1).trim() + "`: \"" + relationDetailEach.get(1).trim() + "\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("}),");
                query.append("-[r: `").append(relationDetailEach.get(4).trim()).append("` {");
                for (int i = 5; i < header.size(); i++) {
                    if (!relationDetailEach.get(i).isEmpty()) {
                        query.append("`").append(header.get(i).trim());
                        query.append("`:\"").append(relationDetailEach.get(i).trim()).append("\"");
                        query.append(",");
//                        check = true;
                    }
                }
                query.append(Constants.FLAG +":false");
//                int lastCommaIndex = query.toString().lastIndexOf(',');
//                if (lastCommaIndex > 0 && check) {
//                    query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
//                }
                query.append("}]-");
                query.append("(b:`").append(relationDetailEach.get(2).trim()).append("` {`").append(header.get(3).trim()).append("`: \"").append(relationDetailEach.get(3).trim()).append("\"");
                query.append(","+ Constants.FLAG +":false");
                query.append("})");
                result = session.run(query.toString()+" RETURN r");
                if(result.hasNext()) {
                    // Check if the relationship exists between Nodes
                    List<Record> recordList = result.list();
                    int relationCount = recordList.size();
                    if(relationCount > 1)
                    {
                        ProjectLogger.log("Duplicate Data Present : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present",dataCount+1);
                    }
                    else {
                        // Deleting relationship between Node

//                        query.append(" DELETE r");

                        ProjectLogger.log("Query generated to DELETE Node Relation : "+query+" SET r."+Constants.FLAG+"=true", LoggerEnum.INFO.name());
                        result = session.run(query.toString()+" SET r."+Constants.FLAG+"=true");
                        if (result.hasNext()) {
                            record = result.next();
                            ProjectLogger.log("Node Relation deleted successfully : " + record.asMap(), LoggerEnum.INFO.name());
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
            }
        }
        session.close();
        return response;
    }



}