package main.java.servers.thread;

import main.java.servers.SocketServer;
import main.java.agreement.MessageAgreement;
import main.java.servers.agreement.MessageParsing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

//接收消息的线程
public class inputThread extends Thread {
    //序号
    private int id;
    //输入流
    private InputStream inputStream;
    // 输入流
    private ObjectInputStream objectInputStream;

    private OutputStream outputStream;

    private ObjectOutputStream objectOutputStream;

    // 服务端socket
    private ServerSocket server;
    // 客户端socket
    private Socket client;

    public inputThread(InputStream inputStream, ObjectInputStream objectInputStream,
                       OutputStream outputStream,ObjectOutputStream objectOutputStream,
                       ServerSocket server, Socket client) {
        this.inputStream = inputStream;
        this.objectInputStream = objectInputStream;
        this.outputStream = outputStream;
        this.objectOutputStream = objectOutputStream;
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(500);
            boolean isClose = false;
            boolean isOpenWrite = false;
            //输入监听
            while (!isClose) {
                //读取协议对象
                MessageAgreement messageAgreement = (MessageAgreement) objectInputStream.readObject();

                //解析并生成转发消息
                List<MessageAgreement> relist = MessageParsing.Parsing(messageAgreement);


                //加入消息队列转发请求
                for (MessageAgreement message : relist) {
                    for (int i = 0; i < message.getLenToID(); i++) {
                        //若是登录回复消息
                        if(message.getType()==3){
                            if(new String(message.getData(), "utf-8").equals("success")){
                                //登录成功则开启id的消息队列，并启动消息队列监听写线程
                                System.out.println("登录成功");
                                SocketServer.Queues.putIfAbsent(message.getToID()[0]);
                                //写线程启动
                                if(!isOpenWrite) {
                                    System.out.println("启动写线程");
                                    new outputThread(message.getToID()[0], outputStream, objectOutputStream, server, client).start();
                                    isOpenWrite=true;
                                }
                            }
                        }
                        //注册则开启id消息队列
                        if(message.getType()==5) {
                            System.out.println("注册成功");
                            SocketServer.Queues.putIfAbsent(message.getToID()[0]);
                            //写线程启动
                            if(!isOpenWrite) {
                                System.out.println("启动写线程");
                                new outputThread(message.getToID()[0], outputStream, objectOutputStream, server, client).start();
                                isOpenWrite=true;
                            }
                        }
                        if(message.getToID()[i]!=0) {
                            if (SocketServer.Queues.isExist(message.getToID()[i])) {
                                System.out.println("转发请求");
                                SocketServer.Queues.write(message.getToID()[i], message);
                            } else {
                                //接收方在发送后接受前关闭了连接
                                try {
                                    SocketServer.Queues.write(message.getFromID(), new MessageAgreement(100, 0, 1, new int[]{message.getFromID()}, 0, "unlogin".getBytes("utf-8")));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //判断是否退出
                    if (message.getType() == 4) {
                        if (new String(message.getData(), "utf-8").equals("success")) {
                            isClose = true;
                            Thread.sleep(10000);
                            objectInputStream.close();
                            inputStream.close();
                        }
                    }
                }
            }
            //20s后关闭TCP链接
            Thread.sleep(20000);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
