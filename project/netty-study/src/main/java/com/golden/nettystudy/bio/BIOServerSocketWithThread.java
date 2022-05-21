package com.golden.nettystudy.bio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: HuGoldWater
 * @description:
 */
public class BIOServerSocketWithThread {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 阻塞体现在：1、连接阻塞；2、IO阻塞；
     * 对连接数量不高的场景使用
     * 使用场景：
     *      1、zookeeper的leader选举；
     *      2、nacos的注册地址信息同步
     * @param args
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(8888);
            System.out.println("启动服务，监听端口：8888");
            while (true){
                Socket socket = serverSocket.accept();// 连接阻塞
                System.out.println("客户端："+socket.getPort());
                executorService.submit(new SocketThread(socket));
            }
        }catch (Exception e){

        }finally {
            try {
                if (serverSocket != null){
                    serverSocket.close();
                }
            }catch (Exception e){

            }
        }
    }

}
