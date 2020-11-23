package main.java.servers.dao.dataBaseMapper;


import java.util.*;

//管理在线状态列表
public class stateMapper {
    private static Set<Integer> list = new HashSet<Integer>();

    public void add(int id){
        list.add(id);
    }
    public Set<Integer> get(){
        return list;
    }
    public void remove(int id){
        list.remove(id);
    }
}
