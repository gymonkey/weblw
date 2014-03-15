/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

import io.netty.util.Timer;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.vertx.java.core.http.WebSocket;

/**
 * @author mayou.lyt
 */
public class ResponseCmdChain {

    private Chain cmdChain = new ChainBase();

    public ResponseCmdChain(ConcurrentMap<Integer, ClientConn> conns, Timer timer){
        cmdChain.addCommand(new CreateResponseCmd(conns, timer));
        cmdChain.addCommand(new HeartbeatResponseCmd(conns, timer));
        cmdChain.addCommand(new ImResponseCmd(conns));
    }

    @SuppressWarnings("unchecked")
    public void execute(String cmd, WebSocket ws) {
        Context ctx = new ContextBase();
        ctx.put(ResponseCmd.PARAM, cmd);
        ctx.put(ResponseCmd.IN_CONN, ws);

        try {
            cmdChain.execute(ctx);
        } catch (Exception e) {
        }
    }

}
