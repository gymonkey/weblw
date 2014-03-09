/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mayou.weblw.conn.ServerConn;

/**
 * @author mayou.lyt
 */
public class Timer {

    private static Logger                            logger = LoggerFactory.getLogger(Timer.class);

    private RollingNumber                            head   = new RollingNumber(0, 6);

    private RollingNumber                            tail   = new RollingNumber(5, 6);

    private List<ConcurrentMap<Integer, ServerConn>> ring   = new ArrayList<ConcurrentMap<Integer, ServerConn>>();

    private static final Timer                       timer  = new Timer();

    private Timer(){
        init();
    }

    public static Timer getInstance() {
        return timer;
    }

    public void add(ServerConn conn) {
        Preconditions.checkNotNull(conn);
        ring.get(tail.get()).put(conn.getId(), conn);
    }

    public void remove(ServerConn conn) {
        Preconditions.checkNotNull(conn);

        for (ConcurrentMap<Integer, ServerConn> conns : ring) {
            if (conns.containsKey(conn.getId())) {
                conns.remove(conn.getId());
            }
        }
    }

    private void init() {
        for(int idx = 0; idx < 6; ++idx){
            ring.add(new ConcurrentHashMap<Integer, ServerConn>());
        }
        
        Thread timerThread = new Thread("timer-thread") {

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10000L);
                        
                        ConcurrentMap<Integer, ServerConn> conns = ring.get(head.get());

                        if (conns != null && !conns.isEmpty()) {
                            for (ServerConn conn : conns.values()) {
                                logger.error("conn " + conn.getId() + " timeout! After " + head.getRound() * 60 + "s!");
                            }
                        } else {
                            logger.info("conns is null");
                            ;
                        }

                        head.increamentAndGet();
                        tail.increamentAndGet();
                    }
                } catch (InterruptedException e) {
                    // ignore
                }
            }

        };
        timerThread.start();
    }

}
