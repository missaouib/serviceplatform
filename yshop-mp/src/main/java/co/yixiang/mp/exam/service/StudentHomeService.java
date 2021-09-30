package co.yixiang.mp.exam.service;

import co.yixiang.mp.exam.domain.*;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Map;

public interface StudentHomeService {
    public List<StudentHomeRotationImg> getRotationImages();
    public List<ProgramingLanguage> getLanguagesInfo();
    public ProgramingLanguage getLanguageInfoById(Integer langId);
    public List<Paper> getPapersInfo(Integer langId);
    public Paper getPapersInfoByPaperId(Integer paperId);
    public Map<String, Integer> getPaperQueNumByPaperId(Integer paperId);
    public Map<String, Integer> getPaperQueNumRandomByPaperId(Integer paperId,String sno);
    public int insertStudentPaperScore(StudentPaperScore studentPaperScore);
    public List<Map<String, Object>> getSingleQueListByPaperId(Integer paperId);
    public List<Map<String, Object>> getSingleQueListRadomByPaperId(Integer paperId,String sno);
    public List<Map<String, Object>> getMultipleQueListByPaperId(Integer paperId);
    public List<Map<String, Object>> getJudgeQueListByPaperId(Integer paperId);
    public List<Map<String, Object>> getFillQueListByPaperId(Integer paperId);
    public int insertPaperAnswerAndPaperScore(String sno, Integer paperId, List<String> singleAnswers,
                                              List<List<String>> multipleAnswers, List<String> judgeAnswers,
                                              List<String> fillAnswers, int timeUsed);
    public List<StudentPaperScore> getCurrentPaperStatus(String sno, Integer paperId);
    public Map<String, Object> getScoreReport(String sno, Integer paperId);

    public int savePaperHistory(String sno,Integer paperId,String json);

    StudentPaperHistory getStudentPaperHistory(String sno, Integer paperId);

    void deleteStudentPaperHistory(String sno, Integer paperId);
}
