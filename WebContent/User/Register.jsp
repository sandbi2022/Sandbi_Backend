<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
<%@ page import="com.alibaba.fastjson.JSONObject"%>
<%@ page import="com.BCrypt"%>
<%@ page import="com.address.Address"%>


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
		String UserName = result.get("UserName").toString();
		String Email = result.get("Email").toString();
		String Password = result.get("Password").toString();
		String gensalt = BCrypt.gensalt();
		String saltPassword = BCrypt.hashpw(Password, gensalt);
		JSONObject jsonObject = new JSONObject();
		
		String UID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		// 数据库的用户名与密码，需要根据自己的设置
		String USER = "root";
		String PASS = "1mhHKi6DQyMedmBN";
		Connection conn = null;
		Statement stmt = null;

		System.out.println(UserName);
			try {
		Class.forName(JDBC_DRIVER);

		// 打开链接

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String sql;

		sql = "INSERT INTO `User` (`UserName`, `UID`, `Email`, `Password`) VALUES ('"
				+ UserName + "', '" + UID + "', '" + Email + "', '" + saltPassword + "');";
		boolean rs1 = stmt.execute(sql);
		sql = "INSERT INTO `Balance` (`UID`, `USDT`, `BTC`, `ETH`, `BCH`) VALUES ('"
				+ UID + "', '" + 0 + "', '" + 0 + "', '" + 0 + "', '" + 0 + "');";
		boolean rs2 = stmt.execute(sql);
		sql = "INSERT INTO `C2CBalance` (`UID`, `USDT`, `BTC`, `ETH`, `BCH`) VALUES ('"
				+ UID + "', '" + 0 + "', '" + 0 + "', '" + 0 + "', '" + 0 + "');";
		boolean rs3 = stmt.execute(sql);
		sql = "INSERT INTO `MarginBalance` (`UID`, `USDT`, `BTC`, `ETH`, `BCH`) VALUES ('"
				+ UID + "', '" + 0 + "', '" + 0 + "', '" + 0 + "', '" + 0 + "');";
		boolean rs4 = stmt.execute(sql);
		Address address = Address.parent("6F");
		
		sql = "INSERT INTO `Address.BTCTestBet` (`UID`, `PublicKey`, `Address`) VALUES ('"
				+ UID + "', '" + address.getPublicKey() + "', '" + address.getAddressy() + "');";
		boolean rs5 = stmt.execute(sql);
		
		sql = "INSERT INTO `Info` (`UID`, `First`, `Middle`, `Last`, `Birth`, `Gender`, `SSN`) VALUES ('"
				+ UID + "', '', '', '', '', '', '');";
		boolean rs6 = stmt.execute(sql);
		stmt.close();
		conn.close();
		jsonObject.put("UID",UID);
		jsonObject.put("UserName",UserName);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toJSONString());
		response.setStatus(response.SC_OK);

			} catch (Exception ex) {
				response.setStatus(response.SC_BAD_REQUEST,"error");
			}
		
	%>