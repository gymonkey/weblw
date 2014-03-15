/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class HeartbeatResponseCmd extends ResponseCmd {

    private Timer timer;

    HeartbeatResponseCmd(ConcurrentMap<Integer, ClientConn> conns, Timer timer){
        super(conns);
        this.timer = timer;
    }

    @Override
    protected boolean isMyJob(String cmd) {
        Packet packet = new Gson().fromJson(cmd, Packet.class);
        return Objects.equal("heartbeat", packet.getCmd());
    }

    @Override
    protected void execute0(Context ctx) {
        Packet packet = new Gson().fromJson((String) ctx.get(PARAM), Packet.class);
        logger.info("conn " + packet.getFid() + " receive heartbeat response from server");

        final ClientConn conn = Preconditions.checkNotNull(conns.get(packet.getFid()));
        Timeout timeout = timer.newTimeout(new TimerTask() {

            @Override
            public void run(Timeout timeout) throws Exception {
                logger.info("conn " + conn.getId() + " send heartbeat to server");

                Packet sendPacket = new Packet();
                sendPacket.setCmd("heartbeat");
                sendPacket.setFid(conn.getId());

                conn.getWs().writeTextFrame(new Gson().toJson(sendPacket));
            }
        }, 30, TimeUnit.SECONDS);
        conn.setTimeout(timeout);
    }

}
