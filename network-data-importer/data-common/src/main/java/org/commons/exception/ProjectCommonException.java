package org.commons.exception;


import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;

import java.text.MessageFormat;
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

    public ProjectCommonException(ResponseCode rsc)
    {
        this.responseCode = rsc.getResponseCode();
        this.code = rsc.getErrorCode();
        this.message = rsc.getErrorMessage();
    }


    public ProjectCommonException(ResponseCode rsc, String... arguments)
    {
        this.responseCode = rsc.getResponseCode();
        this.code = rsc.getErrorCode();
        this.message = MessageFormat.format(rsc.getErrorMessage(),arguments);
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
        Map<String,Object> errorDetails = new HashMap<>();

        errorDetails.put(Constants.STATUS,responseCode);
        errorDetails.put(Constants.ERROR_CODE,code.toUpperCase());
        errorDetails.put(Constants.ERROR_MESSAGE,message);

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        Date date = new Date();

        response.put(Constants.TIME_STAMP,formatter.format(date));
        response.put(Constants.ERROR_DETAILS,errorDetails);

        return response;
    }

}