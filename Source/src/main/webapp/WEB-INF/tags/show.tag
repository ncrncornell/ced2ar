<%@ tag description="Result Limiting Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<select name="s" id="limitResults">
	<c:choose>
		<c:when test="${size == 25}">
			<option value="10">10</option>
			<option selected="selected" value="25">25</option>
			<option value="50">50</option>
			<option value="100">100</option>
			<option value="250">250</option>
		</c:when>
		<c:when test="${size == 50}">
			<option value="10">10</option>
			<option value="25">25</option>
			<option selected="selected" value="50">50</option>
			<option value="100">100</option>
			<option value="250">250</option>
		</c:when>
		<c:when test="${size == 100}">
			<option value="10">10</option>
			<option value="25">25</option>
			<option value="50">50</option>
			<option selected="selected" value="100">100</option>
			<option value="250">250</option>
		</c:when>
		<c:when test="${size == 250}">
			<option value="10">10</option>
			<option value="25">25</option>
			<option value="50">50</option>
			<option value="100">100</option>
			<option selected="selected" value="250">250</option>
		</c:when>
		<c:otherwise>
			<option selected="selected" value="10">10</option>
			<option value="25">25</option>
			<option value="50">50</option>
			<option value="100">100</option>
			<option value="250">250</option>
		</c:otherwise>
	</c:choose>
</select>