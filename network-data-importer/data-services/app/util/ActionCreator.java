package util;

import org.commons.auth.JwtAuthentication;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

public class ActionCreator implements play.http.ActionCreator {
    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Context ctx) {

                Http.Request request = ctx.request();
                if(!request.path().equals("/v1/auth/create")) {
                    try {
                        if (request.getHeader("user-token") == null || request.getHeader("user-token").isEmpty()) {
                            throw new ProjectCommonException(401, "Unauthorised", "User Authentication required. Please provide the 'user-token'");
                        } else {
                            new JwtAuthentication().verifyUserToken(request.getHeader("user-token"));
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