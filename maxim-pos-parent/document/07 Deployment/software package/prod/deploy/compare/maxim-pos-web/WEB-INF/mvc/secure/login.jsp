<%@include file="../includes/base.jsp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Maxim Login</title>
 
<style type="text/css">
#login-wrapper {
	margin-top: 100px;
	font-size: 14px;
	font-family: Helvetica, Arial, sans-serif;
}

#login-wrapper input {
	font-size: 14px;
	font-family: Helvetica, Arial, sans-serif;
}

#login-wrapper #login-top {
	width: 100%;
	padding: 140px 0 50px 0;
	text-align: center;
}

#login-wrapper #login-content {
	text-align: left;
	width: 300px;
	margin: 0 auto;
}

#login-wrapper .input-row {
	padding-top: 5px;
	padding-bottom: 5px; 
}

#login-wrapper .label-text {
	display: inline-block;
    width: 70px;
}

</style>
</head>
<body id="login">
	<div id="login-wrapper" class="png_bg">
		<div id="login-content">
			<div
				style="background: url('<%=request.getContextPath()%>/images/system/logo.gif'); background-repeat: no-repeat; background-position: left; height: 80px;">
			</div>
			<form name="loginForm"
				action="<%=request.getContextPath()%>/spring/secure/j_spring_security_check"
				method="POST">
				<c:if test="${SPRING_SECURITY_LAST_EXCEPTION.message != null}">
					<div class="notification information png_bg">
						<div style="color:red">${SPRING_SECURITY_LAST_EXCEPTION.message}</div>
					</div>
				</c:if>
				<div class="input-row">
					<label class="label-text">Username</label> <input class="text-input" type="text"
						name="j_username" value="${SPRING_SECURITY_LAST_USERNAME}" />
				</div>
				
				<div class="input-row">
					<label class="label-text">Password</label> <input class="text-input" type="password"
						name="j_password" value="${SPRING_SECURITY_LAST_PASSWORD }" />
				</div>
				
				<div class="input-row">
					<input class="button" type="submit" value="Login" />
				</div>
			</form>
		</div>
	</div>
</body>
</html>
