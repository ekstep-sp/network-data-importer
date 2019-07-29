package org.commons.response;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer successCount;
    private String operation;
    private Map<String,List<String>> errorData;

    public Response() {
        errorData = new HashMap<>();
        successCount = 0;
        operation = "";
    }


    public void addErrorData(String name, int errorLine) {
        if(errorData.get(name)!=null) {
            errorData.get(name).add("Data Line Number : "+errorLine);
        }
        else {
            List<String> list = new ArrayList<>();
            list.add("Data Line Number : "+errorLine);
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("Time Stamp",formatter.format(date));
        responseMap.put("Success Count",successCount);

        if(errorData.size()>0)
        {
            responseMap.put(operation, "Partial Success");
            responseMap.put("Error Details",errorData);

        }
        else
        {
            responseMap.put(operation, "Success");

        }
        return responseMap;
    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}

