
package com.futures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FuturesOrder {

    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "1mhHKi6DQyMedmBN";

    public static void createOrder(String tradePair, String User, int tradeType, double amount, int coefficient, double price, double stopUpPrice, double stopDownPrice) {
    	FuturesTrade pendingTrade = new FuturesTrade(tradePair);
        String TID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        pendingTrade.setTID(TID);
        if (tradeType == 0) {//0是买单1是卖单
            pendingTrade.setBuyer(User);
        } else {
            pendingTrade.setSeller(User);
        }
        pendingTrade.setAmount(amount);
        pendingTrade.setCoefficient(coefficient);
        pendingTrade.setDeposit(amount * price);
        pendingTrade.setDoneAmount(0);
        pendingTrade.setFinishAmount(0);
        pendingTrade.setPrice(price);
        pendingTrade.setStopUpPrice(stopUpPrice);
        pendingTrade.setStopDownPrice(stopDownPrice);
        pendingTrade.setTradeType(tradeType);
        pendingTrade.setTradeState(0);
        creatPendingTrade(pendingTrade);

        double surplusAmount = amount;

        while (true) {
        	FuturesTrade competitorTrade = FindFuturesOrders.findOnePendingTrade((tradeType + 1) % 2, tradePair);

            if (competitorTrade.getTradeState() == -1) {
                return;
            }
            double competitorPrice = competitorTrade.getPrice();
            double competitorSurplusAmount = competitorTrade.getAmount() - competitorTrade.getDoneAmount();
            if (tradeType == 0 && competitorPrice > price) {//0是买单1是卖单
                return;
            }
            if (tradeType == 1 && competitorPrice < price) {//0是买单1是卖单
                return;
            }

            if (competitorSurplusAmount > surplusAmount) {
                pendingTrade.setDoneAmount(surplusAmount);
                pendingTrade.setTradeState(4);
                changePendingTrade(pendingTrade);
                competitorTrade.setDoneAmount(competitorTrade.getDoneAmount() + surplusAmount);
                competitorTrade.setTradeState(1);
                changePendingTrade(competitorTrade);

                FuturesTrade historyTrade = new FuturesTrade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTID(historyTID);
                if (tradeType == 0) {//0是买单1是卖单
                    historyTrade.setBuyer(User);
                    historyTrade.setSeller(competitorTrade.getSeller());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(User);
                }
                unfreeze(User, pendingTrade.getTradePair(), pendingTrade.getTradeType(), surplusAmount, pendingTrade.getPrice());
                unfreeze(competitorTrade.getUser(), competitorTrade.getTradePair(), competitorTrade.getTradeType(), surplusAmount, competitorTrade.getPrice());
                historyTrade.setAmount(surplusAmount);
                historyTrade.setPrice(competitorPrice);
                creatHistoryTrade(historyTrade);
                return;
            }
            if (competitorSurplusAmount == surplusAmount) {
                pendingTrade.setDoneAmount(surplusAmount);
                pendingTrade.setTradeState(4);
                changePendingTrade(pendingTrade);
                competitorTrade.setDoneAmount(competitorTrade.getAmount());
                competitorTrade.setTradeState(4);
                changePendingTrade(competitorTrade);

                FuturesTrade historyTrade = new FuturesTrade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTID(historyTID);
                if (tradeType == 0) {//0是买单1是卖单
                    historyTrade.setBuyer(User);
                    historyTrade.setSeller(competitorTrade.getSeller());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(User);
                }
                unfreeze(User, pendingTrade.getTradePair(), pendingTrade.getTradeType(), surplusAmount, pendingTrade.getPrice());
                unfreeze(competitorTrade.getUser(), competitorTrade.getTradePair(), competitorTrade.getTradeType(), surplusAmount, competitorTrade.getPrice());
                historyTrade.setAmount(surplusAmount);
                historyTrade.setPrice(competitorPrice);
                creatHistoryTrade(historyTrade);
                return;
            }
            if (competitorSurplusAmount < surplusAmount) {
                pendingTrade.setDoneAmount(competitorSurplusAmount);
                pendingTrade.setTradeState(1);
                changePendingTrade(pendingTrade);
                competitorTrade.setDoneAmount(competitorTrade.getAmount());
                competitorTrade.setTradeState(4);
                changePendingTrade(competitorTrade);
                surplusAmount = surplusAmount - competitorSurplusAmount;

                FuturesTrade historyTrade = new FuturesTrade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTID(historyTID);
                if (tradeType == 0) {//0是买单1是卖单
                    historyTrade.setBuyer(User);
                    historyTrade.setSeller(competitorTrade.getSeller());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(User);
                }
                unfreeze(User, pendingTrade.getTradePair(), pendingTrade.getTradeType(), competitorSurplusAmount, pendingTrade.getPrice());
                unfreeze(competitorTrade.getUser(), competitorTrade.getTradePair(), competitorTrade.getTradeType(), competitorSurplusAmount, competitorTrade.getPrice());
                historyTrade.setAmount(competitorSurplusAmount);
                historyTrade.setPrice(competitorPrice);
                creatHistoryTrade(historyTrade);
            }
        }
    }
    
    public static void cancelPendingOrder(String TID, String tradePair) {
    	FuturesTrade pendingTrade = FindFuturesOrders.findTradeByTID(TID, tradePair);
    	String sql;
    	if(pendingTrade.getDoneAmount() == 0) {
    		sql = "update C2CPendingTrade." + tradePair + " set State = " + 6 + " where TID=\"" + TID + "\";";
    	} else {
    		sql = "update C2CPendingTrade." + tradePair + " set State = " + 4 + " where TID=\"" + TID + "\";";
    	}
        
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            
            if (pendingTrade.getTradeType() == 1) {
                unfreeze(pendingTrade.getSeller(), tradePair,pendingTrade.getTradeType(), (pendingTrade.getAmount() - pendingTrade.getDoneAmount()), pendingTrade.getPrice());
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    

    public static void changePendingTrade(FuturesTrade pendingTrade) {
        String sql = "update PendingTrade." + pendingTrade.getTradePair() + " set doneAmount = " + pendingTrade.getDoneAmount() + ", State = " + pendingTrade.getTradeState() + " where TID=\"" + pendingTrade.getTID() + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void creatPendingTrade(FuturesTrade pendingTrade) {

        String sql = "insert into PendingTrade." + pendingTrade.getTradePair() ;
        sql = sql + " value(\"" + pendingTrade.getTID() + "\",\"" + pendingTrade.getUser() + "\"," + pendingTrade.getAmount() ;
        sql = sql + "," + pendingTrade.getDoneAmount() + "," + pendingTrade.getFinishAmount();
        sql = sql + "," + pendingTrade.getPrice() + "," + pendingTrade.getStopUpPrice()+ "," + pendingTrade.getStopDownPrice();
        sql = sql + "," + pendingTrade.getStopUpPrice()+ "," + pendingTrade.getStopDownPrice();
        sql = sql + "," + pendingTrade.getCoefficient()+ "," + pendingTrade.getDeposit();
        sql = sql + "," + pendingTrade.getTradeType() + "," + pendingTrade.getTradeState() + ");";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            freeze(pendingTrade.getUser(), pendingTrade.getTradePair(), pendingTrade.getTradeType(), pendingTrade.getAmount(), pendingTrade.getPrice());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void creatHistoryTrade(FuturesTrade historyTrade) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = dateformat.format(date);

        String sql = "insert into HistoryTrade." + historyTrade.getTradePair() + " value(\"" + historyTrade.getTID() + "\",\"" + historyTrade.getBuyer() + "\",\"" + historyTrade.getSeller() + "\"," + historyTrade.getAmount() + "," + historyTrade.getPrice() + ",\"" + time + "\");";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            change(historyTrade.getBuyer(), historyTrade.getTradePair(), 0, historyTrade.getAmount(), historyTrade.getPrice());
            change(historyTrade.getSeller(), historyTrade.getTradePair(), 1, historyTrade.getAmount(), historyTrade.getPrice());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void change(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        System.out.println(tradePair);
        if (tradePair.equals("BTCUSDT")) {
            currency = "BTC";
            currency1 = "USDT";
        }
        if (tradePair.equals("ETHUSDT")) {
            currency = "ETH";
            currency1 = "USDT";
        }
        if (tradePair.equals("BCHUSDT")) {
            currency = "BCH";
            currency1 = "USDT";
        }
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency, getBalance(user, currency) + amount * 0.999);
            changeBalance(user, currency1, getBalance(user, currency1) - amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) - amount);
            changeBalance(user, currency1, getBalance(user, currency1) + amount * price * 0.999);
            break;
        }

    }
    
    public static void unfreeze(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        if (tradePair.equals("BTCUSDT")) {
            currency = "FreezeBTC";
            currency1 = "FreezeUSDT";
        }
        if (tradePair.equals("ETHUSDT")) {
            currency = "FreezeETH";
            currency1 = "FreezeUSDT";
        }
        if (tradePair.equals("BCHUSDT")) {
            currency = "FreezeBCH";
            currency1 = "FreezeUSDT";
        }
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency1, getBalance(user, currency1) - amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) - amount);
        	break;
        }

    }

    public static void freeze(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        if (tradePair.equals("BTCUSDT")) {
            currency = "FreezeBTC";
            currency1 = "FreezeUSDT";
        }
        if (tradePair.equals("ETHUSDT")) {
            currency = "FreezeETH";
            currency1 = "FreezeUSDT";
        }
        if (tradePair.equals("BCHUSDT")) {
            currency = "FreezeBCH";
            currency1 = "FreezeUSDT";
        }
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency1, getBalance(user, currency1) + amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) + amount);
        	break;
        }
    }

    public static void changeBalance(String user, String currency, double amount) {
        String sql = "update Sandbi.Balance set " + currency + " = " + amount + " where UID=\"" + user + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static double getBalance(String user, String currency) {
        String sql = "select * from `Balance` Where UID = '" + user + "';";
        double amount = 0;
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                amount = Double.parseDouble(rs.getString(currency));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return amount;
    }
}
