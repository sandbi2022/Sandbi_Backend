<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>

	<%
		request.setCharacterEncoding("utf-8");
	

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
		sql = "select * from `TradePair` ;";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			JSONObject tradePair = new JSONObject();
			tradePair.put("Coin1",rs.getString("Coin1"));
			tradePair.put("Coin2",rs.getString("Coin2"));
			tradePair.put("LimitCount",rs.getString("LimitCount"));
			tradePair.put("LimitPrice",rs.getString("LimitPrice"));
			tradePair.put("TradePair",rs.getString("TradePair"));
			
			jsonObject.put(rs.getString("TradePair"),tradePair.toJSONString());
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