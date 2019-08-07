package org.dataexporter.actors.node;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.dataexporter.actors.node.dao.NodeManagementDao;
import org.dataexporter.actors.node.dao.impl.NodeManagementDaoImpl;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;


public class NodeManagementActor extends UntypedActor {


    NodeManagementDao nodeManagementDao;

    public static Props props() {
        ProjectLogger.log("Inside NodeManagementActor props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(NodeManagementActor.class, NodeManagementActor::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof Request)
        {
            Request request = (Request) message;
            String operation = request.getOperation();
            ProjectLogger.log("Inside NodeManagementActor Receive", LoggerEnum.DEBUG.name());
//                  Method method = this.getClass().getMethod(operation,Request.class);
//                  method.invoke(null,request);
            switch (operation) {
                case "createNode": {
                    createNode(request);
                    break;
                }
                case "updateNode":
                    updateNode(request);
                    break;
                case "deleteNode":
                    deleteNode(request);
                    break;
                 default:
                     unSupportedOperation(request);
            }
        }
    }

    public NodeManagementActor()  {
        nodeManagementDao = new NodeManagementDaoImpl();

    }


        private void createNode(Request request) {

        ProjectLogger.log("Create Node method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
            nodeManagementDao = new NodeManagementDaoImpl();

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