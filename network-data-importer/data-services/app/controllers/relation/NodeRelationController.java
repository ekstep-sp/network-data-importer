package controllers.relation;

import com.google.inject.Inject;
import controllers.BaseController;
import controllers.relation.validator.NodeRelationRequestValidator;
import org.commons.exception.ProjectCommonException;
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

public class NodeRelationController extends BaseController {

    private HttpExecutionContext httpExecutionContext;
    Request request;

    public NodeRelationController() {

    }

    @Inject
    public NodeRelationController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }

    public CompletionStage<Result> createNodeRelation() {

        try {
            request = processRequest(request(), "CreateRelation");
        }
        catch (ProjectCommonException e) {
            return CompletableFuture.supplyAsync(() -> {
               return Results.status(e.getResponseCode(),Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }

        return (new BaseController().handleRequest(request,httpExecutionContext));
    }

    public CompletionStage<Result> updateNodeRelation() {

        try {
            request = processRequest(request(), "UpdateRelation");
        }
        catch (ProjectCommonException e) {
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(),Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return (new BaseController().handleRequest(request,httpExecutionContext));
    }


    private Request processRequest(Http.Request request, String operation) throws ProjectCommonException {

        Request customRequest = null;

            new NodeRelationRequestValidator().validateNodeRelationRequest(request);
            Http.MultipartFormData body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = body.getFile("data");
            Map<String, Object> nodeRelationData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());

            String sourceNodeLabel = ((String[]) body.asFormUrlEncoded().get("source-label"))[0];
            String targetNodeLabel = ((String[]) body.asFormUrlEncoded().get("target-label"))[0];

            customRequest = new Request(operation);
            customRequest.setRequestPath(request().path());

            customRequest.setRequestParameter("nodeSourceLabel", sourceNodeLabel.trim());
            customRequest.setRequestParameter("nodeTargetLabel", targetNodeLabel.trim());
            customRequest.setRequestParameter("data", nodeRelationData);

        return customRequest;
    }

}
