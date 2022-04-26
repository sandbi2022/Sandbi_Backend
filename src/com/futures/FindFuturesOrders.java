/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.futures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class FindFuturesOrders {
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "1mhHKi6DQyMedmBN";
    

    public static ArrayList<FuturesTrade> findHistoryTrade(String tradePair) {
        String sql = "Select * from FuturesTrade." + tradePair + ";";
        ArrayList<FuturesTrade> historyTrades = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	FuturesTrade historyTrade = new FuturesTrade(tradePair);
                historyTrade.setTID(rs.getString("TID"));
                historyTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                historyTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                historyTrade.setBuyer(rs.getString("Buyer"));
                historyTrade.setSeller(rs.getString("Seller"));
                historyTrade.setTime(rs.getString("TTime"));
                historyTrade.setTradeState(Integer.parseInt(rs.getString("State")));
                historyTrades.add(historyTrade);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return historyTrades;
    }
    
    public static ArrayList<FuturesTrade> findUserHistoryTrade(String UID, String tradePair) {
        String sql = "Select * from FuturesTrade." + tradePair + " where Buyer = \""+ UID +"\" Or Seller = \""+ UID +"\";";
        //买单，return 最便宜卖单
        ArrayList<FuturesTrade> historyTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	FuturesTrade historyTrade = new FuturesTrade(tradePair);
                historyTrade.setTID(rs.getString("TID"));
                historyTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                historyTrade.setPrice(Double.parseDouble(rs.getString("Price")));

                historyTrade.setBuyer(rs.getString("Buyer"));
                historyTrade.setSeller(rs.getString("Seller"));
                historyTrade.setTime(rs.getString("TTime"));
                historyTrade.setTradeState(Integer.parseInt(rs.getString("State")));
                historyTrades.add(historyTrade);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return historyTrades;
    }
    
    public static ArrayList<FuturesTrade> findPendingTrade(int tradeType, String tradePair) {
        String sql = "";
        ArrayList<FuturesTrade> pendingTrades = new ArrayList<>();
        if (tradeType == 0) {

            sql = "Select * from FuturesPendingTrade." + tradePair + " where TradeType=0 AND (State = 0 or State =1) ORDER BY Price DESC limit 100;";
        } else {
            sql = "Select * from FuturesPendingTrade." + tradePair + " where TradeType=1 AND (State = 0 or State =1) ORDER BY Price ASC limit 100;";
        }
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	FuturesTrade pendingTrade = new FuturesTrade(tradePair);
                pendingTrade.setTID(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                if (tradeType == 0) {
                    pendingTrade.setBuyer(rs.getString("User"));
                } else {
                    pendingTrade.setSeller(rs.getString("User"));
                }
                pendingTrade.setTradeType(tradeType);
                pendingTrade.setTradeState(Integer.parseInt(rs.getString("State")));
                pendingTrades.add(pendingTrade);
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return pendingTrades;
    }
    
    public static ArrayList<FuturesTrade> findUserPendingTrade(String UID, String tradePair) {
        String sql = "Select * from FuturesPendingTrade." + tradePair + " where User = \""+ UID +"\";";
        System.out.println(sql);
        //买单，return 最便宜卖单
        ArrayList<FuturesTrade> pendingTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	FuturesTrade pendingTrade = new FuturesTrade(tradePair);
                pendingTrade.setTID(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                pendingTrade.setBuyer(UID);
                pendingTrade.setTradeType(Integer.parseInt(rs.getString("TradeType")));
                pendingTrade.setTradeState(Integer.parseInt(rs.getString("State")));
                pendingTrades.add(pendingTrade);
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return pendingTrades;
    }
    
    public static FuturesTrade findTradeByTID(String TID, String tradePair) {
    	FuturesTrade pendingTrade = new FuturesTrade(tradePair);
        pendingTrade.setTradeState(-1);
        String sql = "Select * from PendingTrade." + tradePair + " where TID=\"" + TID + "\" ORDER BY Price DESC limit 1";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	pendingTrade.setTID(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
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
    
    public static FuturesTrade findOnePendingTrade(int tradeType, String tradePair) {

    	FuturesTrade pendingTrade = new FuturesTrade(tradePair);
        pendingTrade.setTradeState(-1);
        String sql = "";
        //买单，return 最便宜卖单

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
                pendingTrade.setTID(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                if (tradeType == 0) {//0是买单1是卖单
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
}

/*
 * CREATE TABLE `FuturesPendingTrade`.`BTCUSDT` (
  `TID` VARCHAR(45) NOT NULL,
  `User` VARCHAR(45) NULL,
  `Amount` DOUBLE NULL,
  `DoneAmount` DOUBLE NULL,
  `FinishAmount` DOUBLE NULL,
  `Price` DOUBLE NULL,
  `StopUpPrice` DOUBLE NULL,
  `StopDownPrice` DOUBLE NULL,
  `Coefficient` INT(11) NULL,
  `Deposit` DOUBLE NULL,
  `TradeType` INT(11) NULL,
  `State` INT(11) NULL,
  PRIMARY KEY (`TID`));


CREATE TABLE `FuturesTrade`.`BTCUSDT` (
  `TID` VARCHAR(45) NOT NULL,
  `PendingTID` VARCHAR(45) NULL,
  `Seller` VARCHAR(45) NULL,
  `Buyer` VARCHAR(45) NULL,
  `Amount` DOUBLE NULL,
  `Price` DOUBLE NULL,
  `TTime` DATETIME NULL,
  PRIMARY KEY (`TID`));

*/

