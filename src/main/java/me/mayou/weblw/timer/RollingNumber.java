/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package me.mayou.weblw.timer;

import com.google.common.base.Preconditions;

/**
 * @author mayou.lyt
 */
class RollingNumber {

    private int roll;
    
    private volatile int count;
    
    private int round = 0;

    RollingNumber(int count, int roll){
        Preconditions.checkArgument(roll > 0);
        Preconditions.checkArgument(count > 0);
        this.roll = roll;
        this.count = count;
    }

    int increamentAndGet() {
       count = (count + 1) % roll;
       
       if(count == 0){
           ++round;
       }
       
       return count;
    }
    
    int get(){
        return count;
    }
    
    int getRound(){
        return round;
    }

}
