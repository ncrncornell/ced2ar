<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:main>
	<div>
		<h2>Variable Groups</h2>
		<c:if test="${not empty sessionScope.codebooks}">
			<c:forEach var="codebook" items="${codebooks}">
				<p>
					<a href="${baseURI}/codebooks/${codebook.value[0]}/v/${codebook.value[1]}/groups/">
						${codebook.value[4]}
					</a>
				</p>
			</c:forEach>
		</c:if>
	</div>
</t:main>