package net.mengkang.manager;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.mengkang.dto.ClientStatus;
import net.mengkang.dto.UserStatus;
import net.mengkang.entity.Client;
import net.mengkang.entity.RoomInfo;
import net.mengkang.service.MessageService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class MessMgr {

    public static void distribution(Channel channel, String request){
        JSONObject json = new JSONObject(request);
        int codeId = (Integer) json.get("code");
        switch (codeId){
            case 10100:{
                registMessage(channel,json);
                return;
            }
            case 10101:{
                loginMessage(channel,json);
                return;
            }
            case 10102:{
                getAllRoomInfo(channel,json);
                return;
            }
            case 10103:{
                createRoom(channel,json);
                return;
            }
            case 10104:{
                enterRoom(channel,json);
                return;
            }
            case 10105:{
                addStudent(channel,json);
                return;
            }
            case 10106:{
                getStudent(channel,json);
                return;
            }
        }
//        ClientMgr.sendMessToRoomClient(client,request);
    }

    /**注册消息**/
    private static void registMessage(Channel channel, JSONObject json){
        String username= (String) json.get("user");
        String password = (String) json.get("password");
        Integer userStatus = (Integer) json.get("userStatus");
        Client client0 = RedisMgr.getClient(username);
        if (client0 != null){
            String message = MessageService.createMessage(1,"登录名已经被注册 请换个账号注册",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        Client client = new Client();
        client.setUsername(username);
        client.setPassword(password);
        client.setStatus(ClientStatus.regist.getStatus());
        client.setUserStatus(userStatus);
        client.setClientId(Client.CONCURRENT_INTEGER.getAndIncrement());
        client.setRoomId(Client.CONCURRENT_INTEGER.getAndIncrement());
        //存数据库
        RedisMgr.saveClient(client);

        JSONObject data = new JSONObject();
        data.put("code",10100);
        //1表示成功
        data.put("status",1);
        String dataMessage =data.toString();
        String message = MessageService.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }
    /**登录消息**/
    private static void loginMessage(Channel channel, JSONObject json){
        //这个是登录协议
        String username= (String) json.get("user");
        String password = (String) json.get("password");
        // 到数据库获取客户端 然后匹配 密码
        Client client = RedisMgr.getClient(username);
        if (client == null){
            //你没有注册  请先注册再登录
            String message = MessageService.createMessage(2,"你没有注册请先注册再登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(!client.getPassword().equals(password)){
            //密码不对
            String message = MessageService.createMessage(3,"密码不对",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        client.setStatus(ClientStatus.login.getStatus());
        //更新客户端的状态
        RedisMgr.saveClient(client);
        JSONObject data = new JSONObject();
        data.put("code",10101);
        //1表示成功
        data.put("status",1);
        data.put("userStatus",client.getUserStatus());
        String dataMessage =data.toString();
        String message = MessageService.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }
    /**获取所有的房间信息**/
    public static void getAllRoomInfo(Channel channel, JSONObject json){
        // 获取所有的房间信息
        String user= (String) json.get("user");
        Client client = RedisMgr.getClient(user);
        if (client == null){
            //你没有注册  请先注册再登录
            String message = MessageService.createMessage(4,"你没有注册  请先注册再登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessageService.createMessage(5,"你没有登录 请先登录",0, "");
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
        String message = MessageService.createMessage(0,"",0, data.toString());
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
            String message = MessageService.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessageService.createMessage(6,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getUserStatus()!= UserStatus.teacher.getStatus()){
            //你不是老师 你没有权限创建房间
            String message = MessageService.createMessage(7,"你不是老师 你没有权限创建房间",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        RoomInfo rinfo = new RoomInfo();
        rinfo.setRoomId(Client.CONCURRENT_INTEGER.getAndDecrement());
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
        String message = MessageService.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /***进入房间**/
    public static void enterRoom(Channel channel, JSONObject json){
        //进入房间
        String user= (String) json.get("user");
        Integer roomId= (Integer) json.get("roomId");
        Client client = RedisMgr.getClient(user);
        if (client == null){
            String message = MessageService.createMessage(5,"你没有登录 请先登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(client.getStatus() != ClientStatus.login.getStatus()){
            //你没有登录 请先登录
            String message = MessageService.createMessage(5,"你没有登录 请先登录",0, "");
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
        String message = MessageService.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));

    }

    /**增加学生**/
    public static void addStudent(Channel channel, JSONObject json){

        String teacherUser= (String) json.get("teacherUser");
        String studentName= (String) json.get("stName");
        Integer studentPhone= (Integer) json.get("stPhone");
        Integer studentAddress= (Integer) json.get("stAddress");
        Integer studentGrade= (Integer) json.get("stGrade");
        Integer studentRemark= (Integer) json.get("stRemark");

        Integer studentId = Client.CONCURRENT_INTEGER.decrementAndGet();


        JSONObject studentObject = new JSONObject();
        studentObject.put("studentName",studentName);
        studentObject.put("studentPhone",studentPhone);
        studentObject.put("studentAddress",studentAddress);
        studentObject.put("studentGrade",studentGrade);
        studentObject.put("studentRemark",studentRemark);
        studentObject.put("studentId",studentId);

        RedisMgr.addStudent(teacherUser,studentObject);
        JSONObject data = new JSONObject();
        data.put("code",10105);
        //1表示成功
        data.put("status",1);
        String dataMessage =data.toString();
        String message = MessageService.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public static void getStudent(Channel channel, JSONObject json){
        String teacherUser= (String) json.get("teacherUser");
        List<String> allStudent = RedisMgr.getStudent(teacherUser);
        JSONArray allStudentJson = new JSONArray();
        for (String info : allStudent){
            JSONObject infoJson = new JSONObject();
            infoJson.put("info",info);
            allStudentJson.put(infoJson);
        }
        JSONObject data = new JSONObject();
        data.put("code",10106);
        //1表示成功
        data.put("status",1);
        data.put("data",allStudentJson.toString());
        String message = MessageService.createMessage(0,"",0, data.toString());
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }



}
