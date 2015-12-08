<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div id="loadCoverInner" class="read col-sm-offset-1 col-md-offset-2 col-xs-12 col-sm-10 col-md-8">
	<a id="readingModeExit" class="closeWindow" href="search?q=${fn:trim(query)}"
		title="Back to search mode">×</a>
	<div id="details">
		<h2>Reading Mode</h2>
		<form action="read" method="get" id="viewControls" class="lb2">
			<label> <em>Reading results for query</em> <input
				id="readQuery" type="text" disabled="disabled"
				value="${fn:trim(query)}" /> <input type="hidden" name="q"
				value="${fn:trim(query)}" />
			</label>
		</form>
		<c:if test="${count > 0}">
			<p class="lb rc">Viewing variable ${pageNumber} of ${count}</p>
		</c:if>
		<c:set var="prevV" value="${pageNumber -1}" />
		<c:set var="nextV" value="${pageNumber +1}" />
		<c:choose>
			<c:when test="${prevV <= 0 }">
				<span > <span class="btn disabled ">
						Previous Variable </span> <a class="btn readBtnActive"
					id="buttonPageNext" href="read?q=${fn:trim(query)}&p=${nextV}">
						Next Variable </a>
				</span>
			</c:when>
			<c:when test="${nextV > count}">
				<span > 
					<a id="buttonPagePrev" class="btn readBtnActive"href="read?q=${fn:trim(query)}&p=${prevV}"> Previous Variable </a>
					<span class="btn disabled">Next Variable</span>
				</span>
			</c:when>
			<c:when test="count = 0">
				<span > <span class="btn disabled">
						Previous Variable </span> <span class="btn disabled"> Next
						Variable </span>
				</span>
			</c:when>
			<c:otherwise>
				<span > <a class="btn readBtnActive"
					id="buttonPagePrev" href="read?q=${fn:trim(query)}&p=${prevV}">
						Previous Variable </a> <a class="btn readBtnActive"
					id="buttonPageNext" href="read?q=${fn:trim(query)}&p=${nextV}">
						Next Variable </a>
				</span>
			</c:otherwise>
		</c:choose>
		<div id="results">
			<c:choose>
				<c:when test="${not empty data}">
					${data}
				</c:when>
				<c:otherwise>
					<p>No variables found.</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
<script type="text/javascript" src="${baseURI}/scripts/toggleB.js"></script>