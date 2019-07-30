package org.dataexporter.actors.relation;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.dataexporter.actors.relation.dao.RelationManagementDao;
import org.dataexporter.actors.relation.dao.impl.RelationManagementDaoImpl;
import java.util.Map;


public class RelationManagementActor extends AbstractActor {


    RelationManagementDao relationManagementDao;

    public static Props props() {
        ProjectLogger.log("Inside RelationManagementActor props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(RelationManagementActor.class, RelationManagementActor::new);
    }


    public RelationManagementActor() {

        receive(ReceiveBuilder
                .match(Request.class, request -> {
                    relationManagementDao = new RelationManagementDaoImpl();
                    String operation = request.getOperation();
                    switch (operation) {
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
                            unSupportedOperation(request);
                    }
                })
                .build());
    }


    private void createNodeRelation(Request request) {

        ProjectLogger.log("Create Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.createNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = relationManagementDao.createNodeRelation((Map<String, Object>) requestMap.get("data"));
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

    private void updateNodeRelation(Request request) {

        ProjectLogger.log("Update Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.updateNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = relationManagementDao.updateNodeRelation((Map<String, Object>) requestMap.get("data"));
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


    private void deleteNodeRelation(Request request) {

        ProjectLogger.log("Delete Node Relation method called", LoggerEnum.DEBUG.name());
        try {
            Map<String,Object> requestMap = request.getRequest();
//            Response response = dataExportDao.deleteNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
            Response response = relationManagementDao.deleteNodeRelation((Map<String, Object>) requestMap.get("data"));
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