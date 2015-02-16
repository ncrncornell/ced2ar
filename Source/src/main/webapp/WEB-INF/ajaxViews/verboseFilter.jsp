<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="filterCount" value="0" scope="page" />
<p>
	Searching
	<c:forEach var="cb" items="${filter}">
		<c:if test="${cb[5] eq 'true'}">
			<c:if test="${filterCount gt 0}">, </c:if>
			<a href="${baseURI}/codebooks/${cb[1]}/v/${cb[2]}"
				title="${cb[4]} (View cover)">${cb[3]} (${cb[2]})</a>
			<c:set var="filterCount" value="${filterCount+1}" scope="page" />
		</c:if>
	</c:forEach>	
	<c:choose>
		<c:when test="${filterCount gt 0}">
			<a id="clearFilter" href="#" title="Clear all filters"><i class="fa fa-times-circle"></i></a>
		</c:when>
		<c:otherwise>
			all codebooks. No filters active.
		</c:otherwise>
	</c:choose>
</p>