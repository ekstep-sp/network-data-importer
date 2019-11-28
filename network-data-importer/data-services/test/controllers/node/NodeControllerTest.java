package controllers.node;

import controllers.BaseControllerTest;
import org.commons.responsecode.ResponseCode;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class NodeControllerTest extends BaseControllerTest {



    @Test
    public void testUnknownRequestUrl() {

        File fileToTestCreateNode = new File(this.getClass().getClassLoader().getResource("relation/createRelation.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/unknown/create",
                        "POST",
                        createNodeRequest(fileToTestCreateNode));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 404);
    }


    @Test
    public void testCreateNodeSuccess() {

        File fileToTestCreateNode = new File(this.getClass().getClassLoader().getResource("node/createNode.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/relation/create",
                        "POST",
                        createNodeRequest(fileToTestCreateNode));
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testCreateNodeFailure() {
        Result result =
                performMultipartFormTest(
                        "/v1/node/create",
                        "POST",
                        createNodeRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.mandatoryParameterMissing.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }


    @Test
    public void testUpdateNodeSuccess() {

        File fileToTestUpdateNode = new File(this.getClass().getClassLoader().getResource("node/updateNode.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/node/update",
                        "PATCH",
                        createNodeRequest(fileToTestUpdateNode));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testUpdateNodeFailure() {
        Result result =
                performMultipartFormTest(
                        "/v1/node/update",
                        "PATCH",
                        createNodeRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.mandatoryParameterMissing.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }

    @Test
    public void testDeleteNodeSuccess() {

        File fileToTestDeleteNode = new File(this.getClass().getClassLoader().getResource("node/deleteNode.csv").getFile());

        Result result =
                performMultipartFormTest(
                        "/v1/node/delete",
                        "POST",
                        createNodeRequest(fileToTestDeleteNode));
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testDeleteNodeFailure() {
        Result result =
                performMultipartFormTest(
                        "/v1/node/delete",
                        "POST",
                        createNodeRequest(null));
//        assertEquals(getResponseCode(result), ResponseCode.mandatoryParameterMissing.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
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

}
