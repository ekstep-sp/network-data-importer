package controllers.auth;

import controllers.auth.validator.AuthenticationRequestValidator;
import org.commons.auth.JwtAuthentication;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.util.Constants;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationController extends Controller {

    public Result createUserToken() {
        // To generate JWT Authentication Token

        ProjectLogger.log("Create User Token Api called", LoggerEnum.DEBUG.name());

        Http.Request request = request();
        String token = null;
        Map<String,Object> response =  new HashMap<>();

        try {
            new AuthenticationRequestValidator().validateCreateUserTokenRequest(request);

            Map<String,String[]> body = request.body().asFormUrlEncoded();
            String issuer = body.get(Constants.ISSUER)[0];
            String subject = body.get(Constants.SUBJECT)[0];
            token = new JwtAuthentication().createUserToken(issuer,subject);
//            token = new JwtAuthentication().createUserToken("NIIT","network-visualizer");
        }
        catch (ProjectCommonException e) {
            return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
        }
        response.put("user-token",token);
        Map<String,Object> res = new HashMap<>();
        res.put("Success",response);
        ProjectLogger.log("User Access Token generated successfully : '"+token+"'", LoggerEnum.DEBUG.name());

        return Results.ok(Json.toJson(res));
    }

}
