/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import java.util.concurrent.ConcurrentMap;

import org.apache.commons.chain.Context;

import me.mayou.weblw.conn.ServerConn;
import me.mayou.weblw.packet.Packet;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class ImMsg extends Msg {

    private ConcurrentMap<Integer, ServerConn> conns;

    ImMsg(ConcurrentMap<Integer, ServerConn> conns){
        this.conns = Preconditions.checkNotNull(conns);
    }

    @Override
    protected boolean isMyJob(String msg) {
        Packet packet = new Gson().fromJson(msg, Packet.class);
        return Objects.equal("send", packet.getCmd());
    }

    @Override
    protected void execute0(Context ctx, String cmd) throws Exception {
        final Packet packet = new Gson().fromJson(cmd, Packet.class);

        ServerConn tConn = Preconditions.checkNotNull(conns.get(packet.getTid()));
        tConn.getWs().writeTextFrame(cmd);

        final ServerConn fConn = Preconditions.checkNotNull(conns.get(packet.getFid()));
        packet.setCmd("send_resp");
        fConn.getWs().writeTextFrame(new Gson().toJson(packet));

        logger.info("receive msg: " + packet.getMsg() + " from conn " + packet.getFid() + " to conn " + packet.getTid());
    }

}
