package controllers.node.validator;


import org.commons.exception.ProjectCommonException;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http;
import play.mvc.Http.Request;

import java.io.File;

public class NodeRequestValidator {

    public void validateNodeRequest(Request request) throws ProjectCommonException {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        // check
        if(body == null || body.getFile("data")==null)
            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide parameter 'data' ");


//        String[] data = (String[]) body.asFormUrlEncoded().get("label");
//        if(data==null || data.length==0)
//            throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide parameter 'label' ");
//
//        if(data.length>1)
//            throw new ProjectCommonException(400,"Unique Value Missing","Please provide single value of 'label' ");
//
//        if(data[0]==null || data[0].isEmpty())
//                throw new ProjectCommonException(400,"Mandatory Parameter Missing","Please provide a value in the parameter 'label' ");

    }

}
