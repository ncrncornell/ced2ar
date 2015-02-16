<%@ tag description="Browse All Vars Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<h2>Browse all Variables</h2>
<div id="filterCodebookVerbose"><%@ include
		file="/WEB-INF/ajaxViews/verboseFilter.jsp"%></div>
<c:if test="${count gt 0}">
	<a id="readingModelink" class="hidden-xs"
		href="read?q=${currentLetter}*&amp;p=${rsv}">View in reading mode</a>
</c:if>
<div>
	<form action="all" method="get" id="viewControls">
		<p>
			Show
			<t:show />
			variables
		</p>
	</form>
</div>
