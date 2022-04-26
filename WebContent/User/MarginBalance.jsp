<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>

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
		
		String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		// 数据库的用户名与密码，需要根据自己的设置
		String USER = "root";
		String PASS = "1mhHKi6DQyMedmBN";
		Connection conn = null;
	Statement stmt = null;

	try {
		Class.forName(JDBC_DRIVER);
		JSONObject jsonObject = new JSONObject();
        
		// 打开链接

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "select * from `MarginBalance` Where UID = '"+UID+"';";
		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			
			jsonObject.put("USDT",rs.getString("USDT"));
			jsonObject.put("BTC",rs.getString("BTC"));
			jsonObject.put("ETH",rs.getString("ETH"));
			jsonObject.put("BCH",rs.getString("BCH"));
			jsonObject.put("FreezeUSDT",rs.getString("FreezeUSDT"));
			jsonObject.put("FreezeBTC",rs.getString("FreezeBTC"));
			jsonObject.put("FreezeETH",rs.getString("FreezeETH"));
			jsonObject.put("FreezeBCH",rs.getString("FreezeBCH"));
		}
		rs.close();

		stmt.close();
		conn.close();

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);
		

	} catch (Exception ex) {
		Logger.getLogger("").log(Level.SEVERE, null, ex);
		response.setStatus(response.SC_BAD_REQUEST,"error");
	}
	%>