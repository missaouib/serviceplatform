package co.yixiang.mp.exam.service;

import co.yixiang.mp.exam.domain.Teacher;

import java.util.List;

public interface TeacherSystemService {
    public List<Teacher> teacherLogin(String tno, String psw);
    public boolean updateTeacher(Teacher teacher);
    public String getTeaHuPsw(String tno);
}
