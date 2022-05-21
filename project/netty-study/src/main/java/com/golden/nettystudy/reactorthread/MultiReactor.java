package com.golden.nettystudy.reactorthread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: HuGoldWater
 * @description: 多线程Reactor模型
 */
public class MultiReactor implements Runnable{

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    public MultiReactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        // 如果采用Selector模型，必须要设置非阻塞
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new MultiAcceptor(selector, serverSocketChannel));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    // 说明有链接进来
                    dispatch(iterator.next());
                    // 移除当前就绪的事件，否则他会重复监听
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey key) throws IOException {
        Runnable runnable = (Runnable)key.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }

}
