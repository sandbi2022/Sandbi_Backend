<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.trade.NewTrade"%>
	<%
		request.setCharacterEncoding("utf-8");
	
	HashMap result;
	StringBuilder sb = new StringBuilder();
	   try (BufferedReader reader = request.getReader();) {
	       char[] buff = new char[1024];
	       int len;
	       while ((len = reader.read(buff)) != -1) {
	           sb.append(buff, 0, len);
	       }
	       result=JSON.parseObject(sb.toString(),HashMap.class);
	   } catch (IOException e) {
		   result = new HashMap();
	   }
		String tradePair = result.get("TradePair").toString();
		String UID = result.get("UID").toString();
		double amount = Double.parseDouble(result.get("Amount").toString());
		double price = Double.parseDouble(result.get("Price").toString());
		int tradeType = Integer.parseInt(result.get("TradeType").toString());
		NewTrade.newTrade(tradePair, UID, amount, price, tradeType);

	%>