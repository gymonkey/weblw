/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import me.mayou.weblw.conn.ServerConn;
import me.mayou.weblw.packet.Packet;
import me.mayou.weblw.timer.Timer;

import org.apache.commons.chain.Context;
import org.vertx.java.core.http.ServerWebSocket;

import com.google.common.base.Objects;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateMsg extends Msg {
    
    private AtomicInteger ids = new AtomicInteger(0);
    
    @Override
    protected boolean isMyJob(String msg) {
        return Objects.equal("{cmd:\"create\"}", msg);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute0(Context ctx, String cmd) throws Exception {
        ServerWebSocket ws = (ServerWebSocket) ctx.get(Msg.IN_CONN);
        Map<Integer, ServerConn> conns = (Map<Integer, ServerConn>) ctx.get(Msg.PARAM);
        
        Packet packet = new Packet();
        packet.setCmd("create");
        packet.setFid(ids.incrementAndGet());
        
        ServerConn conn = new ServerConn();
        conn.setId(packet.getFid());
        conn.setWs(ws);
        conns.put(packet.getFid(), conn);
        
        ws.writeTextFrame(new Gson().toJson(packet));
        
        Timer.getInstance().add(conn);
    }

}
