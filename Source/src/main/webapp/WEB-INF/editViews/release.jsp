<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>edit.min</c:set>
<c:set var="js" scope='request'>release</c:set>
<t:main>
	<h2>Release ${codebookInfo[4]} (${codebookInfo[0]} - ${codebookInfo[1]})</h2>
	<c:choose>
		<c:when test="${fn:length(accessLevels) gt 0}">
			<p class="lb2">
				<c:choose>
					<c:when test="${accsCount eq count}">
						<i class="fa fa-check-circle largeIcon"></i> 
						All variables have access levels defined.
					</c:when>
					<c:when test="${accsCount eq '0'}">
						<i class="fa fa-exclamation-triangle largeIcon"></i> 
						No variables have access levels defined. 
						Variables without access levels <b>cannot</b> be released.
					</c:when>
					<c:otherwise>
						<i class="fa fa-exclamation-triangle largeIcon"></i> 
						<b>${accsCount}</b> of <b>${count}</b> variables have access levels defined.
						Variables without access levels <b>cannot</b> be released.
					</c:otherwise>
				</c:choose>
			</p>
			<p class="lb2">
				<a class="releaseItem" href="${baseURI}/edit/codebooks/${codebookInfo[0]}/v/${version}/accessvars">
					<i class="fa fa-plus"></i>Apply access levels</a>
				&nbsp;
				<a class="releaseItem" href="${baseURI}/edit/codebooks/${codebookInfo[0]}/v/${version}/#accessLevels">
					<i class="fa fa-pencil"></i>Edit access levels</a>
			</p>
			<p class="lb3">
				Check which access levels to release. 
				Large codebooks might take a few seconds to compile.
			</p>
			<form id="releaseForm" class="lb2">
				<div class="lb">
					<c:forEach var="level" items="${accessLevels}">
						<label for="releaseLevel">
							<input type="checkbox" name="releaseLevel" value="${level}">${level}
						</label>
					</c:forEach>
				</div>
			</form>
			<div class="lb2">
				<a id="releaseLink" class="lb2" target="_blank" title="Download XML"
					download="${codebookInfo[4]}.xml"
					href="${baseURI}/rest/codebooks/${handle}/release?i="> <i
					class="fa fa-download"></i>Release Codebook
				</a>
			</div>
			<div class="hidden">
				<label for="makeNewVersion">
						<input type="checkbox" name="makeNewVersion" value="yes">Save export as new version
				</label>
				<label id="newVersionLabel" class="hidden" for="newVersionName" >
					Version ID <input type="text" name="newVersionName" value="${version}-r"/>
					<a id="versionClone" class="btn" href="${baseURI}/edit/codebook/${codebookInfo[0]}/v/">
						Clone
					</a>
				</label>
			</div>
		</c:when>
		<c:otherwise>
			<p>Sorry, this codebook does not have access levels.</p>
		</c:otherwise>
	</c:choose>
</t:main>