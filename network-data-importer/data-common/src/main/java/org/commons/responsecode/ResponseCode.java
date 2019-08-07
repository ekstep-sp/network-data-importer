package org.commons.responsecode;

public enum ResponseCode {

    actorTimeoutError(408,"Actor Connection Error", "Service is not able to connect with actor."),
    internalServerError(500,"Internal Server Error", "Process failed,please try again later."),
    unAuthorized(401, "Unauthorised", "User Authentication required. Please provide the 'user-token'."),
    mandatoryParameterMissing(400,"Mandatory Parameter Missing","Please provide parameter {0}."),
    mandatoryParametersMissing(400,"Mandatory Parameters Missing","Please provide parameter '{0}' and {1}."),
    multipleFilesFoundException(400,"Multiple Files Found","Please provide a single File in parameter {0} "),
    databaseSessionCreationError(502,"Database Error","Unable to create a session with the Neo4j Driver."),
    databaseConnectionError(502,"Internal Server Error","Failed to establish connection with Neo4j database."),
    unsupportedActorOperation(400, "Unsupported Actor Operation", "Requested Actor Operation is not supported : {0}."),
    fileDataError(422,"Incorrect File Data","Please provide a file with proper data."),
    uniqueValueError(400, "Unique Value Missing", "Please provide single value of {0}."),
    invalidTokenCredentials(403, "Forbidden", "Please provide a valid {0}."),
    internalFileProcessingError(500,"Internal Server Error","Failed to read the file due to some internal error."),
    databaseUrlMissingError(400,"Missing Neo4j Database Credentials","Unable to get Neo4j database connection url"),
    databaseAuthenticationError(400, "Neo4j Connection Failed", "Failed to connect to Neo4j database due to incorrect credentials."),
    fileMissingError(500,"Internal Server Error","Unable to read the {0} file."),
    unsupportedFileTypeError(400, "Unsupported File Type", "Please provide file of type {0} or {1} "),
    expiredTokenError(403, "Forbidden", "User Token expired. Please create and provide a new {0}."),
    dataRetrievingError(500,"Get Data Error","Failed to read data from the database"),
    dataProcessingError(500,"Process Data Error","Failed to process data"),
    FileCreationError(500,"File Creation Error","Unable to Build file for the data");


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
