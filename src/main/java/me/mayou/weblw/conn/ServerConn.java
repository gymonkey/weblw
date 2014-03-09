/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.conn;

import org.vertx.java.core.http.ServerWebSocket;

/**
 * @author mayou.lyt
 */
public class ServerConn {

    private int id;
    
    private ServerWebSocket ws;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public ServerWebSocket getWs() {
        return ws;
    }
    
    public void setWs(ServerWebSocket ws) {
        this.ws = ws;
    }
    
}
