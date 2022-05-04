<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>
<%@ page import="com.ReadDoc"%>
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
		
		String JDBC_DRIVER = ReadDoc.getSqlInfo().get("JDBC_DRIVER").toString();
	    String DB_URL = ReadDoc.getSqlInfo().get("DB_URL").toString();
	    String USER = ReadDoc.getSqlInfo().get("USER").toString();
	    String PASS = ReadDoc.getSqlInfo().get("PASS").toString();
		Connection conn = null;
	Statement stmt = null;

	try {
		Class.forName(JDBC_DRIVER);
		JSONObject jsonObject = new JSONObject();
        
		// 打开链接

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "select * from Address.BTCTestNet Where UID = '"+UID+"';";
		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			
			jsonObject.put("PublicKey",rs.getString("PublicKey"));
			jsonObject.put("Address",rs.getString("Address"));
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