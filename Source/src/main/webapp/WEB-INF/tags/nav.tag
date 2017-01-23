<%@ tag description="Browse Alphabetically Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<nav class="navbar navbar-inverse">
	<div class="navbar-collapse">
		<ul class="nav navbar-nav">
			<li><a href="${baseURI}/search">Search Variables</a></li>
			<li class="divider-vertical hidden-xs"></li>
			<li class="dropdown"><a href="#" class="dropdown-toggle"
				data-toggle="dropdown">Browse Variables&nbsp;<b class="caret"></b></a>
				<ul class="dropdown-menu">
					<li><a href="${baseURI}/all">View All</a></li>
					<li><a href="${baseURI}/browse">Sort Alphabetically</a></li>
					<li><a href="${baseURI}/groups">Sort by Group</a></li>
				</ul></li>
			<li class="divider-vertical hidden-xs"></li>
			<c:if test="${uiNavBarBrowseCodebook eq  'true' }">
				<li><a href="${baseURI}/codebooks">${uiNavBarBrowseCodebookLabel}</a></li>
				<li class="divider-vertical hidden-xs"></li>
			</c:if>
    		<c:if test="${uiNavBarBrowseStudy eq  'true' }">
    			<li><a href="${baseURI}/codebooks/studies">${uiNavBarBrowseStudyLabel}</a></li>
    			<li class="divider-vertical hidden-xs"></li>
    		</c:if>
			<li><a href="${baseURI}/docs">Documentation</a></li>
			<li class="divider-vertical hidden-xs"></li>
			<li><a href="${baseURI}/about">About</a></li>
		</ul>
	</div>
</nav>