package controllers.node;

import com.google.inject.Inject;
import controllers.BaseController;
import controllers.node.validator.NodeRequestValidator;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.dataimporter.DataImportManagement;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NodeController extends BaseController {

    private HttpExecutionContext httpExecutionContext;
    Request request;

    public NodeController() {

    }

    @Inject
    public NodeController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }

    public CompletionStage<Result> createNode() {
        ProjectLogger.log("Create Node Api called", LoggerEnum.DEBUG.name());

        try {
            request = processRequest(request(), "CreateNode");
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while processing request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (new BaseController().handleRequest(request,httpExecutionContext));
    }



    public CompletionStage<Result> updateNode() {
        ProjectLogger.log("Update Node Api called", LoggerEnum.DEBUG.name());

        try {
            request = processRequest(request(), "UpdateNode");
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while processing request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (new BaseController().handleRequest(request,httpExecutionContext));
    }


    private Request processRequest(Http.Request request, String operation) throws ProjectCommonException {

        new NodeRequestValidator().validateNodeRequest(request);
        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = body.getFile("data");
        Map<String, Object> nodeData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());

        String label = ((String[]) body.asFormUrlEncoded().get("label"))[0];
        Request customRequest = new Request(operation);
        customRequest.setRequestPath(request().path());
        customRequest.setRequestParameter("nodeSourceLabel",label.trim());
        customRequest.setRequestParameter("data",nodeData);
        return customRequest;
    }


}
