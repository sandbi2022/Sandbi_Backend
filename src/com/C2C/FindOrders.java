/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.C2C;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.ReadDoc;

public class FindOrders {
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    

    public static ArrayList<C2CTrade> findHistoryTrade(String tradePair) {
        String sql = "Select * from C2CTrade." + tradePair + ";";
        ArrayList<C2CTrade> historyTrades = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	C2CTrade historyTrade = new C2CTrade(tradePair);
                historyTrade.setTid(rs.getString("TID"));
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
    
    public static ArrayList<C2CTrade> findUserHistoryTrade(String UID, String tradePair) {
        String sql = "Select * from C2CTrade." + tradePair + " where Buyer = \""+ UID +"\" Or Seller = \""+ UID +"\";";
        //买单，return 最便宜卖单
        ArrayList<C2CTrade> historyTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	C2CTrade historyTrade = new C2CTrade(tradePair);
                historyTrade.setTid(rs.getString("TID"));
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
    
    public static ArrayList<C2CTrade> findPendingTrade(int tradeType, String tradePair) {
        String sql = "";
        ArrayList<C2CTrade> pendingTrades = new ArrayList<>();
        if (tradeType == 0) {

            sql = "Select * from C2CPendingTrade." + tradePair + " where TradeType=0 AND (State = 0 or State =1) ORDER BY Price DESC limit 100;";
        } else {
            sql = "Select * from C2CPendingTrade." + tradePair + " where TradeType=1 AND (State = 0 or State =1) ORDER BY Price ASC limit 100;";
        }
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	C2CTrade pendingTrade = new C2CTrade(tradePair);
                pendingTrade.setTid(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setMinAmount(Double.parseDouble(rs.getString("MinAmount")));
                pendingTrade.setMaxAmount(Double.parseDouble(rs.getString("MaxAmount")));
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
    
    public static ArrayList<C2CTrade> findUserPendingTrade(String UID, String tradePair) {
        String sql = "Select * from C2CPendingTrade." + tradePair + " where User = \""+ UID +"\";";
        System.out.println(sql);
        //买单，return 最便宜卖单
        ArrayList<C2CTrade> pendingTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	C2CTrade pendingTrade = new C2CTrade(tradePair);
                pendingTrade.setTid(rs.getString("TID"));
                pendingTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                pendingTrade.setDoneAmount(Double.parseDouble(rs.getString("DoneAmount")));
                pendingTrade.setMinAmount(Double.parseDouble(rs.getString("MinAmount")));
                pendingTrade.setMaxAmount(Double.parseDouble(rs.getString("MaxAmount")));
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
}

/*
 * CREATE TABLE `C2CPendingTrade`.`BCHUSD` (
  `TID` VARCHAR(45) NOT NULL,
  `User` VARCHAR(45) NULL,
  `Amount` DOUBLE NULL,
  `MinAmount` DOUBLE NULL,
  `MaxAmount` DOUBLE NULL,
  `DoneAmount` DOUBLE NULL,
  `Price` DOUBLE NULL,
  `TradeType` INT(11) NULL,
  `State` INT(11) NULL,
  PRIMARY KEY (`TID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
*/
