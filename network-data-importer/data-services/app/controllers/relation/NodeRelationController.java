package controllers.relation;

import com.google.inject.Inject;
import controllers.BaseController;
import controllers.relation.validator.NodeRelationRequestValidator;
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

public class NodeRelationController extends BaseController {
    // to handle all Node Relation related requests

    private HttpExecutionContext httpExecutionContext;
    Request request;

    public NodeRelationController() {

    }

    @Inject
    public NodeRelationController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }

    public CompletionStage<Result> createNodeRelation() {

        ProjectLogger.log("Create Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), "createRelation",httpExecutionContext);
    }

    public CompletionStage<Result> updateNodeRelation() {

        ProjectLogger.log("Update Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), "updateRelation",httpExecutionContext);
    }

    public CompletionStage<Result> deleteNodeRelation() {

        ProjectLogger.log("Delete Node Relation Api called", LoggerEnum.DEBUG.name());
            return processRelationRequest(request(), "deleteRelation",httpExecutionContext);
    }



}
