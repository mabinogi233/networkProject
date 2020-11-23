package main.java.client;

import main.java.agreement.MessageAgreement;
import main.java.client.gui.Gui;
import main.java.client.messageQueue.MessageQueueAdm;
import main.java.client.thread.readThread;
import main.java.client.thread.writeThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class SocketClient {

    public static MessageQueueAdm<Integer, MessageAgreement> queue;
    static {
        queue = new MessageQueueAdm<Integer, MessageAgreement>();
        //此为要发送的缓存 id == 0
        queue.putIfAbsent(0);
        //此为接收缓存 id == 协议type
        queue.putIfAbsent(3);
        queue.putIfAbsent(4);
        queue.putIfAbsent(5);
        queue.putIfAbsent(11);
        queue.putIfAbsent(13);
        queue.putIfAbsent(100);
    }

    public static void main(String [] args) throws IOException, ClassNotFoundException {

        // 获取客户端链接
        Socket client = new Socket(InetAddress.getLocalHost(), 65532);
        System.out.println("客户端是否连接成功:" + client.isConnected());

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());

        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

        readThread r = new readThread(client.getInputStream(),objectInputStream);
        writeThread w = new writeThread(client.getOutputStream(),objectOutputStream);
        r.start();
        w.start();

        Gui.runGUI();

        //主线程等待读写线程结束
        try {
            r.join();
            w.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("客户端关闭");
        client.close();
    }
}
