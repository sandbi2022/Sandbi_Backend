
package com.trade;

import java.sql.*;
import java.util.UUID;

import com.ReadDoc;

import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

public class NewTrade {

	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();

    //First obtain the tradable orders from the Pending Trade in the database for trading and write them into History Trade, and then write them into Pending Trade if there is still an amount remaining after all tradable orders are traded.
    public static void newTrade(String tradePair, String UID, double amount, double price, int tradeType) {
        Trade pendingTrade = new Trade(tradePair);
        String TID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        pendingTrade.setTid(TID);
        if (tradeType % 2 == 0) {//0 is a buy order 1 is a sell order
            pendingTrade.setBuyer(UID);
        } else {
            pendingTrade.setSeller(UID);
        }
        pendingTrade.setAmount(amount);
        pendingTrade.setDoneAmount(0);
        pendingTrade.setPrice(price);
        pendingTrade.setTradeType(tradeType);
        pendingTrade.setTradeState(0);
        creatPendingTrade(pendingTrade);

        double surplusAmount = amount;

        while (true) {
            Trade competitorTrade = findPendingTrade((tradeType + 1) % 2, tradePair);

            if (competitorTrade.getTradeState() == -1) {
                return;
            }
            double competitorPrice = competitorTrade.getPrice();
            double competitorSurplusAmount = competitorTrade.getAmount() - competitorTrade.getDoneAmount();
            if (tradeType % 2 == 0 && competitorPrice > price) {//0 is a buy order 1 is a sell order
                return;
            }
            if (tradeType % 2 == 1 && competitorPrice < price) {//0 is a buy order 1 is a sell order
                return;
            }

            if (competitorSurplusAmount > surplusAmount) {
                pendingTrade.setDoneAmount(surplusAmount);
                pendingTrade.setTradeState(2);
                changePendingTrade(pendingTrade);
                competitorTrade.setDoneAmount(competitorTrade.getDoneAmount() + surplusAmount);
                competitorTrade.setTradeState(1);
                changePendingTrade(competitorTrade);

                Trade historyTrade = new Trade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTid(historyTID);
                if (tradeType % 2 == 0) {//0 is a buy order 1 is a sell order
                    historyTrade.setBuyer(UID);
                    historyTrade.setSeller(competitorTrade.getSeller());
                    historyTrade.setBuyerTradeType(tradeType);
                    historyTrade.setSellerTradeType(competitorTrade.getTradeType());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(UID);
                    historyTrade.setBuyerTradeType(competitorTrade.getTradeType());
                    historyTrade.setSellerTradeType(tradeType);
                }
                unfreeze(UID, pendingTrade.getTradePair(), pendingTrade.getTradeType(), surplusAmount, pendingTrade.getPrice());
                unfreeze(competitorTrade.getUser(), competitorTrade.getTradePair(), competitorTrade.getTradeType(), surplusAmount, competitorTrade.getPrice());
                historyTrade.setAmount(surplusAmount);
                historyTrade.setPrice(competitorPrice);
                creatHistoryTrade(historyTrade);
                return;
            }
            if (competitorSurplusAmount == surplusAmount) {
                pendingTrade.setDoneAmount(surplusAmount);
                pendingTrade.setTradeState(2);
                changePendingTrade(pendingTrade);
                competitorTrade.setDoneAmount(competitorTrade.getAmount());
                competitorTrade.setTradeState(2);
                changePendingTrade(competitorTrade);

                Trade historyTrade = new Trade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTid(historyTID);
                if (tradeType % 2 == 0) {//0 is a buy order 1 is a sell order
                    historyTrade.setBuyer(UID);
                    historyTrade.setSeller(competitorTrade.getSeller());
                    historyTrade.setBuyerTradeType(tradeType);
                    historyTrade.setSellerTradeType(competitorTrade.getTradeType());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(UID);
                    historyTrade.setBuyerTradeType(competitorTrade.getTradeType());
                    historyTrade.setSellerTradeType(tradeType);
                }
                unfreeze(UID, pendingTrade.getTradePair(), pendingTrade.getTradeType(), surplusAmount, pendingTrade.getPrice());
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
                competitorTrade.setTradeState(2);
                changePendingTrade(competitorTrade);
                surplusAmount = surplusAmount - competitorSurplusAmount;

                Trade historyTrade = new Trade(tradePair);
                String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                historyTrade.setTid(historyTID);
                if (tradeType % 2 == 0) {//0 is a buy order 1 is a sell order
                    historyTrade.setBuyer(UID);
                    historyTrade.setSeller(competitorTrade.getSeller());
                    historyTrade.setBuyerTradeType(tradeType);
                    historyTrade.setSellerTradeType(competitorTrade.getTradeType());
                } else {
                    historyTrade.setBuyer(competitorTrade.getBuyer());
                    historyTrade.setSeller(UID);
                    historyTrade.setBuyerTradeType(competitorTrade.getTradeType());
                    historyTrade.setSellerTradeType(tradeType);
                }
                unfreeze(UID, pendingTrade.getTradePair(), pendingTrade.getTradeType(), competitorSurplusAmount, pendingTrade.getPrice());
                unfreeze(competitorTrade.getUser(), competitorTrade.getTradePair(), competitorTrade.getTradeType(), competitorSurplusAmount, competitorTrade.getPrice());
                historyTrade.setAmount(competitorSurplusAmount);
                historyTrade.setPrice(competitorPrice);
                creatHistoryTrade(historyTrade);
            }
        }

    }
    
