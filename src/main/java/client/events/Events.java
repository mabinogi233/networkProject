package main.java.client.events;


import main.java.agreement.MessageAgreement;
import main.java.client.SocketClient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//事件管理，将客户端调用封装为协议，并解析返回协议，类似远程调用
public class Events {

    //超时时间，单位为毫秒
    private static int time = 10000;

    /**
     * 注册，注册成功则返回用户ID，注册失败则返回-
     * 超时则注册失败
     * @param name
     * @param password
     * @return
     */
    //-1为注册失败，注册成功返回用户的id
    public int register(String name,String password){
        try {
            byte[] data = (name + "," + password).getBytes("utf-8");
            MessageAgreement message = new MessageAgreement(2, 0, 1, new int[]{0}, 0, data);
            SocketClient.queue.write(0,message);
            //等待服务端处理，超时即认为注册失败
            long start = System.currentTimeMillis();
            while(SocketClient.queue.isEmtry(5)){
                //超时
                if(System.currentTimeMillis()-start > time){
                    break;
                }
            }
            if(SocketClient.queue.isEmtry(5)){
                //-1为注册失败，注册成功返回用户的id
                return -1;
            }else{
                MessageAgreement message1 = SocketClient.queue.read(5);
                //清空
                while(!SocketClient.queue.isEmtry(5)){
                    SocketClient.queue.read(5);
                }
                return message1.getToID()[0];
            }

        }catch (Exception e){
            e.printStackTrace();
            //-1为注册失败
            return -1;
        }
    }

    /**
     * 登录，成功返回true，失败返回false
     * @param id
     * @param password
     * @return
     */
    public boolean login(int id,String password){
        try {
            byte[] data = password.getBytes("utf-8");
            MessageAgreement message = new MessageAgreement(0, id, 1, new int[]{0}, 0, data);
            SocketClient.queue.write(0,message);
            //等待服务端处理，超时即认为注册失败
            long start = System.currentTimeMillis();
            while(SocketClient.queue.isEmtry(3)){
                //超时
                if(System.currentTimeMillis()-start > time){
                    break;
                }
            }
            if(SocketClient.queue.isEmtry(3)){
                return false;
            }else{
                MessageAgreement message1 = SocketClient.queue.read(3);
                //清空
                while(!SocketClient.queue.isEmtry(3)){
                    SocketClient.queue.read(3);
                }
                if(new String(message1.getData(),"utf-8").equals("success")){
                    return true;
                }
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            //-1为注册失败
            return false;
        }
    }

    /**
     * 注销登录，调用此函数会导致客户端sorket的关闭
     * @param id
     * @param password
     */
    public void unlogin(int id,String password){
        try {
            byte[] data = password.getBytes("utf-8");
            MessageAgreement message = new MessageAgreement(1, id, 1, new int[]{0}, 0, data);
            SocketClient.queue.write(0,message);
            //等待服务端处理
            long start = System.currentTimeMillis();
            while(SocketClient.queue.isEmtry(4)){
                //超时

                if(System.currentTimeMillis()-start > time){
                    break;
                }
            }
            if(SocketClient.queue.isEmtry(4)){
                return;
            }else{
                MessageAgreement message1 = SocketClient.queue.read(4);
                //清空
                while(!SocketClient.queue.isEmtry(4)){
                    SocketClient.queue.read(4);
                }
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 传输信息，发送单播或多播给指定的在线用户
     * @param id
     * @param lenToID
     * @param toID
     * @param dataType
     * @param data
     */
    public void transfor(int id,int lenToID,int [] toID,String dataType,byte[] data){
        MessageAgreement message = new MessageAgreement(10, id, lenToID, toID, 2, data);
        message.setDatatype(dataType);
        SocketClient.queue.write(0,message);
    }

    /**
     * 获取当前在线用户列表
     * @param id
     * @return
     */
    public List<Map<Integer,String>> getOnlineUsers(int id){
        MessageAgreement message = new MessageAgreement(12, id, 1, new int[]{0}, 0, "".getBytes());
        SocketClient.queue.write(0,message);
        List<Map<Integer,String>> list = new ArrayList<Map<Integer, String>>();
        //等待服务端处理
        long start = System.currentTimeMillis();
        while(SocketClient.queue.isEmtry(13)){
            //超时
            if(System.currentTimeMillis()-start > time){
                break;
            }
        }
        if(SocketClient.queue.isEmtry(13)){
            return null;
        }else{
            MessageAgreement message1 = SocketClient.queue.read(13);
            //清空
            while(!SocketClient.queue.isEmtry(13)){
                SocketClient.queue.read(13);
            }
            try {
                String[] user = new String(message1.getData(), "UTF-8").split(",");
                for (int i = 0; i < user.length; i++) {
                    Map<Integer,String> map = new HashMap<Integer, String>();
                    String[] one_user = user[i].split(" ");
                    map.put(Integer.parseInt(one_user[0]),one_user[1]);
                    list.add(map);
                }
                return list;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
