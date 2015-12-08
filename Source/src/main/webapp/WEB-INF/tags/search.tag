<%@ tag description="Search Bar Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<h2>Search</h2>
<div id="filterCodebookVerbose"><%@ include
		file="/WEB-INF/ajaxViews/verboseFilter.jsp"%></div>
<form action="${baseURI}/search" method="get" id="viewControls">
	<div class="lb3">
		<input type="text" name="q" value="${fn:trim(query)}" /> <input
			type="submit" aria-label="Search for variable" class="btn" value="Search" />
	</div>
	<p>
		<a href="advanced_search">Advanced Search</a>
		<c:if test="${not empty fn:trim(query) and count gt 0}">
			<span class="hidden-xs">| <a id="readingModelink"
				href="read?q=${fn:trim(query)}&amp;p=${rsv}">View in reading mode</a></span>
		</c:if>
	</p>
	<p>
		Show
		<t:show />
		variables
	</p>
</form>