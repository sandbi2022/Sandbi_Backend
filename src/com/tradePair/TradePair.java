// Copyright (c) 2006 Damien Miller <djm@mindrot.org>
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

package com.tradePair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ReadDoc;
import com.alibaba.fastjson.JSONObject;
import com.price.Price;
import com.trade.Trade;

public class TradePair {
	
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    
	
	public static ArrayList<HashMap> getTradePairs() {
		ArrayList<HashMap> tradepairs = new ArrayList<>();
		try {
			Class.forName(JDBC_DRIVER);
			JSONObject jsonObject = new JSONObject();
	        
			// open link

			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
			String sql;
			sql = "select * from `TradePair` ;";
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				HashMap<String, String> tradePair = new HashMap<>();
				tradePair.put("Coin1",rs.getString("Coin1"));
				tradePair.put("Coin2",rs.getString("Coin2"));
				tradePair.put("LimitCount",rs.getString("LimitCount"));
				tradePair.put("LimitPrice",rs.getString("LimitPrice"));
				tradePair.put("TradePair",rs.getString("TradePair"));
				
				tradepairs.add(tradePair);
			}
			rs.close();

			st.close();
			con.close();
	} catch(Exception e) {
		System.out.println(e);
	}
		return tradepairs;
	}
	
	public static HashMap<String, String> getTradePair(String tradePair) {
		HashMap<String, String> tradePairInfo = new HashMap<>();
		try {
			Class.forName(JDBC_DRIVER);
			JSONObject jsonObject = new JSONObject();
	        
			// open link

			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
			String sql;
			sql = "select * from `TradePair` Where TradePair = \"" + tradePair + "\";";
			ResultSet rs = st.executeQuery(sql);
			
			if (rs.next()) {
				
				tradePairInfo.put("Coin1",rs.getString("Coin1"));
				tradePairInfo.put("Coin2",rs.getString("Coin2"));
				tradePairInfo.put("LimitCount",rs.getString("LimitCount"));
				tradePairInfo.put("LimitPrice",rs.getString("LimitPrice"));
				tradePairInfo.put("TradePair",rs.getString("TradePair"));
			}
			rs.close();

			st.close();
			con.close();
	} catch(Exception e) {
		System.out.println(e);
	}
		return tradePairInfo;
	}
	
	public static HashMap<Integer, String> getCoins() {
		HashMap<Integer, String> coinInfo = new HashMap<>();
		try {
			Class.forName(JDBC_DRIVER);
			JSONObject jsonObject = new JSONObject();
	        
			// open link

			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
			String sql;
			sql = "select * from `Coins` ;";
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
				
				coinInfo.put(Integer.parseInt(rs.getString("CoinID")),rs.getString("CoinName"));
			}
			rs.close();

			st.close();
			con.close();
	} catch(Exception e) {
		System.out.println(e);
	}
		return coinInfo;
	}
}
