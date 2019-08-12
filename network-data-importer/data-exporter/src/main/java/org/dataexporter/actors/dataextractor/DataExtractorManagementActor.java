package org.dataexporter.actors.dataextractor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.google.common.io.Files;
import com.opencsv.CSVWriter;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;
import org.dataexporter.actors.dataextractor.dao.DataExtractorManagementDao;
import org.dataexporter.actors.dataextractor.dao.impl.DataExtractorManagementDaoImpl;
import org.dataexporter.actors.node.NodeManagementActor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataExtractorManagementActor extends UntypedActor {

    DataExtractorManagementDao dataManagementDao;

    public static Props props() {
        ProjectLogger.log("Inside DataManagement props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(NodeManagementActor.class, NodeManagementActor::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof Request)
        {
            Request request = (Request) message;
            String operation = request.getOperation();
            ProjectLogger.log("Inside DataManagement Receive", LoggerEnum.DEBUG.name());
//                  Method method = this.getClass().getMethod(operation,Request.class);
//                  method.invoke(null,request);
            switch (operation) {
                case "getAllData": {
                    getAllData(request);
                    break;
                }
                default:
                    unSupportedOperation(request);
            }
        }
    }

    public DataExtractorManagementActor()  {
        dataManagementDao = new DataExtractorManagementDaoImpl();

    }


    private void getAllData(Request request) {

        ProjectLogger.log("Get All Data method called", LoggerEnum.DEBUG.name());
        try {
            Map<String, Object> requestMap = request.getRequest();
            dataManagementDao = new DataExtractorManagementDaoImpl();

            Response response = dataManagementDao.getAllData();
            if (response.getSuccessCount() != 0)
            {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FILE_DATE_TIME_FORMAT);
            Date date = new Date();
//            File file = File.createTempFile(formatter.format(date) + "_", ".csv");
                File file = new File(Files.createTempDir(),"Visualizer-Data-"+formatter.format(date)+".csv");
            try {
                // create FileWriter object with file as parameter
                FileWriter outputFile = new FileWriter(file);

                // create CSVWriter with ',' as separator

                CSVWriter writer = new CSVWriter(outputFile, ',',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                List<List<String>> header = (List<List<String>>) response.getSuccessData().get("header");
                List<String> q = new ArrayList<>();
                for (List<String> headerEach : header)
                    q.addAll(headerEach);
                writer.writeNext(q.toArray(new String[0]));

                List<List<List<String>>> data = (List<List<List<String>>>) response.getSuccessData().get("data");

                for (List<List<String>> dataEach : data) {
                    List<String> eachData = new ArrayList<>();
                    for (List<String> oneData : dataEach) {
                        eachData.addAll(oneData);
                    }
                    writer.writeNext(eachData.toArray(new String[0]));
                }
                Map<String, Object> csvFile = new HashMap<>();
                csvFile.put("file", file);
                response.setSuccessData(csvFile);
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                ProjectLogger.log("Error in File creation", e, LoggerEnum.ERROR.name());
                throw new ProjectCommonException(ResponseCode.FileCreationError);
            }
                sender().tell(response, self());
            }
            else
            {
                ProjectLogger.log("No data found in the database", LoggerEnum.DEBUG.name());
                sender().tell(response, self());

            }
        }
        catch (ProjectCommonException e)
        {
            sender().tell(e,self());
        }
        catch (Exception | Error e)
        {
            e.printStackTrace();
            ProjectLogger.log("Error in File creation", e, LoggerEnum.ERROR.name());
            sender().tell(new ProjectCommonException(ResponseCode.FileCreationError),self());
        }
    }




    private void unSupportedOperation(Request request) {

        ProjectCommonException e = new ProjectCommonException(ResponseCode.unsupportedActorOperation, request.getOperation());
        ProjectLogger.log("Unsupported Operation",e, LoggerEnum.ERROR.name());
        sender().tell(e,self());
    }


}
