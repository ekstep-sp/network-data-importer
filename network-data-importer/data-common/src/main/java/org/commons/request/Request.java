package org.commons.request;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    private String operation;
    private String ts;
    private String requestPath;
    private Map<String,Object> request;

    public Request() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        ts = formatter.format(date);
        request = new HashMap<>();
    }

    public Request(String operation) {
        this();
        this.operation=operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public void setRequestParameter(String key, Object value) {
        this.request.put(key,value);
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }


    public Map<String, Object> requestToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Operation",operation);
        map.put("Time Stamp",ts);
        map.put("Requested Path",requestPath);
        map.put("Request Body",request);

        return map;
    }

}
