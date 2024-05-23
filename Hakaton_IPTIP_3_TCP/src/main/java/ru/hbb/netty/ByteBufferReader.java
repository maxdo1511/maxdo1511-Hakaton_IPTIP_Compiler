package ru.hbb.netty;

import io.netty.buffer.ByteBuf;

public class ByteBufferReader {

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

    public static String read_s(Object msg) {
        String data = "";
        if (msg == null) return data;
        ByteBuf buffer = ((ByteBuf) msg);
        while (buffer.readableBytes() > 0) {
            int i = buffer.readByte();
            if (i >= 0) {
                data = data + ((char) i);
            }else {
                data = data + decode_ru(buffer.readByte(), i);
            }
        }
        buffer.release();
        return data;
    }

    private static char decode_ru(int byte_, int external) {
        int abs = Math.abs(byte_);
        int delta = 0;
        if (external == -48) {
            delta = 992 - (-2 * (80 - abs));
        } else if (external == -47) {
            delta = 1000 - (40 - (2 * (128 - abs)));
        }
        abs = abs + delta;
        return (char) abs;
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
