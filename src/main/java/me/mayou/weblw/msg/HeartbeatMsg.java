/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import me.mayou.weblw.conn.ServerConn;
import me.mayou.weblw.packet.Packet;

/**
 * @author mayou.lyt
 */
public class HeartbeatMsg extends Msg {

    private ConcurrentMap<Integer, ServerConn> conns;

    private Timer                              timer;

    HeartbeatMsg(ConcurrentMap<Integer, ServerConn> conns, Timer timer){
        this.conns = Preconditions.checkNotNull(conns);
        this.timer = Preconditions.checkNotNull(timer);
    }

    @Override
    protected boolean isMyJob(String msg) {
        Packet packet = new Gson().fromJson(msg, Packet.class);
        return Objects.equal("heartbeat", packet.getCmd());
    }
    
    @Override
    protected void execute0(Context ctx, String cmd) throws Exception {
        final Packet packet = new Gson().fromJson(cmd, Packet.class);
        final ServerConn conn = Preconditions.checkNotNull(conns.get(packet.getFid()));
        conn.getWs().writeTextFrame(cmd);

        conn.getTimeout().cancel();

        Timeout timeout = timer.newTimeout(new TimerTask() {

            @Override
            public void run(Timeout timeout) throws Exception {
                logger.info("conn " + packet.getFid() + " has not receive any data in last 60s, now we close it");
                conn.getWs().close();
                conns.remove(packet.getFid());
            }
        }, 60, TimeUnit.SECONDS);
        conn.setTimeout(timeout);
    }
}
