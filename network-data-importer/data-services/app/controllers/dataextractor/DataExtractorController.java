package controllers.dataextractor;

import com.google.inject.Inject;
import controllers.BaseController;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.util.ActorOperation;
import org.commons.util.Constants;
import org.dataexporter.actors.dataextractor.DataExtractorManagementActor;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;


import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class DataExtractorController extends BaseController {

    private HttpExecutionContext httpExecutionContext;

    public DataExtractorController() {

    }

    @Inject
    public DataExtractorController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> getAllData() {

        Request customRequest = new Request(ActorOperation.GET_ALL_DATA.getValue());
        customRequest.setRequestPath(request().path());

        return handleCustomRequest(customRequest,httpExecutionContext, DataExtractorManagementActor.class.getSimpleName())
                .thenApply(message -> {
                    Result result;
                    if(message instanceof Result)
                        result = (Result) message;
                    else {
                        Map<String,Object> response = (Map<String, Object>) message;
                        if((int)response.get(Constants.SUCCESS_COUNT) != 0) {
                            Object fileData = response.get("success details");
                            File file = (File) (((Map<String, Object>) fileData).get("file"));
                            ProjectLogger.log("Total File Size : " + (file.length() / 1024) + "kb", LoggerEnum.DEBUG.name());
                            result = ok(new File(file.getAbsolutePath()));
                        }
                        else
                        {
                            result = ok(Json.toJson(response));
                        }
                    }
                    return result;
                });


    }
}
