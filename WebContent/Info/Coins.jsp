<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>
<%@ page import="com.ReadDoc"%>
<%@ page import="com.tradePair.TradePair"%>
	<%
		request.setCharacterEncoding("utf-8");
	
	JSONObject jsonObject = new JSONObject();
	HashMap<Integer, String> coins= TradePair.getCoins();
		for(Integer coinID:coins.keySet()) {
			jsonObject.put(coinID.toString(),coins.get(coinID));
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);
		


	%>