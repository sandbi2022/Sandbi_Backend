<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.alibaba.fastjson.JSON"%>
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
	   System.out.println(result.toString());
		String UID = result.get("UID").toString();
		String OldPassword = result.get("OldPassword").toString();
		String NewPassword = result.get("NewPassword").toString();
		String gensalt = BCrypt.gensalt();
		String saltPassword = BCrypt.hashpw(NewPassword, gensalt);
		
		String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://45.77.155.138/Sandbi?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		// 数据库的用户名与密码，需要根据自己的设置
		String USER = "root";
		String PASS = "1mhHKi6DQyMedmBN";
		Connection conn = null;
	Statement stmt = null;

	try {
		Class.forName(JDBC_DRIVER);

		// 打开链接

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "select * from `User` Where UID = '"+UID+"';";
		System.out.println(sql);
		ResultSet rs = stmt.executeQuery(sql);

		response.setStatus(response.SC_BAD_REQUEST,"User Not Exist");
		while (rs.next()) {

				boolean checkpw = BCrypt.checkpw(OldPassword, rs.getString("Password"));
				if(checkpw){
					response.setStatus(response.SC_OK,"Reset Succsee");
					String sql1 = "UPDATE Sandbi.User SET `Password` = '"+saltPassword+"' WHERE (`UID` = '"+rs.getString("UID")+"');";
					System.out.println(sql1);
					boolean rs1 = stmt.execute(sql1);
				} else {
					response.setStatus(response.SC_BAD_REQUEST,"Password Not Correct");
				}
		break;
		}
		rs.close();

		stmt.close();
		conn.close();


		

	} catch (Exception ex) {
		Logger.getLogger("").log(Level.SEVERE, null, ex);
		response.setStatus(response.SC_BAD_REQUEST,"error");
	}
	%>