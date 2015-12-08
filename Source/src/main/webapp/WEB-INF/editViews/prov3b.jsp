<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="js" scope='request'>prov2b bootstrap/bootstrap-editable.min chosen/chosen.proto.min chosen/chosen.jquery.min</c:set>
<c:set var="css" scope='request'>provEdit edit prov2 bootstrap/bootstrap-editable chosen/chosen.min</c:set>
<t:main>
	<div class="row">
		<h2>Workflow Global View</h2>
		<div id="provSmallGrid" class="col-xs-6 noSelect">
			<div id="provSmall">
				<div id="provHeaderControls">
					
					&nbsp;
					<select id="nodeFilterList" name="nodeFilterList">
						<option>--Show All Nodes--</option>
					</select>
					<button id="provRotateCCW" class="btn" title="Rotate Counter-Clockwise"><i class="fa fa-undo"></i></button>
	                <button id="provRotateCW" class="btn" title="Rotate Clockwise"><i class="fa fa-repeat"></i></button>
	                <button id="provZoomIn" class="btn" title="Zoom In"><i class="fa fa-search-plus"></i></button>
	                <button id="provZoomOut" class="btn" title="Zoom Out"><i class="fa fa-search-minus"></i></button>
					<button id="provShuffle" class="btn"><i class="fa fa-refresh"></i>Shuffle</button>
					<i class="fa fa-question-circle helpIcon hidden-xs" 
						data-toggle="popover" title="" 
						data-content="Click on nodes to select and view details." 
						data-original-title="Workflow Graph">
					</i>	
					<input id="inverted" name="inverted" type="hidden" value="true"/>
					<input id="filterNode" name="filterNode" type="hidden" value="${filterNode}"/>
				</div>	
			
				<div id="graph2Info"></div>
				<div id="graph2" class="noSelect small"></div>
				<script src="${baseURI}/scripts/sigma/sigma.min.js"></script>
				<script src="${baseURI}/scripts/sigma/plugins/sigma.parsers.json.min.js"></script>
				<script src="${baseURI}/scripts/sigma/plugins/sigma.layout.forceAtlas2.min.js"></script>
				<script src="${baseURI}/scripts/sigma/plugins/sigma.renderers.customShapes.min.js"></script>
			</div>
		</div>
		<div class="col-xs-6">
			<%--TODO: Write editing for existing node. Show all relationships, allow to change, or delete --%>
			<div>
				<form id="workflowView" method="POST" action="/" class="lb2"></form>
			</div>
		</div>
	</div>
</t:main>