package controllers.dataextractor;

import controllers.BaseControllerTest;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;

import java.io.File;

import static org.junit.Assert.assertTrue;

@Ignore
public class DataExtractorControllerTest extends BaseControllerTest {


    @Test
    public void testExtractDataRequest() {


        Result result =
                performMultipartFormTest(
                        "/v1/data/read",
                        "GET",
                        null);
//        assertEquals(getResponseCode(result), ResponseCode.success.getErrorCode().toLowerCase());
        assertTrue(getResponseStatus(result) == 200);
    }


}
