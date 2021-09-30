package co.yixiang.mp.exam.dao;

import co.yixiang.mp.exam.domain.BankFillQue;
import co.yixiang.mp.exam.domain.BankFillQueExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface BankFillQueMapper {
    int countByExample(BankFillQueExample example);

    int deleteByExample(BankFillQueExample example);

    int deleteByPrimaryKey(Integer fillId);

    int insert(BankFillQue record);

    int insertSelective(BankFillQue record);

    List<BankFillQue> selectByExample(BankFillQueExample example);

    BankFillQue selectByPrimaryKey(Integer fillId);

    int updateByExampleSelective(@Param("record") BankFillQue record, @Param("example") BankFillQueExample example);

    int updateByExample(@Param("record") BankFillQue record, @Param("example") BankFillQueExample example);

    int updateByPrimaryKeySelective(BankFillQue record);

    int updateByPrimaryKey(BankFillQue record);

    List<BankFillQue> getFillQueListByPaperId(Integer paperId);

    List<BankFillQue> getRandomFillByCountAndLangId(@Param("langId") Integer langId, @Param("fillNum") Integer fillNum);
}