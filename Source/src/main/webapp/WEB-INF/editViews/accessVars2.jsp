<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="js" scope='request'>accessVars2</c:set>
<c:set var="css" scope='request'>access</c:set>
<t:main>
	<h2>Apply Access Levels - ${codebookInfo[4]}</h2>
	<p class="lb2">Check a set of variables, select an access level, and click change levels to update access attributes.</p>
	<form class="lb2" id="accessvars" method="POST" >	
		<input type="hidden" name="baseHandle" value="${baseHandle}" />
		<input type="hidden" name="version" value="${version}" />
		<p>
			<em>Select what sub-elements to mark</em>
			<br />
			<input name="subpaths_all" type="checkbox" value="true" title="Select All" /> Select All
		</p>		
		<div class="row">
			<div class="col-xs-6">
				<p class="lb2">
					<input name="subpaths" type="checkbox" value="mean" title="Mean" /> Mean
					<br />
					<input name="subpaths" type="checkbox" value="medn" title="Median" /> Median
					<br />
					<input name="subpaths" type="checkbox" value="mode" title="Mode" /> Mode
					<br />
					<input name="subpaths" type="checkbox" value="vald" title="Valid" /> Valid
					<br />
					<input name="subpaths" type="checkbox" value="invd" title="Invalid" /> Invalid
					<br />
					<input name="subpaths" type="checkbox" value="min" title="Min" /> Min
					<br />
					<input name="subpaths" type="checkbox" value="max" title="Max" /> Max
					<br />
					<input name="subpaths" type="checkbox" value="stdev" title="Standard Deviation" /> 
					Standard Deviation
					<br />
					<input name="subpaths" type="checkbox" value="sumStatOther" title="Other Summary Statitics" />
					Other Summary Statistics
				</p>
			</div>
			<div class="col-xs-6">
				<p class="lb2">	
					<!-- 
					<input name="subpaths" type="checkbox" value="valrng" title="range" /> Range
					<br />
					 -->
					<input name="subpaths" type="checkbox" value="catgry" title="Values" /> Values
					<br />
					<input name="subpaths" type="checkbox" value="freq" title="Value Frequencies" /> Value Frequencies
					<br />
					<input name="subpaths" type="checkbox" value="percent" title="Value Percentages" /> Value Percentages
					<br />
					<input name="subpaths" type="checkbox" value="crosstab" title="Value Crosstab" /> Value Crosstabs
					<br />
					<input name="subpaths" type="checkbox" value="catStatOther" title="Other Value Statistics" /> Other Value Statistics
					<br />
					<input name="subpaths" type="checkbox" value="labl" title="Label" /> Label
					<br />
					<input name="subpaths" type="checkbox" value="notes" title="Notes" /> Notes
				</p>
			</div>
		</div>
		<p class="lb3">
			<em>Select what access level to apply, then check which variables to apply to. Finally, click changes levels.</em>
		</p>
		<div class="lb2">
			<label for="accsLevels">
				<select name="accsLevels">
				<option value="">undefined</option>
				<c:forEach var="level" items="${accessLevels}">
					<option value="${level}">${level}</option>
				</c:forEach>
				</select>
			</label>
			<button id="accessBtn" class="btn disabled" type="submit">Change Levels</button>
			<span id="accessFeedback" class="feedback"></span>
		</div>
		
		<c:choose>	
			<c:when test="${not empty data}">
				<display:table name="${data}" id="results" requestURI="${type}"
					sort="external" partialList="false" size="${count}" pagesize="${count}">
					<display:column class="cc">					
							<label>
								<input name="vars" type="checkbox" value="${results[0]}" title="Select" />
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
						paramId="c" sortable="true" title="Top Access Level" />
				</display:table>
			</c:when>
			<c:when test="${empty data && searched}">
				<h4 id="results">Sorry, no variables were found in this codebook</h4>
			</c:when>
		</c:choose>
	</form>
</t:main>