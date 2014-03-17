/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import me.mayou.weblw.conn.ServerConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.ServerWebSocket;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateMsg extends Msg {

    private final AtomicInteger                      ids = new AtomicInteger(0);

    private final ConcurrentMap<Integer, ServerConn> conns;

    private final Vertx                              vertx;

    CreateMsg(ConcurrentMap<Integer, ServerConn> conns, Vertx vertx){
        this.conns = Preconditions.checkNotNull(conns);
        this.vertx = Preconditions.checkNotNull(vertx);
    }

    @Override
    protected boolean isMyJob(String msg) {
        return Objects.equal("{cmd:\"create\"}", msg);
    }

    @Override
    protected void execute0(Context ctx, String cmd) throws Exception {
        final ServerWebSocket ws = (ServerWebSocket) ctx.get(Msg.IN_CONN);

        final Packet packet = new Packet();
        packet.setCmd("create");
        packet.setFid(ids.incrementAndGet());

        final ServerConn conn = new ServerConn();
        conn.setId(packet.getFid());
        conn.setWs(ws);
        conn.setReadOpsTime(System.currentTimeMillis());
        long timerId = vertx.setPeriodic(60000, new Handler<Long>() {

            @Override
            public void handle(Long timerId) {
                if (System.currentTimeMillis() - conn.getReadOpsTime() >= 60000) {
                    conns.remove(conn.getId());
                    try {
                        conn.getWs().close();
                    } catch (IllegalStateException e) {
                        logger.info("conn " + conn.getId() + " has been closed");
                    }
                    logger.info("we have not receive any data from conn " + conn.getId() + ", now we close it");
                    vertx.cancelTimer(conn.getTimerId());
                }
            }
        });
        conn.getWs().closeHandler(new Handler<Void>() {

            @Override
            public void handle(Void event) {
                vertx.cancelTimer(conn.getTimerId());
                logger.info("conn " + conn.getId() + " has been closed");
            }
        });
        conn.setTimerId(timerId);

        conns.put(packet.getFid(), conn);

        ws.writeTextFrame(new Gson().toJson(packet));
    }

}
