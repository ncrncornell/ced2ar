<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="css" scope='request'>versionStatus</c:set>
<c:set var="js" scope='request'>gitStatus</c:set>
<t:main>
	<div id="gitStatus">
		<h2>Codebook Status</h2>
		<c:set var="pushAction" value="false"/>
		<c:set var="pullAction" value="false"/>
		<c:set var="conflictAction" value="false"/>
		<c:set var="pullIntoBaseXAction" value="false"/> 
		<c:set var="invalidRemoteAction" value="false"/>
		
		
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Codebook</th>
					<th>Git Status</th>
					<th>Last Local Update</th>
					<th>Last Remote Update</th>
					<th>Exists in BaseX</th>
				</tr>
			</thead>
			<c:forEach items="${codebooks}" var="codebook">
				<tr>
					<c:if test="${(codebook.status eq 'LOCAL_BEHIND') or (codebook.status eq 'LOCAL_DOES_NOT_EXIST')}">
						<c:set var="pullAction" value="true"/> 
					</c:if>
					<c:if test="${(codebook.status eq 'LOCAL_AHEAD') or (codebook.status eq 'REMOTE_DOES_NOT_EXIST')}">
						<c:set var="pushAction" value="true"/> 
					</c:if>
					<c:if test="${codebook.status eq 'CONFLICT'}">
						<c:set var="conflictAction" value="true"/> 
					</c:if>
					<c:if test="${codebook.status eq 'INVALID_REMOTE'}">
						<c:set var="invalidRemoteAction" value="true"/> 
					</c:if>
					
					
					<td>${codebook.codebookName}</td>
					<!--  START Git Status Column -->
					<c:choose>
						<c:when test="${codebook.status eq 'LOCAL_BEHIND'}"> 
							<td class="status_localBehind">${codebook.status}</td>
						</c:when>
						<c:when  test="${codebook.status eq 'UP_TO_DATE'}"> 
							<td class="status_uptodate">${codebook.status}</td>
						</c:when>
						<c:when test="${codebook.status eq 'CONFLICT'}"> 
							<td class="status_conflict">${codebook.status}</td>
						</c:when>
						<c:when test="${codebook.status eq 'INVALID_REMOTE'}"> 
							<td class="status_remote_invalid">${codebook.status}</td>
						</c:when>
						
						<c:when test="${codebook.status eq 'LOCAL_AHEAD'}"> 
							<td class="status_localAhead">${codebook.status}</td>
						</c:when>
						<c:when test="${codebook.status eq 'LOCAL_DOES_NOT_EXIST'}"> 
							<td class="status_local_non_existant">${codebook.status}</td>
						</c:when>
						<c:when test="${codebook.status eq 'REMOTE_DOES_NOT_EXIST'}"> 
							<td class="status_remote_non_existant">${codebook.status}</td>
						</c:when>

						<c:otherwise> 
							<td >${codebook.status}</td>
						</c:otherwise>
						
					</c:choose>
					<!--  END Git Status Column -->
					<td>
						${codebook.formattedLastLocalUpdateTime} <br />
						${codebook.formattedLocalMessage}
					</td>
					<td>
						${codebook.formattedLastRemoteUpdateTime} <br />
						${codebook.formattedRemoteMessage}
					</td>
					<c:choose>
						<c:when test = "${codebook.baseXExistanceStatus eq 'EXISTS'}">
							<td class="status_exist">
								${codebook.baseXExistanceStatus}
							</td>	
						</c:when>
						<c:otherwise>
							<td class="status_does_not_exist">
								${codebook.baseXExistanceStatus}<br>
								<c:if test="${codebook.localGitExistanceStatus eq 'EXISTS'}">
									<br />
									<a class="ingestIntoBasexButton btn" href="ingestintobasex?codebook=${codebook.codebookBaseHandle}&version=${codebook.codebookVersion}" title="Add codebook to BaseX">Add</a>
								</c:if>	
								
							</td>	
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
			
			<tr><td colspan='5'>
				<c:choose>
					<c:when test="${invalidRemoteAction eq true }">
						<c:if test= "${conflictAction eq false}">
							<a class="replaceInvalidRemoteButton btn" href="removeRemote" title="Replace Invalid Remote Codebook with Local">Replace Invalid Codebook</a>
						</c:if>
					</c:when>
					<c:otherwise>
						<!--  Pull Action should come first followed by a Push. Conflicts are resolved by pulling first and merging in favor of remote  -->
						<c:if test="${conflictAction eq true}">
							<a class="conflictButton btn" href="merge" title="Merge in Favor of Remote">Merge</a>
						</c:if>
						<c:if test="${pullAction eq 'true' && conflictAction eq false}">
							<a class="pullFromRemoteButton btn" href="pullfromremote" title="Pull from Remote">Pull from Remote</a>
						</c:if>
						<c:if test="${pushAction eq 'true' && conflictAction eq false && pullAction eq false}">
							<a class="pushToRemoteButton  btn" href="pushtoremote" title="Push to Remote">Push To Remote</a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</td></tr>
		</table>
	</div>
</t:main>