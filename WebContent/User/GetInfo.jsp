<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="com.Info"%>
    <%@ page import="java.io.*"%>
    <%@ page import="java.util.*"%>
    <%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
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
		String UID = result.get("UID").toString();
		
		Info info = Info.getInfo(UID);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(info.toString());
		response.setStatus(response.SC_OK);
	%>