package co.yixiang.mp.exam.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class StudentPaperHistory {
    private Integer id;
    private String sno;
    private Integer paperId;
    private String history;
}
