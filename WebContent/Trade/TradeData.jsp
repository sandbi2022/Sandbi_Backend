<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.trade.TradeData"%>
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
		int period = Integer.parseInt(result.get("Period").toString());
		int second = Integer.parseInt(result.get("Second").toString());
		
		JSONObject jsonObject = new JSONObject();
		ArrayList<TradeData> tradeData = TradeData.getTradeData(period, second, tradePair);
		for(TradeData data : tradeData){
			jsonObject.put(Long.toString(data.getTimestamp()),data.toString());
		}
		Date date = new Date();
		jsonObject.put("time",date.getTime());
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);
		

	%>