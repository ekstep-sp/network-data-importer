package controllers.relation.validator;

import org.commons.exception.ProjectCommonException;
import org.commons.responsecode.ResponseCode;
import play.mvc.Http;

public class NodeRelationRequestValidator {

    public void validateNodeRelationRequest(Http.Request request) throws ProjectCommonException {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        if(body == null || body.getFile("data")==null)
            throw new ProjectCommonException(ResponseCode.mandatoryParameterMissing,"data");
        if(body.getFiles().size()>1)
            throw new ProjectCommonException(ResponseCode.multipleFilesFoundException,"data");

    }
}
