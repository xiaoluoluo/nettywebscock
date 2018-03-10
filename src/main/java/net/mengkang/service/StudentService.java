package net.mengkang.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.mengkang.entity.Client;
import net.mengkang.manager.MessMgr;
import net.mengkang.manager.RedisMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class StudentService extends BaseService{

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
        String message = MessMgr.createMessage(0,"",0, dataMessage);
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
        String message = MessMgr.createMessage(0,"",0, data.toString());
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }





}
