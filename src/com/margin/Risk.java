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

package com.margin;

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

public class Risk {
	
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    
	public static double getAvailableBalance(String UID, int wallet, String currency) {
		Statement stmt = null;
		Connection conn = null;
		Double balance = 0.0;
		try {
			Class.forName(JDBC_DRIVER);
			JSONObject jsonObject = new JSONObject();
	        
			// 打开链接
			String tableName = getTableName(wallet);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql;
			sql = "select * from `" + tableName + "` Where UID = '"+UID+"';";
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				balance = Double.parseDouble(rs.getString(currency)) - Double.parseDouble(rs.getString("Freeze" + currency));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception ex) {
			Logger.getLogger("").log(Level.SEVERE, null, ex);
		}
		return balance;
	}
	
	public static double getBalance(String user, int wallet, String currency) {
        String sql = "select * from `" + getTableName(wallet) + "` Where UID = '" + user + "';";
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
	
	public static String getTableName(int wallet) {
		String tableName = "";
		if(wallet == 0) {
			tableName = "Balance";
		}
		if(wallet == 1) {
			tableName = "C2CBalance";
		}
		if(wallet == 2) {
			tableName = "MarginBalance";
		}
		return tableName;
	}
	
	public static double getRiskRate(String UID) {
		double totalAsset = getTotalAsset(UID);
		if(totalAsset == 0) {
			return 0;
		}
		double totalliability = getTotalLiability(UID);
		if(totalliability == 0 || totalAsset / totalliability > 3) {
			return 3;
		}
		return totalAsset / totalliability;
	}
	
	public static double getTotalAsset(String UID) {
		HashMap<String, String> balance = getMarginBalance(UID);
		double totalAsset = Double.parseDouble(balance.get("USDT"));
		totalAsset = totalAsset + Double.parseDouble(balance.get("BTC")) * Price.getPrice("BTCUSDT");
		totalAsset = totalAsset + Double.parseDouble(balance.get("ETH")) * Price.getPrice("ETHUSDT");
		totalAsset = totalAsset + Double.parseDouble(balance.get("BCH")) * Price.getPrice("BCHUSDT");
		return totalAsset;
	}
	
	public static double getTotalLiability(String UID) {
		HashMap<String, String> balance = getMarginBalance(UID);
		double totalAsset = Double.parseDouble(balance.get("DebtUSDT"));
		totalAsset = totalAsset + Double.parseDouble(balance.get("DebtBTC")) * Price.getPrice("BTCUSDT");
		totalAsset = totalAsset + Double.parseDouble(balance.get("DebtETH")) * Price.getPrice("ETHUSDT");
		totalAsset = totalAsset + Double.parseDouble(balance.get("DebtBCH")) * Price.getPrice("BCHUSDT");
		return totalAsset;
	}
	
	public static HashMap<String, String> getMarginBalance(String UID) {
		HashMap<String, String> balance = new HashMap<>();
		try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            String sql = "select * from `MarginBalance` Where UID = '"+UID+"';";
    		ResultSet rs = st.executeQuery(sql);
    		
    		if (rs.next()) {
    			
    			balance.put("USDT",rs.getString("USDT"));
    			balance.put("BTC",rs.getString("BTC"));
    			balance.put("ETH",rs.getString("ETH"));
    			balance.put("BCH",rs.getString("BCH"));
    			balance.put("FreezeUSDT",rs.getString("FreezeUSDT"));
    			balance.put("FreezeBTC",rs.getString("FreezeBTC"));
    			balance.put("FreezeETH",rs.getString("FreezeETH"));
    			balance.put("FreezeBCH",rs.getString("FreezeBCH"));
    			balance.put("DebtUSDT",rs.getString("DebtUSDT"));
    			balance.put("DebtBTC",rs.getString("DebtBTC"));
    			balance.put("DebtETH",rs.getString("DebtETH"));
    			balance.put("DebtBCH",rs.getString("DebtBCH"));
    		}
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
		return balance;
	}
	
	public static ArrayList<HashMap> getTradePair() {
		ArrayList<HashMap> tradepairs = new ArrayList<>();
		try {
			Class.forName(JDBC_DRIVER);
			JSONObject jsonObject = new JSONObject();
	        
			// 打开链接

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
}
