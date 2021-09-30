package co.yixiang.modules.gjp.Lib;

import java.util.List;

public class SaleOrderEntity {
	private String BuyerMessage ;
    private float BuyerPayment ;
    private float CustomerFreightFee;
    public  BuyerEntity EShopBuyer ;
    private float Expressagencyfee ;
    private String FreightBillNO ;
    private String FreightCode;
    private String InvoiceTitle ;
    private float Mallfee ;
    public List<SaleOrderDetailEntity> OrderDetails ;
    private float PaiedTotal ;
    private String PayNo ;
    private float PreferentialTotal ;
    private int RefundStatus ;
    private int SellerFlag ;
    private String SellerMemo ;
    private int ShippingType ;
    private String ShopKey ;
    private String StepTradeDeliveryTime ;
    private int StepTradeStatus ;
    private float TaxAmount ;
    private float Taxfee ;
    private float TaxTotal ;
    private float Total ;
    private String TradeCreateTime ;
    private String TradeFinishTime ;
    private String TradeId ;
    private String TradeModifiedTime ;
    private String TradePaiedTime ;
    private int TradeStatus ;
    private float TradeTotal ;
    private int TradeType ;
    
    
    public void setBuyerMessage(String BuyerMessage) {
 		this.BuyerMessage = BuyerMessage;
    }
	public String getBuyerMessage() {
 		return BuyerMessage;
	}
   
	public void setBuyerPayment(float BuyerPayment) {
 		this.BuyerPayment = BuyerPayment;
	}
	public float getBuyerPayment() {
 		return BuyerPayment;
	}
   
    public void setCustomerFreightFee(float CustomerFreightFee) {
    	this.CustomerFreightFee = CustomerFreightFee;
    }
	public float getCustomerFreightFee() {
 		return CustomerFreightFee;
	}
   
    public void setExpressagencyfee(float Expressagencyfee) {
 		this.Expressagencyfee = Expressagencyfee;
    }
	public float getExpressagencyfee() {
 		return Expressagencyfee;
	}
   
    public void setFreightBillNO(String FreightBillNO) {
 		this.FreightCode = FreightBillNO;
    }
	public String getFreightBillNO() {
 		return FreightBillNO;
	}
    
    public void setFreightCode(String FreightCode) {
 		this.FreightCode = FreightCode;
    }
	public String getFreightCode() {
 		return FreightCode;
	}
    
    public void setInvoiceTitle(String InvoiceTitle) {
 		this.InvoiceTitle = InvoiceTitle;
    }
	public String getInvoiceTitle() {
 		return InvoiceTitle;
	}
    
	public void setMallfee(float Mallfee) {
 		this.Mallfee = Mallfee;
	}
	public float getMallfee() {
 		return Mallfee;
	}
    
    public void setPaiedTotal(float PaiedTotal) {
 		this.PaiedTotal = PaiedTotal;
    }
	public float getPaiedTotal() {
 		return PaiedTotal;
	}
  
    public void setPayNo(String PayNo) {
 		this.PayNo = PayNo;
    }
	public String getPayNo() {
 		return PayNo;
	}
  
    public void setPreferentialTotal(float PreferentialTotal) {
 		this.PreferentialTotal = PreferentialTotal;
    }
	public float getPreferentialTotal() {
 		return PreferentialTotal;
	}
  
     public void setRefundStatus(int RefundStatus) {
 		this.RefundStatus = RefundStatus;
     }
	public int getRefundStatus() {
 		return RefundStatus;
	}
   
   public void setSellerFlag(int SellerFlag) {
 		this.SellerFlag = SellerFlag;
   }
   public int getSellerFlag() {
 		return SellerFlag;
   }
  
   public void setSellerMemo(String SellerMemo) {
 		this.SellerMemo = SellerMemo;
   }
   public String getSellerMemo() {
	   return SellerMemo;
   }
  
   public void setShippingType(int ShippingType) {
	   this.ShippingType = ShippingType;
   }
   public int getShippingType() {
 		return ShippingType;
   }
  
   public void setShopKey(String ShopKey) {
 		this.ShopKey = ShopKey;
   }
   public String getShopKey() {
	   return ShopKey;
   }
  
   public void setStepTradeDeliveryTime(String StepTradeDeliveryTime) {
 		this.StepTradeDeliveryTime = StepTradeDeliveryTime;
   }
   public String getStepTradeDeliveryTime() {
 		return StepTradeDeliveryTime;
   }
  
   public void setStepTradeStatus(int StepTradeStatus) {
 		this.StepTradeStatus = StepTradeStatus;
   }
   public int getStepTradeStatus() {
 		return StepTradeStatus;
   }
  
   public void setTaxAmount(float TaxAmount) {
 		this.TaxAmount = TaxAmount;
   }
   public float getTaxAmount() {
 		return TaxAmount;
   }
  
   public void setTaxfee(float Taxfee) {
 		this.Taxfee = Taxfee;
   }
   public float getTaxfee() {
 		return Taxfee;
   }

   public void setTaxTotal(float TaxTotal) {
 		this.TaxTotal = TaxTotal;
   }
   public float getTaxTotal() {
 		return TaxTotal;
   }
  
   public void setTotal(float Total) {
 		this.Total = Total;
   }
   public float getTotal() {
 		return Total;
   }
  
   public void setTradeCreateTime(String TradeCreateTime) {
 		this.TradeCreateTime = TradeCreateTime;
   }
   public String getTradeCreateTime() {
 		return TradeCreateTime;
   }
  
   public void setTradeFinishTime(String TradeFinishTime) {
 		this.TradeFinishTime = TradeFinishTime;
   }
   public String getTradeFinishTime() {
 		return TradeFinishTime;
   }
 
   public void setTradeId(String TradeId) {
 		this.TradeId = TradeId;
   }
   public String getTradeId() {
 		return TradeId;
   }
 
   public void setTradeModifiedTime(String TradeModifiedTime) {
 		this.TradeModifiedTime = TradeModifiedTime;
   }
   public String getTradeModifiedTime() {
 		return TradeModifiedTime;
   }
  
   public void setTradePaiedTime(String TradePaiedTime) {
 		this.TradePaiedTime = TradePaiedTime;
   }
   public String getTradePaiedTime() {
 		return TradePaiedTime;
   }
  
   public void setTradeStatus(int TradeStatus) {
 		this.TradeStatus = TradeStatus;
   }
   public int getTradeStatus() {
 		return TradeStatus;
   }
    
   public void setTradeTotal(float TradeTotal) {
 		this.TradeTotal = TradeTotal;
   }
   public float getTradeTotal() {
 		return TradeTotal;
   }
   
   public void setTradeType(int TradeType) {
 		this.TradeType = TradeType;
   }
   public int getTradeType() {
 		return TradeType;
   }
   
   public BuyerEntity getEShopBuyer() {
	return EShopBuyer;
   }
   public void setEShopBuyer(BuyerEntity eShopBuyer) {
	EShopBuyer = eShopBuyer;
   }
   
   public List<SaleOrderDetailEntity> getOrderDetails() {
	return OrderDetails;
   }
   public void setOrderDetails(List<SaleOrderDetailEntity> orderDetails) {
	OrderDetails = orderDetails;
   }
   
}
