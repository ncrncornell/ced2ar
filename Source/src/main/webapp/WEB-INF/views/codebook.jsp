<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:if test="${not print}">
	<c:set var="js" scope='request'>toggle hints/popups hints/crowdsourceWarning</c:set>
</c:if>
<t:main>
	<c:if test="${not empty newVersion}">
		<c:set var="newHandle" scope="page">${baseHandle}${newVersion}</c:set>
		<em> <i class="fa fa-exclamation-triangle"></i> This codebook is
			outdated. Please use <a href="${baseURI}/codebooks/${baseHandle}/v/${newVersion}">
				${codebooks[newHandle][4]} </a>
		</em>
	</c:if>
	<c:if test="${not empty codebook}">			
			<c:if test="${not empty pdf}">
				<a href="${pdf}" class="printButton" target="_blank"
					title="Download as PDF" aria-label="Download as PDF" download="${codebookTitl}.pdf"> <i
					class="fa fa-file-pdf-o"></i>
				</a>
			</c:if>
			<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/exportToSTATA"
				class="printButton3b printRemove" title="Download Stata variable values" aria-label="Download Stata variable values">Stata</a>
			<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/exportToSAS"
				class="printButton3b printRemove" title="Download SAS variable values" aria-label="Download SAS variable values">SAS</a>
			<a href="${baseURI}/rest/codebooks/${handle}"<%--?type=gitNotes --%>
				class="printButton" target="_blank" title="Download raw XML" aria-label="Raw XML"
				download="${codebookTitl}.xml"> <i class="fa fa-code"></i>
			</a>
			<a href="?print=y" class="printButton" target="_blank"
				title="View print layout" aria-label="View print layout"> <i class="fa fa-print"></i>
			</a>
		<c:if test="${editing}">
				<c:choose>
					<%-- TODO: Probably a better way to handle this --%>
					<c:when test="${empty userEmail and empty userName}">
						<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/"
							class="printButton" title="Login" 
							aria-label="View variables changed"> <i class="fa fa-sign-in"></i>
						</a>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty baseHandle && not empty version}">
							<c:if test="${git}">
								<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/versions2"
									class="printButton" title="View variables changed" 
									aria-label="View variables changed"> <i class="fa fa-git"></i>
								</a>
							</c:if>
							<a
								href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/release"
								class="printButton" title="Release codebook" aria-label="Release codebook"> <i
								class="fa fa-unlock"></i>
							</a>
							<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/"
								class="printButton" title="Edit this page" aria-label="Edit this page"> <i
								class="fa fa-pencil"></i>
							</a>
						</c:if>
					</c:otherwise>
				</c:choose>
		</c:if>
		<div id="details" itemscope itemtype="http://schema.org/Dataset">
			<c:if test="${not empty timeStamp}">
				<p><em>This document was generated: ${timeStamp}</em></p>
			</c:if>
			<h2 itemprop="name">${codebookTitl}</h2>
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