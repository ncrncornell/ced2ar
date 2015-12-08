<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="js" scope='request'>provObj bootstrap/bootstrap-editable.min</c:set>
<c:set var="css" scope='request'>edit provEdit bootstrap/bootstrap-editable</c:set>
<t:main>
	<h2>Workflow Entity - ${targetNode['label']}</h2>
	<div id="provEdit">
		<form>
			<input id="provID" type="hidden" value="${targetNode['id']}" />
			<fieldset>
				<legend>
					Select an object
				</legend>
				<p>ID: ${targetNode['id']}</p>
				<p>
					Label: 
					<a href="#" id="provLabel" class="bse">${targetNode['label']} <i class="fa fa-pencil"></i></a>
				</p>
				<p>
					URI:
					<a href="#" id="provURI" class="bse">${targetNode['uri']} <i class="fa fa-pencil"></i></a>
				</p>
				<p>Type: ${nodeTypes[targetNode['nodeType']].name}</p>
				<p>
					Date:
					<a href="#" id="provDate" class="bse">${targetNode['date']} <i class="fa fa-pencil"></i></a>
				</p>
				<p><a href="${baseURI}/prov2?n=${targetNode['id']}">View on Graph</a></p>
				<c:if test="${isCodebook}">
					<p><a href="${baseURI}/edit/prov/${targetNode['id']}?d=true">Generate Disclosure Form</a></p>
				</c:if>
				<span class="edc">
					<a id="nodeDelete" class="noSelect" href="#" title="Delete entire node?">
						<i class="fa fa-trash-o"></i> Delete node
					</a>
				</span>
			</fieldset>
			<fieldset>
				<legend>
					Outgoing Relationships
					<a href="${baseURI}/edit/prov?o=${targetNode['id']}" title="Add Relationship">
						<i class="fa fa-plus"></i>
					</a>
				</legend>
				<c:choose>
					<c:when test="${fn:length(outEdges) gt 0}">
						<c:forEach var="edge" items="${outEdges}">
							<p>
								<span class="edc noSelect">
									<a class="edgeDelete noSelect" href="#" title="Delete this edge?">
										<i class="fa fa-trash-o"></i>
									</a>
									<input type="hidden" class="edgeSource" value="${targetNode['id']}" />
									<input type="hidden" class="edgeTarget" value="${edge['target']}" />
									<input type="hidden" class="edgeType" value="${edge['edgeType']}" />
								</span>
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
			<fieldset>
				<legend>
					Incoming Relationships
					<a href="${baseURI}/edit/prov?s=${targetNode['id']}" title="Add Relationship">
						<i class="fa fa-plus"></i>
					</a>			
				</legend>
				<c:choose>
					<c:when test="${fn:length(inEdges) gt 0}">
						<c:forEach var="edge" items="${inEdges}">
							<p>
								<span class="edc noSelect">	
									<a class="edgeDelete noSelect" href="#" title="Delete this edge?">
										<i class="fa fa-trash-o"></i>
									</a>
									<input type="hidden" class="edgeSource" value="${edge['source']}" />
									<input type="hidden" class="edgeTarget" value="${targetNode['id']}" />
									<input type="hidden" class="edgeType" value="${edge['edgeType']}" />
								</span>
								<span class="edge">
									<a href="${baseURI}/edit/prov/${edge['source']}">
										${edge['source']}
									</a>&nbsp;
									${flatPreds[edge['edgeType']]}&nbsp;
									${targetNode['id']}
								</span>
							</p>
						</c:forEach>		
					</c:when>
					<c:otherwise>
						<p><em>No incoming edges</em></p>
					</c:otherwise>
				</c:choose>
			</fieldset>
        </form>
	</div>
</t:main>