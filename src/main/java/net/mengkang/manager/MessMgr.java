package net.mengkang.manager;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.mengkang.dto.ClientStatus;
import net.mengkang.dto.UserStatus;
import net.mengkang.entity.Client;
import net.mengkang.entity.RoomInfo;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class MessMgr {

    //  消息分发器
    //0 老师登录和注册  注意 老师需要权限
    //1 老师创建房间 年级grade 学生名studentname 学科subjetct 知识点info  时间time
    //2 老师获取所有的房间列表
    //3 课件完成
    //4 获取所有的数据接口
    public static  void distribution(Channel channel, String request ){

        JSONObject json = new JSONObject(request);
        System.out.println(" 收到 " + request);
        int codeId = (Integer) json.get("code");

        if(codeId == 10103){
            // 创建房间
            String user= (String) json.get("user");
            String grade= (String) json.get("grade");
            String subject= (String) json.get("subject");
            String studentName= (String) json.get("studentName");
            String info= (String) json.get("info");
            Client client = RedisMgr.getClient(user);
            if (client == null){
                //你没有注册  请先注册再登录
                return ;
            }
            if(client.getStatus() != ClientStatus.login.getStatus()){
                //你没有登录 请先登录
            }
            if(client.getUserStatus()!= UserStatus.teacher.getStatus()){
                //你不是老师 你没有权限创建房间
            }
            RoomInfo rinfo = new RoomInfo();
            rinfo.setRoomId(Client.CONCURRENT_INTEGER.getAndDecrement());
            rinfo.setGrade(grade);
            rinfo.setSubject(subject);
            rinfo.setStudentname(studentName);
            rinfo.setInfo(info);
            RedisMgr.createClassRoom(user,rinfo);
        }

        if(codeId == 10104){
            //进入房间
            String user= (String) json.get("user");
            String roomId= (String) json.get("roomId");
            Client client = RedisMgr.getClient(user);
            if (client == null){
                //
                return ;
            }
            if(client.getStatus() != ClientStatus.login.getStatus()){
                //你没有登录 请先登录
            }
            if(client.getUserStatus()== UserStatus.teacher.getStatus()){
                //如果是老师 就让他进入
                return;
            }
            // 如果是学生 那么就判断他是否有这个课程

        }
        // 其他消息是广播消息
//        ClientMgr.sendMessToRoomClient(client,request);
    }

}
