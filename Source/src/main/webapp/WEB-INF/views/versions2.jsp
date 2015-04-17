<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:main>
	<h2>Modified Variables</h2>
	<div id="details">
	<c:choose>
		<c:when test="${not empty versions}">
			<table class="table3">
				<tr>
					<th>Variable Name</th>
					<th>Date Changed</th>
					<th>Commit Message</th>
					<th>User</th>
					<th>Origin</th>
				</tr>
				<c:forEach var="version" items="${versions}">	
					<tr>
						<td>
							<a href="${baseURI}/codebooks/${baseHandle}/v/${codebookVersion}/vars/${version[0]}"
							title="${version[0]}">${version[0]}</a>
						</td>
						<td>
							${version[2]}
						</td>
						<td>
							<a class="iLinkR" href="${gitURL}/commits/${version[1]}" title="View Changes" target="_blank">
								View commit<i class="fa fa-external-link"></i>
							</a>
						</td>
						<td>
							${version[3]}
						</td>
						<td>
							<c:choose>
								<c:when test="${fn:trim(version[4]) eq 'true'}"><em>Local Change</em></c:when>
								<c:otherwise><em>Remote Change</em></c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
					<p><em><i class="fa fa-exclamation-triangle largeIcon"></i> No variable commits found</em></p>
		</c:otherwise>
	</c:choose>
	</div>
</t:main>