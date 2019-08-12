package controllers.health;

import com.google.inject.Inject;
import controllers.BaseController;
import org.commons.database.Neo4jConnectionManager;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HealthController extends BaseController {
    // to handle all Node related requests

    private HttpExecutionContext httpExecutionContext;

    public HealthController() {

    }

    @Inject
    public HealthController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> checkHealth() {
        ProjectLogger.log("Check Health Api called", LoggerEnum.DEBUG.name());
        return CompletableFuture.supplyAsync(() -> {
            Map<String,Object> health = new HashMap<>();
            health.put("Server Healthy","true");
            boolean check = Neo4jConnectionManager.checkDatabaseConnectionStatus();
            health.put("Database Healthy","true");
            Map<String,Object> response = new HashMap<>();
            response.put("Response",health);
            return ok(Json.toJson(response));
        },httpExecutionContext.current());
    }


    public CompletionStage<Result> checkDatabaseHealth() {
        ProjectLogger.log("Check Database Health Api called", LoggerEnum.DEBUG.name());
        return CompletableFuture.supplyAsync(() -> {
            Map<String,Object> health = new HashMap<>();
            boolean check = Neo4jConnectionManager.checkDatabaseConnectionStatus();
            health.put("Database Healthy",check);
            Map<String,Object> response = new HashMap<>();
            response.put("Response",health);
            return ok(Json.toJson(response));
        },httpExecutionContext.current());
    }



}
