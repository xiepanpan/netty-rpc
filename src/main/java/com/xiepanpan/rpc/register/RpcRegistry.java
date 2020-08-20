package com.xiepanpan.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiepanpan
 * @Date: 2020/8/20 0020
 * @Description:
 */
public class RpcRegistry {

    public static ConcurrentHashMap<String, Object> registryMap;

    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline channelPipeline = ch.pipeline();
                            //拆包粘包处理
                            /**入参有5个，分别解释如下
                             maxFrameLength:框架的最大长度。如果帧的长度大于此值，则将抛出TooLongFrameException
                             lengthFieldoffset:长度属性的偏移量。即对应的长度属性在整个消息数据中的位置
                             lengthFieldLength:长度字段的长度。如果长度属性是int型，那么这个值就是4 (long型就是8)
                             lengthAdjustment:要添加到长度属性值的补偿值
                             initialBytesToStrip:从解码帧中去除的第一个字节数
                             */

                            channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            channelPipeline.addLast(new LengthFieldPrepender(4));
                            //编码 解码(JDK 默认序列化)
                            channelPipeline.addLast("encoder",new ObjectEncoder());
                            channelPipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            channelPipeline.addLast(new RegistryHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();

            System.out.println("RPC Registry start listen at"+this.port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}