    public static void cancelPendingOrder(String TID, String tradePair) {
        String sql = "update PendingTrade." + tradePair + " set State = " + 3 + " where TID=\"" + TID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            Trade pendingTrade = findTradeByTID(TID, tradePair);
            if (pendingTrade.getTradeType() % 2 == 1) {
                unfreeze(pendingTrade.getSeller(), tradePair,pendingTrade.getTradeType(), (pendingTrade.getAmount() - pendingTrade.getDoneAmount()), pendingTrade.getPrice());
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static Trade findTradeByTID(String TID, String tradePair) {
    	Trade pendingTrade = new Trade(tradePair);
        pendingTrade.setTradeState(-1);
        String sql = "Select * from PendingTrade." + tradePair + " where TID=\"" + TID + "\" ORDER BY Price DESC limit 1";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	pendingTrade.setTid(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                pendingTrade.setTradeType(Integer.parseInt(rs.getString("TradeType")));
                if (pendingTrade.getTradeType() % 2 == 0) {
                    pendingTrade.setBuyer(rs.getString("User"));
                } else {
                    pendingTrade.setSeller(rs.getString("User"));
                }
                
                pendingTrade.setTradeState(Integer.parseInt(rs.getString("State")));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return pendingTrade;
    }

    public static Trade findPendingTrade(int tradeType, String tradePair) {

        Trade pendingTrade = new Trade(tradePair);
        pendingTrade.setTradeState(-1);
        String sql = "";
        //buy order, return cheapest sell order

        if (tradeType == 0) {

            sql = "Select * from PendingTrade." + tradePair + " where (TradeType=0 or TradeType =2) AND (State = 0 or State =1) ORDER BY Price DESC limit 1";
        } else {
            sql = "Select * from PendingTrade." + tradePair + " where (TradeType=1 or TradeType =3) AND (State = 0 or State =1) ORDER BY Price ASC limit 1;";
        }
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                pendingTrade.setTid(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                if (tradeType == 0) {//0 is a buy order 1 is a sell order
                    pendingTrade.setBuyer(rs.getString("User"));
                } else {
                    pendingTrade.setSeller(rs.getString("User"));
                }
                pendingTrade.setTradeType(Integer.parseInt(rs.getString("TradeType")));
                pendingTrade.setTradeState(Integer.parseInt(rs.getString("State")));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return pendingTrade;
    }

    public static void changePendingTrade(Trade pendingTrade) {
        String sql = "update PendingTrade." + pendingTrade.getTradePair() + " set doneAmount = " + pendingTrade.getDoneAmount() + ", State = " + pendingTrade.getTradeState() + " where TID=\"" + pendingTrade.getTid() + "\";";
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

    public static void creatPendingTrade(Trade pendingTrade) {
        String user = "";
        if (pendingTrade.getTradeType() % 2 == 0) {
            user = pendingTrade.getBuyer();
        } else {
            user = pendingTrade.getSeller();
        }
        String sql = "insert into PendingTrade." + pendingTrade.getTradePair() + " value(\"" + pendingTrade.getTid() + "\",\"" + user + "\"," + pendingTrade.getAmount() + "," + pendingTrade.getDoneAmount() + "," + pendingTrade.getPrice() + "," + pendingTrade.getTradeType() + "," + pendingTrade.getTradeState() + ")";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            freeze(user, pendingTrade.getTradePair(), pendingTrade.getTradeType(), pendingTrade.getAmount(), pendingTrade.getPrice());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void creatHistoryTrade(Trade historyTrade) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = dateformat.format(date);

        String sql = "insert into HistoryTrade." + historyTrade.getTradePair() + " value(\"" + historyTrade.getTid() + "\",\"" + historyTrade.getBuyer() + "\",\"" + historyTrade.getSeller() + "\"," + historyTrade.getAmount() + "," + historyTrade.getPrice() + ",\"" + time + "\");";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            change(historyTrade.getBuyer(), historyTrade.getTradePair(), historyTrade.getBuyerTradeType(), historyTrade.getAmount(), historyTrade.getPrice());
            change(historyTrade.getSeller(), historyTrade.getTradePair(), historyTrade.getSellerTradeType(), historyTrade.getAmount(), historyTrade.getPrice());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static HashMap<String, String> getTradeCurrency(String tradePair){
    	HashMap<String, String> pair = new HashMap<>();
		try {
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement st = con.createStatement();
			String sql;
			sql = "select * from `TradePair` ;";
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
				if(rs.getString("TradePair").equals(tradePair))
				pair.put("Coin1",rs.getString("Coin1"));
				pair.put("Coin2",rs.getString("Coin2"));
				
			}
			rs.close();

			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return pair;
    }

    public static void change(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        System.out.println(tradePair);
        HashMap<String, String> hashPaie = getTradeCurrency(tradePair);
        currency = hashPaie.get("Coin1");
        currency1 = hashPaie.get("Coin2");
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency, getBalance(user, currency) + amount * 0.999);
            changeBalance(user, currency1, getBalance(user, currency1) - amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) - amount);
            changeBalance(user, currency1, getBalance(user, currency1) + amount * price * 0.999);
            break;
        case 2:
        	changeMarginBalance(user, currency, getMarginBalance(user, currency) + amount * 0.999);
        	changeMarginBalance(user, currency1, getMarginBalance(user, currency1) - amount * price);
        	break;
        case 3:
        	changeMarginBalance(user, currency, getMarginBalance(user, currency) - amount);
        	changeMarginBalance(user, currency1, getMarginBalance(user, currency1) + amount * price * 0.999);
        	break;
        }

    }
    
    public static void unfreeze(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        HashMap<String, String> hashPair = getTradeCurrency(tradePair);
        currency = "Freeze" + hashPair.get("Coin1");
        currency1 = "Freeze" + hashPair.get("Coin2");
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency1, getBalance(user, currency1) - amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) - amount);
        	break;
        case 2:
        	changeMarginBalance(user, currency1, getMarginBalance(user, currency1) - amount * price);
        	break;
        case 3:
        	changeMarginBalance(user, currency, getMarginBalance(user, currency) - amount);
        	break;
        }

    }

