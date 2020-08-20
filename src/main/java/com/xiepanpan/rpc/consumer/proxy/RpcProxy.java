package com.xiepanpan.rpc.consumer.proxy;

import com.xiepanpan.rpc.core.msg.InvokerMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.Channel;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20
 * @Description:
 */
public class RpcProxy {
    public static <T> T create(Class<?> clazz) {
        MethodProxy methodProxy = new MethodProxy(clazz);
        Class<?>[] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, methodProxy);
        return result;
    }
}

class MethodProxy implements InvocationHandler {

    private Class<?> clazz;
    public MethodProxy(Class<?> clazz) {
        this.clazz = clazz;
    }


    //代理 调用IRpcHello接口中每一个方法的时候，实际就是发起一次网络请求
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //如果传进来的是一个已经实现的具体类 直接忽略
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this,args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            //如果传进来的是一个接口，我们就远程调用
            return rpcInvoke(method,args);
        }
        return null;
    }

    private Object rpcInvoke(Method method,Object[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        InvokerMsg msg = new InvokerMsg();

        msg.setClassName(this.clazz.getName());
        msg.setMethodName(method.getName());
        msg.setValues(args);
        msg.setParams(method.getParameterTypes());

        final RpcProxyHandler handler = new RpcProxyHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //处理拆包粘包问题
                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));

                            //处理编解码问题
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            //自己业务的处理
                            pipeline.addLast("handler",handler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().writeAndFlush(msg).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
        return handler.getResult();
    }
}