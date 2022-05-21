package com.golden.nettystudy.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: HuGoldWater
 * @description:
 */
public class Acceptor implements Runnable {

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        SocketChannel channel;
        try {
            channel = serverSocketChannel.accept();
            System.out.println(channel.getRemoteAddress() + "：收到一个客户端连接");
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ,new Handler(channel));
        } catch (Exception e) {

        }
    }
}
