package controllers.data;

import com.google.inject.Inject;
import controllers.BaseController;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.util.ActorOperation;
import org.commons.util.Constants;
import org.dataexporter.actors.data.DataManagementActor;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;


import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class DataController extends BaseController {

    private HttpExecutionContext httpExecutionContext;

    public DataController() {

    }

    @Inject
    public DataController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }


    public CompletionStage<Result> getAllData() {

        Request customRequest = new Request(ActorOperation.GET_ALL_DATA.getValue());
        customRequest.setRequestPath(request().path());

        return handleCustomRequest(customRequest,httpExecutionContext, DataManagementActor.class.getSimpleName())
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
                            result.withHeader("Access-Control-Allow-Origin","true");
//                            result.withHeader("content-disposition", "inline;filename=" + file.getName());
//                            result.withHeader("Content-Type", "text/csv");
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
