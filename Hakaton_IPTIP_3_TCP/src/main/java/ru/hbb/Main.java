package ru.hbb;

import ru.hbb.netty.NettyClient;
import ru.hbb.service.AIService;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        //create Singletons
        new AIService();
        new NettyClient();

        //Start client
        NettyClient.getInstance().start();
    }

}