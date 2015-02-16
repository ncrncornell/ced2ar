<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>access edit.min</c:set>
<c:set var="js" scope='request'>vargroupAddVars edit.min</c:set>
<t:main>
	<h2>Editing ${groupName}</h2>
	<p class="lb2">
		${groupDesc}
		<a title="Edit field" class="editIcon2" href="${groupID}/edit?f=txt">
			<i class="fa fa-pencil"></i>
		</a>
	</p>
	<p class="lb"><em>Hint: Check or uncheck variables to add or remove from this group</em></p>
	<c:choose>	
			<c:when test="${not empty data}">
				<display:table name="${data}" id="results" requestURI="${type}"
					sort="external" partialList="false" size="${count}" pagesize="${count}">
					<display:column class="cc">	
						<label>				
							<c:choose>
								<c:when test="${results[3] eq true}">
									<input name="vars" type="checkbox" value="${results[1]}" title="Select" checked/>
								</c:when>
								<c:otherwise>
									<input name="vars" type="checkbox" value="${results[1]}" title="Select" />
								</c:otherwise>
							</c:choose>
						</label>
					</display:column>
					<display:column sortable="true" class="varNameCol" title="Variable Name">
							<c:url
								value="../vars/${results[0]}"
								var="dLink" />
							<a href="${fn:replace(dLink,'&','&amp;')}">${results[0]}</a>
					</display:column>
					<display:column property="[2]" class="labelCol hidden-xs" paramId="l" sortable="true" title="Label" />
				</display:table>
			</c:when>
			<c:when test="${empty data && searched}">
				<h4 id="results">Sorry, no variables were found in this group</h4>
			</c:when>
		</c:choose>
		<form class="hidden" id="addVarForm" 
		action="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/groups/${groupID}">
		</form>
</t:main>