<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="css" scope='request'>edit.min</c:set>
<c:set var="js" scope='request'>edit.min</c:set>
<t:main>
	<c:if test="${not empty codebook}">		
			<a href="${baseURI}/rest/codebooks/${handle}" class="printButton" target="_blank" 
			title="Download raw XML" download="${codebookTitl}.xml">
				<i class="fa fa-code"></i>
			</a>
			<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/release" class="printButton" title="Release codebook">
				<i class="fa fa-unlock"></i>
			</a>
			<c:if test="${git}">
				<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/versions" 
				class="printButton" title="View versions">
					<i class="fa fa-git"></i>
				</a>
			</c:if>
			<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/" class="printButton" title="Back to viewing mode">
				<i class="fa fa-file-text-o"></i>
			</a>
			<div id="details">				
				<h2 id="iAuthors">
					${codebookTitl}
					<a title="Edit field" href="edit?f=titl" class="editIcon2">
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink" href="${baseURI}/schema/doc/codeBook">
						<i class="fa fa-info-circle"></i>
					</a>
				</h2>
				<p class="value4">
					<c:choose>
						<c:when test="${count gt 0}">
							<a class="printRemove" href="${baseURI}/landing?c=${handle}">View Variables</a>
							<em>(${count} variables)</em>
						</c:when>
						<c:otherwise>
							<em>This codebook does not have variables.</em>
						</c:otherwise>
					</c:choose>
				</p>
				${codebook}
			</div>
	</c:if>
</t:main>