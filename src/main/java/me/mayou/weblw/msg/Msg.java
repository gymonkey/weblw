/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.msg;

import me.mayou.weblw.conn.ServerConn;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author mayou.lyt
 */
public abstract class Msg implements Command{

    static final String CMD = "command";
    
    static final String PARAM = "param";
    
    static final String IN_CONN = "inConn";
    
    protected abstract boolean isMyJob(String msg);
    
    protected abstract void execute0(Context ctx, String cmd) throws Exception;
    
    @Override
    public boolean execute(Context context) throws Exception {
        Preconditions.checkNotNull(!Strings.isNullOrEmpty((String)context.get(CMD)));
        Preconditions.checkNotNull(context.get(PARAM));
        
        if(isMyJob((String)context.get(CMD))){
            execute0(context, (String)context.get(CMD));
            return PROCESSING_COMPLETE;
        }else{
            return CONTINUE_PROCESSING;
        }
    }

    
}
