<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page isErrorPage="true" import="java.io.*" %>

<c:set var="baseURI" scope="request">${pageContext.request.contextPath}</c:set>
<t:error>
	<h1>500</h1>
	<p>An internal error has occurred. Sorry for the inconvenience.</p>
	<a href="${baseURI}">Return to main page</a>
	<h2>Technical Details</h2>
	<div>
		<p>${pageContext.exception}</p>
		<h3>Stack trace:</h3>
		<c:forEach var="trace" items="${pageContext.exception.stackTrace}">
			<p>${trace}</p>
		</c:forEach>
	</div>	
</t:error>
