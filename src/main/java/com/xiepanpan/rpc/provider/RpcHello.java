package com.xiepanpan.rpc.provider;

import com.xiepanpan.rpc.api.IRpcHello;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20 0020
 * @Description:
 */
public class RpcHello implements IRpcHello {
    public String hello(String name) {
        return "hello,"+name+"!";
    }
}