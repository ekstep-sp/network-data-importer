package org.dataexporter.actors.node.dao.impl;


import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
import org.commons.util.Constants;
import org.dataexporter.actors.node.dao.NodeManagementDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.v1.*;

import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;

public class NodeManagementDaoImpl implements NodeManagementDao {

    private List<String> header;
    private StatementResult result;
    private List<List<String>> dataList;

    public NodeManagementDaoImpl() {
    }


    @Override
    public Response createNode(Map<String,Object> nodeData) throws Exception {

        // To create a Node
        Response response= new Response();
        response.setOperation("Create Node");
        int dataCount=0;
        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        Session session = Neo4jConnectionManager.getSession();

        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            if(nodeDetailsEach.get(0).trim().isEmpty())
            {
                continue;
            }
            try {

                StringBuilder query = new StringBuilder("MATCH (a:`"+ nodeDetailsEach.get(0).trim() + "` {`");

                query.append(header.get(1).trim()).append("`: \"").append(nodeDetailsEach.get(1).trim()).append("\"");
                query.append("}) RETURN a");
                ProjectLogger.log("Query generated to Check if Node already Exists : "+query, LoggerEnum.INFO.name());


                result = session.run(query.toString());
                if(result.hasNext()) {

                    Record record = result.next();
                    NodeValue node = (NodeValue) record.get(0);
                    ProjectLogger.log("Data Already Present : " + node.asMap(), LoggerEnum.WARN.name());
                    response.addErrorData("Data Already Present",dataCount+1);
                }
                else
                {
                    boolean check = false;
                    query = new StringBuilder("CREATE (a:`" + nodeDetailsEach.get(0).trim() + "` { ");
                    for (int i = 1; i < header.size(); i++) {
                        if (!nodeDetailsEach.get(i).isEmpty()) {
                            query.append("`").append(header.get(i).trim());
                            query.append("`:\"").append(nodeDetailsEach.get(i).trim()).append("\"");
                            query.append(",");
                            check = true;
                        }
                    }

//                    int lastCommaIndex = query.toString().lastIndexOf(',');
//                    if (lastCommaIndex > 0 && check) {
//                        query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
//                    }
                    query.append(Constants.FLAG).append(": false");
                    query.append("}) RETURN a");

                    ProjectLogger.log("Query generated to CREATE Node : "+query, LoggerEnum.INFO.name());

                    StringBuilder finalQuery = query;
//                    Transaction transaction = session.beginTransaction();
//                    try
//                    {
//                        transaction.run(finalQuery.toString());
//                        transaction.commitAsync();
//                    }
//                        result = session.writeTransaction(tx -> {
//                            StatementResult result = tx.run(finalQuery.toString());
//                            tx.success();
//                            tx.close();
//                            return result;
//                        });
//                ProjectLogger.log("Node created Successfully : "+result.single().get(0).asMap(), LoggerEnum.INFO.name());



                    result = session.run(query.toString());
                    if (result.hasNext()) {
                        Record record = result.next();
                        NodeValue node = (NodeValue) record.get(0);
                        ProjectLogger.log("Node created successfully : "+node.asMap(), LoggerEnum.INFO.name());
                    }

                }


//                    StringBuilder query = new StringBuilder("MERGE (a:`" + nodeDetailsEach.get(0).trim() + "` { ");
//
//                    boolean check = false;
//                    for (int i = 1; i < header.size(); i++) {
//                        if (!nodeDetailsEach.get(i).isEmpty()) {
//                            query.append("`").append(header.get(i).trim()).append("`");
//                            query.append(" : \"").append(nodeDetailsEach.get(i).trim()).append("\"");
//                            query.append(",");
//                            check = true;
//                        }
//                    }
//                    int lastCommaIndex = query.toString().lastIndexOf(',');
//                    if (lastCommaIndex > 0 && check) {
//                        query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
//                    }
//                    query.append("}) RETURN a");
//
//                    ProjectLogger.log("Query generated to Create Node : " + query, LoggerEnum.INFO.name());



//                    final String query2 = query.toString();

//                CompletionStage<List<Record>> runAsync = session.writeTransactionAsync(tx -> tx.runAsync(query2))
//                        .thenComposeAsync( cursor -> cursor.listAsync() );
//                CompletionStage<Record> newResult = runAsync.thenApplyAsync(list -> list.get(0));
//
//                Record record = newResult.toCompletableFuture().get();
//                    NodeValue node = (NodeValue) record.get(0);
//                    ProjectLogger.log("Node created Successfully : "+node.asMap(), LoggerEnum.INFO.name());


//                session.writeTransactionAsync(tx -> tx.runAsync(query2))
//                        .thenComposeAsync( cursor -> cursor.listAsync() )
//                        .whenComplete((list,error) -> {
//                            Record record = list.get(0);
//                            NodeValue node = (NodeValue) record.get(0);
//                            ProjectLogger.log("Node created Successfully : "+node.asMap(), LoggerEnum.INFO.name());
//                            response.addSuccess();
//                        });

//                StringBuilder finalQuery = query;
//                Map transactionResult = null;
//
//                result = session.writeTransaction(tx -> {
//                    StatementResult result = tx.run(finalQuery.toString());
//                    return result;
//                });
//                ProjectLogger.log("Node created Successfully : "+result.single().get(0).asMap(), LoggerEnum.INFO.name());



//                synchronized (session) {
//                    transactionResult = session.writeTransaction(new TransactionWork<Map<String, Object>>() {
//                        @Override
//                        public Map<String, Object> execute(Transaction tx) {
//                            StatementResult result = tx.run(finalQuery.toString());
//                            tx.close();
//                            return result.single().get(0).asMap();
//                        }
//                    });
//                }
//                ProjectLogger.log("Node created Successfully : "+transactionResult, LoggerEnum.INFO.name());
//                    result = session.run(query.toString());
//                    result.single().get(0).asMap();
//                    if (result.hasNext()) {
//                        Record record = result.next();
//                        NodeValue node = (NodeValue) record.get(0);
//                        ProjectLogger.log("Node created Successfully : "+node.asMap(), LoggerEnum.INFO.name());
//                    }

                    response.addSuccess();
            }
            catch (Error | Exception e) {
                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data",dataCount+1);
            }
        }
        return response;
    }



