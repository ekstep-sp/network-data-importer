package util;


import org.commons.util.Constants;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class ResponseHeaders extends Action.Simple {

    @Override
    public CompletionStage<Result>  call(final Http.Context ctx) {
        ctx.response().setHeader(Constants.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        ctx.response().setHeader(Constants.ACCESS_CONTROL_ALLOW_METHODS, "*");
        ctx.response().setHeader(Constants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        ctx.response().setHeader(Constants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            return delegate.call(ctx);
    }

}