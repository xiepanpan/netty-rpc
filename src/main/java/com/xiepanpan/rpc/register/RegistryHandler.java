package com.xiepanpan.rpc.register;

import com.xiepanpan.rpc.core.msg.InvokerMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
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

    public static ConcurrentHashMap<String,Object> registryMap = new ConcurrentHashMap<String,Object>();

    //用来存放class
    private List<String> classCache = new ArrayList<String>();
    
    public RegistryHandler() {
        scanClass("com.xiepanpan.rpc.provider");
        doRegister();
    }

    /**
     * 把扫描到class实例化 放到map中 这就是注册过程
     * 注册的服务名字，叫接口名字
     * 约定优于配置
     */
    private void doRegister() {
        if (classCache.size()==0) {
            return;
        }
        for (String className:classCache) {
            try {
                Class<?> clazz = Class.forName(className);
                //服务名称
                Class<?> interfaces = clazz.getInterfaces()[0];
                registryMap.put(interfaces.getName(),clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 扫描出所有的class
     * @param packageName
     */
    private void scanClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for(File file: dir.listFiles()) {
            if (file.isDirectory()) {
                scanClass(packageName+"."+file.getName());
            } else {
                classCache.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerMsg request = (InvokerMsg) msg;

        //使用反射调用
        if (registryMap.containsKey(request.getClassName())) {
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}