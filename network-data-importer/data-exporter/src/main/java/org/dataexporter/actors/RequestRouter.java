package org.dataexporter.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.responsecode.ResponseCode;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestRouter extends AbstractActor {

    private Map<String,ActorRef> actorCache;

    public static Props props() {
        ProjectLogger.log("Inside RequestRouter props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(RequestRouter.class, RequestRouter::new);
    }

    public RequestRouter() {
        ProjectLogger.log("Inside RequestRouter class", LoggerEnum.DEBUG.name());

        receive(ReceiveBuilder
                .match(Request.class, request -> {
                    String actorClassName = request.getActorClassName();
                    ActorRef actorRef = actorCache.get(actorClassName);
                    ProjectLogger.log("Request received. Calling "+actorClassName+" class to perform operation "+request.getOperation(), LoggerEnum.DEBUG.name());
                    actorRef.tell(request,sender());
                })
                .matchAny(object -> {

                })
                .build());
    }

    @Override
    public void preStart()  {
        actorCache = new HashMap<>();
        try {
            Reflections reflections = new Reflections("org.dataexporter.actors");
            Set<Class<? extends AbstractActor>> actorClasses = reflections.getSubTypesOf(AbstractActor.class);
            for (Class<? extends AbstractActor> actorClass : actorClasses) {
                if(actorClass.getSimpleName().equals("RequestRouter"))
                    continue;
                Method method = actorClass.getMethod("props");
                ActorRef actorRef = getContext().actorOf((Props) method.invoke(null), actorClass.getSimpleName());
                actorCache.put(actorClass.getSimpleName(), actorRef);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }




    private void unSupportedOperation(Request request) {

        ProjectCommonException e = new ProjectCommonException(ResponseCode.unsupportedActorOperation, request.getOperation());
        ProjectLogger.log("Unsupported Operation",e, LoggerEnum.ERROR.name());
        sender().tell(e,self());
    }
}
