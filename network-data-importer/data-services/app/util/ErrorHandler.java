package util;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {

        ProjectLogger.log("Global: onClientError called for path = " + request.path(), LoggerEnum.ERROR.name());
        if(statusCode == 404)
            message = "Api Not Found";
        ProjectCommonException commonException = new ProjectCommonException(statusCode, "Client Error", message);
        ProjectLogger.log("Client Error", commonException, LoggerEnum.ERROR.name());

        return CompletableFuture.completedFuture( Results.status(commonException.getResponseCode(), Json.toJson(commonException.toMap())
        ));
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {

        ProjectLogger.log("Global: onServerError called for path = " + request.path(), LoggerEnum.INFO.name());
        ProjectCommonException commonException = null;
        if (exception instanceof ProjectCommonException) {
            ProjectLogger.log("Global:onServerError: ProjectCommonException occurred for path = " + request.path(), LoggerEnum.ERROR.name());
            commonException = (ProjectCommonException) exception;
        }
        else if (exception instanceof akka.pattern.AskTimeoutException) {
            ProjectLogger.log("Global:onServerError: AskTimeoutException occurred for path = " + request.path(), LoggerEnum.ERROR.name());
            commonException = new ProjectCommonException(ResponseCode.actorTimeoutError);
        } else {
            ProjectLogger.log("Global:onServerError: Unknown exception occurred for path = " + request.path(), LoggerEnum.INFO.name());
            commonException = new ProjectCommonException(ResponseCode.internalServerError);
        }

        ProjectLogger.log("Server Error", commonException, LoggerEnum.ERROR.name());
        return CompletableFuture.completedFuture( Results.status(commonException.getResponseCode(), Json.toJson(commonException.toMap())
        ));
    }

    }