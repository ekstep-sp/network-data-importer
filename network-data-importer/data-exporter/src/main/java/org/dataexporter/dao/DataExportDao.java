package org.dataexporter.dao;

import org.commons.response.Response;

import java.util.Map;

public interface DataExportDao {

//    Response createNode(String nodeLabel, Map<String,Object> nodeData) throws Exception;

    Response createNode(Map<String,Object> nodeData) throws Exception;

    Response updateNode(Map<String,Object> nodedata) throws Exception;

    Response deleteNode(Map<String,Object> nodedata) throws Exception;

//    Response createNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception;

    Response createNodeRelation(Map<String,Object> relationData) throws Exception;

    Response updateNodeRelation(Map<String,Object> relationData) throws Exception;

    Response deleteNodeRelation(Map<String,Object> relationData) throws Exception;

}