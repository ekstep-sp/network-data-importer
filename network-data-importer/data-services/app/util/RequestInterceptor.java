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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

public class RequestInterceptor implements play.http.ActionCreator {

    private static List<String> openUrlList;
    private static List<String> tokenKeys;

    // To handle authentication

    static {
        openUrlList = new ArrayList<>();
        openUrlList.add("/v1/auth/create");
        openUrlList.add("/v1/data/read");
        openUrlList.add("/");
        openUrlList.add("/health");
        openUrlList.add("/database/health");

        tokenKeys = new ArrayList<>();
        tokenKeys.add("user-token");
        tokenKeys.add("x-authenticated-user-token");
        tokenKeys.add("x-authenticated-client-token");
        tokenKeys.add("x-authenticated-client-id");
        tokenKeys.add("authorization");

    }


    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Context ctx) {

                Http.Request request = ctx.request();
                Map<String,String[]> headers = request.headers();
                addResponseHeaderData(ctx);
                if(!openUrlList.contains(request.path())) {
                    try {
                        String userToken = getToken(headers);
                        if (userToken == null || userToken.isEmpty()) {
                            throw new ProjectCommonException(ResponseCode.unAuthorized);
                        } else {
                            new JwtAuthentication().verifyUserToken(userToken);
                        }
                    } catch (ProjectCommonException exc) {
                        ProjectLogger.log("RequestInterceptor: Unauthorised access from path  = " + request.path(),exc, LoggerEnum.INFO.name());
                        return CompletableFuture.supplyAsync(() -> Results.status(exc.getResponseCode(), Json.toJson(exc.toMap())));
                    }
                    ProjectLogger.log("User Access Validated. Access Granted", LoggerEnum.DEBUG.name());
                }
                return delegate.call(ctx);
            }
            };
    }


    private String getToken(Map<String,String[]> headers) {
        for(String tokenKeysEach: tokenKeys)
        {
            if(headers.containsKey(tokenKeysEach)){
                return (headers.get(tokenKeysEach))[0];
            }
        }
        return null;
    }


    private void addResponseHeaderData(Http.Context ctx)
    {
//        ctx.response().setHeader("Access-Control-Allow-Origin","*");
//        ctx.response().setHeader("Access-Control-Allow-Methods", "*");
//        ctx.response().setHeader("Access-Control-Allow-Headers", "*");
//        ctx.response().setHeader("Access-Control-Allow-Credentials", "true");
    }
}