package org.dataexporter.actors.node.dao;

import org.commons.response.Response;
import org.neo4j.driver.v1.StatementResult;

import java.util.Map;

public interface NodeManagementDao {


    Response createNode(Map<String,Object> nodeData) throws Exception;
//void createNode(String query) throws Exception;


    Response updateNode(Map<String,Object> nodedata) throws Exception;

    Response deleteNode(Map<String,Object> nodedata) throws Exception;

}