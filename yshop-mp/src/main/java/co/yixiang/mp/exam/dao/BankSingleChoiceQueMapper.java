package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.BankSingleChoiceQue;
import co.yixiang.mp.exam.domain.BankSingleChoiceQueExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface BankSingleChoiceQueMapper {
    int countByExample(BankSingleChoiceQueExample example);

    int deleteByExample(BankSingleChoiceQueExample example);

    int deleteByPrimaryKey(Integer singleId);

    int insert(BankSingleChoiceQue record);

    int insertSelective(BankSingleChoiceQue record);

    List<BankSingleChoiceQue> selectByExample(BankSingleChoiceQueExample example);

    BankSingleChoiceQue selectByPrimaryKey(Integer singleId);

    int updateByExampleSelective(@Param("record") BankSingleChoiceQue record, @Param("example") BankSingleChoiceQueExample example);

    int updateByExample(@Param("record") BankSingleChoiceQue record, @Param("example") BankSingleChoiceQueExample example);

    int updateByPrimaryKeySelective(BankSingleChoiceQue record);

    int updateByPrimaryKey(BankSingleChoiceQue record);

    List<BankSingleChoiceQue> getSingleQueListByPaperId(Integer paperId);

    List<BankSingleChoiceQue> getSingleQueListRadomByPaperId(@Param("paperId") Integer paperId,@Param("sno") String sno);

    List<BankSingleChoiceQue> getRandomSingleByCountAndLangId(@Param("langId") Integer langId, @Param("singleNum") Integer singleNum);
}