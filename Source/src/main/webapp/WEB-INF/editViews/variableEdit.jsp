<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>edit.min diff</c:set>
<c:set var="js" scope='request'>edit.min diff/diff_match_patch diff/diff</c:set>
<t:main>
	<c:if test="${git}">
		<a
			href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}/versions"
			class="printButton" title="View versions"> <i class="fa fa-git"></i>
		</a>
	</c:if>
	<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}"
		class="printButton" title="Back to viewing mode">
		<i class="fa fa-file-text-o"></i>
	</a>
	<c:if test="${not empty results}">
		<div id="details">${results}</div>
	</c:if>
</t:main>