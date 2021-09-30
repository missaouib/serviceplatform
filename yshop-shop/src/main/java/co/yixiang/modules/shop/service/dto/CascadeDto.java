package co.yixiang.modules.shop.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CascadeDto {
    private String value;
    private String label;
    private String code;
    private List<CascadeDto> children;
}
