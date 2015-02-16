<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="baseURI" scope="request">${pageContext.request.contextPath}</c:set>
<t:error>
	<h1>404</h1>
	<p>The page you requested could not be found</p>
	<a href="${baseURI}">Return to main page</a>
</t:error>