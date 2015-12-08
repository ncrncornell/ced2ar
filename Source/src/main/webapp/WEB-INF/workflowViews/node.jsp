<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>workflow/workflow bootstrap/bootstrap-editable</c:set>
<c:set var="js" scope='request'>workflow/node bootstrap/bootstrap-editable.min</c:set>
<t:main>
		<%--TODO: node delete --%>
		<input id="nodeID" type="hidden" value="${id}" />
		<h3>Node Details</h3>
		<div class="lb">
			<p class="lb3">
				<em><a href="${baseURI}/edit/workflow?start=${id}">View on graph</a></em>
			</p>
			<p>
				Name: 
				<a href="#" id="nodeDisplayName" class="bse">${details.get("displayName")} <i class="fa fa-pencil"></i></a>
			</p>
			<p>Type: ${type}</p>			
			<p>
				URI: 
				<a href="#" id="nodeURI" class="bse">${details.get("uri")} <i class="fa fa-pencil"></i></a>
				
			</p>
			<c:choose>
				<c:when test="${type eq 'Dataset'}">	
					<p>
						DOI:
						<a href="#" id="nodeDOI" class="bse">${details.get("doi")} <i class="fa fa-pencil"></i></a>
					</p>
					<p>
						Notes:
						<a href="#" id="nodeNotes" class="bse">${details.get("notes")} <i class="fa fa-pencil"></i></a>
					</p>
					<p>
						Metadata:
						<a href="#" id="nodeHandle" class="bse">${details.get("handle")} <i class="fa fa-pencil"></i></a>

						<c:if test="${details.get('handle') ne ''}">
							<c:set var="handleInfo" value="${fn:split(details.get('handle'),'.')}"/>
							<br />
							<a id="docLink" title="See metadata" target="_blank"
							href="${baseURI}/codebooks/${handleInfo[0]}/v/${handleInfo[1]}">View metadata</a>
						</c:if>
					</p>
				</c:when>
				<c:when test="${type eq 'Program'}">
					<p>
						Author:
						<a href="#" id="nodeAuthor" class="bse">${details.get("author")} <i class="fa fa-pencil"></i></a>
					</p>
					<p>
						Notes:
						<a href="#" id="nodeNotes" class="bse">${details.get("notes")} <i class="fa fa-pencil"></i></a>
					</p>
				</c:when>		
				<%--
				Provider has nothing unique
				<c:when test="${type e 'Provider'}"></c:when>
				 --%>
			</c:choose>
			
		</div>
		
		<h3>Edges</h3>
		<div class="lb">
			<a href="${baseURI}/edit/workflow/edge" class="btn" 
			title="Add new replication workflow"><i class="fa fa-plus"></i> Add Edge</a>
		</div>
		<div>
			<c:if test="${edges.length() > 0}">
				<c:forEach begin="0" end="${edges.length()-1}" varStatus="loop">
					<c:set var="e" value="${edges.get(loop.index).get('row')}"/>
					<p class="edc">
						<a href="${baseURI}/edit/workflow/n/${e.get(0)}" 
						title="View node">${e.get(1)}</a>&nbsp;${fn:replace(e.get(3).toLowerCase(),"_"," ")}&nbsp;<a 
						href="${baseURI}/edit/workflow/n/${e.get(4)}" title="View node">${e.get(5)}</a>
						<button class="edgeDelete" value="${e.get(2)}"><i class="fa fa-trash-o"></i></button>
					</p>
				</c:forEach>
			</c:if>
		</div>
		
		<h3>Score</h3>	
		<div>
			<p class="lb2" id="totalScore">
				<i class="fa fa-spin fa-spinner"></i> Evaluating node
			</p>
			<c:if test="${type eq 'Dataset'}">
			<p>
				<em>
					Improving this score positively impacts the disclosure avoidance report
				</em>
				<%--Remove link under certain score? --%>
				<br />
				<a class="footerButton" target="_blank" title="View Report" 
				href="${baseURI}/edit/workflow/n/${id}?d=true"><i class="fa fa-file-text-o"></i>View Report</a>
			</p>		
			</c:if>	
		</div>
</t:main>