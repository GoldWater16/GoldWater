package com.golden.nettystudy.reactorthread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author: HuGoldWater
 * @description:
 */
public class MultiDispatchHandler implements Runnable {

    SocketChannel channel;

    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public MultiDispatchHandler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        processor();
    }

    private void processor(){
        executor.execute(new ReaderHandler(channel));
    }

    static class ReaderHandler implements Runnable{
        SocketChannel channel;

        public ReaderHandler(SocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+"=============");
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = 0, total = 0;
            String msg = "";
            try {
                do {
                    len = channel.read(byteBuffer);
                    if (len > 0) {
                        total += len;
                        msg += new String(byteBuffer.array());
                    }
                } while (len > byteBuffer.capacity());
                System.out.println("total:"+total);
                System.out.println(channel.getRemoteAddress() + ":Server receive Msg:" + msg);
            } catch (Exception e) {

            }finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
