package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.PaperQue;
import co.yixiang.mp.exam.domain.PaperQueExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface PaperQueMapper {
    int countByExample(PaperQueExample example);

    int countByExampleRandom(PaperQueExample example);

    int deleteByExample(PaperQueExample example);

    int deleteByPrimaryKey(Integer queId);

    int insert(PaperQue record);

    int insertSelective(PaperQue record);

    int insertSelectiveRandom(PaperQue record);

    List<PaperQue> selectByExample(PaperQueExample example);

    PaperQue selectByPrimaryKey(Integer queId);

    int updateByExampleSelective(@Param("record") PaperQue record, @Param("example") PaperQueExample example);

    int updateByExample(@Param("record") PaperQue record, @Param("example") PaperQueExample example);

    int updateByPrimaryKeySelective(PaperQue record);

    int updateByPrimaryKey(PaperQue record);

    @Delete("delete from paper_que_student where paper_id = #{paperId} and sno=#{sno}")
    int deleteRandom(@Param("paperId") Integer paperId,@Param("sno") String sno);
}