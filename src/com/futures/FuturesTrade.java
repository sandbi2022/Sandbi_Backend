
package com.futures;

public class FuturesTrade {
	private String tradePair;
	private String TID;
	private String PendingTID;
	private String buyer;
	private String seller;
	private String time;
	private double amount;
	private double doneAmount;
	private double finishAmount;
	private double price;
	private double stopUpPrice;
	private double stopDownPrice;
	private double deposit;
	private int tradeType;//0是买单1是卖单
	private int tradeState;//0是未成交1是部分成交2是全部成交
	private int coefficient;
	
	public FuturesTrade(String tradePair){
		this.tradePair = tradePair;
	}
	
	public String getTradePair() {
		return tradePair;
	}
	
	public void setTID(String TID) {
		this.TID = TID;
	}
	
	public String getTID() {
		return TID;
	}
	
	public void setPendingTID(String PendingTID) {
		this.PendingTID = PendingTID;
	}
	
	public String getPendingTID() {
		return PendingTID;
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
	
	public void setFinishAmount(double finishAmount) {
		this.finishAmount = finishAmount;
	}
	
	public double getFinishAmount() {
		return finishAmount;
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
	
	public void setStopUpPrice(double stopUpPrice) {
		this.stopUpPrice = stopUpPrice;
	}
	
	public double getStopUpPrice() {
		return stopUpPrice;
	}
	
	public void setStopDownPrice(double stopDownPrice) {
		this.stopDownPrice = stopDownPrice;
	}
	
	public double getStopDownPrice() {
		return stopDownPrice;
	}
	
	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}
	
	public double getDeposit() {
		return deposit;
	}
	
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}
	
	public int getCoefficient() {
		return coefficient;
	}
	
	public void setTradeState(int tradeState) {
		this.tradeState = tradeState;
	}
	
	public int getTradeState() {
		return tradeState;
	}
        @Override
        public String toString(){
            return String.format("%s %s %s %s %f %f %f %d %d ", tradePair, TID, buyer, seller, amount, doneAmount, price, tradeType, tradeState);
        }
}