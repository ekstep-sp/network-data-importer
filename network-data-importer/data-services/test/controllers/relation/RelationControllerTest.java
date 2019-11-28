package controllers.relation;

import controllers.BaseControllerTest;
import controllers.node.NodeControllerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Ignore
public class RelationControllerTest extends BaseControllerTest {


    // test create api

    @BeforeClass
    public static void createNodesForRelationship() {
//        final ActorSystem system = ActorSystem.create("KafkaProducerSystem");
//        final Materializer materializer = ActorMaterializer.create(system);
        Map<String, Object> requestMap = new HashMap<>();
        File fileToCreateNode = new File(NodeControllerTest.class.getClassLoader().getResource("node/createNode.csv").getFile());
        requestMap.put("data",fileToCreateNode);

//        Http.RequestBuilder request;
//
//            Source<ByteString, ?> src = FileIO.fromFile(fileToCreateNode);
//            Http.MultipartFormData.FilePart<Source<ByteString, ?>> fp = new Http.MultipartFormData.FilePart<>("data", "data.csv", "text/csv", src);
//
//            request = Helpers.fakeRequest()
//                    .headers(headerMap)
//                    .method("POST")
//                    .bodyMultipart(Arrays.asList(fp),materializer)
//                    .uri("/v1/relation/create");
        BaseControllerTest baseControllerTest = new BaseControllerTest();
        baseControllerTest.performMultipartFormTest(
                "/v1/node/create",
                "POST",
                requestMap);
    }


    @Test
    public void testUnknownRequestUrl() {

        File fileToTestCreateRelation = new File(this.getClass().getClassLoader().getResource("relation/createRelation.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/unknown/create",
                        "POST",
                        createRelationRequest(fileToTestCreateRelation));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 404);
    }

    @Test
    public void testCreateRelationSucess() {

        File fileToTestCreateRelation = new File(this.getClass().getClassLoader().getResource("relation/createRelation.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/relation/create",
                        "POST",
                        createRelationRequest(fileToTestCreateRelation));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testCreateRelationFailure() {


        Result result =
                performMultipartFormTest(
                        "/v1/relation/create",
                        "POST",
                        createRelationRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }


    @Test
    public void testUpdateRelationSuccess() {

        File fileToTestUpdateRelation = new File(this.getClass().getClassLoader().getResource("relation/updateRelation.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/relation/update",
                        "PATCH",
                        createRelationRequest(fileToTestUpdateRelation));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testUpdateRelationFailure() {


        Result result =
                performMultipartFormTest(
                        "/v1/relation/update",
                        "PATCH",
                        createRelationRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }

    @Test
    public void testDeleteRelationSuccess() {

        File fileToTestDeleteRelation = new File(this.getClass().getClassLoader().getResource("relation/deleteRelation.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/relation/delete",
                        "POST",
                        createRelationRequest(fileToTestDeleteRelation));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testDeleteRelationFailure() {


        Result result =
                performMultipartFormTest(
                        "/v1/relation/delete",
                        "POST",
                        createRelationRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }

    private Map<String, Object> createRelationRequest(
            File file) {

        Map<String, Object> requestMap = new HashMap<>();
        if(file!=null && file.isFile() && file.exists()) {
            requestMap.put("data",file);
            return requestMap;
        }
        else {
            return null;
        }
    }

    private Map<String, Object> createNodeRequest(
            File file) {

        Map<String, Object> requestMap = new HashMap<>();
        if(file!=null && file.isFile() && file.exists()) {
            requestMap.put("data",file);
            return requestMap;
        }
        else {
            return null;
        }
    }

    @AfterClass
    public static void deleteNodesCreatedForRelationship() {
//        final ActorSystem system = ActorSystem.create("KafkaProducerSystem");
//        final Materializer materializer = ActorMaterializer.create(system);
        Map<String, Object> requestMap = new HashMap<>();
        File fileToCreateNode = new File(NodeControllerTest.class.getClassLoader().getResource("node/deleteNode.csv").getFile());
        requestMap.put("data",fileToCreateNode);

//        Http.RequestBuilder request;
//
//            Source<ByteString, ?> src = FileIO.fromFile(fileToCreateNode);
//            Http.MultipartFormData.FilePart<Source<ByteString, ?>> fp = new Http.MultipartFormData.FilePart<>("data", "data.csv", "text/csv", src);
//
//            request = Helpers.fakeRequest()
//                    .headers(headerMap)
//                    .method("POST")
//                    .bodyMultipart(Arrays.asList(fp),materializer)
//                    .uri("/v1/relation/create");
        BaseControllerTest baseControllerTest = new BaseControllerTest();
        baseControllerTest.performMultipartFormTest(
                "/v1/node/delete",
                "POST",
                requestMap);
    }

}
