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

    //增加学生 会把学生放在一个学生池子里
    // 同时在老师列表里面增加一个学生


    /**增加学生**/
    public static void addStudent(Channel channel, JSONObject json){

        String teacherUser= (String) json.get("teacherUser");

        String studentName= (String) json.get("stName");
        String studentPhone= (String) json.get("stPhone");
        String studentAddress= (String) json.get("stAddress");
        String studentGrade= (String) json.get("stGrade");
        String studentRemark= (String) json.get("stRemark");

//        long studentId = Client.CONCURRENT_INTEGER.getAndIncrement();

        long studentId = getId();


        JSONObject studentObject = new JSONObject();
        studentObject.put("studentName",studentName);
        studentObject.put("studentPhone",studentPhone);
        studentObject.put("studentAddress",studentAddress);
        studentObject.put("studentGrade",studentGrade);
        studentObject.put("studentRemark",studentRemark);
        studentObject.put("studentId",studentId);
        studentObject.put("password",studentId+studentName);

        //暂时没有做重复的过滤
        String studentInfo = RedisMgr.getValue(studentName+"studentInfo");
        if (studentInfo != null){
            String message = MessMgr.createMessage(3,"这个学生名字已经有了 请在学生后面名字加数字",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        RedisMgr.addStudentTOPool(studentName,studentObject);
        RedisMgr.addStudentTOTeacher(teacherUser,studentObject);

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



    //学生进入老师课堂
    public static void studentEnterTeacherClass(Channel channel, JSONObject json){

        String teacherUser= (String) json.get("teacherUser");


        // 如果老师不在线 学生不可以进去

        // 把这个老师的所有课件都发给这个学生
    }



}
