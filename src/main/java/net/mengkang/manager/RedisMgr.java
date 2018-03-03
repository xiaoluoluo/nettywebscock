package net.mengkang.manager;


import net.mengkang.entity.Client;
import net.mengkang.entity.RoomInfo;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class RedisMgr {

    private static JedisPool pool = null;

    public static void startJedisPool() {
        if (pool != null) {
            return;
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大连接数
        jedisPoolConfig.setMaxTotal(100);
        //最大空闲连接
        jedisPoolConfig.setMaxIdle(50);
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379,1000*5);
    }

    /**把客户端保存到缓存中**/
    public static void saveClient(Client client){
        JSONObject clientObject = new JSONObject();
        clientObject.put("user",client.getUsername());
        clientObject.put("password",client.getPassword());
        clientObject.put("id",client.getClientId());
        clientObject.put("userStatus",client.getUserStatus());
        clientObject.put("status",client.getStatus());
        clientObject.put("time",System.currentTimeMillis());
        String clientString = clientObject.toString();
        RedisMgr.setValue(client.getUsername(),clientString);
    }

    /**根据用户名 获取客户端**/
    public static Client getClient(String userName){
        Jedis jedis = pool.getResource();
        String clientData = getValue(userName);
        if(clientData == null || clientData.equals("")){
            //没有这个客户端
            return null;
        }
        JSONObject json = new JSONObject(clientData);
        Client client = new Client();
        int id = (Integer)json.get("id");
        String user = (String)json.get("user");
        String password = (String)json.get("password");
        Integer userStatus = (Integer)json.get("userStatus");
        Integer status = (Integer)json.get("status");
        client.setClientId(id);
        client.setUsername(user);
        client.setPassword(password);
        client.setUserStatus(userStatus);
        client.setStatus(status);
        return client;
    }

    public static void setValue(String key,String value){
        Jedis jedis = pool.getResource();
        jedis.set(key,value);
        jedis.close();
    }

    public static String getValue(String key){
        Jedis jedis = pool.getResource();
        String data = jedis.get(key);
        jedis.close();
        return data;
    }

    /**获取这个用户的所有房间信息**/
    public static List<RoomInfo> getAllRoomInfo(String user){
        Jedis jedis = pool.getResource();
        String key = user+ "roomInfo";
        List<String> allRoomInfo = jedis.lrange(key,0,-1);
        jedis.close();
        List<RoomInfo> roomInfos = new ArrayList<>();
        if(allRoomInfo==null ||allRoomInfo.equals("")){
            return roomInfos;
        }
        for (String roomInfo : allRoomInfo){
            JSONObject json = new JSONObject(roomInfo);
            RoomInfo rinfo = new RoomInfo();
            int roomId = (Integer)json.get("roomId");
            String grade = (String)json.get("grade");
            String studentName = (String)json.get("studentName");
            String subject = (String)json.get("subject");
            String info = (String)json.get("info");
            rinfo.setRoomId(roomId);
            rinfo.setGrade(grade);
            rinfo.setStudentname(studentName);
            rinfo.setSubject(subject);
            rinfo.setInfo(info);
            roomInfos.add(rinfo);
        }
        return roomInfos;
    }

    /**创建房间**/
    public static void createClassRoom(String user,RoomInfo roomInfo){
        Jedis jedis = pool.getResource();
        String key = user+"roomInfo";

        JSONObject clientObject = new JSONObject();
        clientObject.put("roomId",roomInfo.getRoomId());
        clientObject.put("grade",roomInfo.getGrade());
        clientObject.put("studentName",roomInfo.getStudentname());
        clientObject.put("subject",roomInfo.getSubject());
        clientObject.put("info",roomInfo.getInfo());
        String clientObjectString = clientObject.toString();
        jedis.lpush(key,clientObjectString);
        jedis.close();
    }

    /** 保存房间的消息**/
    public static void saveClassRoomMessage(String user,int roomId,String message){
        Jedis jedis = pool.getResource();
        String key = user+roomId+"message";
        jedis.lpush(key,message);
        jedis.close();
    }


    public static  List<String> getAllValue(String key){
        Jedis jedis = pool.getResource();
        //0：表头，-1：表尾
        List<String> resultList = jedis.lrange(key, 0, -1);
        jedis.close();
        return  resultList;
    }

    // 结束的时候需要调用
    public static void end(){
        if(null != pool){
            pool.destroy();
            System.out.println("连接池关闭");
        }
    }
}
