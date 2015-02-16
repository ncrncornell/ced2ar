<%@ tag description="Browse Alphabetically Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<h2>Browse Alphabetically</h2>
<div id="filterCodebookVerbose"><%@ include
		file="/WEB-INF/ajaxViews/verboseFilter.jsp"%></div>
<c:if test="${count gt 0}">
	<a id="readingModelink" href="read?n=${currentLetter}*&amp;p=${rsv}"
		class="hidden-xs">View in reading mode</a>
</c:if>
<div id="browseAlpha">
	<c:set var="alphabet"
		value="${fn:split('A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z', ',')}" />
	<c:forEach var="letter" items="${alphabet}">
		<c:set var="lc" value="" />
		<c:if test="${letter.equals(currentLetter)}">
			<c:set var="lc" value=" currentLetter" />
		</c:if>
		<a class="noSelect browseLetter${lc}" href="browse">${letter}</a>
	</c:forEach>
	<form action="browse" method="get" id="viewControls">
		<input type="hidden" name="a" value="${currentLetter}" />
		<p>
			Show
			<t:show />
			variables
			<c:if test="${not empty currentLetter}">
				starting with ${currentLetter}
			</c:if>
		</p>
	</form>
</div>