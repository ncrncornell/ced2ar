<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<h4>Filter Codebooks</h4>

<i class="fa fa-question-circle helpIcon" data-toggle="popover"
	title="Filtering"
	data-content="Click the checkboxes to add or remove codebooks from the filter">
</i>

<form id="ff" class="noSelect" action="${baseURI}/filterCodebook">
	<c:if test="${not empty sessionScope.filter}">
		<c:set var="fL" scope="session" value="" />
		<table>
			<c:forEach var="cb" items="${filter}" varStatus="i">
				<c:set var="checked" scope="page" value="" />
				<c:set var="cclass" scope="page" value="" />
				<c:if test="${cb[5] eq 'true'}">
					<c:set var="checked" scope="page" value="checked='checked'" />
					<c:set var="cclass" scope="page" value="checked" />
					<c:set var="filtered" scope="page" value="true" />
					<c:set var="fL" scope="session" value="${fL} ${cb[0]}" />
				</c:if>

				<%--Prints out baseHandle header --%>
				<c:if test="${(i.index eq 0) || filter[i.index][1] ne filter[i.index - 1][1]}">	
					<tr class="filterToggle">
						<%--Only one codebook in basehandle --%>
						<c:choose>
							<c:when test="${filter[i.index][1] ne filter[i.index + 1][1]}">
								<c:set var="slabel" scope="session" value="${cb[4]}" />
								<td class="ftiH"></td>
							</c:when>
							<c:otherwise>
								<c:set var="slabel" scope="session" value="Expand&nbsp;for&nbsp;details" />
								<td class="fti"></td>
							</c:otherwise>
						</c:choose>						
						<td class="${cb[1]}">
							<c:set var="headerChecked" scope="page" value="" />
							<c:forEach var="fH" items="${sessionScope.filterHeader}">
								<c:if test="${cb[1] eq fH}">
									<c:set var="headerChecked" scope="page" value="checked" />
								</c:if>								
							</c:forEach>							
							<label class="${headerChecked} ttf" data-toggle="tooltip"
							data-original-title="${fn:replace(slabel,'-','&#8209;')}"> 
								${filter[i.index][3]} 
								<input type="checkbox" name="ctb" value="${cb[1]}" ${headerChecked} />
							</label>
						</td>
						<c:choose>
							<c:when test="${fn:contains(sessionScope.filterShow,cb[1])}">
								<input type="hidden" name="vs" class="filterShow ${cb[1]}"
									value="${cb[1]}">
								<c:set var="fts" scope="page" value=" fts" />
							</c:when>
							<c:otherwise>
								<input type="hidden" name="vs" class="filterShow ${cb[1]}">
								<c:set var="fts" scope="page" value="" />
							</c:otherwise>
						</c:choose>
					</tr>
				</c:if>
				<tr class="filterVersion ${cb[1]} ${fts}">
					<td>
					<label class="checkbox ${cclass} ttf" data-toggle="tooltip" 
					data-original-title="${fn:replace(cb[4],'-','&#8209;')}">
							${fn:replace(cb[2],'-','&#8209;')}&nbsp;<input type="checkbox"
							name="cb" value="${fn:trim(cb[0])}" ${checked} />
					</label></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<input type="hidden" name="updateV" /> <input type="hidden"
		name="updateB" /> <input type="hidden" name="updateT" />
</form>
<p class="fMessage">
	<c:choose>
		<c:when test="${empty sessionScope.filter}">
			Error retrieving data
		</c:when>
	</c:choose>
</p>