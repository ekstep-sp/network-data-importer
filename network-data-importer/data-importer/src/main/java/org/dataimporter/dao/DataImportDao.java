package org.dataimporter.dao;


import java.io.File;
import java.util.Map;

public interface DataImportDao {


    Map<String,Object> readDataFromExcel(File input);

    Map<String,Object> readDataFromCsv(File input);

}