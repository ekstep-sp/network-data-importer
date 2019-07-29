package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import controllers.node.validator.NodeRequestValidator;
import controllers.relation.validator.NodeRelationRequestValidator;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.responsecode.ResponseCode;
import org.dataexporter.DataExportManagement;
import org.dataimporter.DataImportManagement;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;



public class BaseController extends Controller {

private static ActorSystem system;
private static ActorRef actorRef;
private  static Timeout timeout;
//    private HttpExecutionContext httpExecutionContext;


static {
    // Creating Actor for the Data exporter
    ProjectLogger.log("Creating Actor System in BaseController", LoggerEnum.INFO.name());
    system = ActorSystem.create("data-exporter");
    timeout = Timeout.apply(2, TimeUnit.MINUTES);
    actorRef = system.actorOf(DataExportManagement.props(), "export-management");
}

public BaseController() {

}

//    @Inject
//    public BaseController(HttpExecutionContext ec) {
//        this.httpExecutionContext = ec;
//    }


    protected CompletionStage<Result> processNodeRequest(Http.Request request, String operation, HttpExecutionContext httpExecutionContext) throws ProjectCommonException {

    // To process any Node request generated
    Request customRequest;
    try {
        new NodeRequestValidator().validateNodeRequest(request);
        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = body.getFile("data");
        Map<String, Object> nodeData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());

//        String label = ((String[]) body.asFormUrlEncoded().get("label"))[0];
        customRequest = new Request(operation);
        customRequest.setRequestPath(request().path());
//        customRequest.setRequestParameter("nodeSourceLabel", label.trim());
        customRequest.setRequestParameter("data", nodeData);
    }
    catch (ProjectCommonException e) {
            ProjectLogger.log("Error while validating node request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (handleCustomRequest(customRequest,httpExecutionContext));
    }

    protected CompletionStage<Result> processRelationRequest(Http.Request request, String operation, HttpExecutionContext httpExecutionContext) throws ProjectCommonException {

        // To process any Node Relationship request generated
        Request customRequest;
        try {
        new NodeRelationRequestValidator().validateNodeRelationRequest(request);
        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = body.getFile("data");
        Map<String, Object> nodeRelationData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());

//        String sourceNodeLabel = ((String[]) body.asFormUrlEncoded().get("source-label"))[0];
//        String targetNodeLabel = ((String[]) body.asFormUrlEncoded().get("target-label"))[0];

        customRequest = new Request(operation);
        customRequest.setRequestPath(request().path());

//        customRequest.setRequestParameter("nodeSourceLabel", sourceNodeLabel.trim());
//        customRequest.setRequestParameter("nodeTargetLabel", targetNodeLabel.trim());
        customRequest.setRequestParameter("data", nodeRelationData);
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while validating node relation request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (handleCustomRequest(customRequest,httpExecutionContext));
    }


    private CompletionStage<Result> handleCustomRequest(Request request,HttpExecutionContext httpExecutionContext) {
    // To handle the custom request generated after reading the file from the request by using the Actor Model System of Data-Exporter

        return FutureConverters.toJava(
                Patterns.ask(actorRef, request, timeout))
                .thenApplyAsync(response -> {
                    if(response instanceof ProjectCommonException) {
                        ProjectCommonException exc = (ProjectCommonException) response;
                        ProjectLogger.log("Actor returned an error : ",exc, LoggerEnum.ERROR.name());
                        exc.setRequestPath(request.getRequestPath());
                        return Results.status(exc.getResponseCode(), Json.toJson(exc.toMap()));
                    }
                    else if(response instanceof Response) {
                        Map<String,Object> responseMap = ((Response)response).getResponse();
                        ProjectLogger.log("Actor Success Response : "+responseMap, LoggerEnum.INFO.name());
                        return ok(Json.toJson(responseMap));
                    }
                    else {
                        ProjectLogger.log("Unknown response from the Actor", LoggerEnum.WARN.name());
                        ProjectCommonException exc = new ProjectCommonException(ResponseCode.internalServerError);
                        return Results.status(exc.getResponseCode(),Json.toJson(exc.toMap()));
                    }
                },httpExecutionContext.current());
    }

}
