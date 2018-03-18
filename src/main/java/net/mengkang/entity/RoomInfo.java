package net.mengkang.entity;

import io.netty.channel.Channel;

/**
 * Created by luoxiaosong on 2018/3/3.
 */
public class RoomInfo {

    private long roomId;
    private String grade;
    private String studentName;
    private String subject;
    private String info;

    private Channel teacherChannel;

    private Channel studentChannel;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStudentname() {
        return studentName;
    }

    public void setStudentname(String studentname) {
        this.studentName = studentname;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Channel getStudentChannel() {
        return studentChannel;
    }

    public void setStudentChannel(Channel studentChannel) {
        this.studentChannel = studentChannel;
    }

    public Channel getTeacherChannel() {
        return teacherChannel;
    }

    public void setTeacherChannel(Channel teacherChannel) {
        this.teacherChannel = teacherChannel;
    }

}
