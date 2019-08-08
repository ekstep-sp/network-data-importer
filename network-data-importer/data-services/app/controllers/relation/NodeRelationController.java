package controllers.relation;

import com.google.inject.Inject;
import controllers.BaseController;
import controllers.relation.validator.NodeRelationRequestValidator;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.util.ActorOperation;
import org.commons.util.Constants;
import org.dataexporter.actors.relation.RelationManagementActor;
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

public class NodeRelationController extends BaseController {
    // to handle all Node Relation related requests

    private HttpExecutionContext httpExecutionContext;

    public NodeRelationController() {

    }

    @Inject
    public NodeRelationController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }

    public CompletionStage<Result> createNodeRelation() {

        ProjectLogger.log("Create Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), ActorOperation.CREATE_RELATION.getValue(),httpExecutionContext);
    }

    public CompletionStage<Result> updateNodeRelation() {

        ProjectLogger.log("Update Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), ActorOperation.UPDATE_RELATION.getValue(),httpExecutionContext);
    }

    public CompletionStage<Result> deleteNodeRelation() {

        ProjectLogger.log("Delete Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), ActorOperation.DELETE_RELATION.getValue(),httpExecutionContext);
    }


    private CompletionStage<Result> processRelationRequest(Http.Request request, String operation, HttpExecutionContext httpExecutionContext) throws ProjectCommonException {

        // To process any Node Relationship request generated
        Request customRequest;
        try {
            new NodeRelationRequestValidator().validateNodeRelationRequest(request);
            Http.MultipartFormData body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = body.getFile(Constants.DATA);
            Map<String, Object> nodeRelationData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());
            customRequest = new Request(operation);
            customRequest.setRequestPath(request().path());
            customRequest.setRequestParameter("data", nodeRelationData);
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while validating node relation request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return handleCustomRequest(customRequest,httpExecutionContext,RelationManagementActor.class.getSimpleName())
                .thenApply(response -> {
                    Result result;
                    if(response instanceof Result)
                        result = (Result) response;
                    else
                        result = ok(Json.toJson((Map<String,Object>)response));
                    return result;
                });
    }

}
