package ru.hbb.hakaton_3.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.context.ApplicationContext;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private final ApplicationContext applicationContext;

    public MessageHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        String[] data = BufferReader.read(msg).split(":", 2);
        TCPServer tcpServer = applicationContext.getBean(TCPServer.class);
        if (data[1].replaceAll(" ", "").equalsIgnoreCase("finalized")){
            tcpServer.getPayloadsFinalized().put(data[0], true);
        }else {
            tcpServer.getPayloads().put(data[0], tcpServer.getPayloads().get(data[0]) + "\n" + data[1]);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TCPServer tcpServer = applicationContext.getBean(TCPServer.class);
        tcpServer.setConnected(ctx.channel());
        System.out.println("Connected: " + ctx.channel().remoteAddress());
    }

}
