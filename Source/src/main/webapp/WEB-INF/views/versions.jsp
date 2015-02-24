<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:main>
	<h2>Commits</h2>
	<c:choose>
		<c:when test="${type eq 'var'}">
			<p class="lb2">Edits made from this instance of CED<sup>2</sup>AR to the variable <b>${var}</b> in <b>${title}</b>:</p>
		</c:when>
		<c:otherwise>
			<p class="lb2">Edits made from this instance of CED<sup>2</sup>AR to <b>${title}</b>:</p>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${not empty versions}">
			<c:forEach var="version" items="${versions}">	
				<p><a href="${gitURL}/commits/${version[0]}" target="_blank">${version[1]}</a></p>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<p><em><i class="fa fa-exclamation-triangle largeIcon"></i> No commits available from this instance</em></p>
		</c:otherwise>
	</c:choose>
</t:main>