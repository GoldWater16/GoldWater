package com.golden.nettystudy.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: HuGoldWater
 * @description:
 */
public class NIOServerSocket {

    /**
     * NIO中的核心
     *      1、channel
     *      2、buffer
     *      3、selector
     *
     * @param args
     */
    public static void main(String[] args) {
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();){
            serverSocketChannel.configureBlocking(false);// 设置连接非阻塞
            serverSocketChannel.socket().bind(new InetSocketAddress(8888));
            while (true){
                // 是非阻塞的
                SocketChannel socketChannel = serverSocketChannel.accept();// 获得一个客户端连接
                socketChannel.configureBlocking(false);// 设置IO非阻塞
                if (socketChannel  != null){
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int read = socketChannel.read(byteBuffer);
                    if (read > 0){
                        System.out.println(new String(byteBuffer.array()));
                    }else {

                    }
                    System.out.println(new String(byteBuffer.array()));
                    Thread.sleep(1000);
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);
                }else {
                    Thread.sleep(1000);
                    System.out.println("连接未就绪");
                }
            }

        }catch (Exception e){

        }
    }

}
