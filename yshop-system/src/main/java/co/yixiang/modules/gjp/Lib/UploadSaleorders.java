package co.yixiang.modules.gjp.Lib;

import net.sf.json.JSONArray;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadSaleorders {

	/*public String DoUploadSaleOrders()  throws Exception
	{
		String ret = "";
		 //ҵ�������ȡ
		UploadSaleOrdersRequest order = GetUploadSaleOrdersRequest();
        //�ӿڵ��ò�����װ
		 Map<String, String> param;
		try {
			param = GetPostParams(order, Config.token, Config.appkey, Config.sign_key);
	        //post����
		    String postString = ""; 
		        for (String in : param.keySet()) {
		            postString += in + "=" + URLEncoder.encode(param.get(in),"utf-8")  +"&";
		    }
		    postString = postString.substring(0, postString.length() - 1);
			ret =  HttpRequest.sendPost(Config.api_link, postString);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;	
	}

    public static Map<String, String> GetPostParams(UploadSaleOrdersRequest order, String token, String appKey, String signKey) throws NoSuchAlgorithmException
    {
    	Map<String, String> txtParams = new HashMap<String, String>();
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//���Է�����޸����ڸ�ʽ
		Date now = new Date(); 
		String time = dateFormat.format(now);
		
        //���ҵ�����
        BuildOrderParams(order, txtParams);
        // ���ϵͳ����
        txtParams.put("method", order.GetApiName());
        txtParams.put("appkey", appKey);
        txtParams.put("timestamp", time);
        txtParams.put("token", token);
        //ǩ������
		AESCoder coder = new AESCoder();
        txtParams.put("sign", coder.SignRequest(txtParams, signKey));
        return txtParams;
    }
	
    public static void BuildOrderParams(UploadSaleOrdersRequest order, Map<String, String> parameters)
    {
    	parameters.put("shopkey", Config.shop_key);
    	parameters.put("orders",  JSONArray.fromObject(order.orders).toString());

    }
    
	public static UploadSaleOrdersRequest GetUploadSaleOrdersRequest()
    {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//���Է�����޸����ڸ�ʽ
		Date now = new Date(); 
		String time = dateFormat.format(now);
		
        UploadSaleOrdersRequest orders = new UploadSaleOrdersRequest();
        orders.ShopKey = Config.shop_key;
        List<SaleOrderEntity> orderList = new ArrayList<SaleOrderEntity>();
        SaleOrderEntity saleorder = new SaleOrderEntity();
        saleorder.setEShopBuyer(new BuyerEntity()) ;
        saleorder.EShopBuyer.setCustomerEmail("grasp.grasp.com.cn") ;
        saleorder.EShopBuyer.setCustomerReceiver("��ã����");
        saleorder.EShopBuyer.setCustomerReceiverAddress("ruanjianyuan");
        saleorder.EShopBuyer.setCustomerReceiverCity("chengdu");
        saleorder.EShopBuyer.setCustomerReceiverCountry("china");
        saleorder.EShopBuyer.setCustomerReceiverDistrict("gaoxin");
        saleorder.EShopBuyer.setCustomerReceiverMobile("17775527952");
        saleorder.EShopBuyer.setCustomerReceiverPhone("12345678");
        saleorder.EShopBuyer.setCustomerReceiverProvince("sichuan");
        saleorder.EShopBuyer.setCustomerReceiverZipcode("123456");
        saleorder.EShopBuyer.setCustomerShopAccount("zzzz");

        saleorder.setInvoiceTitle("");
        saleorder.setPayNo("1234567890");
        saleorder.setPreferentialTotal(6);
        saleorder.setSellerMemo("bbeizhu");
        saleorder.setShippingType(1);
        saleorder.setStepTradeDeliveryTime(time);
        saleorder.setTradeCreateTime(time);
        saleorder.setTradeFinishTime(time);
        saleorder.setTradePaiedTime(time);
        saleorder.setTradeModifiedTime(time);
        saleorder.setTradeStatus(2);
        saleorder.setTradeTotal(176);
        saleorder.setTradeType(0);
        saleorder.setShippingType(1);
        saleorder.setTradeId("DD000001234");
        saleorder.setTotal(100);
        saleorder.setPreferentialTotal(77);
        saleorder.setOrderDetails(new ArrayList<SaleOrderDetailEntity>());
   
        SaleOrderDetailEntity detail = new SaleOrderDetailEntity();
        detail.setProductName("������Ʒ");
        detail.setPtypeId("110890-9001");
        detail.setQty(1);
        detail.setPrice(70);
        detail.setTradeOriginalPrice(120);
        detail.setSkuId("1108909527");
        detail.setpreferentialtotal(50);
        detail.setPlatformPropertiesName("��ɫ_XL");
        saleorder.OrderDetails.add(detail);

        SaleOrderDetailEntity detail1 = new SaleOrderDetailEntity();
        detail1.setProductName("������Ʒ");
        detail1.setPtypeId("110890-9001");
        detail1.setQty(2);
        detail1.setPrice(0);
        detail1.setpreferentialtotal(116);
        detail1.setSkuId("1108909528");
        detail1.setTradeOriginalPrice(58);
        detail1.setPlatformPropertiesName("��ɫ_XL");
        saleorder.OrderDetails.add(detail1);
        orderList.add(saleorder);
        orders.orders=orderList;
        return orders;
    }*/
	
}
