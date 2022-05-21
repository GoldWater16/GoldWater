package com.golden.nettystudy.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: HuGoldWater
 * @description:
 */
public class BIO {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(8888);
            System.out.println("启动服务，监听端口：8888");
            while (true){
                Socket socket = serverSocket.accept();// 连接阻塞
                System.out.println("客户端："+socket.getPort());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientStr = bufferedReader.readLine();
                System.out.println("收到客户端发送的消息："+clientStr);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bufferedWriter.flush();
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
