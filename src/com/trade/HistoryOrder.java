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

public class HistoryOrder {
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    

    public static ArrayList<Trade> findHistoryTrade(String tradePair) {
        String sql = "Select * from HistoryTrade." + tradePair + ";";
        ArrayList<Trade> historyTrades = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Trade historyTrade = new Trade(tradePair);
                historyTrade.setTid(rs.getString("TID"));
                historyTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                historyTrade.setPrice(Double.parseDouble(rs.getString("Price")));
                historyTrade.setBuyer(rs.getString("Buyer"));
                historyTrade.setSeller(rs.getString("Seller"));
                historyTrade.setTime(rs.getString("TTime"));
                historyTrades.add(historyTrade);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return historyTrades;
    }
    
    public static ArrayList<Trade> findUserHistoryTrade(String UID, String tradePair) {
        String sql = "Select * from HistoryTrade." + tradePair + " where Buyer = \""+ UID +"\" Or Seller = \""+ UID +"\";";
        //买单，return 最便宜卖单
        ArrayList<Trade> historyTrades = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Trade historyTrade = new Trade(tradePair);
                historyTrade.setTid(rs.getString("TID"));
                historyTrade.setAmount(Double.parseDouble(rs.getString("Amount")));
                historyTrade.setPrice(Double.parseDouble(rs.getString("Price")));

                historyTrade.setBuyer(rs.getString("Buyer"));
                historyTrade.setSeller(rs.getString("Seller"));
                historyTrade.setTime(rs.getString("TTime"));
                historyTrades.add(historyTrade);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return historyTrades;
    }
}
