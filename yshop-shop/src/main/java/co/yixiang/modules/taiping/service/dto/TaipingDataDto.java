package co.yixiang.modules.taiping.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TaipingDataDto {

    @NotBlank
    private String data;
}
