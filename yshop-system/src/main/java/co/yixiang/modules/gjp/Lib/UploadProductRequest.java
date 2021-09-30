package co.yixiang.modules.gjp.Lib;

import co.yixiang.modules.gjp.Lib.vo.SelfbuiltmallproductVo;
import lombok.Data;

import java.util.List;

@Data
public class UploadProductRequest {
    public List<SelfbuiltmallproductVo> products ;
    public String ShopKey ;
    public  String GetApiName()
    {
    	return "zyx.selfbuiltmall.uploadproducts";
    }
    

}
