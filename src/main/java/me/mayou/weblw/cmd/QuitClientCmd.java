/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;

import org.apache.commons.chain.Context;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;


/**
 * @author mayou.lyt
 */
public class QuitClientCmd extends ClientCmd {

    private AtomicBoolean isStart;
    
    QuitClientCmd(ConcurrentMap<Integer, ClientConn> wsMap, AtomicBoolean isStart){
        super(wsMap);
        this.isStart = Preconditions.checkNotNull(isStart);
    }
    
    @Override
    protected boolean isMyJob(String cmd) {
        return Objects.equal("quit", new Gson().fromJson(cmd, Packet.class).getCmd());
    }

    @Override
    protected void execute0(Context ctx) throws Exception {
        isStart.set(false);
        logger.info("client is soon to be shut down");
    }

}
