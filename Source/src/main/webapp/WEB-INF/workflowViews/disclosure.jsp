<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>disclosure</c:set>
<t:main>
	<h1 class="headerCenter">Appendix D: Clearance Request Memo</h1>
	<h2 class="headerCenter">Request for Clearance of Research Output</h2>
	<p class="headerCenter starBottom">Center for Economic Studies and Research Data Centers</p>
	<div>	
		<div class="starBottom">
			<div class="lb2">
				<p class="headerFillIn">
					<span class="headerFillInLabel">Project # : </span>
				</p>
				<p class="headerFillIn">
					<span class="headerFillInLabel">Submitted by  : </span>
				</p>
			</div>
			<p>
				<em>For CES reviewer to complete</em>
			</p>
			<p class="headerFillIn">
				<span class="headerFillInLabel">Cleared for release : </span>
				</p>
				<p class="headerFillIn">
					<span class="headerFillInLabel">Cleared by : </span>
				</p>
		</div>
		<div>
			<h4>1. General information</h4>
			<div class="lb2">
				<p>
					a. Details
				</p>
				<div class="response">
					<p>			
						Name: ${details.get("displayName")}
					</p>
					<p>
						Type: ${type}
					</p>			
					<p>
						URI: ${details.get("uri")}
						
					</p>
					<c:choose>
						<c:when test="${type eq 'Dataset'}">	
							<p>
								DOI:${details.get("doi")}
							</p>
							<p>
								Metadata Handle:${details.get("handle")}
							</p>
							<p>
								Notes: ${details.get("notes")}
							</p>
						</c:when>
						<c:when test="${type eq 'Program'}">
							<p>
								Author: ${details.get("author")}
							</p>
							<p>
								Notes: ${details.get("notes")}
							</p>
						</c:when>		
					</c:choose>
				</div>
			<div class="lb2">
				<p>b. Relationships</p>
				<div class="response">
					<c:if test="${edges.length() > 0}">
						<c:forEach begin="0" end="${edges.length()-1}" varStatus="loop">
						
							<c:set var="e" value="${edges.get(loop.index).get('row')}"/>				
							<p>${e.get(1)}&nbsp;${fn:replace(e.get(3).toLowerCase(),"_"," ")}&nbsp;${e.get(5)}</p>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<h4>2. Variable Definitions</h4>
			<div class="lb2">  
				<c:if test="${not empty vars}">
					${vars}
				</c:if>
			</div>
		</div>
	</div>
</t:main>