<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="baseURI" scope="request">${pageContext.request.contextPath}</c:set>
<t:error>
	<h1>Access Denied</h1>
		<p>
			You don't have permission to access this page 
			<c:choose>
				<c:when test="${not empty userName and not empty userEmail}">
					(${userName}&nbsp;${userEmail})
				</c:when>
				<c:when test="${not empty userName}">
					(${userName})
				</c:when>
				<c:when test="${not empty userEmail}">
					(${userEmail})
				</c:when>
			</c:choose>
		</p>
	<a href="${baseURI}">Return to main page</a>
</t:error>