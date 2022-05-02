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

import com.alibaba.fastjson.JSONObject;
import com.trade.Trade;

public class Info {
	
	private static String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
    private static String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
    private static String USER = ReadDoc.getSqlInfo().get("USER").toString();
    private static String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
    
	private String UID;
	private String First;
	private String Middle;
	private String Last;
	private String SSN;
	private int Gender;
	private int Birth;
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getFirst() {
		return First;
	}
	public void setFirst(String first) {
		First = first;
	}
	public String getMiddle() {
		return Middle;
	}
	public void setMiddle(String middle) {
		Middle = middle;
	}
	public String getLast() {
		return Last;
	}
	public void setLast(String last) {
		Last = last;
	}
	public String getSSN() {
		return SSN;
	}
	public void setSSN(String sSN) {
		SSN = sSN;
	}
	public int getGender() {
		return Gender;
	}
	public void setGender(int gender) {
		Gender = gender;
	}
	public int getBirth() {
		return Birth;
	}
	public void setBirth(int birth) {
		Birth = birth;
	}
	public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UID", UID);
        jsonObject.put("First", First);
        jsonObject.put("Middle", Middle);
        jsonObject.put("Last", Last);
        jsonObject.put("SSN", SSN);
        jsonObject.put("Gender", Gender);
        jsonObject.put("Birth", Birth);

        return jsonObject.toJSONString();
    }
	
	public Info(String UID) {
		this.UID = UID;
	}
	public static Info getInfo(String UID) {
		Info info = new Info(UID);
		String sql = "select * from Sandbi.Info WHERE UID = \"" + UID + "\";";
        ArrayList<Trade> trades = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	
            	info.setUID(rs.getString("UID"));
            	info.setFirst(rs.getString("First"));
            	info.setMiddle(rs.getString("Middle"));
            	info.setLast(rs.getString("Last"));
            	info.setSSN(rs.getString("SSN"));
            	info.setGender(Integer.parseInt(rs.getString("Gender")));
            	info.setBirth(Integer.parseInt(rs.getString("Birth")));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
		return info;
	}
	
	public static void setInfo(Info info) {
		String sql = "update Sandbi.Info set First = \"" + info.getFirst() + "\", Middle = \"" + info.getMiddle() + "\", Last = \"" + info.getLast() + "\", SSN = \"" + info.getSSN() + "\", Gender = " + info.getGender() + ", Birth = " + info.getBirth() + " where UID=\"" + info.getUID() + "\";";
        System.out.println(sql);
		try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
	}
}
