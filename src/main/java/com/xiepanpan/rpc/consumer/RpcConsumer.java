package com.xiepanpan.rpc.consumer;

import com.xiepanpan.rpc.api.IRpcCalc;
import com.xiepanpan.rpc.api.IRpcHello;
import com.xiepanpan.rpc.consumer.proxy.RpcProxy;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20 0020
 * @Description:
 */
public class RpcConsumer {

    public static void main(String[] args) {
        IRpcHello rpcHello = RpcProxy.create(IRpcHello.class);
        System.out.println(rpcHello.hello("xp"));

        int a=8,b=2;
        IRpcCalc rpcCalc = RpcProxy.create(IRpcCalc.class);
        System.out.println("a+b="+rpcCalc.add(a,b));
        System.out.println("a-b="+rpcCalc.sub(a,b));
        System.out.println("a*b="+rpcCalc.mult(a,b));
        System.out.println("a/b="+rpcCalc.div(a,b));
    }
}