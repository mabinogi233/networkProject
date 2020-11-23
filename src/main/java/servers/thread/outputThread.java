package main.java.servers.thread;

import main.java.servers.SocketServer;
import main.java.agreement.MessageAgreement;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class outputThread extends Thread{
    //序号
    private int id;
    //输入流
    private OutputStream outputStream;
    // 输入流
    private ObjectOutputStream objectOutputStream;
    // 服务端socket
    private ServerSocket server;
    // 客户端socket
    private Socket client;

    public outputThread(int id,OutputStream outputStream, ObjectOutputStream objectOutputStream, ServerSocket server, Socket client) {
        this.id = id;
        this.outputStream = outputStream;
        this.objectOutputStream = objectOutputStream;
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //读取本id的消息队列
            boolean isClose = false;
            while(!isClose) {
                Thread.sleep(300);
                //等待消息队列不为空
                while(SocketServer.Queues.isEmtry(id)){}

                MessageAgreement message = SocketServer.Queues.read(id);
                //若是注销回复消息，且注销成功，则结束线程
                if(message.getType() == 4){
                    if (new String(message.getData(), "utf-8").equals("success")) {
                        isClose = true;
                    }
                }
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
            Thread.sleep(5000);
            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
