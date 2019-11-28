package controllers;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.commons.database.Neo4jConnectionManager;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.neo4j.test.rule.TestDirectory;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import play.Application;
import play.core.j.JavaResultExtractor;
import play.http.ActionCreator;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import util.RequestInterceptor;
import play.test.Helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        RequestInterceptor.class,
        Neo4jConnectionManager.class
})
@SuppressStaticInitializationFor({})
@PowerMockIgnore("javax.management.*")
@Ignore
public class BaseControllerTest {


    public static Application app;
    public static Map<String, String[]> headerMap;
    public static TestDirectory testDirectory;
    final ActorSystem system = ActorSystem.create("networkVisualizerSystem");
    final Materializer materializer = ActorMaterializer.create(system);


    @BeforeClass
    public static void startApp() {
        app =  Helpers.fakeApplication();
        Helpers.start(app);
        testDirectory = TestDirectory.testDirectory();
        headerMap = new HashMap<>();
        headerMap.put("user-token", new String[] {"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuZXR3b3JrLXZpc3VhbGl6ZXIiLCJpc3MiOiJOSUlUIiwiaWF0IjoxNTY1NzU4ODI4fQ.0DkKIeEbwVjM-QeIciKnsx0Q8YVmcdWmUg6JaEshn2TrIwZU58KsBO8KeJ5C_z0vj0iyQ8C49esUO9nv-3w6jA"});
        headerMap.put("Content/Type",new String[] {"application/x-www-form-urlencoded"});


//        Neo4jConnectionManager.checkDatabaseConnectionStatus();
    }

    @Before
    public void mockNeo4jAndJwt() throws Exception {
//        JwtAuthentication jwtAuth = Mockito.mock(JwtAuthentication.class);
//        RequestInterceptor interceptor = Mockito.mock(RequestInterceptor.class);

//        RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);



//        PowerMockito.whenNewapp(JwtAuthentication.class).withNoArguments().thenReturn(jwtAuth);
//        Mockito.when(interceptor.createAction(Mockito.anyObject(),Mockito.anyObject())).thenReturn()
//            Mockito.doNothing().when(jwtAuth).verifyUserToken(Mockito.anyString());
//            PowerMockito.doNothing().when(JwtAuthentication.class,"verifyUserToken","verify this token");
    }

    public Result performMultipartFormTest(String url, String method, Map<String,Object> map) {

        Http.RequestBuilder request;

        if (map!=null) {
            Source<ByteString, ?> src = FileIO.fromFile((File) map.get("data"));
            Http.MultipartFormData.FilePart<Source<ByteString, ?>> fp = new Http.MultipartFormData.FilePart<>("data", "data.csv", "text/csv", src);
            request = Helpers.fakeRequest()
                    .headers(headerMap)
                    .method(method)
                    .bodyMultipart(Arrays.asList(fp),materializer)
                    .uri(url);
        } else {
            request = Helpers.fakeRequest()
                    .headers(headerMap)
                    .method(method)
                    .uri(url);
        }
        Result result = Helpers.route(app,request);
        return result;
    }

    public Result performUrlencodedFormTest(String url, String method, Map<String,String[]> map) {

        Http.RequestBuilder request;
        if (map!=null) {

            request = Helpers.fakeRequest()
                    .bodyFormArrayValues(map)
                    .headers(headerMap)
                    .method(method)
                    .uri(url);
        } else {
            request = Helpers.fakeRequest()
                    .headers(headerMap)
                    .method(method)
                    .uri(url);
        }
        Result result = Helpers.route(app,request);
        return result;
    }

//    public String mapTo(Map map) {
//        ObjectMapper mapperObj = new ObjectMapper();
//        String jsonResp = "";
//
//        if (map != null) {
//            try {
//                jsonResp = mapperObj.writeValueAsString(map);
//            } catch (IOException e) {
//                ProjectLogger.log(e.getMessage(),e, LoggerEnum.ERROR.name());
//            }
//        }
//        return jsonResp;
//    }

//    public String getResponseCode(Result result) {
//
//        byte[] body = result.body();
//
//        ObjectMapper om = new ObjectMapper();
//        final ObjectReader reader = om.reader();
//        JsonNode newNode = null;
//        try {
//            newNode = reader.readTree(new ByteArrayInputStream(body));
//        } catch (JsonProcessingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//        String responseStr = Helpers.contentAsString(result);
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            ProjectCommonException exception = mapper.readValue(responseStr, ProjectCommonException.class);
//            if (exception != null) {
//                return exception.getCode().toLowerCase();
//            }
//        } catch (Exception e) {
//            ProjectLogger.log(
//                    "BaseControllerTest:getResponseCode: Exception occurred with error message = "
//                            + e.getMessage(),
//                    LoggerEnum.ERROR.name());
//        }
//        return "";
//    }

    public int getResponseStatus(Result result) {
        return result.status();
    }

    @AfterClass
    public static void destroyTestDatabase()
    {

    }
}
