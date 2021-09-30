package co.yixiang.mp.exam.service;

import java.util.List;
import java.util.Map;

public interface StudentSearchService {
    public List<Map<String, Object>> getSearchPapers(String keyword);
}
