package org.dataexporter.actors.dataextractor.dao;

import org.commons.response.Response;


public interface DataExtractorManagementDao {

    Response getAllData() throws Exception;
}
