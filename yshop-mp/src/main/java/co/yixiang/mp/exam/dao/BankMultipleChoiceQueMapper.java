package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.BankMultipleChoiceQue;
import co.yixiang.mp.exam.domain.BankMultipleChoiceQueExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface BankMultipleChoiceQueMapper {
    int countByExample(BankMultipleChoiceQueExample example);

    int deleteByExample(BankMultipleChoiceQueExample example);

    int deleteByPrimaryKey(Integer multipleId);

    int insert(BankMultipleChoiceQue record);

    int insertSelective(BankMultipleChoiceQue record);

    List<BankMultipleChoiceQue> selectByExample(BankMultipleChoiceQueExample example);

    BankMultipleChoiceQue selectByPrimaryKey(Integer multipleId);

    int updateByExampleSelective(@Param("record") BankMultipleChoiceQue record, @Param("example") BankMultipleChoiceQueExample example);

    int updateByExample(@Param("record") BankMultipleChoiceQue record, @Param("example") BankMultipleChoiceQueExample example);

    int updateByPrimaryKeySelective(BankMultipleChoiceQue record);

    int updateByPrimaryKey(BankMultipleChoiceQue record);

    List<BankMultipleChoiceQue> getMultipleQueListByPaperId(Integer paperId);

    List<BankMultipleChoiceQue> getRandomMultipleByCountAndLangId(@Param("langId") Integer langId, @Param("multipleNum") Integer multipleNum);
}