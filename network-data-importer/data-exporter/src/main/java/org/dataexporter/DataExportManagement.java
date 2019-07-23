package org.dataexporter;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.request.Request;
import org.commons.response.Response;
import org.dataexporter.dao.impl.DataExportDaoImpl;
import java.util.Map;


public class DataExportManagement extends AbstractActor {



    public static Props props() {
        return Props.create(DataExportManagement.class, DataExportManagement::new);
    }


    public DataExportManagement() {

        receive(ReceiveBuilder
                .match(Request.class, request -> {

                    String operation = request.getOperation().toLowerCase();
                    switch (operation) {
                        case "createnode":
                            createNode(request);
                            break;
                        case "updatenode":
                            updateNode(request);
                            break;
                        case "createrelation":
                            createNodeRelation(request);
                            break;
                        case "updaterelation":
                            updateNodeRelation(request);
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
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = new DataExportDaoImpl().createNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
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

    private void updateNode(Request request) {
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = new DataExportDaoImpl().updateNode((String) requestMap.get("nodeSourceLabel"), (Map<String, Object>) requestMap.get("data"));
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

    private void createNodeRelation(Request request) {
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = new DataExportDaoImpl().createNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
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
        try {
            Map<String,Object> requestMap = request.getRequest();
            Response response = new DataExportDaoImpl().updateNodeRelation((String) requestMap.get("nodeSourceLabel"), (String) requestMap.get("nodeTargetLabel"), (Map<String, Object>) requestMap.get("data"));
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

}