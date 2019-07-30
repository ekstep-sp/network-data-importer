package org.dataexporter.actors.node;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.dataexporter.actors.node.dao.NodeManagementDao;
import org.dataexporter.actors.node.dao.impl.NodeManagementDaoImpl;
import java.util.Map;


public class NodeManagementActor extends AbstractActor {


    NodeManagementDao nodeManagementDao;

    public static Props props() {
        ProjectLogger.log("Inside NodeManagementActor props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(NodeManagementActor.class, NodeManagementActor::new);
    }


    public NodeManagementActor() {

        receive(ReceiveBuilder
                .match(Request.class, request -> {
                    nodeManagementDao = new NodeManagementDaoImpl();
                    String operation = request.getOperation();
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
                        default:
                            unSupportedOperation(request);
                    }
                })
                .build());
    }


    private void createNode(Request request) {

        ProjectLogger.log("Create Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.createNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = nodeManagementDao.createNode((Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            sender().tell(e,self());
        }
        catch (Exception | Error e)
        {
            sender().tell(e,self());
        }
    }

    private void updateNode(Request request) {

        ProjectLogger.log("Update Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.updateNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = nodeManagementDao.updateNode((Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            sender().tell(e,self());
        }
    }

    private void deleteNode(Request request) {

        ProjectLogger.log("Delete Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.deleteNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = nodeManagementDao.deleteNode((Map<String, Object>) requestMap.get("data"));
            sender().tell(response, self());
        }
        catch (ProjectCommonException e)
        {
            sender().tell(e,self());
        }
        catch (Exception e)
        {
            sender().tell(e,self());
        }
    }


    private void unSupportedOperation(Request request) {

        ProjectCommonException e = new ProjectCommonException(ResponseCode.unsupportedActorOperation, request.getOperation());
        ProjectLogger.log("Unsupported Operation",e, LoggerEnum.ERROR.name());
        sender().tell(e,self());
    }

}