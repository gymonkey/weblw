/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.chat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import me.mayou.weblw.dialog.Dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class ChatServer extends Verticle {

    private static final Logger                           logger = LoggerFactory.getLogger(ChatServer.class);

    private ConcurrentMap<Integer, ServerWebSocket> wsMap  = new ConcurrentHashMap<Integer, ServerWebSocket>();

    private AtomicInteger idGenerator = new AtomicInteger();
    
    @Override
    public void start() {
        vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {

            @Override
            public void handle(final ServerWebSocket ws) {
                ws.dataHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer buf) {
                        Dialog dialog = new Gson().fromJson(buf.toString(), Dialog.class);
                        logger.info("receive msg: " + dialog.getMsg() + " from conn " + dialog.getFid()
                                    + ", now send msg to conn " + dialog.getTid());

                        wsMap.get(dialog.getTid()).writeTextFrame(buf.toString());
                    }
                });
                
                int connId = idGenerator.incrementAndGet();
                Dialog dialog = new Dialog();
                dialog.setFid(connId);
                ws.writeTextFrame(new Gson().toJson(dialog));
                
                wsMap.put(connId, ws);
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
