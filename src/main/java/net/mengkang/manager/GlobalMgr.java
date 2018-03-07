package net.mengkang.manager;

import net.mengkang.service.ClassRoomService;
import net.mengkang.service.StudentService;
import net.mengkang.service.TeacherService;

public class GlobalMgr {

    ClassRoomService classRoomService = new ClassRoomService();

    StudentService studentService = new StudentService();

    TeacherService teacherService = new TeacherService();

    public ClassRoomService getClassRoomService(){
        return classRoomService;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public TeacherService getTeacherService() {
        return teacherService;
    }

}
