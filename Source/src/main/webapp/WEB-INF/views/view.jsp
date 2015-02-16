<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:if test="${type ne 'error'}">
	<c:set var="css" scope='request'>filter.min</c:set>
</c:if>
<c:set var="js" scope='request'>filter.min load.min read.min</c:set>
<%--This JSP page can be used to display errors, search results, browsing results (alphabetical starting with a letter), or all results --%>
<t:main>
	<c:choose>
		<c:when test="${type.equals('error')}">
			<t:search />
		</c:when>
		<c:when test="${type.equals('search')}">
			<t:search />
		</c:when>
		<c:when test="${type.equals('browse')}">
			<t:browse />
		</c:when>
		<c:when test="${type.equals('all')}">
			<t:all />
		</c:when>
	</c:choose>
	<form id="rf" action="filterCompare" method="get">
		<c:choose>
			<c:when test="${not empty data}">
				<display:table name="${data}" id="results" requestURI="${type}"
					sort="external" partialList="true" size="${count}"
					pagesize="${size}">
					<display:column class="cc">
						<c:set var="curCompare"
							value="${results[3]}${results[4]} ${results[0]}" />
						<input name="cc" type="checkbox" value="${curCompare}"
							title="Add to compare" />
					</display:column>
					<display:column sortable="true" class="varNameCol"
						title="Variable Name">
						<c:url
							value="codebooks/${results[3]}/v/${results[4]}/vars/${results[0]}"
							var="dLink" />
						<a href="${fn:replace(dLink,'&','&amp;')}">${results[0]}</a>
					</display:column>
					<display:column property="[1]" class="labelCol hidden-xs"
						headerClass="hidden-xs" paramId="l" sortable="true" title="Label" />
					<display:column property="[2]" class="codeBookCol" paramName=""
						paramId="c" sortable="true" title="Codebook" />
				</display:table>
			</c:when>
			<c:when test="${empty data && searched}">
				<h4 id="results">Sorry, no variables were found</h4>
			</c:when>
		</c:choose>
	</form>
</t:main>