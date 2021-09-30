package co.yixiang.mp.exam.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.mp.api.ApiResult;
import co.yixiang.mp.exam.dao.BankSingleChoiceQueMapper;
import co.yixiang.mp.exam.dao.PaperQueMapper;
import co.yixiang.mp.exam.domain.*;
import co.yixiang.mp.exam.service.StudentHomeService;
import co.yixiang.mp.exam.util.ServerResponse;
import co.yixiang.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/student")
//@CrossOrigin
public class StudentHomeController {
    /*
        学生首页控制器：包含获取首页轮播图数据，获取编程语言信息
     */

    @Autowired
    private StudentHomeService studentHomeService;

    @Autowired
    BankSingleChoiceQueMapper bankSingleChoiceQueMapper;

    @Autowired
    PaperQueMapper paperQueMapper;
    //Log4j日志处理
    public static Logger log = LoggerFactory.getLogger(StudentSystemController.class);

    /*
        获取学生首页轮播图数据
     */
    @RequestMapping("/getRotationImages")
    public ServerResponse getRotationImages(){
        List<StudentHomeRotationImg> resultList = studentHomeService.getRotationImages();
        if(resultList != null && resultList.size() > 0){
            return ServerResponse.createBySuccess("轮播图数据获取成功",resultList);
        }
        else {
            return ServerResponse.createByError("轮播图数据获取失败");
        }
    }

    /*
        获取学生首页全部全部编程语言信息
     */
    @RequestMapping("/getLanguagesInfo")
    public ServerResponse getLanguagesInfo(){
        List<ProgramingLanguage> resultList = studentHomeService.getLanguagesInfo();
        if(resultList != null && resultList.size() > 0){
            return ServerResponse.createBySuccess("编程语言信息获取成功",resultList);
        }
        else {
            return ServerResponse.createByError("编程语言信息获取失败");
        }
    }

    /*
        通过langId获取编程语言信息
     */
    @RequestMapping("/getLanguageInfoById")
    public ServerResponse getLanguageInfoById(@RequestParam("langId")Integer langId ){
        ProgramingLanguage programingLanguage = studentHomeService.getLanguageInfoById(langId);
        if(programingLanguage != null){
//            log.info(programingLanguage.getLangImgSrc());
            return ServerResponse.createBySuccess("编程语言id为" + langId + "的信息获取成功",programingLanguage);
        }
        else {
            return ServerResponse.createByError("编程语言id为" + langId + "的信息获取失败");
        }
    }

    /*
       通过langId获取试卷信息
    */
    @RequestMapping("/getPapersInfo")
    public ApiResult getPapersInfo(@RequestParam("langId")Integer langId ){
        List<Paper> resultList = studentHomeService.getPapersInfo(langId);
        if(resultList != null && resultList.size() > 0){
//            log.info(""+resultList);
            return ApiResult.ok(resultList);
        }
        else if(resultList.size() == 0){
            return ApiResult.fail("试卷列表为空");
        }
        else {
            return ApiResult.fail("编程语言id为" + langId + "的试卷获取失败");
        }
    }

    /*
        请求获取当前试卷状态，即是否已完成
     */
    @RequestMapping(value = "/getCurrentPaperStatus", method = RequestMethod.POST)
    public ServerResponse getCurrentPaperStatus(@RequestBody(required = false)StudentPaperScore studentPaperScore){
        String sno = studentPaperScore.getSno();
        Integer paperId = studentPaperScore.getPaperId();
        List<StudentPaperScore> resultList = studentHomeService.getCurrentPaperStatus(sno, paperId);
        if(resultList != null && resultList.size() > 0){
            return ServerResponse.createBySuccess("当前试卷已完成",1);
        }
        else {
            return ServerResponse.createByError("当前试卷未完成");
        }
    }

