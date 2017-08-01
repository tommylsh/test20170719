<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%String baseUrl = getServletContext().getInitParameter("BaseUrl");%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Spring Rest Client</title>

<link rel="stylesheet" type="text/css"
	href="<%out.print(baseUrl); %>Styles/site.css">

<script type="text/javascript"
	src="<%out.print(baseUrl); %>Scripts/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="<%out.print(baseUrl); %>Scripts/vkbeautify.0.99.00.beta.js"></script>
<script type="text/javascript"
	src="<%out.print(baseUrl); %>Scripts/jsonxml.js"></script>
<script type="text/javascript"
	src="<%out.print(baseUrl); %>Scripts/index.js"></script>
	
<script type="text/javascript">
	var urlbase = "<%out.print(baseUrl); %>";
	
	$(document).ready(function() {
		pageobject.initpage(urlbase);
	});
</script>	
</head>
<body>
<div>
<div>
	<textarea id="txtResult" readonly="readonly"></textarea>
</div>
<div>
	<span id="spanMessage" class="msgNormal">
		Click the buttons to test the rest calls...
	</span>
</div>
<div>
	<input type="button" id="btnGetXML" value="GET XML" />
	<input type="button" id="btnGetJson" value="GET Json" />
	<input type="button" id="btnPostJsonAcceptXML" value="POST Json Accept XML" />
	<input type="button" id="btnPOSTXMLAcceptJson" value="POST XML Accept Json" />
</div>
</div>

</body>
</html>