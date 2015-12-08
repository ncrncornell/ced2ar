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
					a. Name of this request's subdirectory under the project's main clearance directory:
				</p>
				<p class="response">${targetNode['uri']}</p>
			</div>
			${disclosure}
			<div class="lb2">
				<p>c. Program that produced the file (e.g., output.sas or model.do)</p>
				<div class="response">
					<c:forEach var="edge" items="${outEdges}">
						<p>
							<span class="edge">
								${targetNode['id']}&nbsp;
								${flatPreds[edge['edgeType']]}&nbsp;
								<a href="${pageContext.request.serverName}:${pageContext.request.localPort}${baseURI}/edit/prov/${edge['target']}"> 
									${edge['target']}
								</a>	
							</span>
						</p>
					</c:forEach>		
				</div>
			</div>
			<h4>4. Variable Definitions</h4>
			<div class="lb2">
				${vars}
			</div>
		</div>
	</div>
</t:main>