/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.conn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import me.mayou.weblw.packet.Packet;

import org.vertx.java.core.http.WebSocket;

/**
 * @author mayou.lyt
 */
public class ClientConn {

    private int               id;

    private WebSocket         ws;

    private volatile long     lastReadTime;

    private volatile long     lastWriteTime;

    private AtomicLong        packetId    = new AtomicLong();

    private long              heartbeatTimerId;

    private long              readIdleTimerId;

    private Map<Long, Packet> sendPackets = new HashMap<Long, Packet>();

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
        this.lastWriteTime = lastWriteTime;
    }

    public long getReadIdleTimerId() {
        return readIdleTimerId;
    }

    public void setReadIdleTimerId(long readIdleTimerId) {
        this.readIdleTimerId = readIdleTimerId;
    }

    public void putPacket(Packet packet) {
        sendPackets.put(packet.getId(), packet);
    }

    public Packet getPacket(long id) {
        return sendPackets.get(id);
    }

}
