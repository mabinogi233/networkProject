package main.java.client;

import main.java.client.events.Events;

import java.util.Map;

//测试Events类
public class test {
    public void mainx() {
        Events events = new Events();
        //测试
        /*
        System.out.println("注册test");
        int id = events.register("abc","abcd");
        System.out.println(id);
        System.out.println("登录test");
        System.out.println(events.login(id,"abcd"));
        System.out.println("在线用户列表：");
        for(Map<Integer,String> map:events.getOnlineUsers(id)){
            for(Map.Entry<Integer, String> entry : map.entrySet()){
                System.out.print(entry.getKey());
                System.out.print("  ");
                System.out.println(entry.getValue());
            }
        }

        System.out.println("登出test");
        events.unlogin(id,"abcd");
        System.out.println("结束测试");
        */
        //发送消息测试
        System.out.println("登录test a");
        System.out.println(events.login(1,"abc"));
    }

    public void mainy() {
        Events events = new Events();
        //测试
        /*
        System.out.println("注册test");
        int id = events.register("abc","abcd");
        System.out.println(id);
        System.out.println("登录test");
        System.out.println(events.login(id,"abcd"));
        System.out.println("在线用户列表：");
        for(Map<Integer,String> map:events.getOnlineUsers(id)){
            for(Map.Entry<Integer, String> entry : map.entrySet()){
                System.out.print(entry.getKey());
                System.out.print("  ");
                System.out.println(entry.getValue());
            }
        }

        System.out.println("登出test");
        events.unlogin(id,"abcd");
        System.out.println("结束测试");
        */
        //发送消息测试
        System.out.println("登录test b");
        System.out.println(events.login(2,"abc"));
        System.out.println("在线用户列表：");
        for(Map<Integer,String> map:events.getOnlineUsers(2)){
            for(Map.Entry<Integer, String> entry : map.entrySet()){
                System.out.print(entry.getKey());
                System.out.print("  ");
                System.out.println(entry.getValue());
            }
        }
        System.out.println("单播测试");
        try {
            events.transfor(2, 1, new int[]{1}, "0", "test one".getBytes("utf-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void mainz() {
        Events events = new Events();
        //测试
        /*
        System.out.println("注册test");
        int id = events.register("abc","abcd");
        System.out.println(id);
        System.out.println("登录test");
        System.out.println(events.login(id,"abcd"));
        System.out.println("在线用户列表：");
        for(Map<Integer,String> map:events.getOnlineUsers(id)){
            for(Map.Entry<Integer, String> entry : map.entrySet()){
                System.out.print(entry.getKey());
                System.out.print("  ");
                System.out.println(entry.getValue());
            }
        }

        System.out.println("登出test");
        events.unlogin(id,"abcd");
        System.out.println("结束测试");
        */
        //发送消息测试
        System.out.println("登录test c");
        System.out.println(events.login(3,"abc"));
        System.out.println("在线用户列表：");
        for(Map<Integer,String> map:events.getOnlineUsers(3)){
            for(Map.Entry<Integer, String> entry : map.entrySet()){
                System.out.print(entry.getKey());
                System.out.print("  ");
                System.out.println(entry.getValue());
            }
        }
        System.out.println("多播测试");
        try {
            events.transfor(3, 2, new int[]{1, 2}, "0", "test many".getBytes("utf-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
