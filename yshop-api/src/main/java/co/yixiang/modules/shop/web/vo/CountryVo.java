package co.yixiang.modules.shop.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CountryVo implements Serializable {
    private String text;
/*    private Integer badge = 3;
    private Boolean dot = false;*/
    private String className = "my-class";
    List<CountryChildVo> children;
    private String id;

}
