/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package me.mayou.weblw.packet;

/**
 * @author mayou.lyt
 */
public class Packet {

    private String cmd;
    
    private int fid;
    
    private int tid;
    
    private String msg;
 
    public String getCmd() {
        return cmd;
    }
    
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    
    public int getFid() {
        return fid;
    }
    
    public void setFid(int fid) {
        this.fid = fid;
    }
    
    public int getTid() {
        return tid;
    }
    
    public void setTid(int tid) {
        this.tid = tid;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
