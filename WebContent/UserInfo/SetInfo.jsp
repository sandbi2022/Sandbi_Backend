<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="com.dashboard.Info"%>
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
		String First = result.get("First").toString();
		String Middle = result.get("Middle").toString();
		String Last = result.get("Last").toString();
		String Birth = result.get("Birth").toString();
		String Gender = result.get("Gender").toString();
		String SSN = result.get("SSN").toString();
		
		Info info = new Info(UID);
		info.setFirst(First);
		info.setMiddle(Middle);
		info.setLast(Last);
		info.setSSN(SSN);
		info.setBirth(Integer.parseInt(Birth));
		info.setGender(Integer.parseInt(Gender));
		Info.setInfo(info);
		
		response.setStatus(response.SC_OK);
	%>