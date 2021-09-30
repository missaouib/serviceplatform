package co.yixiang.mp.exam.service;

import co.yixiang.mp.exam.domain.StudentHomeRotationImg;

import java.util.List;
import java.util.Map;

public interface AdminRotationImgService {
    public List<Map<String, Object>> getRotationImgsList();
    public List<Map<String, Object>> searchRotationImgsList(String imgTitle, String admName);
    public int insertRotationImgInfo(StudentHomeRotationImg studentHomeRotationImg);
    public int updateRotationImgInfo(StudentHomeRotationImg studentHomeRotationImg);
    public int deleteRotationImgInfo(Integer imgId);
}
