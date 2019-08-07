package controllers.node;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.google.inject.Inject;
import controllers.BaseController;
import controllers.node.validator.NodeRequestValidator;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.response.Response;
import org.commons.util.ActorOperation;
import org.commons.util.Constants;
import org.dataexporter.actors.node.NodeManagementActor;
import org.dataimporter.DataImportManagement;
import play.api.mvc.ResponseHeader;
import play.http.HttpEntity;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NodeController extends BaseController {
    // to handle all Node related requests

    private HttpExecutionContext httpExecutionContext;

    public NodeController() {

    }

    @Inject
    public NodeController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> createNode() {
        ProjectLogger.log("Create Node Api called", LoggerEnum.DEBUG.name());
//        File file = (File)((request().body().asMultipartFormData().getFile(Constants.DATA).getFile()));
//        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        byte[] bytesArray = new byte[(int) file.length()];
//        try {
//            inputStream.read(bytesArray);
//            return ok(new SerialBlob(bytesArray));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SerialException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return ok();

        return processNodeRequest(request(), ActorOperation.CREATE_NODE.getValue(),httpExecutionContext);
    }

    public CompletionStage<Result> updateNode() {

        ProjectLogger.log("Update Node Api called", LoggerEnum.DEBUG.name());
        return processNodeRequest(request(), ActorOperation.UPDATE_NODE.getValue(), httpExecutionContext);
    }

    public CompletionStage<Result> deleteNode() {

        ProjectLogger.log("Delete Node Api called", LoggerEnum.DEBUG.name());
            return processNodeRequest(request(), ActorOperation.DELETE_NODE.getValue(),httpExecutionContext);
    }

    private CompletionStage<Result> processNodeRequest(Http.Request request, String operation, HttpExecutionContext httpExecutionContext) throws ProjectCommonException {

        // To process any Node request generated
        Request customRequest;
        try {
            new NodeRequestValidator().validateNodeRequest(request);
            Http.MultipartFormData body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = body.getFile(Constants.DATA);
            Map<String, Object> nodeData = new DataImportManagement().importData(filePart.getFilename(), filePart.getFile());
            customRequest = new Request(operation);
            customRequest.setRequestPath(request().path());
            customRequest.setRequestParameter("data", nodeData);
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log("Error while validating node request : ",e, LoggerEnum.ERROR.name());
            return CompletableFuture.supplyAsync(() -> {
                        return Results.status(e.getResponseCode(), Json.toJson(e.toMap()));
                    },
                    httpExecutionContext.current());
        }
        return handleCustomRequest(customRequest,httpExecutionContext,NodeManagementActor.class.getSimpleName())
                .thenApply(response -> {
            Result result;
                    if(response instanceof Result)
                        result = (Result) response;
                    else
                        result = ok(Json.toJson((Map<String,Object>)response));
                    return result;
                });
    }


}
