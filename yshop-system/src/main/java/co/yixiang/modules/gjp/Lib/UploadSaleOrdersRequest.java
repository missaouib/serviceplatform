package co.yixiang.modules.gjp.Lib;

import java.util.List;

public class UploadSaleOrdersRequest {
    public List<SaleOrderEntity> orders ;
    public String ShopKey ;
    public  String GetApiName()
    {
    	return "beefun.selfbuiltmall.uploadsaleorders";
    }
    
    public List<SaleOrderEntity> getOrderDetails() {
    	return orders;
       }
       public void setOrderDetails(List<SaleOrderEntity> orders) {
    	   this.orders = orders;
       }
       
       public void setShopKey(String ShopKey) {
    		this.ShopKey = ShopKey;
      }
      public String getShopKey() {
    		return ShopKey;
      }
}
