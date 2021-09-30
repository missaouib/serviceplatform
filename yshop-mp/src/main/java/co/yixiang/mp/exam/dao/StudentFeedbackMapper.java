package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.StudentFeedback;
import co.yixiang.mp.exam.domain.StudentFeedbackExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface StudentFeedbackMapper {
    int countByExample(StudentFeedbackExample example);

    int deleteByExample(StudentFeedbackExample example);

    int deleteByPrimaryKey(Integer feedbackId);

    int insert(StudentFeedback record);

    int insertSelective(StudentFeedback record);

    List<StudentFeedback> selectByExample(StudentFeedbackExample example);

    StudentFeedback selectByPrimaryKey(Integer feedbackId);

    int updateByExampleSelective(@Param("record") StudentFeedback record, @Param("example") StudentFeedbackExample example);

    int updateByExample(@Param("record") StudentFeedback record, @Param("example") StudentFeedbackExample example);

    int updateByPrimaryKeySelective(StudentFeedback record);

    int updateByPrimaryKey(StudentFeedback record);
}