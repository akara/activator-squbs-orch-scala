/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.svc;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.japi.pf.ReceiveBuilder;
import com.paypal.myorg.akarasqt1serv.msgs.Event;
import com.paypal.myorg.akarasqt1serv.msgs.EventType;
import org.squbs.pattern.spray.japi.ChunkedMessageEndFactory;
import org.squbs.pattern.spray.japi.ContentTypeFactory;
import org.squbs.pattern.spray.japi.HttpEntityFactory;
import org.squbs.pattern.spray.japi.HttpResponseBuilder;
import spray.http.*;
import spray.routing.RequestContext;

public class Mediator extends AbstractActor {

    private static String streamEnd = "event: streamEnd\ndata: End of stream\n\n";

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public Mediator(RequestContext ctx) {

        context().actorSelection("/user/akarasqt1servcube/akarasqt1serv").tell(EventType.START, self());
        HttpResponseBuilder builder = new HttpResponseBuilder();
        builder.entity(HttpEntityFactory.create(ContentTypeFactory.create("text/event-stream"), toSSE("Starting")));
        HttpResponse responseStart = builder.build();
        ctx.responder().tell(new ChunkedResponseStart(responseStart), self());


        receive(ReceiveBuilder.
                        match(Event.class, e -> {
                            String eventMessage = toSSE(e.getMessage());
                            log.info('\n' + eventMessage);
                            ctx.responder().tell(MessageChunk.apply(eventMessage), self());
                        }).
                        matchEquals(EventType.END, e -> {
                            log.info('\n' + streamEnd);
                            ctx.responder().tell(MessageChunk.apply(streamEnd), self());
                            ctx.responder().tell(ChunkedMessageEndFactory.create(), self());
                            context().stop(self());
                        }).
                        match(Tcp.ConnectionClosed.class, ev -> {
                            log.warning("Connection closed, {}", ev);
                            context().stop(self());
                        }).
                        matchAny(o -> log.info("received unknown message"))
                        .build()
        );
    }

    private String toSSE(String msg) {
        return "event: lyric\ndata: " + msg.replace("\n", "\ndata: ") + "\n\n";
    }


}
