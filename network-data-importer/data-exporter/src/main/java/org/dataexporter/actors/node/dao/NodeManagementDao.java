package org.dataexporter.actors.node.dao;

import org.commons.response.Response;

import java.util.Map;

public interface NodeManagementDao {


    Response createNode(Map<String,Object> nodeData) throws Exception;

    Response updateNode(Map<String,Object> nodedata) throws Exception;

    Response deleteNode(Map<String,Object> nodedata) throws Exception;

}