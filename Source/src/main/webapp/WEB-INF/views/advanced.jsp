<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>filter adv</c:set>
<c:set var="js" scope='request'>advancedSearch filter</c:set>
<t:main>
	<div id="adv">
		<h2>Advanced Search</h2>
		<div id="advSearchVerbose">
			<p>
				<c:forEach var="i" begin="1" end="6" step="1">
					<span id="all-${i}d"></span>
					<span id="any-${i}d"></span>
					<span id="none-${i}d"></span>
				</c:forEach>
			</p>
		</div>
		<c:set var="fields">Any Field,Variable Name,Label,Description,Codebook Instructions,Variable Concept</c:set>
		<form id="viewControls" action="advanced" method="get">
			<div class="nav nav-tabs">
				<%--Now support right in addition to left tabs. CSS rules for both are present. Right tabs fair better for mobile first design --%>
				<ul class="nav tabs-right ">
					<c:forEach var="i" begin="1" end="6" step="1">
						<li ${i eq 1 ? 'class="active"':''}><a href="#t${i}"
							data-toggle="tab">${fn:split(fields,',')[i-1]}</a></li>
					</c:forEach>
				</ul>
				<div class="tab-content">
					<c:forEach var="i" begin="1" end="6" step="1">
						<div class="tab-pane ${i eq 1 ? 'active':''}" id="t${i}">
							<h4>${fn:split(fields,',')[i-1]}</h4>
							<label>...must contain <b>ALL</b> of the following<input
								type="text" name="all-${i}" /></label> <label>...must contain <b>at
									least one</b> of the following<input type="text" name="any-${i}" /></label>
							<label>...must <b>NOT</b> contain any of the following<input
								type="text" name="none-${i}" /></label>
						</div>
					</c:forEach>
				</div>
			</div>
			<div id="advCtrls">
				<input type="submit" class="btn" value="Search" />
				<button type="button" class="btn" onclick="resetForm()">Reset
					Form</button>
			</div>
		</form>
	</div>
</t:main>