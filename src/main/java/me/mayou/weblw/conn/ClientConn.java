/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.conn;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.WebSocket;

/**
 * @author mayou.lyt
 */
public class ClientConn {

    private static final Logger logger = LoggerFactory.getLogger(ClientConn.class);
    
    private int           id;

    private WebSocket     ws;

    private volatile long lastReadTime;
    
    private volatile long lastWriteTime;

    private AtomicLong    packetId = new AtomicLong();
    
    private long heartbeatTimerId;
    
    private long readIdleTimerId;

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

    public long getMaxPacketId() {
        return packetId.get();
    }

    public long getNextPacketId() {
        return packetId.incrementAndGet();
    }
    
    public long getHeartbeatTimerId() {
        return heartbeatTimerId;
    }
    
    public void setHeartbeatTimerId(long heartbeatTimerId) {
        this.heartbeatTimerId = heartbeatTimerId;
    }

    
    public long getLastReadTime() {
        return lastReadTime;
    }
    
    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }
    
    public long getLastWriteTime() {
        return lastWriteTime;
    }
    
    public void setLastWriteTime(long lastWriteTime) {
        logger.info("conn " + id + " set lastWriteTie " + lastWriteTime);
        this.lastWriteTime = lastWriteTime;
    }
    
    public long getReadIdleTimerId() {
        return readIdleTimerId;
    }
    
    public void setReadIdleTimerId(long readIdleTimerId) {
        this.readIdleTimerId = readIdleTimerId;
    }
    
}
