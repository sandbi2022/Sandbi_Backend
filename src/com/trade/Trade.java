
package com.trade;

public class Trade {
	private String tradePair;
	private String Tid;
	private String buyer;
	private String seller;
	private String time;
	private double amount;
	private double doneAmount;
	private double price;
	private int tradeType;//0是买单1是卖单
	private int buyerTradeType;
	private int sellerTradeType;
	private int tradeState;//0是未成交1是部分成交2是全部成交3取消订单
	
	public Trade(String tradePair){
		this.tradePair = tradePair;
	}
	
	public String getTradePair() {
		return tradePair;
	}
	
	public void setTid(String Tid) {
		this.Tid = Tid;
	}
	
	public String getTid() {
		return Tid;
	}
	
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	
	public String getBuyer() {
		return buyer;
	}
	
	public void setSeller(String seller) {
		this.seller = seller;
	}
	
	public String getSeller() {
		return seller;
	}
	
	public String getUser() {
		if(tradeType == 0) {
			return buyer;
		}
		return seller;
	}
    
    public void setTime(String time) {
    	this.time = time;
    }

    public String getTime() {
    	return time;
    }
	
	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}
	
	public int getTradeType() {
		return tradeType;
	}
	
	public void setBuyerTradeType(int buyerTradeType) {
		this.buyerTradeType = buyerTradeType;
	}
	
	public int getBuyerTradeType() {
		return buyerTradeType;
	}
	
	public void setSellerTradeType(int sellerTradeType) {
		this.sellerTradeType = sellerTradeType;
	}
	
	public int getSellerTradeType() {
		return sellerTradeType;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setDoneAmount(double doneAmount) {
		this.doneAmount = doneAmount;
	}
	
	public double getDoneAmount() {
		return doneAmount;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setTradeState(int tradeState) {
		this.tradeState = tradeState;
	}
	
	public int getTradeState() {
		return tradeState;
	}
        @Override
        public String toString(){
            return String.format("%s %s %s %s %f %f %f %d %d ", tradePair, Tid, buyer, seller, amount, doneAmount, price, tradeType, tradeState);
        }
}