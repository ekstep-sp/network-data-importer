package controllers.node.validator;


import org.commons.exception.ProjectCommonException;
import org.commons.responsecode.ResponseCode;
import play.mvc.Http;
import play.mvc.Http.Request;


public class NodeRequestValidator {

    public void validateNodeRequest(Request request) throws ProjectCommonException {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        // check
        if(body == null || body.getFile("data")==null)
            throw new ProjectCommonException(ResponseCode.mandatoryParameterMissing,"data");
        if(body.getFiles().size()>1)
            throw new ProjectCommonException(ResponseCode.multipleFilesFoundException,"data");
    }

}
