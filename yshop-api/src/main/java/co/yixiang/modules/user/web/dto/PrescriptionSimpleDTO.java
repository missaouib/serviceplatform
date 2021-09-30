package co.yixiang.modules.user.web.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class PrescriptionSimpleDTO {
    private String imagePath;
    private String infoDate;
    private String maker;
}
