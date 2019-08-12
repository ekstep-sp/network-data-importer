package util;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    // To handle all errors in the project

    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        // To handle all client errors in the project

        ProjectLogger.log("Global: onClientError called for path = " + request.path(), LoggerEnum.ERROR.name());
        if(statusCode == 404)
            message = Constants.API_NOT_FOUND;
        ProjectCommonException commonException = new ProjectCommonException(statusCode, "Client Error", message);
        ProjectLogger.log("Client Error", commonException, LoggerEnum.ERROR.name());
        Result result = Results.status(commonException.getResponseCode(), Json.toJson(commonException.toMap()));
        result.withHeader(Constants.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        result.withHeader(Constants.ACCESS_CONTROL_ALLOW_METHODS, "*");
        result.withHeader(Constants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        result.withHeader(Constants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        return CompletableFuture.completedFuture(result);
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        // To handle all Server errors in the project

        ProjectLogger.log("Global: onServerError called for path = " + request.path(), LoggerEnum.INFO.name());
        ProjectCommonException commonException;
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