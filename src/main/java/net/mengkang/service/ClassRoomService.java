package net.mengkang.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.mengkang.dto.ClientStatus;
import net.mengkang.dto.UserStatus;
import net.mengkang.entity.Client;
import net.mengkang.entity.RoomInfo;
import net.mengkang.manager.MessMgr;
import net.mengkang.manager.RedisMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ClassRoomService extends BaseService{

    /**获取所有的房间信息**/
    public static void getAllRoomInfo(Channel channel, JSONObject json){
        // 获取所有的房间信息
        String user= (String) json.get("user");
        Client client = RedisMgr.getClient(user);
        if (client == null){
            //你没有注册  请先注册再登录
            String message = MessMgr.createMessage(4,"你没有注册  请先注册再登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessMgr.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        List<RoomInfo> allRoomInfo = RedisMgr.getAllRoomInfo(user);
        // 把所有的房间信息发给前端
        JSONArray allRoomInfoJson = new JSONArray();
        for (RoomInfo info : allRoomInfo){
            JSONObject infoJson = new JSONObject();
            infoJson.put("roomId",info.getRoomId());
            infoJson.put("grade",info.getGrade());
            infoJson.put("subject",info.getSubject());
            infoJson.put("studentName",info.getStudentname());
            infoJson.put("info",info.getInfo());
            allRoomInfoJson.put(infoJson);
        }
        JSONObject data = new JSONObject();
        data.put("code",10102);
        //1表示成功
        data.put("status",1);
        data.put("data",allRoomInfoJson.toString());
        String message = MessMgr.createMessage(0,"",0, data.toString());
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /**学生获取房间信息**/
    // 这个消息需要改
    public static void getStudentRoomInfo(Channel channel, JSONObject json){
        // 获取所有的房间信息
        String studentName= (String) json.get("studentName");
        List<RoomInfo> allRoomInfo = RedisMgr.studentGetAllRoomInfo(studentName);
        if (allRoomInfo.isEmpty()){
            JSONObject data = new JSONObject();
            data.put("code",10107);
            //1表示成功
            data.put("status",0);
            data.put("data","");
            String message = MessMgr.createMessage(0,"",0, data.toString());
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        // 把所有的房间信息发给前端
        JSONArray allRoomInfoJson = new JSONArray();
        for (RoomInfo info : allRoomInfo){
            JSONObject infoJson = new JSONObject();
            infoJson.put("roomId",info.getRoomId());
            infoJson.put("grade",info.getGrade());
            infoJson.put("subject",info.getSubject());
            infoJson.put("studentName",info.getStudentname());
            infoJson.put("info",info.getInfo());
            allRoomInfoJson.put(infoJson);
        }
        JSONObject data = new JSONObject();
        data.put("code",10107);
        //1表示成功
        data.put("status",1);
        data.put("data",allRoomInfoJson.toString());
        String message = MessMgr.createMessage(0,"",0, data.toString());
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /**创建房间消息--只有老师可以创建房间**/
    public static void createRoom(Channel channel, JSONObject json){
        String user= (String) json.get("user");
        String grade= (String) json.get("grade");
        String subject= (String) json.get("subject");
        String studentName= (String) json.get("studentName");
        String info= (String) json.get("info");
        Client client = RedisMgr.getClient(user);
        if (client == null){
            //你没有注册  请先注册再登录
            String message = MessMgr.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessMgr.createMessage(6,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getUserStatus()!= UserStatus.teacher.getStatus()){
            //你不是老师 你没有权限创建房间
            String message = MessMgr.createMessage(7,"你不是老师 你没有权限创建房间",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        RoomInfo rinfo = new RoomInfo();
        rinfo.setRoomId(Client.CONCURRENT_INTEGER.getAndIncrement());
        rinfo.setGrade(grade);
        rinfo.setSubject(subject);
        rinfo.setStudentname(studentName);
        rinfo.setInfo(info);
        RedisMgr.createClassRoom(user,rinfo);
        //老师创建房间信息的同时 也会给学生创建房间信息 他们的房间信息是一样的
        RedisMgr.createStudentClassRoom(studentName,rinfo);
        JSONObject data = new JSONObject();
        data.put("code",10103);
        //1表示成功
        data.put("status",1);
        String dataMessage =data.toString();
        String message = MessMgr.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /***进入房间**/
    public static void enterRoom(Channel channel, JSONObject json){
        //进入房间
        String user= (String) json.get("user");
        Integer roomId= (Integer) json.get("roomId");
        Client client = RedisMgr.getClient(user);
        if (client == null){
            String message = MessMgr.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessMgr.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getUserStatus()== UserStatus.teacher.getStatus()){
            //如果是老师 就让他进入
            RedisMgr.enterRoom(user,roomId);
            return;
        }
        if(client.getUserStatus()== UserStatus.student.getStatus()){
            // 如果是学生 那么就判断他是否有这个课程
            return;
        }
        JSONObject data = new JSONObject();
        data.put("code",10104);
        //1表示成功
        data.put("status",1);
        String dataMessage =data.toString();
        String message = MessMgr.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));

    }

}
