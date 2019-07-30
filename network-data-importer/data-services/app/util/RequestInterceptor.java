package util;

import org.commons.auth.JwtAuthentication;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

public class RequestInterceptor implements play.http.ActionCreator {
    // To handle authentication

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Context ctx) {

                Http.Request request = ctx.request();
                if(!request.path().equals("/v1/auth/create")) {
                    try {
                        String userToken = request.getHeader("user-token");
                        if (userToken == null || userToken.isEmpty()) {
                            throw new ProjectCommonException(ResponseCode.unAuthorized);
                        } else {
                            new JwtAuthentication().verifyUserToken(userToken);
                        }
                    } catch (ProjectCommonException exc) {
                        return CompletableFuture.supplyAsync(() -> {
                            return Results.status(exc.getResponseCode(), Json.toJson(exc.toMap()));
                        });
                    }
                    ProjectLogger.log("User Access Validated. Access Granted", LoggerEnum.DEBUG.name());
                }
                return delegate.call(ctx);
            }
            };
    }
}