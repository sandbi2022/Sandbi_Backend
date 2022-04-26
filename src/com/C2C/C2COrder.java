
package com.C2C;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class C2COrder {

    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "1mhHKi6DQyMedmBN";

    public static void createOrder(String tradePair, String User, int tradeType, double amount, double maxAmount, double minAmount, double price) {
        C2CTrade pendingOrder = new C2CTrade(tradePair);
        String TID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        pendingOrder.setTid(TID);
        if (tradeType == 0) {//0是买单1是卖单
            pendingOrder.setBuyer(User);
        } else {
            pendingOrder.setSeller(User);
        }
        pendingOrder.setAmount(amount);
        pendingOrder.setMaxAmount(maxAmount);
        pendingOrder.setMinAmount(minAmount);
        pendingOrder.setAmount(amount);
        pendingOrder.setDoneAmount(0);
        pendingOrder.setPrice(price);
        pendingOrder.setTradeType(tradeType);
        pendingOrder.setTradeState(0);
        creatPendingC2CTrade(pendingOrder);
    }

    public static void acceptOrder(String UID, String TID, double amount, String tradePair) {
        C2CTrade competitorTrade = findPendingTrade(TID, tradePair);
        double doneAmount = competitorTrade.getDoneAmount();
        doneAmount = doneAmount + amount;

        C2CTrade historyC2CTrade = new C2CTrade(tradePair);
        String historyTID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        historyC2CTrade.setTid(historyTID);
        if (competitorTrade.getTradeType() == 1) {//0是买单1是卖单
            historyC2CTrade.setBuyer(UID);
            historyC2CTrade.setSeller(competitorTrade.getSeller());
        } else {
            historyC2CTrade.setBuyer(competitorTrade.getBuyer());
            historyC2CTrade.setSeller(UID);
            freeze(UID, tradePair, amount);
        }
        historyC2CTrade.setPrice(competitorTrade.getPrice());
        historyC2CTrade.setAmount(amount);
        creatHistoryC2CTrade(historyC2CTrade);
        C2CTrade pendingTrade = new C2CTrade(tradePair);
        pendingTrade.setTid(TID);
        pendingTrade.setDoneAmount(doneAmount);
        if (doneAmount == competitorTrade.getAmount()) {
            pendingTrade.setTradeState(2);
        } else {
            pendingTrade.setTradeState(1);
        }
        changePendingTrade(pendingTrade);

    }
    
    public static void payOrder(String TID, String tradePair) {
        String sql = "update C2CTrade." + tradePair + " set State = " + 1 + " where TID=\"" + TID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            C2CTrade historytTade = findHistoryTrade(TID, tradePair);
            unfreeze(historytTade.getSeller(), tradePair, historytTade.getAmount());

            exchange(historytTade.getSeller(), historytTade.getBuyer(), tradePair, historytTade.getAmount());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void finishOrder(String TID, String tradePair) {
        String sql = "update C2CTrade." + tradePair + " set State = " + 2 + " where TID=\"" + TID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            C2CTrade historytTade = findHistoryTrade(TID, tradePair);
            unfreeze(historytTade.getSeller(), tradePair, historytTade.getAmount());

            exchange(historytTade.getSeller(), historytTade.getBuyer(), tradePair, historytTade.getAmount());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void cancelAcceptOrder(String TID, String tradePair) {
        String sql = "update C2CTrade." + tradePair + " set State = " + 3 + " where TID=\"" + TID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            C2CTrade historytTade = findHistoryTrade(TID, tradePair);
            unfreeze(historytTade.getSeller(), tradePair, historytTade.getAmount());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void cancelPendingOrder(String TID, String tradePair) {
        String sql = "update C2CPendingTrade." + tradePair + " set State = " + 3 + " where TID=\"" + TID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            C2CTrade pendingTrade = findPendingTrade(TID, tradePair);
            if (pendingTrade.getTradeType() == 1) {
                unfreeze(pendingTrade.getSeller(), tradePair, (pendingTrade.getAmount() - pendingTrade.getDoneAmount()));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static C2CTrade findHistoryTrade(String TID, String tradePair) {

        C2CTrade historyTrade = new C2CTrade(tradePair);
        historyTrade.setTradeState(-1);
        String sql = "Select * from C2CTrade." + tradePair + " where TID=\"" + TID + "\" ORDER BY Price DESC limit 1";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                historyTrade.setTid(rs.getString("TID"));
                historyTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                historyTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                historyTrade.setBuyer(rs.getString("Buyer"));
                historyTrade.setSeller(rs.getString("Seller"));
                historyTrade.setTradeState(Integer.parseInt(rs.getString("State")));
                historyTrade.setTime(rs.getString("TTime"));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return historyTrade;
    }

    public static C2CTrade findPendingTrade(String TID, String tradePair) {

        C2CTrade pendingTrade = new C2CTrade(tradePair);
        pendingTrade.setTradeState(-1);
        String sql = "Select * from C2CPendingTrade." + tradePair + " where TID=\"" + TID + "\" ORDER BY Price DESC limit 1";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                pendingTrade.setTid(rs.getString("TID"));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setTradeType(Integer.parseInt(rs.getString("TradeType")));
                if (pendingTrade.getTradeType() == 0) {//0是买单1是卖单
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

    public static void creatPendingC2CTrade(C2CTrade pendingOrder) {
        String sql = "insert into C2CPendingTrade." + pendingOrder.getTradePair() + " value(\"" + pendingOrder.getTid() + "\",\"" + pendingOrder.getUser() + "\"," + pendingOrder.getAmount() + "," + pendingOrder.getMinAmount() + "," + pendingOrder.getMaxAmount() + "," + pendingOrder.getDoneAmount() + "," + pendingOrder.getPrice() + "," + pendingOrder.getTradeType() + "," + pendingOrder.getTradeState() + ")";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            if (pendingOrder.getTradeType() == 1) {
                freeze(pendingOrder.getSeller(), pendingOrder.getTradePair(), pendingOrder.getAmount());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void creatHistoryC2CTrade(C2CTrade C2CTrade) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = dateformat.format(date);

        String sql = "insert into C2CTrade." + C2CTrade.getTradePair() + " value(\"" + C2CTrade.getTid() + "\",\"" + C2CTrade.getSeller()+ "\",\"" + C2CTrade.getBuyer() + "\"," + C2CTrade.getAmount() + "," + C2CTrade.getPrice() + ",\"" + time + "\",0);";
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

    public static void changePendingTrade(C2CTrade pendingTrade) {
        String sql = "update C2CPendingTrade." + pendingTrade.getTradePair() + " set doneAmount = " + pendingTrade.getDoneAmount() + ", State = " + pendingTrade.getTradeState() + " where TID=\"" + pendingTrade.getTid() + "\";";
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

    public static void unfreeze(String user, String tradePair, double amount) {
        String currency = "";
        if (tradePair.equals("BTCUSD")) {
            currency = "FreezeBTC";
        }
        if (tradePair.equals("ETHUSD")) {
            currency = "FreezeETH";
        }
        if (tradePair.equals("BCHUSD")) {
            currency = "FreezeBCH";
        }
        if (tradePair.equals("USDTUSD")) {
            currency = "FreezeUSDT";
        }
        changeBalance(user, currency, getBalance(user, currency) - amount);
    }

    public static void freeze(String user, String tradePair, double amount) {
        String currency = "";
        if (tradePair.equals("BTCUSD")) {
            currency = "FreezeBTC";
        }
        if (tradePair.equals("ETHUSD")) {
            currency = "FreezeETH";
        }
        if (tradePair.equals("BCHUSD")) {
            currency = "FreezeBCH";
        }
        if (tradePair.equals("USDTUSD")) {
            currency = "FreezeUSDT";
        }
        changeBalance(user, currency, getBalance(user, currency) + amount);
    }

    public static void exchange(String seller, String buyer, String tradePair, double amount) {
        String currency = "";
        if (tradePair.equals("BTCUSD")) {
            currency = "BTC";
        }
        if (tradePair.equals("ETHUSD")) {
            currency = "ETH";
        }
        if (tradePair.equals("BCHUSD")) {
            currency = "BCH";
        }
        if (tradePair.equals("USDTUSD")) {
            currency = "USDT";
        }
        changeBalance(seller, currency, getBalance(seller, currency) - amount);
        changeBalance(buyer, currency, getBalance(buyer, currency) + amount);
    }

    public static void changeBalance(String user, String currency, double amount) {
        String sql = "update Sandbi.C2CBalance set " + currency + " = " + amount + " where UID=\"" + user + "\";";
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
        String sql = "select * from Sandbi.C2CBalance Where UID = '" + user + "';";
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
