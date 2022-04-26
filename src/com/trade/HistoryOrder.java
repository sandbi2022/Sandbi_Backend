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

public class HistoryOrder {
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "1mhHKi6DQyMedmBN";
    

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
