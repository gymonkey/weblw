/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.vertx.java.core.http.ServerWebSocket;

import me.mayou.weblw.packet.Packet;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class ImMsg extends Msg {

    @Override
    protected boolean isMyJob(String msg) {
        Packet packet = new Gson().fromJson(msg, Packet.class);
        return Objects.equal("send", packet.getCmd());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute0(Context ctx, String cmd) throws Exception {
        Map<Integer, ServerWebSocket> conns = (Map<Integer, ServerWebSocket>) ctx.get(Msg.PARAM);

        Packet packet = new Gson().fromJson(cmd, Packet.class);

        ServerWebSocket toConn = conns.get(packet.getTid());
        Preconditions.checkNotNull(toConn);

        toConn.writeTextFrame(cmd);

        logger.info("receive msg: " + packet.getMsg() + " from conn " + packet.getFid() + " to conn " + packet.getTid());
    }

}
