package co.yixiang.mp.exam.service.impl;

import co.yixiang.mp.exam.dao.StudentMapper;
import co.yixiang.mp.exam.domain.Student;
import co.yixiang.mp.exam.domain.StudentExample;
import co.yixiang.mp.exam.service.StudentSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudentSystemServiceImpl implements StudentSystemService {

    @Autowired
    StudentMapper studentMapper;

    /*
        根据学号查找学生信息
     */
    public Student getStudentInfoBySno(String sno) {
        return studentMapper.selectByPrimaryKey(sno);
    }

    /*
        校验学生登录
     */
    @Override
    public List<Student> checkStudentPsw(String sno, String stuPsw) {
        StudentExample example = new StudentExample();
        StudentExample.Criteria criteria = example.createCriteria();
        criteria.andSnoEqualTo(sno).andStuPswEqualTo(stuPsw);
        List<Student> result = studentMapper.selectByExample(example);
        return result;
    }

    /*
        判断学号是否存在数据库学生表中
     */
    @Override
    public boolean snoIsExist(String sno) {
        StudentExample example = new StudentExample();
        StudentExample.Criteria criteria = example.createCriteria();
        criteria.andSnoEqualTo(sno);
        List<Student> result = studentMapper.selectByExample(example);
        if(result.size()>0)
            return true;
        else
            return false;
    }

    /*
        注册学生信息，学生注册插入学生信息到学生表
     */
    @Override
    public int registerStudent(String sno, String stuPsw, String stuName,
                               String stuSex, String stuEmail, String stuPhone, Date stuCreateTime,
                               String stuSecurityCode) {
        Student student = new Student();
        student.setSno(sno);
        student.setStuPsw(stuPsw);
        student.setStuName(stuName);
        student.setStuSex(stuSex);
        student.setStuEmail(stuEmail);
        student.setStuPhone(stuPhone);
        student.setStuCreateTime(stuCreateTime);
        int result = studentMapper.insertSelective(student);
        return result;
    }

    /*
        更新学生信息
     */
    public boolean updateStudent(Student student){
        int result = studentMapper.updateByPrimaryKey(student);
        if (result > 0){
            return true;
        }
        return false;
    }

}
