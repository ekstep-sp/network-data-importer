package controllers.auth;

import controllers.BaseControllerTest;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


// Cannot be tested due to a bug in the Play testing framework
// Need to find a work around this error


@Ignore
public class AuthenticationControllerTest extends BaseControllerTest {

    private final String username = "NIIT";
    private final String password = "network-visualizer";

    @Test
    public void testUnknownRequestUrl() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/unknown/create",
                        "POST",
                        createAuthRequest(username,password));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 404);
    }


    @Test
    public void testCreateAuthTokenSuccess() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/auth/create",
                        "POST",
                        createAuthRequest(username,password));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }

    @Test
    public void testCreateAuthTokenWithWrongUsernameFailure() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/auth/create",
                        "POST",
                        createAuthRequest(username+"123",password));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 403);
    }

    @Test
    public void testCreateAuthTokenWithWrongPasswordFailure() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/auth/create",
                        "POST",
                        createAuthRequest(username,password+"123"));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 403);
    }


    @Test
    public void testCreateAuthTokenWithNoUsernameFailure() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/auth/create",
                        "POST",
                        createAuthRequest(null,password));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }

    @Test
    public void testCreateAuthTokenWithNoPasswordFailure() {


        Result result =
                performUrlencodedFormTest(
                        "/v1/auth/create",
                        "POST",
                        createAuthRequest(username,null));
//        assertEquals(getResponseCode(result), ResponseCode.apiNotFound.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 400);
    }


    private Map<String, String[]> createAuthRequest(String username, String password) {

        Map<String, String[]> requestMap = new HashMap<>();
        if(username != null || password != null) {
            if(username != null)
                requestMap.put("issuer",new String[]{username});
            if(password != null)
                requestMap.put("subject",new String[]{password});
        }
        else {
            return null;
        }
        return requestMap;
    }
}
