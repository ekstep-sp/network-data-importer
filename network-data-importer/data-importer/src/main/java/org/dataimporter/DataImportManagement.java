package org.dataimporter;

import org.commons.exception.ProjectCommonException;
import org.dataimporter.dao.DataImportDao;
import org.dataimporter.dao.impl.DataImportDaoImpl;

import java.io.File;
import java.util.*;


public class DataImportManagement {

    public Map<String,Object> importData(String fileName,File input) {

        DataImportDao dataImporter = new DataImportDaoImpl();

        Map<String, Object> allData = new HashMap<>();

        try {
            String strFileExtn = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (strFileExtn.equalsIgnoreCase("xlsx")) {
                allData = dataImporter.readDataFromExcel(input);
            } else if (strFileExtn.equalsIgnoreCase("csv")) {
                allData = dataImporter.readDataFromCsv(input);
            } else {
                throw new ProjectCommonException(400, "Invalid File Type", "Please provide file of type '.csv' or '.xlsx' ");
            }

        }
        catch (ProjectCommonException e) {
            throw e;
        }
        return allData;
    }
}