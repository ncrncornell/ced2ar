<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>provEdit</c:set>
<t:main>
	<h2>Disclosure - ${targetNode['label']}</h2>
	<div id="provEdit">	
		<fieldset>
			<legend>
				<h3>Details</h3>
			</legend>
			<div class="lb2">
				<p>Project #:</p>
				<p>Submitted by :</p>
			</div>
			<p>
				<em>For reviewer to complete</em>
			</p>
			<p>Cleared for release:</p>
			<p>Cleared by:</p>
		</fieldset>
		<fieldset>
			<legend>
				<h3>General Information</h3>
			</legend>
			<p>Codebook ID: ${targetNode['id']}</p>
			<p>
				Codebook Label: ${targetNode['label']}
			</p>
			<p>
				Codebook Location: ${targetNode['uri']}
			</p>
			<p>Type: ${nodeTypes[targetNode['nodeType']].name}</p>
			<p>Report generated: ${rp}</p>
		</fieldset>
		<fieldset>
			<legend>
				<h3>Related Items</h3>
			</legend>
			<c:choose>
				<c:when test="${fn:length(outEdges) gt 0}">
					<c:forEach var="edge" items="${outEdges}">
						<p>
							<span class="edge">
								${targetNode['id']}&nbsp;
								${flatPreds[edge['edgeType']]}&nbsp;
								<a href="${baseURI}/edit/prov/${edge['target']}"> 
									${edge['target']}
								</a>
							</span>
						</p>
					</c:forEach>		
				</c:when>
				<c:otherwise>
					<p><em>No outgoing edges</em></p>
				</c:otherwise>
			</c:choose>		
		</fieldset>
		<c:if test="${not empty vars}">
		<fieldset >
			<legend>
				<h3>Codebook Information</h3>
			</legend>
			${vars}
		</fieldset>
		</c:if>
	</div>
</t:main>