/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.cmd;

import java.util.concurrent.ConcurrentMap;

import me.mayou.weblw.conn.ClientConn;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * @author mayou.lyt
 */
public abstract class ClientCmd implements Command {

    static final Logger                         logger = LoggerFactory.getLogger(ClientCmd.class);

    public static final String                  CMD    = "cmd";

    protected final ConcurrentMap<Integer, ClientConn> wsMap;

    public ClientCmd(ConcurrentMap<Integer, ClientConn> wsMap){
        this.wsMap = Preconditions.checkNotNull(wsMap);
    }

    protected abstract boolean isMyJob(String cmd);

    protected abstract void execute0(Context ctx) throws Exception;

    @Override
    public boolean execute(Context context) throws Exception {
        if (isMyJob((String) context.get(CMD))) {
            execute0(context);
            return PROCESSING_COMPLETE;
        } else {
            return CONTINUE_PROCESSING;
        }
    }

}
