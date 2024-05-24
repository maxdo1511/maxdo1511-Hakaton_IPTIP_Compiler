package ru.hbb.hakaton_3.service;

import io.netty.buffer.ByteBuf;

public class BufferReader {

    public static String read(Object msg){
        String data = "";
        if (msg == null) return data;
        ByteBuf buffer = ((ByteBuf) msg).copy();
        while (buffer.readableBytes() > 0){
            data = data + ((char) buffer.readByte());
        }
        buffer.release();
        return data;
    }

    public static String getPacketInfo(Object msg) {
        String info = "";
        if (msg == null) throw new RuntimeException("No packet info");
        ByteBuf buffer = ((ByteBuf) msg).copy();
        char last_syb = '0';
        while (buffer.readableBytes() > 0 || last_syb != '-'){
            last_syb = (char) buffer.readByte();
            info = info + last_syb;
        }
        buffer.release();
        return info;
    }

}
