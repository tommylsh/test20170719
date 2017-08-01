<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
<title>Logout</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/login/style.css"
	type="text/css" media="screen" />
</head>
<body> 
<p><h2>${logoutMsg }</h2></p>
<p><a href="login.jsp">Login Again</a></p>
</body>
</html>
