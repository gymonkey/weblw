/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import java.util.concurrent.ConcurrentMap;

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

    HeartbeatMsg(ConcurrentMap<Integer, ServerConn> conns){
        this.conns = Preconditions.checkNotNull(conns);
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
        conn.setReadOpsTime(System.currentTimeMillis());
    }
}
