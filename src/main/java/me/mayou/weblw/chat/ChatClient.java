/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import me.mayou.weblw.cmd.ClientCmdChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.platform.Verticle;

/**
 * @author mayou.lyt
 */
public class ChatClient extends Verticle {

    private static final Logger               logger = LoggerFactory.getLogger(ChatClient.class);
    
    private AtomicBoolean isStart = new AtomicBoolean(true);

    @Override
    public void start() {
        final HttpClient client = vertx.createHttpClient().setHost("10.125.48.74").setPort(9999).setMaxPoolSize(3);
        final Verticle verticle = this;

        Thread t = new Thread() {

            @Override
            public void run() {
                ClientCmdChain chain = new ClientCmdChain(client, isStart);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (isStart.get()) {
                    try {
                        String cmdStr = reader.readLine();
                        chain.execute(cmdStr);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }

                client.close();
                verticle.getContainer().exit();
            }
        };
        t.start();
    }
}
