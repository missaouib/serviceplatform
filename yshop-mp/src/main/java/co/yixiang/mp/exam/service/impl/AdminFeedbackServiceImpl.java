package co.yixiang.mp.exam.service.impl;

import co.yixiang.mp.exam.dao.StudentFeedbackMapper;
import co.yixiang.mp.exam.dao.StudentMapper;
import co.yixiang.mp.exam.domain.Student;
import co.yixiang.mp.exam.domain.StudentFeedback;
import co.yixiang.mp.exam.domain.StudentFeedbackExample;
import co.yixiang.mp.exam.service.AdminFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminFeedbackServiceImpl implements AdminFeedbackService {

    @Autowired
    StudentFeedbackMapper studentFeedbackMapper;
    @Autowired
    StudentMapper studentMapper;

    @Override
    public List<Map<String, Object>> getFeedbacksList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        StudentFeedbackExample studentFeedbackExample = new StudentFeedbackExample();
        studentFeedbackExample.setOrderByClause("feedback_status asc");
        List<StudentFeedback> studentFeedbackList = studentFeedbackMapper.selectByExample(studentFeedbackExample);
        for (StudentFeedback studentFeedback : studentFeedbackList) {
            int index = studentFeedbackList.indexOf(studentFeedback);
            Map<String, Object> map = new HashMap<>();
            map.put("id", index + 1);
            map.put("feedbackId", studentFeedback.getFeedbackId());
            map.put("feedbackContent", studentFeedback.getFeedbackContent());
            map.put("feedbackCreateTime", studentFeedback.getFeedbackCreateTime());
            map.put("sno", studentFeedback.getSno());
            Student student = studentMapper.selectByPrimaryKey(studentFeedback.getSno());
            map.put("stuImgSrc", student.getStuImgSrc());
            map.put("stuName", studentFeedback.getStuName());
            map.put("admAnswer", studentFeedback.getAdmAnswer());
            map.put("ano", studentFeedback.getAno());
            map.put("admName", studentFeedback.getAdmName());
            map.put("feedbackStatus", studentFeedback.getFeedbackStatus());
            map.put("replyTime", studentFeedback.getReplyTime());
            resultList.add(map);
        }
        return resultList;
    }

    @Override
    public int getUnReplyCount() {
        StudentFeedbackExample studentFeedbackExample = new StudentFeedbackExample();
        StudentFeedbackExample.Criteria criteria = studentFeedbackExample.createCriteria();
        criteria.andFeedbackStatusEqualTo("0");
        int result = studentFeedbackMapper.countByExample(studentFeedbackExample);
        return result;
    }

    @Override
    public List<Map<String, Object>> searchFeedbacksList(String feedbackContent, String stuName, String admAnswer, String admName, String feedbackStatus) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        StudentFeedbackExample studentFeedbackExample = new StudentFeedbackExample();
        StudentFeedbackExample.Criteria criteria = studentFeedbackExample.createCriteria();
        if (!feedbackContent.equals("undefined")) {
            criteria.andFeedbackContentLike("%" + feedbackContent + "%");
        }
        if (!stuName.equals("undefined")) {
            criteria.andStuNameLike("%" + stuName + "%");
        }
        if (!admAnswer.equals("undefined")) {
            criteria.andAdmAnswerLike("%" + admAnswer + "%");
        }
        if (!admName.equals("undefined")) {
            criteria.andAdmNameLike("%" + admName + "%");
        }
        if (!feedbackStatus.equals("undefined")) {
            if (feedbackStatus.equals("0")) {
                criteria.andFeedbackStatusEqualTo("0");
            } else {
                List<String> value = new ArrayList<>();
                value.add("1");
                value.add("2");
                criteria.andFeedbackStatusIn(value);
            }
        }
        studentFeedbackExample.setOrderByClause("feedback_status asc");
        List<StudentFeedback> studentFeedbackList = studentFeedbackMapper.selectByExample(studentFeedbackExample);
        for (StudentFeedback studentFeedback : studentFeedbackList) {
            int index = studentFeedbackList.indexOf(studentFeedback);
            Map<String, Object> map = new HashMap<>();
            map.put("id", index + 1);
            map.put("feedbackId", studentFeedback.getFeedbackId());
            map.put("feedbackContent", studentFeedback.getFeedbackContent());
            map.put("feedbackCreateTime", studentFeedback.getFeedbackCreateTime());
            map.put("sno", studentFeedback.getSno());
            Student student = studentMapper.selectByPrimaryKey(studentFeedback.getSno());
            map.put("stuImgSrc", student.getStuImgSrc());
            map.put("stuName", studentFeedback.getStuName());
            map.put("admAnswer", studentFeedback.getAdmAnswer());
            map.put("admName", studentFeedback.getAdmName());
            map.put("ano", studentFeedback.getAno());
            map.put("feedbackStatus", studentFeedback.getFeedbackStatus());
            map.put("replyTime", studentFeedback.getReplyTime());
            resultList.add(map);
        }
        return resultList;
    }

    @Override
    public int deleteFeedback(Integer feedbackId) {
        int resule = studentFeedbackMapper.deleteByPrimaryKey(feedbackId);
        return resule;
    }

    @Override
    public int replyFeedback(StudentFeedback studentFeedback) {
        studentFeedback.setReplyTime(new Date());
        studentFeedback.setFeedbackStatus("1");
        int result = studentFeedbackMapper.updateByPrimaryKeySelective(studentFeedback);
        return result;
    }
}
