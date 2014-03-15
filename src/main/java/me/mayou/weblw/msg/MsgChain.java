/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ServerConn;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.vertx.java.core.http.ServerWebSocket;

/**
 * @author mayou.lyt
 */
public class MsgChain {

    private ConcurrentMap<Integer, ServerConn> conns    = new ConcurrentHashMap<Integer, ServerConn>();

    private Timer                              timer    = new HashedWheelTimer();

    private Chain                              msgChain = new ChainBase();

    public MsgChain(){
        msgChain.addCommand(new ImMsg(conns, timer));
        msgChain.addCommand(new HeartbeatMsg(conns, timer));
        msgChain.addCommand(new CreateMsg(conns, timer));
    }

    @SuppressWarnings("unchecked")
    public void process(ServerWebSocket ws, String cmd) {
        Context ctx = new ContextBase();
        ctx.put(Msg.CMD, cmd);
        ctx.put(Msg.PARAM, conns);
        ctx.put(Msg.IN_CONN, ws);

        try {
            msgChain.execute(ctx);
        } catch (Exception e) {
        }
    }
}
