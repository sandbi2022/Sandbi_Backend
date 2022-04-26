
package com.price;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Price {
	
	private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	private static String USER = "root";
	private static String PASS = "1mhHKi6DQyMedmBN";
	
	public static double getPrice(String tradePair) {
        String sql = "select * from HistoryTrade." + tradePair + " ORDER BY TTime DESC limit 1;";
        double price = 0;
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                price = Double.parseDouble(rs.getString("Price"));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        if(price == 0) {
        	price = getOpenPrice(tradePair);
        }
        return price;
    }

    public static double getOpenPrice(String tradePair) {
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = dateformat.format(new Date(date.getTime() - 24 * 60 * 60 * 1000L));
        
        String sql = "select * from HistoryTrade." + tradePair + " WHERE TTime < \"" + time + "\" ORDER BY TTime DESC limit 1;";
        double price = 0;
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                price = Double.parseDouble(rs.getString("Price"));
            }
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return price;
    }
}