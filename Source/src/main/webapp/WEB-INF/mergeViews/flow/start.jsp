<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>inlineLoad</c:set>
<c:set var="css" scope='request'></c:set>
<t:merge>
	<h2>Merge</h2>
	<p>Please select a codebook to begin</p>
	<form:form action="${flowExecutionUrl}" method='post'>
		<select name="codebook" class="btn">
			<c:forEach items="${mergeProperties.getCodebooks()}" var="codebook">
				<option value="${codebook}">${codebook}</option>
			</c:forEach>
		</select>
		<input name="_eventId_compareVars" type="submit" class="btn inlineLoad" value='Start'>
	</form:form>
</t:merge>
