package ru.hbb.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.Getter;

public class NettyClient {

    @Getter
    private static NettyClient instance;
    private SocketChannel channel;
    private static final String ip = "localhost";
    private static final int port = 8080;
    public static final String DELIMITER = "$_";

    public NettyClient() {
        instance = this;
    }

    public void start() {
        run();
    }

    public void stop() {
        if (channel != null) {
            channel.close();
        }
    }

    public void sendMessage(String message) {
        message = message + DELIMITER;
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
        }
    }

    private void run() {
        new Thread(() -> {
            EventLoopGroup worker = new NioEventLoopGroup(5);
            try {
                Bootstrap b = new Bootstrap();
                b.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        channel = ch;
                        ByteBuf byteBuf = Unpooled.copiedBuffer(DELIMITER.getBytes());
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, byteBuf));
                        ch.pipeline().addLast(new MainHandler(), new StringEncoder(CharsetUtil.UTF_8), new StringDecoder(CharsetUtil.UTF_8));
                    }
                });
                ChannelFuture future = b.connect(ip, port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if (channel.isActive()) {
                    channel.close();
                }
                channel = null;
            } finally {
                worker.shutdownGracefully();
            }
        }).start();
    }

}
