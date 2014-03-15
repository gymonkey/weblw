/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class HeartbeatResponseCmd extends ResponseCmd {

    HeartbeatResponseCmd(ConcurrentMap<Integer, ClientConn> conns){
        super(conns);
    }

    @Override
    protected boolean isMyJob(String cmd) {
        Packet packet = new Gson().fromJson(cmd, Packet.class);
        return Objects.equal("heartbeat", packet.getCmd());
    }

    @Override
    protected void execute0(Context ctx) {
        Packet packet = new Gson().fromJson((String) ctx.get(PARAM), Packet.class);
        logger.info("conn " + packet.getFid() + "receive heartbeat response from server");
    }

}
