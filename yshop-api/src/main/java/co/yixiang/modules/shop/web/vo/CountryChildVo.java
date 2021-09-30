package co.yixiang.modules.shop.web.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CountryChildVo implements Serializable {
    private String text;
    private String id;
    private Boolean disable = false;
    private String name;
}
