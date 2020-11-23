package main.java.servers;

import main.java.agreement.MessageAgreement;
import main.java.servers.messageQueue.MessageQueueAdm;
import main.java.servers.thread.inputThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    //输入消息队列管理器和输出消息队列管理器
    public static MessageQueueAdm<Integer,MessageAgreement> Queues = new MessageQueueAdm<Integer,MessageAgreement>();

    public static void main(String [] args) {
        try {
            // 监听指定的端口
            int port = 65532;
            ServerSocket server = new ServerSocket(port);
            //作为这个TCP连接的编号，对每个连接，分配两个线程
            System.out.println("服务端启动成功，等待TCP连接");
            while (true) {
                Socket socket = server.accept();
                System.out.println("连接成功");
                //输入流
                InputStream inputStream = socket.getInputStream();
                System.out.println("输入流获取成功");
                //封装对象输入流
                ObjectInputStream objectInputStreams = new ObjectInputStream(inputStream);

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                System.out.println("启动读线程");
                //启动读线程，写线程在登录成功后创建
                new inputThread(inputStream, objectInputStreams, outputStream,objectOutputStream,server, socket).start();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}