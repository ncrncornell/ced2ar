<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="js" scope='request'>accessVars</c:set>
<c:set var="css" scope='request'>access</c:set>
<t:main>
	<h2>Apply Access Levels - ${codebookInfo[4]}</h2>
	<p class="lb2">Check a set of variables, select an access level, and click change levels to update access attributes.</p>
	<form class="lb2" id="accessvars" action="accessvars" method="POST" >
		<div class="lb2">
			<label for="accsLevels">Mark selected as
				<select name="accsLevels">
				<option value="">undefined</option>
				<c:forEach var="level" items="${accessLevels}">
					<option value="${level}">${level}</option>
				</c:forEach>
				</select>
			</label>
			<button id="accessBtn" class="btn disabled" type="submit">Change Levels</button>
		</div>
		<c:choose>	
			<c:when test="${not empty data}">
				<display:table name="${data}" id="results" requestURI="${type}"
					sort="external" partialList="false" size="${count}" pagesize="${count}">
					<display:column class="cc">					
							<label>
								<input name="cc" type="checkbox" value="${results[0]}" title="Select" />
							</label>
					</display:column>
					<display:column sortable="true" class="varNameCol" title="Variable Name">
							<c:url
								value="vars/${results[0]}"
								var="dLink" />
							<a href="${fn:replace(dLink,'&','&amp;')}">${results[0]}</a>
					</display:column>
					<display:column property="[1]" class="labelCol hidden-xs"
						headerClass="hidden-xs" paramId="l" sortable="true" title="Label" />
					<display:column property="[2]" class="codeBookCol" paramName=""
						paramId="c" sortable="true" title="Access Level" />
				</display:table>
			</c:when>
			<c:when test="${empty data && searched}">
				<h4 id="results">Sorry, no variables were found in this codebook</h4>
			</c:when>
		</c:choose>
	</form>
</t:main>