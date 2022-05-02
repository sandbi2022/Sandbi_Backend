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

package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;
import com.trade.Trade;

public class Manage {
	
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
	        
			// ´ò¿ªÁ´½Ó
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
	
	public static boolean manage(String UID, int manageFrom, int manageTo, String currency, double amount) {
		if(amount < getAvailableBalance(UID, manageFrom, currency)) {
			return false;
		}
		String sql = "update Sandbi."+getTableName(manageFrom)+" set " + currency + " = " + (getBalance(UID, manageFrom, currency) - amount) + " where UID=\"" + UID + "\";";
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            sql = "update Sandbi."+getTableName(manageTo)+" set " + currency + " = " + (getBalance(UID, manageTo, currency) + amount) + " where UID=\"" + UID + "\";";
            st.execute(sql);
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
		return true;
	}
}
