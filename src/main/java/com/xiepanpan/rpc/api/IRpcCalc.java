package com.xiepanpan.rpc.api;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20
 * @Description:  计算接口
 */
public interface IRpcCalc {

    /**
     * 加
     * @param a
     * @param b
     * @return
     */
    public int add(int a,int b);

    /**
     * 减
     * @param a
     * @param b
     * @return
     */
    public int sub(int a,int b);

    /**
     * 乘
     * @param a
     * @param b
     * @return
     */
    public int mult(int a,int b);

    /**
     * 除
     * @param a
     * @param b
     * @return
     */
    public int div(int a,int b);
}
