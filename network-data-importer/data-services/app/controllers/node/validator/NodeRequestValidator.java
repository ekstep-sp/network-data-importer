package controllers.node.validator;


import org.commons.exception.ProjectCommonException;
import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;
import play.mvc.Http;
import play.mvc.Http.Request;


public class NodeRequestValidator {

    public void validateNodeRequest(Request request) throws ProjectCommonException {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        // check
        if(body == null || body.getFile(Constants.DATA)==null)
            throw new ProjectCommonException(ResponseCode.mandatoryParameterMissing, Constants.DATA);
        if(body.getFiles().size()>1)
            throw new ProjectCommonException(ResponseCode.multipleFilesFoundException,Constants.DATA);
    }

}
