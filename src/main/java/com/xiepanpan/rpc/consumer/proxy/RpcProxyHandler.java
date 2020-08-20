package com.xiepanpan.rpc.consumer.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20
 * @Description:
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private Object result;
    public Object getResult(){
        return this.result;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.result = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exception is general");
    }
}
