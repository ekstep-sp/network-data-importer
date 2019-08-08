package org.dataexporter.actors.data.dao;

import org.commons.response.Response;


public interface DataManagementDao {

    Response getAllData() throws Exception;
}
