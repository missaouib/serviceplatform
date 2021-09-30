package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.StudentPaperHistory;
import co.yixiang.mp.exam.domain.StudentPaperScore;
import co.yixiang.mp.exam.domain.StudentPaperScoreExample;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface StudentPaperScoreMapper {
    int countByExample(StudentPaperScoreExample example);

    int deleteByExample(StudentPaperScoreExample example);

    int deleteByPrimaryKey(Integer scoreId);

    int insert(StudentPaperScore record);

    int insertSelective(StudentPaperScore record);

    List<StudentPaperScore> selectByExample(StudentPaperScoreExample example);

    StudentPaperScore selectByPrimaryKey(Integer scoreId);

    int updateByExampleSelective(@Param("record") StudentPaperScore record, @Param("example") StudentPaperScoreExample example);

    int updateByExample(@Param("record") StudentPaperScore record, @Param("example") StudentPaperScoreExample example);

    int updateByPrimaryKeySelective(StudentPaperScore record);

    int updateByPrimaryKey(StudentPaperScore record);

    @Insert("INSERT INTO student_paper_history(sno,paper_id,history)\n" +
            "VALUES(#{record.sno},#{record.paperId},#{record.history})")
    int insertPaperHistory(@Param("record") StudentPaperHistory record);

    @Select("select id, sno,paper_id as paperId,history from student_paper_history where sno = #{sno} and paper_id=#{paperId}")
    StudentPaperHistory getStudentPaperHistory(@Param("sno") String sno,@Param("paperId")Integer paperId);

    @Update("update student_paper_history set history= #{record.history} ,update_time=now() where id = #{record.id} ")
    int updatePaperHistoryById(@Param("record") StudentPaperHistory record);

    @Delete("delete from student_paper_history where  sno = #{sno} and paper_id=#{paperId}")
    void deleteStudentPaperHistory(@Param("sno") String sno,@Param("paperId")Integer paperId);

}