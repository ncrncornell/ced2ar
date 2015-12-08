<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'></c:set>
<c:set var="css" scope='request'></c:set>
<t:merge>
	<h2>Merge Variables</h2>
	<form:form action="${flowExecutionUrl}" method='post'>
		<p>The following variables have changed:</p>
		<div class="lb2">
			<c:forEach items="${mergeProperties.getDiffVars()}" var="variable">
				<p>${variable}</p>
			</c:forEach>
		</div>
		<c:if test="${mergeProperties.getDiffVars().size() eq 0}">
			<p class="lb2"><em>No matching variables have changed</em></p>
		</c:if>
		<input name="_eventId_doMerge" type="submit" class="btn" value='Continue'>
	</form:form>
</t:merge>