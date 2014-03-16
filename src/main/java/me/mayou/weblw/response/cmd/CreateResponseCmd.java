/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;
import org.vertx.java.core.http.WebSocket;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateResponseCmd extends ResponseCmd {

    private Timer timer;

    CreateResponseCmd(ConcurrentMap<Integer, ClientConn> conns, Timer timer){
        super(conns);
        this.timer = Preconditions.checkNotNull(timer);
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
        conn.setLastOpTime(System.currentTimeMillis());
        conns.put(conn.getId(), conn);

        timer.newTimeout(new CheckTask(timer, conn), 30, TimeUnit.SECONDS);

        logger.info("conn " + conn.getId() + " is created");
    }

    private static class CheckTask implements TimerTask {

        private Timer      timer;

        private ClientConn conn;

        public CheckTask(Timer timer, ClientConn conn){
            this.timer = Preconditions.checkNotNull(timer);
            this.conn = Preconditions.checkNotNull(conn);
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (System.currentTimeMillis() - conn.getLastOpTime() >= 30 * 1000) {
                logger.info("conn " + conn.getId() + " send heartbeat to server");

                Packet sendPacket = new Packet();
                sendPacket.setCmd("heartbeat");
                sendPacket.setFid(conn.getId());

                conn.getWs().writeTextFrame(new Gson().toJson(sendPacket));
                conn.setLastOpTime(System.currentTimeMillis());
            }
            timer.newTimeout(this, 30, TimeUnit.SECONDS);
        }
    }

}
