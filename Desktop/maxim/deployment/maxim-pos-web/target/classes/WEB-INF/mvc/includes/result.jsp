<%@include file="../includes/base.jsp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<c:if test="${result != null }">
	<h3>${result ? 'Successful Operation!' : 'Failure Operation !'}</h3>
</c:if>