package controllers.auth.validator;

import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import play.mvc.Http;

import java.util.Map;

public class AuthenticationRequestValidator {

    public void validateCreateUserTokenRequest(Http.Request request) throws ProjectCommonException {

        try {
            Map<String, String[]> body = request.body().asFormUrlEncoded();
            if (body == null || body.isEmpty())
                throw new ProjectCommonException(ResponseCode.mandatoryParametersMissing, "issuer","subject");

            String[] issuer = body.get("issuer");
            if (issuer == null || issuer.length == 0 || issuer[0].trim().isEmpty())
                throw new ProjectCommonException(ResponseCode.mandatoryParameterMissing,"issuer");

            String[] subject = body.get("subject");
            if (subject == null || subject.length == 0 || subject[0].trim().isEmpty())
                throw new ProjectCommonException(ResponseCode.mandatoryParameterMissing,"subject");

            if (issuer.length > 1)
                throw new ProjectCommonException(ResponseCode.uniqueValueError,"issuer");

            if (subject.length > 1)
                throw new ProjectCommonException(ResponseCode.uniqueValueError,"subject");

        }
        catch (ProjectCommonException e)
        {
            ProjectLogger.log("Error while validating Authentication request :",e, LoggerEnum.ERROR.name());
            throw e;
        }
    }
}
