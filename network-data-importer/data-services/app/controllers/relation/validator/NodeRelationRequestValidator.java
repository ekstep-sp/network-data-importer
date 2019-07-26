package controllers.relation.validator;

import org.commons.exception.ProjectCommonException;
import play.mvc.Http;

public class NodeRelationRequestValidator {

    public void validateNodeRelationRequest(Http.Request request) throws ProjectCommonException {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        if(body == null || body.getFile("data")==null)
            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide parameter 'data' ");


//        String[] data = (String[]) body.asFormUrlEncoded().get("source-label");
//        if(data==null || data.length==0)
//            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide parameter 'source-label' ");
//
//        if(data.length>1)
//            throw new ProjectCommonException(400,"Unique Value Missing","Please provide single value of 'source-label' ");
//
//        if(data[0]==null || data[0].isEmpty())
//            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide a value in the parameter 'source-label' ");
//
//
//        data = (String[]) body.asFormUrlEncoded().get("target-label");
//        if(data==null || data.length==0)
//            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide parameter 'target-label' ");
//
//        if(data.length>1)
//            throw new ProjectCommonException(400,"Unique Value Missing","Please provide single value of 'target-label' ");
//
//        if(data[0]==null || data[0].isEmpty())
//            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide a value in the parameter 'target-label' ");

    }
}
