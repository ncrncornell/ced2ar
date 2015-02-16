<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:main>
	<c:if test="${editing}">
		<a href="${baseURI}/edit/codebooks" class="printButton"
			title="Edit codebooks"> <i class="fa fa-cog"></i>
		</a>
	</c:if>
	<div>
		<h2>Codebooks</h2>
		<c:if test="${not empty sessionScope.codebooks}">
			<c:forEach var="codebook" items="${codebooks}">
				<p>
					<a href="${baseURI}/codebooks/${codebook.value[0]}/v/${codebook.value[1]}"
					 title="${codebook.value[0]} - ${codebook.value[1]}">${codebook.value[4]} 
					</a>
				</p>
			</c:forEach>
		</c:if>
	</div>
</t:main>