//    @Override
//    public void createNode(String query) {
//
//        // To create a Node
//        ProjectLogger.log("Create Node called in NodeManagementDaoImpl", LoggerEnum.DEBUG.name());
//
//        StatementResult result = null;
//            try (Session session = getSession()) {
//
//                result = session.run(query);
//
//            }
//            catch (Error | Exception e) {
//                ProjectLogger.log("Error in Data",e, LoggerEnum.ERROR.name());
////                response.addErrorData("Error in data",dataCount+1);
//            }
////        return result;
//    }


    @Override
    public Response updateNode(Map<String,Object> nodeData) throws Exception {

        // Update a Node
        Response response= new Response();
        response.setOperation("Update Node");
        int dataCount=0;

        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        Session session = Neo4jConnectionManager.getSession();
        Record record;
        for(List<String> nodeDetailsEach : dataList) {

            dataCount++;

            try {

                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeDetailsEach.get(0).trim() + "` { `");
                query.append(header.get(1).trim()).append("`: \"").append(nodeDetailsEach.get(1).trim()).append("\"");
                query.append("}) RETURN a");
                ProjectLogger.log("Query generated to Update Node : "+query, LoggerEnum.INFO.name());

                result = session.run(query.toString());
                if(result.hasNext()) {
                    // Check if the Node Exists
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if(nodeCount > 1)
                    {
                        // update the Node
                        ProjectLogger.log("Duplicate Data Present : "+(recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present",dataCount+1);
                    }

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
        return response;
    }



    @Override
    public Response deleteNode(Map<String,Object> nodeData) throws Exception {

        // Update a Node
        Response response = new Response();
        response.setOperation("Delete Node");
        int dataCount = 0;

        header = (List<String>) nodeData.get("header");
        dataList = (List<List<String>>) nodeData.get("data");

        Record record;
        Session session = Neo4jConnectionManager.getSession();
            for (List<String> nodeDetailsEach : dataList) {

            dataCount++;

            try {

                boolean check = false;
                StringBuilder query = new StringBuilder("MATCH (a:`" + nodeDetailsEach.get(0).trim() + "` { ");
                for (int i = 1; i < header.size(); i++) {
                    if (!nodeDetailsEach.get(i).isEmpty()) {
                        query.append("`").append(header.get(i).trim());
                        query.append("`:\"").append(nodeDetailsEach.get(i).trim()).append("\"");
                        query.append(",");
                        check = true;
                    }
                }
                int lastCommaIndex = query.toString().lastIndexOf(',');
                if (lastCommaIndex > 0 && check) {
                    query = new StringBuilder(query.substring(0, lastCommaIndex) + query.substring(lastCommaIndex + 1));
                }
                query.append(" })");


                result = session.run(query.toString() + " RETURN a");
                if (result.hasNext()) {
                    // Check if the Node Exists
                    List<Record> recordList = result.list();
                    int nodeCount = recordList.size();

                    if (nodeCount > 1) {
                        // update the Node
                        ProjectLogger.log("Duplicate Data Present : " + (recordList.get(0).get(0)).asMap(), LoggerEnum.WARN.name());
                        response.addErrorData("Duplicate Data Present", dataCount + 1);
                    } else {
                        query.append(" DETACH DELETE a");
                        ProjectLogger.log("Query generated to DELETE Node : " + query, LoggerEnum.INFO.name());
                        result = session.run(query.toString());
                        if (result.hasNext()) {
                            record = result.next();
                            ProjectLogger.log("Node deleted successfully : " + record.asMap(), LoggerEnum.INFO.name());
                        }

                        response.addSuccess();
                    }
                } else {
                    ProjectLogger.log("No Such Data Present", LoggerEnum.WARN.name());
                    response.addErrorData("No Such Data Present", dataCount + 1);
                }

            } catch (Error | Exception e) {
                ProjectLogger.log("Error in Data", e, LoggerEnum.ERROR.name());
                response.addErrorData("Error in data", dataCount + 1);
            }
        }
        return response;
    }


}