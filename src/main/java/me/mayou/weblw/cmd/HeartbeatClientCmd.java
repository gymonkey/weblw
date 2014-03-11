/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class HeartbeatClientCmd extends ClientCmd {

    Thread        heartbeatThread;

    AtomicBoolean isStart = new AtomicBoolean(false);

    HeartbeatClientCmd(final ConcurrentMap<Integer, ClientConn> wsMap){
        super(wsMap);

        heartbeatThread = new Thread("heartbeat") {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(40000L);

                        for (ClientConn conn : wsMap.values()) {
                            Packet packet = new Packet();
                            packet.setCmd("heartbeat");
                            packet.setFid(conn.getId());

                            conn.getWs().writeTextFrame(new Gson().toJson(packet));

                            logger.info("conn " + conn.getId() + " send heartbeat");
                        }

                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }

        };

        heartbeatThread.setDaemon(true);
    }

    @Override
    protected boolean isMyJob(String cmd) {
        return Objects.equal("heartbeat", new Gson().fromJson(cmd, Packet.class));
    }

    @Override
    protected void execute0(Context ctx) throws Exception {
        if(isStart.compareAndSet(false, true)){
            heartbeatThread.start();
        }
    }

}
