/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.msgs;

public class Event {
    private String msg;
    public Event(String msg){
        this.msg = msg;
    }

//    public String getMsg(){
//        return this.msg;
//    }

    public String getMessage(){
        return this.msg;
    }
}


