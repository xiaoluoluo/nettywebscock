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

    /**
     *
     * @param request
     * @return
     */
    public static Client getClientByRequest(String request) {
        JSONObject json = new JSONObject(request);
        String username= (String) json.get("user");
        Client client = RedisMgr.getClient(username);
        return client;
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
        String clientData = getValue(userName);
        if(clientData == null || clientData.equals("")){
            //没有这个客户端 -- 说明这个用户没有注册过
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
        String key = user+ "roomInfo";

        List<String> allRoomInfo = getAllValue(key);
        List<RoomInfo> roomInfos = new ArrayList<>();
        if(allRoomInfo==null ||allRoomInfo.equals("")){
            return roomInfos;
        }
        for (String roomInfo : allRoomInfo){
            JSONObject json = new JSONObject(roomInfo);
            RoomInfo rinfo = new RoomInfo();
            long roomId = (Integer)json.get("roomId");
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

    /**学生获取这个用户的所有房间信息**/
    public static List<RoomInfo> studentGetAllRoomInfo(String studentName){
        String key = studentName+ "roomInfo";
        List<String> allRoomInfo = getAllValue(key);

        List<RoomInfo> roomInfos = new ArrayList<>();
        if(allRoomInfo==null ||allRoomInfo.isEmpty()){
            return roomInfos;
        }
        for (String roomInfo : allRoomInfo){
            JSONObject json = new JSONObject(roomInfo);
            RoomInfo rinfo = new RoomInfo();
            long roomId = (Long)json.get("roomId");
            String grade = (String)json.get("grade");
            String studentName0 = (String)json.get("studentName");
            String subject = (String)json.get("subject");
            String info = (String)json.get("info");
            rinfo.setRoomId(roomId);
            rinfo.setGrade(grade);
            rinfo.setStudentname(studentName0);
            rinfo.setSubject(subject);
            rinfo.setInfo(info);
            roomInfos.add(rinfo);
        }
        return roomInfos;
    }

    /**老师创建房间**/
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

    /**学生创建房间**/
    public static void createStudentClassRoom(String studentName,RoomInfo roomInfo){
        Jedis jedis = pool.getResource();
        String key = studentName+"roomInfo";
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

    //获取房间信息 user 如果是老师用用户名 如果是学生就是学生名字
    public static JSONObject getClassRoom(String user,long roomId){
        String key = user+ "roomInfo";
        List<String> allRoomInfo = getAllValue(key);
        JSONObject classRoomjson = null;
        for (String roomInfo : allRoomInfo) {
            JSONObject json = new JSONObject(roomInfo);
            long rId = (Long)json.get("roomId");
            if (roomId == rId){
                classRoomjson = json;
            }
        }
        return classRoomjson;
    }



    /**进入房间**/
    public static boolean hasClassRoom(String user,int roomId){
        String key = user+ "roomInfo";
        List<String> allRoomInfo = getAllValue(key);
        for (String roomInfo : allRoomInfo) {
            JSONObject json = new JSONObject(roomInfo);
            long rId = (Long)json.get("roomId");
            if (roomId == rId){
                return true;
            }
        }
        return false;
    }

    /**增加学生到学生库**/
    public static void addStudentTOPool(String studentName,JSONObject clientObject){
        String key = studentName+"studentInfo";
        String clientObjectString = clientObject.toString();
        setValue(key,clientObjectString);
    }

    /**增加学生到老师下面**/
    public static void addStudentTOTeacher(String teacherUser ,JSONObject clientObject){
        String key = teacherUser+"studentInfo";
        Jedis jedis = pool.getResource();
        String clientObjectString = clientObject.toString();
        jedis.lpush(key,clientObjectString);
        jedis.close();
    }

    /**老师获取自己的学生**/
    public static  List<String> getStudent(String teacherUser){
        String key = teacherUser+"studentInfo";
        List<String> allStudentInfo = getAllValue(key);
        return allStudentInfo;
    }

    /** 保存房间的消息**/
    public static void saveRoomMessage(String user,int roomId,String message){
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
