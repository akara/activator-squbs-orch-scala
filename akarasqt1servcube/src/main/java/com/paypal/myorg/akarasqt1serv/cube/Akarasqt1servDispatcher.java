/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.cube;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.paypal.myorg.akarasqt1serv.msgs.EventType;

public class Akarasqt1servDispatcher extends AbstractActor{

    public Akarasqt1servDispatcher() {
        receive(ReceiveBuilder.
                        matchEquals(EventType.START, et -> {
                           context().actorOf(Props.create(Akarasqt1servActor.class)).forward(EventType.START, context());
                        })
                        .build()
        );
    }

}
