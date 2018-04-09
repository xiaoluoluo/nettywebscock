package net.mengkang.manager;

import io.netty.channel.Channel;
import net.mengkang.service.ClassRoomService;
import net.mengkang.service.LoginService;
import net.mengkang.service.StudentService;
import org.json.JSONObject;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class MessMgr {

    public static void distribution(Channel channel, String request){
        JSONObject json = new JSONObject(request);
        int codeId = (Integer) json.get("code");
        switch (codeId){
            case 10100:{
                LoginService.registMessage(channel,json);
                return;
            }
            case 10101:{
                LoginService.loginMessage(channel,json);
                return;
            }
            case 10102:{
                ClassRoomService.getAllRoomInfo(channel,json);
                return;
            }
            case 10103:{
                ClassRoomService.createRoom(channel,json);
                return;
            }
            case 10104:{
                // 进入房间
                ClassRoomService.enterTeacherRoom(channel,json);
                return;
            }
            case 10105:{
                StudentService.addStudent(channel,json);
                return;
            }
            case 10106:{
                StudentService.getStudent(channel,json);
                return;
            }
            case 10107:{
                //学生获取房间信息
                ClassRoomService.getStudentRoomInfo(channel,json);
                return;
            }
            case 10108:{
                ClassRoomService.enterStudentRoom(channel,json);
                return;
            }
            case 10109:{
                // 增加第一面板消息  也就是 学生跟老师共同面板
                ClassRoomService.addRoomMessage(channel,json);
                return;
            }
            case 10110:{
                // 增加第一面板消息  这面板的消息 暂时不发给学生端的
                ClassRoomService.addFirsPageMessage(channel,json);
                return;
            }
            case 10111:{
                //同步  把老师第一面板的消息 同步到第二面板
                ClassRoomService.addFirsPageTOSecondPageMessage(channel,json);
                return;
            }
            case 10112:{
                // 老师 进入第一面板
                ClassRoomService.enterTeacherFirstRoom(channel,json);
            }

        }
//        ClientMgr.sendMessToRoomClient(client,request);
    }

    public static String createMessage(int errorCode,String errorMess,long clientId, String message) {
        JSONObject msg = new JSONObject();
        msg.put("errorCode",errorCode);
        msg.put("errorMess",errorMess);
        msg.put("clientId",clientId);
        msg.put("data",message);
        msg.put("time",System.currentTimeMillis());
        return msg.toString();
    }
}
