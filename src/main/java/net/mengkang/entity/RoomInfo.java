package net.mengkang.entity;

/**
 * Created by luoxiaosong on 2018/3/3.
 */
public class RoomInfo {

    private int roomId;
    private String grade;
    private String studentName;
    private String subject;
    private String info;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
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
}
