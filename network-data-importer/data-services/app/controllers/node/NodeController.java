package controllers.node;

import com.google.inject.Inject;
import controllers.BaseController;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import java.util.concurrent.CompletionStage;

public class NodeController extends BaseController {

    private HttpExecutionContext httpExecutionContext;

    public NodeController() {

    }

    @Inject
    public NodeController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> createNode() {
        ProjectLogger.log("Create Node Api called", LoggerEnum.DEBUG.name());
            return processNodeRequest(request(), "CreateNode",httpExecutionContext);
    }

    public CompletionStage<Result> updateNode() {

        ProjectLogger.log("Update Node Api called", LoggerEnum.DEBUG.name());
        return processNodeRequest(request(), "UpdateNode", httpExecutionContext);
    }

    public CompletionStage<Result> deleteNode() {

        ProjectLogger.log("Delete Node Api called", LoggerEnum.DEBUG.name());
            return processNodeRequest(request(), "DeleteNode",httpExecutionContext);
    }



}
