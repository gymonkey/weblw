/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import io.netty.util.Timer;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;
import me.mayou.weblw.packet.Packet;
import me.mayou.weblw.response.cmd.ResponseCmdChain;

import org.apache.commons.chain.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class CreateClientCmd extends ClientCmd {

    private HttpClient       client;

    private ResponseCmdChain chain;

    CreateClientCmd(ConcurrentMap<Integer, ClientConn> wsMap, HttpClient client, Timer timer){
        super(wsMap);
        this.client = Preconditions.checkNotNull(client);
        chain = new ResponseCmdChain(wsMap, timer);
    }

    @Override
    protected boolean isMyJob(String cmd) {
        return Objects.equal("create", new Gson().fromJson(cmd, Packet.class).getCmd());
    }

    @Override
    protected void execute0(Context ctx) throws Exception {
        client.connectWebsocket("/", new Handler<WebSocket>() {

            @Override
            public void handle(final WebSocket ws) {
                ws.dataHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer buf) {
                        chain.execute(buf.toString(), ws);
                    }
                });
            }
        });
    }

}
