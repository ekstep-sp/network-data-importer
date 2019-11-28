package org.dataimporter;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.dataimporter.dao.DataImportDao;
import org.dataimporter.dao.impl.DataImportDaoImpl;

import java.io.File;
import java.util.*;


public class DataImportManagement {

    public Map<String,Object> importData(String fileName,File input) {

        DataImportDao dataImporter = new DataImportDaoImpl();

        Map<String, Object> allData;

        try {
            String strFileExtn = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (strFileExtn.equalsIgnoreCase("xlsx")) {
                // Check if the file is of type Excel
                allData = dataImporter.readDataFromExcel(input);
            } else if (strFileExtn.equalsIgnoreCase("csv")) {
                // Check if the file is of type csv
                allData = dataImporter.readDataFromCsv(input);
            } else {
                ProjectLogger.log("File Type not Supported", LoggerEnum.ERROR.name());
                throw new ProjectCommonException(ResponseCode.unsupportedFileTypeError,".csv",".xlsx");
            }

        }
        catch (ProjectCommonException e) {
            throw e;
        }
        return  allData;
    }


//    public CompletableFuture<Map<String,Object>> importData(String fileName,File input, String excess) {
//
//        DataImportDao dataImporter = new DataImportDaoImpl();
//
//        Map<String, Object> allData;
//
//        try {
//            String strFileExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
//
//            if (strFileExtn.equalsIgnoreCase("xlsx")) {
//                // Check if the file is of type Excel
//                allData = dataImporter.readDataFromExcel(input);
//            } else if (strFileExtn.equalsIgnoreCase("csv")) {
//                // Check if the file is of type csv
//                allData = dataImporter.readDataFromCsv(input);
//            } else {
//                ProjectLogger.log("File Type not Supported", LoggerEnum.ERROR.name());
//                throw new ProjectCommonException(ResponseCode.unsupportedFileTypeError,".csv",".xlsx");
//            }
//
//        }
//        catch (ProjectCommonException e) {
//            throw e;
//        }
////        return  allData;
//        CompletableFuture<Map<String,Object>> completableFuture = CompletableFuture.supplyAsync(() -> allData);
//        return completableFuture;
//    }
}