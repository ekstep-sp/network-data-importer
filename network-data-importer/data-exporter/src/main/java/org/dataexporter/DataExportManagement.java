package org.dataexporter;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.dataexporter.dao.DataExportDao;
import org.dataexporter.dao.impl.DataExportDaoImpl;
import java.util.Map;


public class DataExportManagement extends AbstractActor {


    DataExportDao dataExportDao = new DataExportDaoImpl();

    public static Props props() {
        ProjectLogger.log("Inside DataExportManagement props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(DataExportManagement.class, DataExportManagement::new);
    }


    public DataExportManagement() {

        receive(ReceiveBuilder
                .match(Request.class, request -> {

                    String operation = request.getOperation().toLowerCase();
                    switch (operation) {
                        case "createNode":
                            createNode(request);
                            break;
                        case "updateNode":
                            updateNode(request);
                            break;
                        case "deleteNode":
                            deleteNode(request);
                            break;
                        case "createRelation":
                            createNodeRelation(request);
                            break;
                        case "updateRelation":
                            updateNodeRelation(request);
                            break;
                        case "deleteRelation":
                            deleteNodeRelation(request);
                            break;
                        default:
                            throw new ProjectCommonException(400, "Unsupported Operation", "Request Operation is not supported : " + operation);
                    }
                    })
                .build());
    }

//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(Request.class, request -> {
//                    String operation = request.getOperation();
//                    switch (operation) {
//                        case "createNode":
//                            createNode(request);
//                            break;
//                        case "updateNode":
//                            updateNode(request);
//                            break;
//                        case "createRelation":
//                            createNodeRelation(request);
//                            break;
//                        case "updateRelation":
//                            updateNodeRelation(request);
//                            break;
//                        default:
//                            throw new ProjectCommonException(400,"Unsupported Operation","Request Operation is not supported : "+operation);
//                    }
//                        })
//                .build();
//    }

    private void createNode(Request request) {

        ProjectLogger.log("Create Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.createNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in create node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in create node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }

    private void updateNode(Request request) {

        ProjectLogger.log("Update Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.updateNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in update node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in update node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }

    private void deleteNode(Request request) {

        ProjectLogger.log("Delete Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.deleteNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in delete node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in delete node method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }

    private void createNodeRelation(Request request) {

        ProjectLogger.log("Create Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.createNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in create node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in create node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }

    private void updateNodeRelation(Request request) {

        ProjectLogger.log("Update Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.updateNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in update node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in update node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }


    private void deleteNodeRelation(Request request) {

        ProjectLogger.log("Delete Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = dataExportDao.deleteNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error in delete node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            ProjectLogger.log("Error in delete node relation method",e, LoggerEnum.ERROR.name());
            sender().tell(e,self());
        }
    }

}