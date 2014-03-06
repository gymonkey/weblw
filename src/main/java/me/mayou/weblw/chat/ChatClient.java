/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.cmd.Cmd;
import me.mayou.weblw.dialog.Dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author mayou.lyt
 */
public class ChatClient extends Verticle {

    private static final Logger               logger = LoggerFactory.getLogger(ChatClient.class);

    private ConcurrentMap<Integer, WebSocket> wsMap  = new ConcurrentHashMap<Integer, WebSocket>();

    @Override
    public void start() {
        final HttpClient client = vertx.createHttpClient().setHost("10.125.48.74").setPort(9999).setMaxPoolSize(2);
        final Verticle verticle = this;

        Thread t = new Thread() {

            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        String cmdStr = reader.readLine();
                        // Cmd cmd = new Gson().fromJson(cmdStr, Cmd.class);
                        // Preconditions.checkNotNull(Strings.nullToEmpty(cmd.getCmd()));
                        //
                        // if (Objects.equal("quit", cmd.getCmd())) {
                        // break;
                        // } else if (Objects.equal("send", cmd.getCmd())) {
                        // Preconditions.checkArgument(cmd.getFid() > 0);
                        // Preconditions.checkArgument(cmd.getTid() > 0);
                        // Preconditions.checkArgument(cmd.getFid() != cmd.getTid());
                        // Preconditions.checkNotNull(wsMap.get(cmd.getFid()));
                        // Preconditions.checkNotNull(Strings.emptyToNull(cmd.getMsg()));
                        //
                        // Dialog dialog = new Dialog();
                        // dialog.setFid(cmd.getFid());
                        // dialog.setTid(cmd.getTid());
                        // dialog.setMsg(cmd.getMsg());
                        //
                        // logger.info("send msg: " + dialog.getFid() + ", from conn " + dialog.getFid()
                        // + ", to conn " + dialog.getTid());
                        //
                        // wsMap.get(cmd.getFid()).writeTextFrame(cmdStr);
                        // } else if (Objects.equal("create", cmd.getCmd())) {
                        client.connectWebsocket("/", new Handler<WebSocket>() {

                            @Override
                            public void handle(final WebSocket ws) {
                                ws.dataHandler(new Handler<Buffer>() {

                                    @Override
                                    public void handle(Buffer buf) {
                                        Dialog dialog = new Gson().fromJson(buf.toString(), Dialog.class);
                                        Preconditions.checkArgument(dialog.getFid() > 0);

                                        if (Strings.isNullOrEmpty(dialog.getMsg())) {
                                            wsMap.putIfAbsent(dialog.getFid(), ws);
                                            logger.info("conn " + dialog.getFid() + " is create");
                                        } else {
                                            logger.info("conn " + dialog.getTid() + " receive msg: " + dialog.getMsg()
                                                        + " from conn " + dialog.getFid());
                                        }
                                    }
                                });
                                logger.error("socket");
                            }
                        });
                        // }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }

                // client.close();
                // verticle.getContainer().exit();
            }
        };
        t.start();
    }
}
