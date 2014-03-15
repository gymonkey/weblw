/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.mayou.weblw.conn.ServerConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;
import org.vertx.java.core.http.ServerWebSocket;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateMsg extends Msg {

    private AtomicInteger                      ids = new AtomicInteger(0);

    private Timer                              timer;

    private ConcurrentMap<Integer, ServerConn> conns;

    CreateMsg(ConcurrentMap<Integer, ServerConn> conns, Timer timer){
        this.timer = Preconditions.checkNotNull(timer);
        this.conns = Preconditions.checkNotNull(conns);
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

        Timeout timeout = timer.newTimeout(new TimerTask() {

            @Override
            public void run(Timeout timeout) throws Exception {
                logger.info("conn " + packet.getFid() + " has not receive any data in last 60s, now we close it");
                ws.close();
                conns.remove(packet.getFid());
            }
        }, 60, TimeUnit.SECONDS);

        ServerConn conn = new ServerConn();
        conn.setId(packet.getFid());
        conn.setWs(ws);
        conn.setTimeout(timeout);
        conns.put(packet.getFid(), conn);

        ws.writeTextFrame(new Gson().toJson(packet));
    }

}
