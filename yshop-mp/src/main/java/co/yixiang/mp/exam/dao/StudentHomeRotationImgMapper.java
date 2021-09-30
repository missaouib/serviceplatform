package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.StudentHomeRotationImg;
import co.yixiang.mp.exam.domain.StudentHomeRotationImgExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface StudentHomeRotationImgMapper {
    int countByExample(StudentHomeRotationImgExample example);

    int deleteByExample(StudentHomeRotationImgExample example);

    int deleteByPrimaryKey(Integer imgId);

    int insert(StudentHomeRotationImg record);

    int insertSelective(StudentHomeRotationImg record);

    List<StudentHomeRotationImg> selectByExample(StudentHomeRotationImgExample example);

    StudentHomeRotationImg selectByPrimaryKey(Integer imgId);

    int updateByExampleSelective(@Param("record") StudentHomeRotationImg record, @Param("example") StudentHomeRotationImgExample example);

    int updateByExample(@Param("record") StudentHomeRotationImg record, @Param("example") StudentHomeRotationImgExample example);

    int updateByPrimaryKeySelective(StudentHomeRotationImg record);

    int updateByPrimaryKey(StudentHomeRotationImg record);
}