package com.trade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ReadDoc;
import com.alibaba.fastjson.JSONObject;

public class TradeData {

	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();

    private double open;
    private double close;
    private double high;
    private double low;
    private double volume;//成交量
    private double turnover;//成交额
    private long timestamp;

    public TradeData(double open, double close, double high, double low, double volume, double turnover, long timestamp) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.turnover = turnover;
        this.timestamp = timestamp;
    }
    
    public long getTimestamp() {
    	return this.timestamp;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("open", open);
        jsonObject.put("close", close);
        jsonObject.put("high", high);
        jsonObject.put("low", low);
        jsonObject.put("volume", volume);
        jsonObject.put("turnover", turnover);
        jsonObject.put("timestamp", timestamp);

        return jsonObject.toJSONString();
    }

    public static ArrayList<TradeData> getTradeData(int period, int second, String tradePair) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        Long startTime = ((long) Math.floor(date.getTime() / second / 1000) + 1) * second * 1000 - period * second * 1000L;
        
        String time = dateformat.format(new Date(startTime));
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
        
        sql = "select * from HistoryTrade." + tradePair + " WHERE TTime >= \"" + time + "\" ORDER BY TTime ASC;";
        ArrayList<Trade> trades = new ArrayList<>();
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
                trades.add(historyTrade);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(startTime);
        ArrayList<TradeData> data = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < period; i++) {

            double open = price;
            double close = price;
            double high = price;
            double low = price;
            double volume = 0;
            double turnover = 0;
            long timestamp = startTime + i * second * 1000L;
            Long tradeTime = 0L;
            try {
                if (j < trades.size()) {
                    tradeTime = format.parse(trades.get(j).getTime()).getTime();
                } else {
                    tradeTime = 0L;
                }

                while (timestamp <= tradeTime && tradeTime < timestamp + second * 1000L) {
                    Trade trade = trades.get(j);
                    price = trade.getPrice();
                    if (volume == 0) {
                        open = trade.getPrice();
                        close = trade.getPrice();
                        high = trade.getPrice();
                        low = trade.getPrice();
                        volume = trade.getAmount();
                        turnover = trade.getAmount() * trade.getPrice();
                    } else {
                        close = trade.getPrice();
                        if (trade.getPrice() > high) {
                            high = trade.getPrice();
                        }
                        if (trade.getPrice() < low) {
                            low = trade.getPrice();
                        }
                        volume = volume + trade.getAmount();
                        turnover = turnover + trade.getAmount() * trade.getPrice();
                    }
                    j = j + 1;
                    if (j < trades.size()) {
                        tradeTime = format.parse(trades.get(j).getTime()).getTime();
                    } else {
                        tradeTime = 0L;
                    }
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            data.add(new TradeData(open, close, high, low, volume, turnover, timestamp));

        }
        return data;
    }
}
