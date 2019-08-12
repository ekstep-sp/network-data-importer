package org.dataexporter.actors.dataextractor.dao.impl;

import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;
import org.dataexporter.actors.dataextractor.dao.DataExtractorManagementDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.NullValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import java.util.*;

public class DataExtractorManagementDaoImpl implements DataExtractorManagementDao {

    private List<List<String>> header;
    private List<List<List<String>>> data;
    private List<List<String>> dataEach;


    public DataExtractorManagementDaoImpl() {

        header = new ArrayList<>();
        data = new ArrayList<>();
    }


    @Override
    public Response getAllData() {

        // Create Node Relationship
        Response response= new Response();
        response.setOperation("Read All Data");
        header.add(new ArrayList<>());
        header.add(new ArrayList<>());
        header.add(new ArrayList<>());
        header.get(0).add("Source Label");
        header.get(1).add("Target Label");
        header.get(2).add("Relationship Label");

        Session session = Neo4jConnectionManager.getSession();

            try {
//                StringBuilder query = new StringBuilder("MATCH (a)-[r]->(b) WHERE a."+ Constants.FLAG +"=false AND b."+ Constants.FLAG +"=false AND r."+ Constants.FLAG +"=false");
                StringBuilder query = new StringBuilder("MATCH (a)-[r]->(b)");
                query.append(" RETURN a,b,r");
                ProjectLogger.log("Query generated to get all Nodes having Relationships : " + query, LoggerEnum.INFO.name());

                StatementResult resultWithRelation = session.run(query.toString());

//                query = new StringBuilder("MATCH (c) WHERE NOT (c)-[]-() AND c."+ Constants.FLAG +"=false");
                query = new StringBuilder("MATCH (c) WHERE NOT (c)-[]-()");

                query.append(" RETURN c");
                ProjectLogger.log("Query generated to get all Nodes having No Relationships : " + query, LoggerEnum.INFO.name());

                StatementResult resultWithoutRelation = session.run(query.toString());
                if (!(resultWithRelation.hasNext() || resultWithoutRelation.hasNext())) {
                    ProjectLogger.log("No Data found in the Database", LoggerEnum.INFO.name());
                    response.addSuccessData("Success", "No Data Found");
                    return response;
                } else {
                    try {
                    if (resultWithRelation.hasNext()) {
                        List<Record> recordList = resultWithRelation.list();
//                    System.out.println(recordList);
                        for (Record record : recordList) {
                            dataEach = new ArrayList<>();
                            dataEach.add(new ArrayList<>());
                            dataEach.add(new ArrayList<>());
                            dataEach.add(new ArrayList<>());

                            for (int i = 0; i < record.size(); i++) {
                                if (!(record.get(i) instanceof NullValue) && record.get(i) instanceof NodeValue) {
                                    Node node = record.get(i).asNode();
                                    addNodeLabel(node, i);
                                    addNode(node, i);
                                } else if (!(record.get(i) instanceof NullValue) && record.get(i) instanceof RelationshipValue) {
                                    Relationship relationship = record.get(i).asRelationship();
                                    addRelationshipKey(relationship, i);
                                    addRelationship(relationship, i);
                                }
                            }

                            data.add(dataEach);
                            response.addSuccess();
                        }
                    }

                    if (resultWithoutRelation.hasNext()) {
                        List<Record> recordList = resultWithoutRelation.list();
//                    System.out.println(recordList);
                        for (Record record : recordList) {
                            dataEach = new ArrayList<>();
                            List<String> eachData = new ArrayList<>();
                            dataEach.add(eachData);
                            if (!(record.get(0) instanceof NullValue)) {
                                Node node = record.get(0).asNode();
                                addNodeLabel(node, 0);
                                addNode(node, 0);
                            }
                            data.add(dataEach);
                            response.addSuccess();
                        }
                    }
                }
                    catch (Exception | Error e)
                    {
                        ProjectLogger.log("Error while processing data", e, LoggerEnum.ERROR.name());
                        throw new ProjectCommonException(ResponseCode.dataProcessingError);
                    }
            }
            }
            catch (Error |Exception e) {
                ProjectLogger.log("Error while retrieving data", e, LoggerEnum.ERROR.name());
                throw new ProjectCommonException(ResponseCode.dataRetrievingError);
            }
            formatEachDataLength();

            response.addSuccessData("Success","Successfully read data");
            response.addSuccessData("header",header);
            response.addSuccessData("data",data);
        session.close();

        return response;

    }


    private void ensureSize(List<String> list, int size) {
        // Prevent excessive copying while we're adding
        while (list.size() < size) {
            list.add(null);
        }
    }


    private void addNodeLabel(Node node, int position)
    {
        Iterator<String> iterator = node.labels().iterator();
        ensureSize(dataEach.get(position),header.get(position).size());
        while (iterator.hasNext()) {
            dataEach.get(position).set(0, iterator.next());
        }
    }


    private void addNode(Node node,int position)
    {
        Map<String, Object> nodeData = node.asMap();
        addProperties(nodeData,position);

    }



    private void addRelationshipKey(Relationship relationship,int position)
    {
        ensureSize(dataEach.get(position),header.get(position).size());
        dataEach.get(position).set(0, relationship.type());
    }


    private void addRelationship(Relationship relationship, int position)
    {
        Map<String, Object> relationshipData = relationship.asMap();
        addProperties(relationshipData,position);
    }

    private void addProperties(Map<String,Object> properties, int position)
    {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (header.get(position).contains(entry.getKey())) {
                ensureSize(dataEach.get(position),header.get(position).indexOf(entry.getKey())+1);
                dataEach.get(position).set(header.get(position).indexOf(entry.getKey()), entry.getValue().toString());
            } else {
                header.get(position).add(entry.getKey());
                ensureSize(dataEach.get(position),header.get(position).indexOf(entry.getKey())+1);
                dataEach.get(position).set(header.get(position).indexOf(entry.getKey()), entry.getValue().toString());
            }
        }
    }

    private void formatEachDataLength() {

        for(List<List<String>> dataEach : data)
        {
            for(int i=0 ; i<header.size() ;i++)
            {
                if(i<dataEach.size())
                ensureSize(dataEach.get(i),header.get(i).size());
            }
        }
    }
}
