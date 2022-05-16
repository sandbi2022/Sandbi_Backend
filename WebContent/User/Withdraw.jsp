<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="com.Manage"%>
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
		String Address = result.get("Address").toString();
		Double Amount = Double.parseDouble(result.get("Amount").toString());
		try {
			String cmd = "python3 /opt/tomcat/withdraw.py " + Address + " " + Amount;
			//String cmd = "ls";
			System.out.println(cmd);
			Process process = Runtime.getRuntime().exec(cmd);
			//int status = process.waitFor();
			//if(status != 0){
			//	System.err.println("Failed to call shell's command and the return status's is: " + status);
			//}
//	            process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
			
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line1 = "";
			while ((line1 = error.readLine()) != null) {
				System.out.println(line1);
			}
			error.close();
			
			
			Manage.change(UID, 0, "ALGO", Amount);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		response.setStatus(response.SC_OK);
	%>