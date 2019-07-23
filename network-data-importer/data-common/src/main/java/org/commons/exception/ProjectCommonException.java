package org.commons.exception;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProjectCommonException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private int responseCode;
    private String requestPath;

    public ProjectCommonException() {

    }

    public ProjectCommonException(int responseCode, String code, String message) {
        this.code = code;
        this.message = message;
        this.responseCode = responseCode;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> response = new HashMap<>();
        Map<String,Object> errorMessage = new HashMap<>();

        errorMessage.put("Status",responseCode);
        errorMessage.put("Error Code",code);
        errorMessage.put("Error Message",message);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        response.put("Time Stamp",formatter.format(date));
        response.put("Error Message",errorMessage);

        return response;
    }
}