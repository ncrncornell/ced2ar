<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'></c:set>
<c:set var="css" scope='request'></c:set>
<t:merge>
	<%-- TODO: Use nice jquery selector --%>
	<form:form action="${flowExecutionUrl}" method='post'>	
		<div id="details" class="row">		
			<div class="col-xs-6">
				<h2>Add</h2>
				<p class="lb2">
					These variables exist in the crowdsourced codebook, but not in the official codebook. 
					<br />Check those which you wish to add to the official codebook:
				</p>
				<c:choose>
					<c:when test="${mergeProperties.getUniqueRemoteVars().size() gt 0}">
						<c:forEach items="${mergeProperties.getUniqueRemoteVars()}" var="variable">
							<p>
								<input type="checkbox" name="variablesAdd" value="${variable}"/>${variable}
							</p>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p><em>No extra variables in remote</em></p>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="col-xs-6">
				<h2>Remove</h2>
				<p class="lb2">
					These variables exist in the official codebook, but not in the crowdsourced codebook.
					<br />Check those which you wish to remove from the official codebook:
				</p>
				<c:choose>
					<c:when test="${mergeProperties.getUniqueLocalVars().size() gt 0}">
						<c:forEach items="${mergeProperties.getUniqueLocalVars()}" var="variable">
							<p>
								<input type="checkbox" name="variablesRemove" value="${variable}"/>${variable}
							</p>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p><em>No extra variables in local</em></p>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<input name="_eventId_addVars" type="submit" class="btn" value='Continue'>
	</form:form>
</t:merge>