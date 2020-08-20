package com.xiepanpan.rpc.provider;

import com.xiepanpan.rpc.api.IRpcCalc;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20
 * @Description:
 */
public class RpcCalc implements IRpcCalc {
    public int add(int a, int b) {
         return a+b;
    }

    public int sub(int a, int b) {
        return a-b;
    }

    public int mult(int a, int b) {
        return a*b;
    }

    public int div(int a, int b) {
        return a/b;
    }
}
