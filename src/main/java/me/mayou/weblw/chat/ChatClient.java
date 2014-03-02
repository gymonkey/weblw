/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

/**
 * @author mayou.lyt
 */
public class ChatClient extends Verticle {

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    
    @Override
    public void start() {
        vertx.createHttpClient().setHost("10.125.48.74").setPort(9999).connectWebsocket("/", new Handler<WebSocket>() {
            
            @Override
            public void handle(final WebSocket ws) {
                ws.dataHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer buf) {
                        logger.info("receive " + buf.toString());
                    }
                });
                ws.writeTextFrame("hello vertx");
                logger.info("send hello vertx");
            }
        }).exceptionHandler(new Handler<Throwable>() {

            @Override
            public void handle(Throwable t) {
                logger.error(t.getMessage());
            }
        });
    }
}
