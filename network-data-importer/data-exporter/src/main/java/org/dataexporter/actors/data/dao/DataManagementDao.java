package org.dataexporter.actors.data.dao;

import org.commons.response.Response;

import java.util.Map;

public interface DataManagementDao {

    Response getAllData() throws Exception;
}
