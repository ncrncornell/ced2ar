<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<h4>Compare Variables</h4>
<form name="compare" action="compare" method="get">
	<c:if test="${not empty compare}">
		<c:forEach var="data" items="${compare}">
			<c:set var="var" value="${fn:split(data,' ')}" />
			<p>
				${var[1]} <a class="closeWindow" onclick="removeCompare(this)"
					title='Remove variable'>&times;</a> <input type="hidden" name="cv"
					value="${data}">
			</p>
		</c:forEach>
		<a href="${baseURI}/compare" class="btn">Compare</a>
		<button type="button" class="btn" onclick="resetCompare()">Clear</button>
	</c:if>
</form>
<p class="fMessage">
	<c:choose>
		<c:when test="${empty sessionScope.filter}">
			Error retrieving data
		</c:when>
		<c:when test="${empty compare}">
			No variables selected
		</c:when>
	</c:choose>
</p>