package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.dataexporter.DataExportManagement;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import scala.compat.java8.FutureConverters;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;



public class BaseController extends Controller {

private static ActorSystem system;
private static ActorRef actorRef;
private  static Timeout timeout;
//    private HttpExecutionContext httpExecutionContext;


static {
    ProjectLogger.log("Creating Actor System in BaseController", LoggerEnum.INFO.name());
    system = ActorSystem.create("data-actor");
    timeout = Timeout.apply(5, TimeUnit.MINUTES);
    actorRef = system.actorOf(DataExportManagement.props(), "export-management");
}

public BaseController() {

}

//    @Inject
//    public BaseController(HttpExecutionContext ec) {
//        this.httpExecutionContext = ec;
//    }

    public CompletionStage<Result> handleRequest(Request request,HttpExecutionContext httpExecutionContext) {


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
                        return Results.status(400,"Internal Server Error");
                    }
                },httpExecutionContext.current());
    }

}
