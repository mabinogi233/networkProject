package main.java.client.thread;

import main.java.agreement.MessageAgreement;
import main.java.client.SocketClient;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;

public class readThread extends Thread {
    //异常终止指示
    public volatile boolean exit = false;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;

    public readThread(InputStream inputStream,ObjectInputStream objectInputStream){
        this.inputStream = inputStream;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run(){
        while(true) {
            try {
                //减轻CPU 负载
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //异步终止
            if(exit){
                try {
                    objectInputStream.close();
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                break;
            }
            //接收信息
            boolean isClose = false;
            MessageAgreement message = null;
            while (true) {
                try {
                    message = (MessageAgreement) objectInputStream.readObject();
                    if(message.getType()==4 && new String(message.getData(),"utf-8").equals("success")){
                        isClose = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (message == null) {
                    continue;
                } else {
                    break;
                }
            }
            //加入指定的缓冲区
            SocketClient.queue.write(message.getType(),message);
            if (message.getType()==11){
                System.out.print("收到从 ");
                System.out.print(message.getFromID());
                System.out.print(" 发送的数据 ");
                try {
                    if(message.getData()!=null) {
                        String a = new String(message.getData(), "utf-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(message.getType());
            /*
            System.out.println("接收到一个协议");
            System.out.print("协议类型：");
            System.out.println(message.getType());
            System.out.print("发送方id：");
            System.out.println(message.getFromID());
            System.out.print("接收方id数目：");
            System.out.println(message.getLenToID());
            System.out.print("接收方id：");
            for(int i = 0;i<message.getLenToID();i++){
                System.out.print(message.getToID()[i]);
                System.out.print("  ");
            }
            System.out.print("数据类型：");
            System.out.println(message.getDatatype());
            System.out.print("数据：");
            System.out.println(message.getData().toString());
            */
            if(isClose){
                try {
                    Thread.sleep(10000);
                    System.out.println("客户端关闭读监听");
                    objectInputStream.close();
                    inputStream.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        System.out.println("读线程结束");
    }
}
