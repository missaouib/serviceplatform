package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.ProgramingLanguage;
import co.yixiang.mp.exam.domain.ProgramingLanguageExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface ProgramingLanguageMapper {
    int countByExample(ProgramingLanguageExample example);

    int deleteByExample(ProgramingLanguageExample example);

    int deleteByPrimaryKey(Integer langId);

    int insert(ProgramingLanguage record);

    int insertSelective(ProgramingLanguage record);

    List<ProgramingLanguage> selectByExample(ProgramingLanguageExample example);

    ProgramingLanguage selectByPrimaryKey(Integer langId);

    int updateByExampleSelective(@Param("record") ProgramingLanguage record, @Param("example") ProgramingLanguageExample example);

    int updateByExample(@Param("record") ProgramingLanguage record, @Param("example") ProgramingLanguageExample example);

    int updateByPrimaryKeySelective(ProgramingLanguage record);

    int updateByPrimaryKey(ProgramingLanguage record);
}