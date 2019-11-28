package org.dataexporter.actors.relation.dao;

import org.commons.exception.ProjectCommonException;
import org.commons.response.Response;

import java.util.Map;

public interface RelationManagementDao {


    Response createNodeRelation(Map<String,Object> relationData) throws Exception;

    Response updateNodeRelation(Map<String,Object> relationData) throws Exception;

    Response deleteNodeRelation(Map<String,Object> relationData) throws Exception;

}