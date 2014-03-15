/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.response.cmd;

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
public abstract class ResponseCmd implements Command {

    static final String                                PARAM   = "params";

    static final String                                IN_CONN = "inConn";

    protected static final Logger                      logger  = LoggerFactory.getLogger(ResponseCmd.class);

    protected final ConcurrentMap<Integer, ClientConn> conns;

    ResponseCmd(ConcurrentMap<Integer, ClientConn> conns){
        this.conns = Preconditions.checkNotNull(conns);
    }

    protected abstract boolean isMyJob(String cmd);

    protected abstract void execute0(Context ctx);

    @Override
    public boolean execute(Context context) throws Exception {
        String params = Preconditions.checkNotNull((String) context.get(PARAM));

        if (isMyJob(params)) {
            execute0(context);
            return PROCESSING_COMPLETE;
        } else {
            return CONTINUE_PROCESSING;
        }
    }

}
