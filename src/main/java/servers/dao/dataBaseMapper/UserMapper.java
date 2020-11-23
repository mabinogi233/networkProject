package main.java.servers.dao.dataBaseMapper;

import main.java.servers.dao.entry.users;

import java.util.HashMap;
import java.util.Map;

public class UserMapper {
    //map应使用数据库，此处屏蔽数据库操作
    private static Map<Integer, users> map = new HashMap<Integer, users>();
    static {
        //初始化map 测试时使用
        users user = new users();
        user.setId(1);
        user.setPassword("abc");
        user.setName("test_a");
        map.put(1,user);

        users user1 = new users();
        user1.setId(2);
        user1.setPassword("abc");
        user1.setName("test_b");
        map.put(2,user1);

        users user2 = new users();
        user2.setId(3);
        user2.setPassword("abc");
        user2.setName("test_c");
        map.put(3,user2);


    }
    public users select(int id){
        return map.get(id);
    }
    public void delete(int id){
        map.remove(id);
    }
    public void update(users user){
        map.remove(user.getId());
        map.put(user.getId(),user);
    }
    public void insert(users user){
        map.putIfAbsent(user.getId(), user);
    }


}