    public static void freeze(String user, String tradePair, int tradeType, double amount, double price) {
        String currency = "", currency1 = "";
        HashMap<String, String> hashPair = getTradeCurrency(tradePair);
        currency = "Freeze" + hashPair.get("Coin1");
        currency1 = "Freeze" + hashPair.get("Coin2");
        switch(tradeType) {
        case 0:
        	changeBalance(user, currency1, getBalance(user, currency1) + amount * price);
        	break;
        case 1:
        	changeBalance(user, currency, getBalance(user, currency) + amount);
        	break;
        case 2:
        	changeMarginBalance(user, currency1, getMarginBalance(user, currency1) + amount * price);
        	break;
        case 3:
        	changeMarginBalance(user, currency, getMarginBalance(user, currency) + amount);
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
    public static void changeMarginBalance(String user, String currency, double amount) {
        String sql = "update Sandbi.MarginBalance set " + currency + " = " + amount + " where UID=\"" + user + "\";";
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

    public static double getMarginBalance(String user, String currency) {
        String sql = "select * from `MarginBalance` Where UID = '" + user + "';";
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

/*CREATE TABLE `Sandbi`.`MarginBalance` (
`UID` CHAR(45) NOT NULL,
`USDT` DOUBLE NULL,
`BTC` DOUBLE NULL,
`ETH` DOUBLE NULL,
`BCH` DOUBLE NULL,
`FreezeUSDT` DOUBLE NULL,
`FreezeBTC` DOUBLE NULL,
`FreezeETH` DOUBLE NULL,
`FreezeBCH` DOUBLE NULL,
PRIMARY KEY (`UID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
*/