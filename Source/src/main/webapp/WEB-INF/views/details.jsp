<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:if test="${not print}">
	<c:set var="js" scope='request'>toggleB hints/popups hints/crowdsourceWarning</c:set>
</c:if>
<t:main>
	<c:if test="${not empty newVersion}">
		<c:set var="newHandle" scope="page">${baseHandle}${newVersion}</c:set>
		<em> <i class="fa fa-exclamation-triangle"></i>
			This codebook is outdated. Please use 
			<a href="${baseURI}/codebooks/${baseHandle}/v/${newVersion}">
				${codebooks[newHandle][4]}
			</a>
		</em>
	</c:if>
	<c:if test="${not empty results}">
		<c:choose>
			<c:when test="${type eq 'var'}">
				<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}/exportToSTATA"
				class="printButton3b printRemove" title="Download Stata variable values" aria-label="Download Stata variable values">Stata</a>
				<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}/exportToSAS"
				class="printButton3b printRemove" title="Download SAS variable values" aria-label="Download SAS variable values">SAS</a>	
				<a href="?print=y" class="printButton" target="_blank"
					title="View print version" aria-label="View print verson"> <i class="fa fa-print"></i>
				</a>
				<c:if test="${editing}">
					<c:choose>
						<%-- TODO: Probably a better way to handle this --%>
						<c:when test="${empty empty userEmail and empty userName}">
							<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/vars/${var}"
								class="printButton" title="Login" 
								aria-label="View variables changed"> <i class="fa fa-sign-in"></i>
							</a>
						</c:when>
						<c:otherwise>
							<c:if test="${git}">
								<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}/versions"
										class="printButton" title="View versions"> <i
									class="fa fa-git"></i>
								</a>
							</c:if>
							<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/vars/${var}"
								class="printButton" title="Edit this page"><i class="fa fa-pencil"></i>
							</a>	
						</c:otherwise>
					</c:choose>				
				</c:if>
			</c:when>
			<c:when test="${type eq 'group'}">
				<c:if test="${editing}">
					<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/groups"
					class="printButton" title="Add Group"> 
						<i class="fa fa-plus"></i>
					</a>
				</c:if>
			</c:when>
			<c:when test="${type eq 'groupD'}">
				<c:if test="${editing}">
					<a href="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/groups/${groupID}"
					class="printButton" title="Edit Group"> 
						<i class="fa fa-pencil"></i>
					</a>
				</c:if>
			</c:when>
		</c:choose>
		
		<div id="details" itemscope itemtype="http://schema.org/Dataset/Variable">${results}</div>
	</c:if>
</t:main>