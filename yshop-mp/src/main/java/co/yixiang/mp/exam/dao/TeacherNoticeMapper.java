package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.TeacherNotice;
import co.yixiang.mp.exam.domain.TeacherNoticeExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface TeacherNoticeMapper {
    int countByExample(TeacherNoticeExample example);

    int deleteByExample(TeacherNoticeExample example);

    int deleteByPrimaryKey(Integer noticeId);

    int insert(TeacherNotice record);

    int insertSelective(TeacherNotice record);

    List<TeacherNotice> selectByExample(TeacherNoticeExample example);

    TeacherNotice selectByPrimaryKey(Integer noticeId);

    int updateByExampleSelective(@Param("record") TeacherNotice record, @Param("example") TeacherNoticeExample example);

    int updateByExample(@Param("record") TeacherNotice record, @Param("example") TeacherNoticeExample example);

    int updateByPrimaryKeySelective(TeacherNotice record);

    int updateByPrimaryKey(TeacherNotice record);
}