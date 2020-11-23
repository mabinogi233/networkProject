package main.java.client.gui;



import main.java.agreement.MessageAgreement;
import main.java.client.SocketClient;
import main.java.client.events.Events;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class Gui {
    private static Events events = new Events();
    //登录后获取ID
    private static int id = -1;
    private static String passwords = "";
    private static boolean isFile = false;
    private static File selectFile = null;
    //线程异步终止
    private static volatile boolean exit = false;

    public static void runGUI(){
        //创建登录界面
        JFrame frame = new JFrame();
        //标题
        frame.setTitle("登录界面");
        //窗体大小
        frame.setSize(300,200);
        //关闭时自动退出
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        //设置大小可变
        frame.setResizable(false);
        //设置居中显示
        frame.setLocationRelativeTo(null);

        JPanel s = new JPanel();
        JPanel z = new JPanel();
        JPanel x = new JPanel();

        JLabel user = new JLabel(" ID :");
        JLabel password =  new JLabel("密码:");

        JTextField userTF = new JTextField(10);
        JPasswordField pwdTF = new JPasswordField(10);

        JButton okB =  new JButton("登录");
        JButton noB =  new JButton("注册");

        okB.addActionListener(e->{
            boolean isLogin = events.login(Integer.parseInt(userTF.getText()),String.valueOf(pwdTF.getPassword()));
            System.out.println(isLogin);
            if(isLogin){
                frame.dispose();
                id = Integer.parseInt(userTF.getText());
                passwords = String.valueOf(pwdTF.getPassword());
                mainWindow();
            }else{
                JOptionPane.showMessageDialog(null, "登陆失败！请检测ID和密码", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });

        //点击注册生成注册界面
        noB.addActionListener(e1->{
            JFrame frame1 = new JFrame();
            //标题
            frame1.setTitle("注册界面");
            //窗体大小
            frame1.setSize(280,180);

            //设置大小可变
            frame1.setResizable(false);
            //设置居中显示
            frame1.setLocationRelativeTo(null);

            JPanel s1 = new JPanel();
            JPanel z1 = new JPanel();
            JPanel x1 = new JPanel();

            JLabel user1 = new JLabel("用户名称:");
            JLabel password1 =  new JLabel("输入密码:");

            JTextField userTF1 = new JTextField(10);
            JTextField pwdTF1 = new JTextField(10);

            JButton okB1 =  new JButton("点击注册");

            //布局习惯：窗体中布局面板，面板中布局组件
            frame1.setLayout(new BorderLayout());
            frame1.add(s1,BorderLayout.NORTH );
            frame1.add(z1, BorderLayout.CENTER );
            frame1.add(x1,BorderLayout.SOUTH);

            s1.add(user1);
            s1.add(userTF1);
            z1.add( password1);
            z1.add(pwdTF1);
            x1.add(okB1);

            //设置窗体可见性
            frame1.setVisible(true);
            okB1.addActionListener(e2-> {
                int isRegister = events.register(userTF1.getText(),pwdTF1.getText());
                if (isRegister>0) {
                    JOptionPane.showInternalMessageDialog(null, "您的ID为"+String.valueOf(isRegister),"注册成功", JOptionPane.INFORMATION_MESSAGE);
                    frame1.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "注册失败", "注册失败", JOptionPane.ERROR_MESSAGE);
                    frame1.dispose();
                }
            });
        });


        //布局习惯：窗体中布局面板，面板中布局组件
        frame.setLayout(new BorderLayout());
        frame.add(s,BorderLayout.NORTH );
        frame.add(z, BorderLayout.CENTER );
        frame.add(x,BorderLayout.SOUTH);

        s.add(user);
        s.add(userTF);
        z.add( password);
        z.add(pwdTF);
        x.add(okB);
        x.add(noB);


        //设置窗体可见性
        frame.setVisible(true);
    }


    public static void mainWindow(){
        JFrame jFrame = new JFrame();
        jFrame.setTitle("多人在线聊天System");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        jFrame.setResizable(false);

        //关闭事件
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                //终止readThread，writeThread，readQueueThread线程
                //结束消息队列的监听
                exit = true;
                //发送注销信息
                events.unlogin(id,passwords);
            }
        });


        //以下为发送窗口
        JPanel jPanel1 = new JPanel();
        JTextArea input = new JTextArea(20,50);
        input.setFont(new Font("标楷体", Font.BOLD, 14));
        jPanel1.add(new JScrollPane(input));


        //以下为接收窗口
        JPanel jPanel2 = new JPanel();
        JTextArea output = new JTextArea(20,50);
        output.setFont(new Font("标楷体", Font.BOLD, 14));
        output.append("输出不可修改\n");
        output.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(output);

        //读接受到消息队列线程开启
        Thread readQueueThread = new Thread(new Runnable(){
            public void run() {
                try {
                    while (true) {
                        if(exit){
                            return;
                        }
                        //一秒读取一次，减轻CPU负载
                        Thread.sleep(1000);
                        while (SocketClient.queue.isEmtry(11)) { };
                        MessageAgreement message = SocketClient.queue.read(11);
                        if (message != null && message.getData()!=null) {
                            System.out.println("接收到文字"+message.getDataTypeString());
                            if (message.getDataTypeString().equals("0")) {
                                output.append("接收到 id = " + Integer.toString(message.getFromID()));
                                output.append(" 发送的文字：\n  " +new String(message.getData(),"utf-8"));
                                output.append("\n");
                            }else if(message.getDataTypeString().equalsIgnoreCase("jpg") ||
                                    message.getDataTypeString().equalsIgnoreCase("png") ||
                                    message.getDataTypeString().equalsIgnoreCase("jpeg") ||
                                    message.getDataTypeString().equalsIgnoreCase("bmp")){
                                output.append("接收到 id = " + Integer.toString(message.getFromID()));
                                output.append(" 发送的图片:\n  图片已保存到D:\\recFile文件夹中" );
                                String fileName = Integer.toString(message.getFromID()) + "Rec" +
                                        Long.toString(System.currentTimeMillis()) +"."+ message.getDataTypeString();
                                File file = new File("D:\\recFile\\"+fileName);
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(message.getData());
                                output.append("\n");
                            }else{
                                output.append("接收到 id = " + Integer.toString(message.getFromID()));
                                output.append(" 发送的文件:\n  文件已保存到D:\\recFile文件夹中" );
                                output.append("\n");
                                String fileName = Integer.toString(message.getFromID()) + "Rec" +
                                        Long.toString(System.currentTimeMillis()) +"."+ message.getDataTypeString();
                                File file = new File("D:\\recFile\\"+fileName);
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(message.getData());
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        readQueueThread.start();

        //下拉框自动定位到最后一行
        jScrollPane.getViewport().setViewPosition(new Point(0, jScrollPane.getVerticalScrollBar().getMaximum()));
        jPanel2.add(jScrollPane);
        //按钮板
        JPanel jPanel4 = new JPanel();
        JButton jButton1 = new JButton("点击发送");
        //单击按钮执行的方法
        jButton1.addActionListener(e -> {
            //创建新的窗口
            JFrame frame = new JFrame("请选择发送的对象");
            JPanel panel = new JPanel();
            //根据在线人信息填充复选框
            List<Map<Integer,String>> listx = events.getOnlineUsers(id);
            int onlineUserConut = listx.size();

            panel.setLayout(new GridLayout(onlineUserConut+1,1,100,20));
            JCheckBox[] jCheckBoxes = new JCheckBox[onlineUserConut];



            int j = 0;
            for (Map<Integer,String> map:listx){
                for(Map.Entry<Integer,String> entry:map.entrySet()){
                    jCheckBoxes[j] = new JCheckBox(entry.getValue()+" "+Integer.toString(entry.getKey()));
                    panel.add(jCheckBoxes[j]);
                }
                j++;
            }

            //传输按钮
            JButton jButton = new JButton("发送");

            jButton.addActionListener(e1 ->{
                try {
                    byte[] bytes = null;
                    //读取 input 的值
                    String inputTest = input.getText();

                    //判断传输类型，生成byte
                    String type = "0";
                    if (isFile) {
                        if (selectFile == null) {
                            //不存在文件
                            throw new RuntimeException();
                        } else {
                            int index = selectFile.getName().lastIndexOf(".");
                            if (index == -1) {
                                type = "";
                            }
                            String houZhui = selectFile.getName().substring(index + 1);
                            if (houZhui.equalsIgnoreCase("jpg") ||
                                    houZhui.equalsIgnoreCase("png") ||
                                    houZhui.equalsIgnoreCase("jpeg") ||
                                    houZhui.equalsIgnoreCase("bmp")) {
                                type = houZhui;
                            } else {
                                type = houZhui;
                            }
                            bytes = Files.readAllBytes(selectFile.toPath());
                        }
                    }else {
                        bytes = inputTest.getBytes("utf-8");
                    }
                    //获取接收对象

                    List<Integer> toLDlist = new ArrayList<Integer>();
                    //读取 复选框的值
                    for (int i = 0; i < jCheckBoxes.length; i++) {
                        if (jCheckBoxes[i].isSelected()) {
                            //复选框内容：name id
                            toLDlist.add(Integer.parseInt(jCheckBoxes[i].getText().split(" ")[1]));
                        }
                        ;
                    }
                    //封装为数组
                    int[] toID = new int[toLDlist.size()];
                    int lentoID = 0;
                    for (Integer integer : toLDlist) {
                        toID[lentoID] = integer;
                        lentoID++;
                        System.out.println(integer);
                    }

                    if(lentoID<=0){
                        throw new RuntimeException();
                    }

                    //调用Event.transfor传输数据
                    events.transfor(id,lentoID,toID,type,bytes);

                    //关闭子jFrame
                    frame.dispose();
                    //output 显示 发送成功
                    output.append("发送");
                    if(type.equals("0")){
                        output.append("文字");
                    }else if(type.equalsIgnoreCase("jpg") ||
                            type.equalsIgnoreCase("png") ||
                            type.equalsIgnoreCase("jpeg") ||
                            type.equalsIgnoreCase("bmp")){
                        output.append("图片");
                    }else{
                        output.append("文件");
                    }
                    output.append("成功\n");
                    input.setText("");
                    jScrollPane.getViewport().setViewPosition(new Point(0, jScrollPane.getVerticalScrollBar().getMaximum()));
                    input.setEditable(true);
                    isFile = false;
                    selectFile = null;
                }catch (Exception e5){
                    e5.printStackTrace();
                    frame.dispose();
                    output.append("发送失败\n");
                    input.setText("");
                    input.setEditable(true);
                    isFile = false;
                    selectFile = null;
                }
            });

            panel.add(jButton);
            frame.add(new JScrollPane(panel));
            //设置在屏幕的位置
            frame.setLocation(100, 50);
            //窗体大小
            frame.setSize(200, 300);
            //显示窗体
            frame.setVisible(true);
        });

        jPanel4.add(jButton1);
        //添加文件和图片
        JButton jButton4 = new JButton("添加图片或文件");
        jButton4.addActionListener(e2 -> {
            JFileChooser jfc=new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
            jfc.showDialog(new JLabel(), "选择");
            File file=jfc.getSelectedFile();
            if(file!=null) {
                input.setText(file.getAbsolutePath());
                //锁定输入框，置文件位
                input.setEditable(false);
                isFile = true;
                selectFile = file;
            }
        });
        jPanel4.add(jButton4);


        //清除输入按钮
        JButton jButton5 = new JButton("清除输入");
        jButton5.addActionListener(e2 -> {
            input.setText("");
            input.setEditable(true);
            isFile = false;
        });
        jPanel4.add(jButton5);

        JPanel jPane3 = new JPanel();
        jPane3.setLayout(new BorderLayout());
        jPane3.add(jPanel1,BorderLayout.CENTER);
        jPane3.add(jPanel2,BorderLayout.NORTH);
        jPane3.add(jPanel4,BorderLayout.SOUTH);
        jFrame.add(jPane3);

        //以下为在线用户列表窗口
        JPanel jPanel3 = new JPanel();
        JTextArea online = new JTextArea(43,25);
        online.setEditable(false);
        //获取在线用户列表
        online.setText("*****************************\n");
        online.setFont(new Font("标楷体", Font.BOLD, 16));
        List<Map<Integer,String>> list = events.getOnlineUsers(id);

        if(list.size()>0) {
            for (Map<Integer, String> map : list) {
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    online.append("在线用户： " + entry.getValue());
                    online.append("\n  id：" + Integer.toString(entry.getKey()));
                    online.append("\n*****************************\n");
                }
            }
        }

        //刷新在线状态线程
        Thread flushThread = new Thread(new Runnable(){
            public void run() {
                try {
                    while (true) {
                        if (exit) {
                            return;
                        }
                        //减轻CPU负载
                        Thread.sleep(2000);
                        //获取在线用户列表
                        online.setText("*****************************\n");
                        online.setFont(new Font("标楷体", Font.BOLD, 16));
                        List<Map<Integer,String>> list = events.getOnlineUsers(id);

                        if(list.size()>0) {
                            for (Map<Integer, String> map : list) {
                                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                                    online.append("在线用户： " + entry.getValue());
                                    online.append("\n  id：" + Integer.toString(entry.getKey()));
                                    online.append("\n*****************************\n");
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        flushThread.start();


        jPanel3.add(new JScrollPane(online));
        jFrame.add(jPanel3, BorderLayout.EAST);

        jFrame.setSize(1000, 750);
        jFrame.setVisible(true);
    }
}
