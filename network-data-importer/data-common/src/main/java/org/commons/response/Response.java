package org.commons.response;

import org.commons.util.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer successCount;
    private String operation;
    private Map<String,List<String>> errorData;
    private Map<String,Object> successData;

    public Response() {
        errorData = new HashMap<>();
        successData = new HashMap<>();
        successCount = 0;
        operation = "";
    }


    public void addErrorData(String name, int errorLine) {
        if(errorData.get(name)!=null) {
            errorData.get(name).add(Constants.DATA_LINE_NUMBER+" : "+errorLine);
        }
        else {
            List<String> list = new ArrayList<>();
            list.add(Constants.DATA_LINE_NUMBER+" : "+errorLine);
            errorData.put(name, list);
        }
    }


    public Map<String, List<String>> getErrorData() {
        return errorData;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public void addSuccess() {
        successCount++;
    }



    public Map<String, Object> getResponse() {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        Date date = new Date();
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put(Constants.TIME_STAMP,formatter.format(date));
        responseMap.put(Constants.SUCCESS_COUNT,successCount);

        if(errorData.size()>0)
        {
            responseMap.put(operation, Constants.PARTIAL_SUCCESS);
            responseMap.put(Constants.ERROR_DETAILS,errorData);
        }
        else
        {
            responseMap.put(operation,Constants.SUCCESS);
        }
        if(successData.size()>0)
        {
            responseMap.put(operation, Constants.SUCCESS);
            responseMap.put(Constants.SUCCESS_DETAILS,successData);
        }
        return responseMap;
    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String,Object> getSuccessData() {
        return successData;
    }

    public void setSuccessData(Map<String, Object> successData) {
        this.successData = successData;
    }

    public void addSuccessData(String key, Object data) {
        this.successData.put(key, data);
    }
}

