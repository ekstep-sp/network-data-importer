package org.dataexporter.dao;

import org.commons.response.Response;

import java.util.Map;

public interface DataExportDao {

    Response createNode(String nodeLabel, Map<String,Object> nodeData) throws Exception;

    Response updateNode(String nodeLabel, Map<String,Object> nodedata) throws Exception;

    Response deleteNode(String nodeLabel, Map<String,Object> nodedata) throws Exception;

    Response createNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception;

    Response updateNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception;

    Response deleteNodeRelation(String nodeSourceLabel, String nodeTargetLabel, Map<String,Object> relationData) throws Exception;

}