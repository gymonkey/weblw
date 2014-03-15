/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.conn;

import io.netty.util.Timeout;

import org.vertx.java.core.http.WebSocket;

/**
 * @author mayou.lyt
 */
public class ClientConn {

    private int id;
    
    private WebSocket ws;
    
    private Timeout timeout;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public WebSocket getWs() {
        return ws;
    }
    
    public void setWs(WebSocket ws) {
        this.ws = ws;
    }

    public Timeout getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }
    
}
