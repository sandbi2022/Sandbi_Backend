<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.mail.TestJavaMail"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
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
	String Email = result.get("Email").toString();
	String code = result.get("Code").toString();
	TestJavaMail.mail(Email, code); %>
</body>
</html>