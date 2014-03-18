/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.WebSocket;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateResponseCmd extends ResponseCmd {

    private final Vertx vertx;

    CreateResponseCmd(ConcurrentMap<Integer, ClientConn> conns, Vertx vertx){
        super(conns);
        this.vertx = Preconditions.checkNotNull(vertx);
    }

    @Override
    protected boolean isMyJob(String cmd) {
        Packet packet = new Gson().fromJson(cmd, Packet.class);
        return Objects.equal("create", packet.getCmd());
    }

    @Override
    protected void execute0(Context ctx) {
        Packet packet = new Gson().fromJson((String) ctx.get(PARAM), Packet.class);

        final ClientConn conn = new ClientConn();
        conn.setId(packet.getFid());
        conn.setWs(Preconditions.checkNotNull((WebSocket) ctx.get(IN_CONN)));
        conn.setLastReadTime(System.currentTimeMillis());
        conn.setLastWriteTime(System.currentTimeMillis());
        long heartbeatTimerId = vertx.setPeriodic(30000, new Handler<Long>() {

            @Override
            public void handle(Long timerId) {
                if (System.currentTimeMillis() - conn.getLastWriteTime() >= 30000) {
                    Packet packet = new Packet();
                    packet.setCmd("heartbeat");
                    packet.setId(conn.getNextPacketId());
                    packet.setFid(conn.getId());

                    conn.getWs().writeTextFrame(new Gson().toJson(packet));
                    
                    conn.setLastWriteTime(System.currentTimeMillis());
                }
            }
        });
        conn.setHeartbeatTimerId(heartbeatTimerId);
        long readIdleTimerId = vertx.setPeriodic(60000, new Handler<Long>() {

            @Override
            public void handle(Long timerId) {
                if (System.currentTimeMillis() - conn.getLastReadTime() >= 60000) {
                    conns.remove(conn.getId());
                    try {
                        conn.getWs().close();
                    } catch (IllegalStateException e) {
                        logger.info("conn " + conn.getId() + " has been close");
                    }
                    logger.info("conn " + conn.getId() + " has not receive from server, now we close it");
                    vertx.cancelTimer(conn.getReadIdleTimerId());
                    vertx.cancelTimer(conn.getHeartbeatTimerId());
                }
            }
        });
        conn.setReadIdleTimerId(readIdleTimerId);
        conn.getWs().closeHandler(new Handler<Void>() {

            @Override
            public void handle(Void event) {
                conns.remove(conn.getId());
                vertx.cancelTimer(conn.getHeartbeatTimerId());
                vertx.cancelTimer(conn.getReadIdleTimerId());
                logger.info("conn " + conn.getId() + " is now closed");
            }
        });

        conns.put(conn.getId(), conn);

        logger.info("conn " + conn.getId() + " is created");
    }
}