    /*
       通过paperId获取试卷及单选题、多选题、判断题和填空题信息
    */
    @RequestMapping("/getPapersInfoByPaperId")
    @AnonymousAccess
    public ApiResult getPapersInfoByPaperId(@RequestParam("paperId")Integer paperId ){


        String uid = SecurityUtils.getUserId().toString();
        StudentPaperHistory studentPaperHistory = studentHomeService.getStudentPaperHistory(uid,paperId);
        if(studentPaperHistory != null) {
            String history = studentPaperHistory.getHistory();
            Map map = JSONUtil.toBean(history,Map.class);
            return ApiResult.ok(map);

        } else {
            // 判断试卷类型：随机/固定
            Paper paper = studentHomeService.getPapersInfoByPaperId(paperId);
            if( 1 == paper.getPaperType() ) {  // 随机试卷
                paperQueMapper.deleteRandom(paperId,uid);
//  插入试卷问题信息到paper_que表
                //  单选题
                List<BankSingleChoiceQue> bankSingleChoiceQueList = bankSingleChoiceQueMapper.getRandomSingleByCountAndLangId(1, 50);
                for (BankSingleChoiceQue bankSingleChoiceQue : bankSingleChoiceQueList) {
                    int singleId = bankSingleChoiceQue.getSingleId();
                    PaperQue paperQue = new PaperQue();
                    paperQue.setQueType(1);
                    paperQue.setSingleId(singleId);
                    paperQue.setPaperId(paperId);
                    paperQue.setSno(uid);
                    paperQueMapper.insertSelectiveRandom(paperQue);
                    //更新compose_flag字段
                    bankSingleChoiceQue.setComposeFlag("1");
                    bankSingleChoiceQueMapper.updateByPrimaryKeySelective(bankSingleChoiceQue);
                }

                Map<String, Integer> numObj = studentHomeService.getPaperQueNumRandomByPaperId(paperId,uid);
                List<Map<String, Object>> singleQueList = studentHomeService.getSingleQueListRadomByPaperId(paperId,uid);
                List<Map<String, Object>> multipleQueList =  new ArrayList<>();  //studentHomeService.getMultipleQueListByPaperId(paperId);
                List<Map<String, Object>> judgeQueList = new ArrayList<>();  //studentHomeService.getJudgeQueListByPaperId(paperId);
                List<Map<String, Object>> fillQueList = new ArrayList<>();  //studentHomeService.getFillQueListByPaperId(paperId);
                if(paper != null && numObj != null){
/*            List listResult = new ArrayList();
            listResult.add(paper);
            listResult.add(numObj);*/
                    Map<String, Object> map = new HashMap<>();
                    map.put("paperInfo",paper);
                    map.put("queNumInfo",numObj);
                    map.put("singleQueList",singleQueList);
                    map.put("multipleQueList",multipleQueList);
                    map.put("judgeQueList",judgeQueList);
                    map.put("fillQueList",fillQueList);
                    return ApiResult.ok(map);
                }
                else {
                    return ApiResult.fail("试卷id为" + paperId + "的试卷信息获取失败");
                }

            } else  {
                Map<String, Integer> numObj = studentHomeService.getPaperQueNumByPaperId(paperId);
                List<Map<String, Object>> singleQueList = studentHomeService.getSingleQueListByPaperId(paperId);
                List<Map<String, Object>> multipleQueList = studentHomeService.getMultipleQueListByPaperId(paperId);
                List<Map<String, Object>> judgeQueList = studentHomeService.getJudgeQueListByPaperId(paperId);
                List<Map<String, Object>> fillQueList = studentHomeService.getFillQueListByPaperId(paperId);
                if(paper != null && numObj != null){
/*            List listResult = new ArrayList();
            listResult.add(paper);
            listResult.add(numObj);*/
                    Map<String, Object> map = new HashMap<>();
                    map.put("paperInfo",paper);
                    map.put("queNumInfo",numObj);
                    map.put("singleQueList",singleQueList);
                    map.put("multipleQueList",multipleQueList);
                    map.put("judgeQueList",judgeQueList);
                    map.put("fillQueList",fillQueList);
                    return ApiResult.ok(map);
                }
                else {
                    return ApiResult.fail("试卷id为" + paperId + "的试卷信息获取失败");
                }
            }



        }




    }
    /*
        插入学生成绩表成绩信息，包含三个字段，考试开始时间、学号和试卷id
     */
    @RequestMapping(value = "/insertStudentPaperScore", method = RequestMethod.POST)
    public ApiResult insertStudentPaperScore(@RequestBody(required = false)StudentPaperScore studentPaperScore){
        studentPaperScore.setStartTime(new Date());
        Integer uid = SecurityUtils.getUserId().intValue();
        studentPaperScore.setSno(String.valueOf(uid));
        int result = studentHomeService.insertStudentPaperScore(studentPaperScore);
        if(result == 0)
            return ApiResult.fail("数据库错误，插入学生成绩表失败");
        else
            return ApiResult.ok("学号" + studentPaperScore.getSno() + "试卷id" + studentPaperScore.getPaperId() + "初始化插入学生成绩表信息成功");
    }

