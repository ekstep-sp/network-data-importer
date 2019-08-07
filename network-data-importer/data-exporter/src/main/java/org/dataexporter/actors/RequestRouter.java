package org.dataexporter.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.BalancingPool;
import akka.routing.DefaultResizer;
import akka.routing.FromConfig;
import akka.routing.SmallestMailboxPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.request.Request;
import org.commons.responsecode.ResponseCode;
import org.reflections.Reflections;
import scala.collection.immutable.IndexedSeq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestRouter extends UntypedActor {

    private Map<String,ActorRef> actorCache;


    public RequestRouter() {

    }


    public static Props props() {
        ProjectLogger.log("Inside RequestRouter props() method : Creating New Actor", LoggerEnum.DEBUG.name());
        return Props.create(RequestRouter.class, RequestRouter::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        ProjectLogger.log("Inside RequestRouter class", LoggerEnum.DEBUG.name());

        if(message instanceof Request)
        {
            Request request = (Request) message;
            String operation = request.getOperation();
            ProjectLogger.log("Inside NodeManagementActor Receive", LoggerEnum.DEBUG.name());
            {
                String actorClassName = request.getActorClassName();
                ActorRef actorRef = actorCache.get(actorClassName);
                ProjectLogger.log("Request received. Calling "+actorClassName+" class to perform operation "+request.getOperation(), LoggerEnum.DEBUG.name());
                actorRef.tell(request,sender());
            }

        }

    }


    @Override
    public void preStart()  {
        ProjectLogger.log("Inside preStart of RequestRouter class", LoggerEnum.DEBUG.name());
        actorCache = new HashMap<>();
        try {
            Reflections reflections = new Reflections("org.dataexporter.actors");
            Set<Class<? extends UntypedActor>> actorClasses = reflections.getSubTypesOf(UntypedActor.class);
//            Config config = ConfigFactory.load();
//            System.out.println(config.getConfig("RequestRouter/NodeManagementActor"));
            for (Class<? extends UntypedActor> actorClass : actorClasses) {
                if(actorClass.getSimpleName().equals("RequestRouter"))
                    continue;
                Method method = actorClass.getMethod("props");
//                ActorRef actorRef = getContext().actorOf(new SmallestMailboxPool(2).props((Props) method.invoke(null)), actorClass.getSimpleName());
//                ActorRef actorRef = getContext().actorOf(new BalancingPool(5).props((Props)method.invoke(null)),actorClass.getSimpleName());
//                ActorRef actorRef = getContext().actorOf(Props.create(actorClass).withRouter(new FromConfig()), actorClass.getSimpleName());
//                ActorRef actorRef = getContext().actorOf((Props) method.invoke(null), actorClass.getSimpleName());
//                DefaultResizer defaultResizer = new DefaultResizer(2,10,1,0.2,0.5,0.2,10);
//                System.out.println(ConfigFactory.load());
                ActorRef actorRef = getContext().actorOf((Props.create(actorClass)).withRouter(new FromConfig()), actorClass.getSimpleName());

                actorCache.put(actorClass.getSimpleName(), actorRef);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
//        catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }

    }




    private void unSupportedOperation(Request request) {

        ProjectCommonException e = new ProjectCommonException(ResponseCode.unsupportedActorOperation, request.getOperation());
        ProjectLogger.log("Unsupported Operation",e, LoggerEnum.ERROR.name());
        sender().tell(e,self());
    }
}
