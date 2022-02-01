<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.logging.*"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		request.setCharacterEncoding("utf-8");
		String UserName = request.getParameter("UserName");
		String Email = request.getParameter("Email");
		String Password = request.getParameter("Password");

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
				+ UserName + "', '" + UID + "', '" + Email + "', '" + Password + "');";
		boolean rs1 = stmt.execute(sql);
		stmt.close();
		conn.close();

		response.setStatus(response.SC_OK);

			} catch (Exception ex) {
		Logger.getLogger("").log(Level.SEVERE, null, ex);
			}
		
	%>
</body>
</html>