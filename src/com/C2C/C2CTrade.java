
package com.C2C;

public class C2CTrade {
	private String tradePair;
	private String Tid;
	private String buyer;
	private String seller;
	private String time;
	private double amount;
	private double maxAmount;
	private double minAmount;
	private double doneAmount;
	private double price;
	private int tradeType;//0 is a buy order 1 is a sell order
	private int tradeState;//0 means no deal 1 means partial deal 2 means all deal
	
	public C2CTrade(String tradePair){
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
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}
	
	public double getMaxAmount() {
		return maxAmount;
	}
	
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}
	
	public double getMinAmount() {
		return minAmount;
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