/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class SendClientCmd extends ClientCmd {

    SendClientCmd(ConcurrentMap<Integer, ClientConn> wsMap){
        super(wsMap);
    }

    @Override
    protected boolean isMyJob(String cmd) {
        return Objects.equal("send", new Gson().fromJson(cmd, Packet.class).getCmd());
    }

    @Override
    protected void execute0(Context ctx) throws Exception {
        String cmd = (String) ctx.get(CMD);

        Packet packet = new Gson().fromJson(cmd, Packet.class);
        ClientConn conn = Preconditions.checkNotNull(wsMap.get(packet.getFid()));
        conn.getWs().writeTextFrame(cmd);
        conn.setLastOpTime(System.currentTimeMillis());

        logger.info("send msg: " + packet.getMsg() + " from conn " + packet.getFid() + " to conn " + packet.getTid());
    }

}
