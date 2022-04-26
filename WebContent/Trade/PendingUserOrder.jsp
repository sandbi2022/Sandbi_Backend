<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.trade.PendingOrder"%>
<%@ page import="com.trade.Trade"%>
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
		ArrayList<Trade> PendingOrders = PendingOrder.findUserPendingTrade(UID, tradePair);
		
		JSONObject jsonObject = new JSONObject();
		for(Trade trade:PendingOrders){
			JSONObject tradeJson = new JSONObject();
			tradeJson.put("tradePair", trade.getTradePair());
			tradeJson.put("Tid", trade.getTid());
			tradeJson.put("buyer", trade.getBuyer());
			tradeJson.put("seller", trade.getSeller());
			tradeJson.put("amount", trade.getAmount());
			tradeJson.put("doneAmount", trade.getDoneAmount());
			tradeJson.put("price", trade.getPrice());
			tradeJson.put("tradeType", trade.getTradeType());
			tradeJson.put("tradeState", trade.getTradeState());
			
			jsonObject.put(trade.getTid(),tradeJson.toJSONString());
		}
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);
	%>