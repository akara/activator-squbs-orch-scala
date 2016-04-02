/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.cube;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.paypal.myorg.akarasqt1serv.msgs.Event;
import com.paypal.myorg.akarasqt1serv.msgs.EventType;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.paypal.myorg.akarasqt1serv.cube.LyricsHelper.*;

public class Akarasqt1servActor extends AbstractActor{


    private ActorSystem system = context().system();

    private static int start = 99;
    private ActorRef target = null;

    public Akarasqt1servActor(){
        receive(ReceiveBuilder.
                        matchEquals(EventType.START, et -> {
                            target = sender();
                            nextEvent(start);
                        }).
                        match(TimedUp.class, t -> t.counter == 0, t -> {
                            target.tell(new Event(lyricEnd(start)), self());
                            target.tell(EventType.END, self());
                            context().stop(self());
                        }).
                        match(TimedUp.class, t -> {
                            nextEvent(t.counter);
                        })
                        .build()
        );
    }

    private void nextEvent(int n){
        target.tell(new Event(lyric(n)), self());
        Runnable run = () -> {
            self().tell(new TimedUp(n - 1), self());
        };
        system.scheduler().scheduleOnce(Duration.create(50 * n, TimeUnit.MILLISECONDS), run, system.dispatcher());
    }

    private static class TimedUp{
        int counter;
        TimedUp(int counter){
            this.counter = counter;
        }
    }
}
