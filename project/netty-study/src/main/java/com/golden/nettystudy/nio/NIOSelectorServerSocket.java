package com.golden.nettystudy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: HuGoldWater
 * @description:
 */
public class NIOSelectorServerSocket implements Runnable {

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    public NIOSelectorServerSocket(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        // 如果采用Selector模型，必须要设置非阻塞
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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
        if (key.isAcceptable()) {
            register(key);
        } else if (key.isReadable()) {
            read(key);
        } else if (key.isWritable()) {

        }
    }

    private void register(SelectionKey key) throws IOException {
        // 客户端连接
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        // 获取客户端连接
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        // 得到SocketChannel
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        System.out.println("Server Receive Msg:" + new String(byteBuffer.array()));
    }

    public static void main(String[] args) throws IOException {
        NIOSelectorServerSocket nioSelectorServerSocket = new NIOSelectorServerSocket(8888);
        new Thread(nioSelectorServerSocket).start();
    }

}
