package ru.hbb.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.hbb.service.AIService;

import java.io.IOException;

public class MainHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String s_data = ByteBufferReader.read(msg);
        String[] data = s_data.split(":", 2);
        if (data.length == 1) {
            try {
                AIService.getInstance().userConnect(data[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            AIService.getInstance().predict(data[0], data[1]);
        }catch (Exception e){
            try {
                AIService.getInstance().userDisconnect(data[0]);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

}
