/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import me.mayou.weblw.conn.ClientConn;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.vertx.java.core.http.HttpClient;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author mayou.lyt
 */
public class ClientCmdChain {

    private final Chain cmdChain = new ChainBase();

    public ClientCmdChain(HttpClient client, AtomicBoolean isStart){
        ConcurrentMap<Integer, ClientConn> wsMap = new ConcurrentHashMap<Integer, ClientConn>();
        
        Timer timer = new HashedWheelTimer();

        cmdChain.addCommand(new CreateClientCmd(wsMap, client, timer));
        cmdChain.addCommand(new QuitClientCmd(wsMap, isStart));
        cmdChain.addCommand(new SendClientCmd(wsMap));
    }
    
    @SuppressWarnings("unchecked")
    public void execute(String cmd) throws Exception{
        Context ctx = new ContextBase();
        ctx.put(ClientCmd.CMD, Preconditions.checkNotNull(Strings.emptyToNull(cmd)));
        cmdChain.execute(ctx);
    }

}
