package main.java.client.thread;

import main.java.agreement.MessageAgreement;
import main.java.client.SocketClient;

import java.io.*;
import java.util.Scanner;

public class writeThread extends Thread {
    //异步终止
    public volatile boolean exit = false;

    private OutputStream outputStream;

    private ObjectOutputStream objectOutputStream;

    public writeThread(OutputStream outputStream,ObjectOutputStream objectOutputStream){
        this.outputStream = outputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run(){
        //Scanner scanner = new Scanner(System.in);
        while(true){
            try {
                //减轻CPU 负载
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //异步终止
            if(exit){
                try {
                    objectOutputStream.close();
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                break;
            }
            //监听id==0的缓冲区
            while(SocketClient.queue.isEmtry(0)){};
            MessageAgreement message = SocketClient.queue.read(0);
            /*
            MessageAgreement message = new MessageAgreement();
            System.out.println("输入协议");
            System.out.print("协议类型：");
            message.setType(scanner.nextInt());
            System.out.print("发送方id：");
            message.setFromID(scanner.nextInt());
            System.out.print("接收方id数目：");
            message.setLenToID(scanner.nextInt());
            System.out.print("接收方id：");
            int [] test = new int[message.getLenToID()];
            for(int i = 0;i<message.getLenToID();i++){
                test[i] = (scanner.nextInt());
            }
            message.setToID(test);
            System.out.print("数据类型：");
            message.setDatatype(scanner.nextInt());
            System.out.print("数据：");
            scanner.nextLine();
            try {
                message.setData(scanner.nextLine().getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            try {
                objectOutputStream.writeObject(message);
                Thread.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            if(message.getType()==1){
                try {
                    Thread.sleep(10000);
                    System.out.println("客户端关闭写监听");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        System.out.println("写线程结束");
    }
}