    /*
       插入学生成绩表成绩信息和学生试卷答题记录信息
    */
    @RequestMapping(value = "/submitPaper", method = RequestMethod.POST)
    public ApiResult submitPaper(@RequestBody Map<String, Object> obj){
        Integer uid = SecurityUtils.getUserId().intValue();
        //学号
       // String sno = (String) obj.get("sno");
        String sno = String.valueOf(uid);
        //试卷id
        Integer paperId = Integer.valueOf( String.valueOf(obj.get("paperId")));
        //单选题答案数组
        List<String> singleAnswers = (List) obj.get("singleAnswers");
        //多选题答案数组
        List<List<String>> multipleAnswers = (List) obj.get("multipleAnswers");
        //判断题答案数组
        List<String> judgeAnswers = (List) obj.get("judgeAnswers");
        //填空题答案数组
        List<String> fillAnswers = (List) obj.get("fillAnswers");
        //考试花费时间
      //  int timeUsed = (Integer) obj.get("timeUsed");
        int timeUsed = 0 ;
        int result = studentHomeService.insertPaperAnswerAndPaperScore(sno, paperId, singleAnswers,
                multipleAnswers, judgeAnswers,
                fillAnswers, timeUsed);

        studentHomeService.deleteStudentPaperHistory(sno, paperId);
        paperQueMapper.deleteRandom(paperId,sno);
        if(result == 0){
            return ApiResult.fail("数据库错误，插入学生试卷答案记录表或学生成绩表失败");
        }
        else {
            return ApiResult.ok("用户id " + sno + "试卷id" + paperId + "交卷成功，数据已插入数据库并已自动计算出成绩");
        }
    }

    /*
        请求获取当前试卷的成绩报告
     */
    @RequestMapping(value = "/getScoreReport")
    public ServerResponse getScoreReport(@RequestParam("sno")String sno,
                                         @RequestParam("paperId")Integer paperId) {

        //试卷成绩报告数据
        Map<String, Object> map = studentHomeService.getScoreReport(sno, paperId);

        if(map != null){
            return ServerResponse.createBySuccess("获取当前试卷的成绩报告数据成功",map);
        }
        else {
            return ServerResponse.createByError("数据库错误，获取当前试卷的成绩报告数据失败");
        }
    }


    /*
   记录试卷做题情况
*/
    @RequestMapping(value = "/saveHistory", method = RequestMethod.POST)
    public ApiResult saveHistory(@RequestBody JSONObject jsonObject){
        Integer uid = SecurityUtils.getUserId().intValue();
        //学号
        // String sno = (String) obj.get("sno");
        String sno = String.valueOf(uid);
        //试卷id
        Integer paperId = Integer.valueOf( String.valueOf(jsonObject.get("paperId")));

        int result = studentHomeService.savePaperHistory(sno, paperId, jsonObject.toString());

        if(result == 0){
            return ApiResult.fail("数据库错误，插入答题记录失败");
        }
        else {
            return ApiResult.ok("用户id " + sno + "试卷id" + paperId + "保存答题记录成功");
        }
    }
}
