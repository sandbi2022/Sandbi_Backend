<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>
<%@ page import="com.tradePair.TradePair"%>
	<%
		request.setCharacterEncoding("utf-8");
	

	JSONObject jsonObject = new JSONObject();
	ArrayList<HashMap> tradePairsInfo= TradePair.getTradePairs();
		for(HashMap<String, String> tradePairInfo:tradePairsInfo) {
			JSONObject tradePairs = new JSONObject();
			
			for(String tradePairKey:tradePairInfo.keySet()) {
				tradePairs.put(tradePairKey.toString(),tradePairInfo.get(tradePairKey));
			}
			jsonObject.put(tradePairInfo.get("TradePair"),tradePairs.toString());
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);
		

	%>