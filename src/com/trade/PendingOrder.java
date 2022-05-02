/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.ReadDoc;


public class PendingOrder {
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    public static ArrayList<Trade> findPendingTrade(int tradeType, String tradePair) {
        String sql = "";
        ArrayList<Trade> pendingTrades = new ArrayList<>();
        if (tradeType == 0) {

            sql = "Select * from PendingTrade." + tradePair + " where TradeType=0 AND (State = 0 or State =1) ORDER BY Price DESC limit 100;";
        } else {
            sql = "Select * from PendingTrade." + tradePair + " where TradeType=1 AND (State = 0 or State =1) ORDER BY Price ASC limit 100;";
        }
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Trade pendingTrade = new Trade(tradePair);
                pendingTrade.setTid(rs.getString("TID"));
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
    
    public static ArrayList<Trade> findUserPendingTrade(String UID, String tradePair) {
        String sql = "Select * from PendingTrade." + tradePair + " where User = \""+ UID +"\";";
        System.out.println(sql);
        //买单，return 最便宜卖单
        ArrayList<Trade> pendingTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Trade pendingTrade = new Trade(tradePair);
                pendingTrade.setTid(rs.getString("TID"));
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
}
