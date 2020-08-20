package com.xiepanpan.rpc.register;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20 0020
 * @Description:
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    public static ConcurrentHashMap<String,Object> registryMap;

    //用来存放class
    private List<String> classCache = new ArrayList<String>();
    
    public RegistryHandler() {
        scanClass("");
        doRegister();
    }

    /**
     * 把扫描到class实例化 放到map中 这就是注册过程
     * 注册的服务名字，叫接口名字
     * 约定优于配置
     */
    private void doRegister() {

    }

    /**
     * 扫描出所有的class
     * @param packageName
     */
    private void scanClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}