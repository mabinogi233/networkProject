package main.java.servers.agreement;

import main.java.agreement.MessageAgreement;
import main.java.servers.dao.dataBaseMapper.UserMapper;
import main.java.servers.dao.dataBaseMapper.stateMapper;
import main.java.servers.dao.entry.users;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;


//服务端消息解析
public class MessageParsing {

    private static stateMapper statemapper = new stateMapper();

    private static UserMapper usermapper = new UserMapper();


    /**
     * 解析消息，并处理消息，返回待转发的消息列表
     * @param message
     * @return
     * @throws UnsupportedEncodingException
     */
    public static List<MessageAgreement> Parsing(MessageAgreement message) throws UnsupportedEncodingException {
        int type = message.getType();
        //回执消息
        List<MessageAgreement> list = new ArrayList<MessageAgreement>();
        if(type==0) {
            //登录请求处理
            //获取信息
            int id = message.getFromID();
            String password = new String(message.getData(), "utf-8");
            //验证
            users trueUser = usermapper.select(id);
            //不存在此用户
            if (trueUser == null) {
                list.add(new MessageAgreement(3, 0, 1, new int[]{id}, 0, "noUser".getBytes("UTF-8")));
                return list;
            }
            //验证密码
            if (trueUser.getPassword().equals(password)) {
                //密码正确,登录成功，

                statemapper.add(id);

                // 封装回复协议
                list.add(new MessageAgreement(3, 0, 1, new int[]{id}, 0, "success".getBytes("UTF-8")));
                return list;
            } else {
                //密码错误
                list.add(new MessageAgreement(3, 0, 1, new int[]{id}, 0, "passwordError".getBytes("UTF-8")));
                return list;
            }
        }else if(type==1) {
            //注销请求处理
            //获取信息
            int id = message.getFromID();
            String password = new String(message.getData(), "utf-8");
            //验证
            users trueUser = usermapper.select(id);
            //不存在此用户
            if (trueUser == null) {
                list.add(new MessageAgreement(4, 0, 1, new int[]{id}, 0, "noUser".getBytes("UTF-8")));
                return list;
            }
            //注销成功

            statemapper.remove(id);
            // 封装回复协议
            list.add(new MessageAgreement(4, 0, 1, new int[]{id}, 0, "success".getBytes("UTF-8")));
            return list;

        } else if(type==2) {
            //注册请求处理
            //获取信息
            //随机生成一个唯一的ID
            int id;
            do {
                id = new Random().nextInt(10000000);
            } while (usermapper.select(id) != null);
            String data = new String(message.getData(), "utf-8");
            //用户名 密码，,分隔
            String[] datas = data.split(",");
            String password = datas[1];
            String name = datas[0];
            //验证
            users trueUser = usermapper.select(id);
            users user = new users();
            user.setId(id);
            user.setName(name);
            user.setPassword(password);
            usermapper.insert(user);
            list.add(new MessageAgreement(5, 0, 1, new int[]{id}, 0, "success".getBytes("UTF-8")));
            return list;

        } else if(type==10){
            //处理发送请求
            int id = message.getFromID();
            int toLen = message.getLenToID();
            int[] toID = message.getToID();
            //多点转单点
            for(int i=0;i<toLen;i++){
                list.add(new MessageAgreement(11, id, 1, new int[]{toID[i]}, message.getDatatype(),message.getData()));
            }
            return list;

        }else if(type==12){
            //处理获取在线用户列表
            //此时dataType为在线用户个数，data存储在线用户ID和姓名，每个用户通过,分隔，eg: A B,C D,E F
            StringBuffer data = new StringBuffer();
            Set<Integer> set = statemapper.get();
            for (Integer integer:set){
                data.append(integer);
                data.append(" ");
                data.append(usermapper.select(integer).getName());
                data.append(",");
            }
            data.deleteCharAt(data.length()-1);
            list.add(new MessageAgreement(13, 0, 1, new int[]{message.getFromID()}, set.size(),data.toString().getBytes("utf-8")));
            return list;
        }
        //协议解析静态扩展
        return null;
    }
}
