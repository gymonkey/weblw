/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.chat;

import me.mayou.weblw.msg.MsgChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

/**
 * @author mayou.lyt
 */
public class ChatServer extends Verticle {

    private static final Logger                           logger = LoggerFactory.getLogger(ChatServer.class);
    
    private MsgChain chain;
    
    @Override
    public void start() {
        chain = new MsgChain(vertx);
        vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {

            @Override
            public void handle(final ServerWebSocket ws) {
                ws.dataHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer buf) {
                        chain.process(ws, buf.toString());
                    }
                });
                chain.process(ws, "{cmd:\"create\"}");
            }
        }).listen(9999, new Handler<AsyncResult<HttpServer>>() {

            @Override
            public void handle(AsyncResult<HttpServer> event) {
                logger.info(String.valueOf(event.succeeded()));
                if (!event.succeeded()) {
                    logger.error(event.cause().getMessage());
                }
            }

        });
    }

}
