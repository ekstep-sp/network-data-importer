package org.commons.responsecode;

public enum ResponseCode {

    unAuthorized("UNAUTHORIZED_USER", "You are not authorized."),
    OK(200);



    private int responseCode;
    private String errorCode;
    private String errorMessage;

    ResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    ResponseCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    ResponseCode( int responseCode, String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
