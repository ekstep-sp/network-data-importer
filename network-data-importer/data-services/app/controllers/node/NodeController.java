package controllers.node;

import com.google.inject.Inject;
import controllers.BaseController;
import controllers.node.validator.NodeRequestValidator;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.util.ActorOperation;
import org.commons.util.Constants;
import org.dataexporter.actors.node.NodeManagementActor;
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
    // to handle all Node related requests

    private HttpExecutionContext httpExecutionContext;

    public NodeController() {

    }

    @Inject
    public NodeController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> createNode() {
        ProjectLogger.log("Create Node Api called", LoggerEnum.DEBUG.name());
            return processNodeRequest(request(), ActorOperation.CREATE_NODE.getValue(),httpExecutionContext);
    }

    public CompletionStage<Result> updateNode() {

        ProjectLogger.log("Update Node Api called", LoggerEnum.DEBUG.name());
        return processNodeRequest(request(), ActorOperation.UPDATE_NODE.getValue(), httpExecutionContext);
    }

    public CompletionStage<Result> deleteNode() {

        ProjectLogger.log("Delete Node Api called", LoggerEnum.DEBUG.name());
            return processNodeRequest(request(), ActorOperation.DELETE_NODE.getValue(),httpExecutionContext);
    }

    private CompletionStage<Result> processNodeRequest(Http.Request request, String operation, HttpExecutionContext httpExecutionContext) throws ProjectCommonException {

        // To process any Node request generated
        Request customRequest;
        try {
            new NodeRequestValidator().validateNodeRequest(request);
            Http.MultipartFormData body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = body.getFile(Constants.DATA);
            Map<String, Object> nodeData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());
            customRequest = new Request(operation);
            customRequest.setRequestPath(request().path());
            customRequest.setRequestParameter("data", nodeData);
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while validating node request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (handleCustomRequest(customRequest,httpExecutionContext,NodeManagementActor.class.getSimpleName()));
    }


}
