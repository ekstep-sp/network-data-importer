package org.dataimporter.dao.impl;

import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.dataimporter.dao.DataImportDao;

import java.io.*;
import java.util.*;

public class DataImportDaoImpl implements DataImportDao {

    Map<String, Object> allData = new HashMap<>();


    @Override
    public Map<String,Object> readDataFromExcel(File input){

        // Reading the EXCEL file provided in the request
        List<List<String>> contentList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        try {
            FileInputStream excelFile = new FileInputStream(input);
            Workbook workbook = new XSSFWorkbook(excelFile);

            int iNumOfSheets = workbook.getNumberOfSheets();

            for (int sheetIndex = 0; sheetIndex < iNumOfSheets; sheetIndex++)
            {
                int count = -1;
                int dataLength=0;

                Sheet datatypeSheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> iterator = datatypeSheet.iterator();
                List<String> sheetData = null;

                while (iterator.hasNext()) {

                    sheetData = new ArrayList();
                    Row currentRow = iterator.next();

                    Iterator<Cell> cellIterator = currentRow.iterator();

                    if (count == -1) {
                        while (cellIterator.hasNext()) {

                            Cell currentCell = cellIterator.next();
                            if (currentCell != null) {
                                XSSFCell dataCell = (XSSFCell) currentCell;
//                                    dataCell.setCellType(Cell.CELL_TYPE_STRING);
                                if (!(dataCell.toString()).isEmpty()) {
                                    headers.add(dataCell.toString());
                                    dataLength++;
                                }
                            }
                        }
                    }
                    else {
                        for(int cellNo=0; cellNo<dataLength; cellNo++)
//                            while (cellIterator.hasNext())
                        {

                            Cell currentCell = currentRow.getCell(cellNo);
                            if (currentCell != null) {
                                XSSFCell dataCell = (XSSFCell) currentCell;
//                                    dataCell.setCellType(Cell.CELL_TYPE_STRING);
                                if (!dataCell.toString().isEmpty()) {
                                    sheetData.add(dataCell.toString());
                                } else {
                                    sheetData.add("");
                                }
                            } else {
                                sheetData.add("");
                            }
                        }
                        contentList.add(sheetData);
                    }
                    count++;
                }
            }
        } catch(IOException e){
            ProjectLogger.log("Unable to process excel file due to incorrect format of data : ",e, LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.fileDataError);
        } catch (Exception e) {
            ProjectLogger.log("Internal Processing error while reading excel file : ",e, LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.internalFileProcessingError);
        }

        allData.put("header",headers);
        allData.put("data",contentList);

        return allData;
    }



    @Override
    public Map<String,Object> readDataFromCsv(File input) {

        // Reading the CSV file provided in the request
        List<List<String>> contentList = new ArrayList<>();
        List<String> header = new ArrayList<>();
        int count = -1;
        try {

            FileReader filereader = new FileReader(input);

//            CSVReader csvReader = new CSVReaderBuilder(filereader)
//                    .withSkipLines(1)
//                    .build();

            CSVReader csvReader = new CSVReader(filereader);
            Object[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                List<String> rowData = new ArrayList<>();
                if(count == -1) {
                    for (Object cell : nextRecord) {
                        header.add(cell.toString());
                    }
                }
                    else {
                    for (Object cell : nextRecord) {
                        rowData.add(cell.toString());
                    }
                    contentList.add(rowData);
                }
                    count++;
            }
        }
        catch(IOException e){
            ProjectLogger.log("Unable to process csv file due to incorrect format of data : ",e, LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.fileDataError);
        } catch (Exception e) {
            ProjectLogger.log("Internal Processing error while reading csv file : ",e, LoggerEnum.ERROR.name());
            throw new ProjectCommonException(ResponseCode.internalFileProcessingError);
        }
        allData.put("header",header);
        allData.put("data",contentList);

        return allData;
    }

}