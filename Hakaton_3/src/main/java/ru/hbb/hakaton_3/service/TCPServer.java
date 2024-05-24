package ru.hbb.hakaton_3.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class TCPServer {

    @Getter
    @Setter
    private Channel connected;
    private EventLoopGroup connect_group;
    private EventLoopGroup handlers;
    private SocketChannel channel;
    private static final String DELIMITER = "$_";
    @Getter
    private Map<String, String> payloads = new HashMap<>();
    @Getter
    private Map<String, Boolean> payloadsFinalized = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    public String getSample(String name, String data) throws InterruptedException {
        if (connected == null) throw new RuntimeException("Server error! (code: 01)");
        if (payloads.containsKey(name)) throw new RuntimeException("Server error! (code: 02)");
        payloads.put(name, "");
        payloadsFinalized.put(name, false);
        String message = name + ":" + data;
        connected.writeAndFlush(message + DELIMITER);
        while (!payloadsFinalized.get(name)) {
            Thread.sleep(100);
        }
        String result = payloads.get(name);
        String[] code = result.split("```");
        if (code.length > 1) {
            result = code[1];
            //remove first line
            result = result.substring(result.indexOf("\n") + 1);
        }
        payloads.remove(name);
        payloadsFinalized.remove(name);
        return result;
    }

    @PostConstruct
    private void init(){
        Thread thread = new Thread(() -> {
            run("127.0.0.1", 8000, 5, 5);
        });
        thread.start();
    }

    private void run(String ip, int port, int connection_threads, int handler_threads) {
        connect_group = new NioEventLoopGroup(connection_threads);
        handlers = new NioEventLoopGroup(handler_threads);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(connect_group, handlers).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    channel = ch;
                    ByteBuf byteBuf = Unpooled.copiedBuffer(DELIMITER.getBytes());
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, byteBuf));
                    ch.pipeline().addLast(new MessageHandler(applicationContext), new StringEncoder(CharsetUtil.UTF_8), new StringDecoder(CharsetUtil.UTF_8));
                }
            });
            ChannelFuture future = b.bind(new InetSocketAddress(ip, port)).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            connect_group.shutdownGracefully();
            handlers.shutdownGracefully();
        }
    }

}
