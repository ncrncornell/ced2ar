<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>prov2</c:set>
<c:set var="js" scope='request'>prov2e</c:set>
<t:main>
	<div id="provHeaderControls">
		<h2>Workflow Graph</h2>
		<%--
		<select id="provRootNode">
			<option>--Show All Nodes--</option>
		</select>
		  --%>
		<button id="provShuffle" title="Reset with root nodes" class="btn">Reset</button>
		<button id="provCluster" title="Hold to cluster" class="btn hidden">Cluster</button>		
		<form name="provFilter" class="noSelect">
			<label for="authors">Authors</label>
			<input type="checkbox" name="authors" checked>
			<label for="texts">Texts</label>
			<input type="checkbox" name="texts" checked>
			<label for="Theses">Theses</label>
			<input type="checkbox" name="theses" checked>
		</form>
		<label for="edgeLabels">Edge Labels</label>
		<input type="checkbox" id="edgeLabelToggle" name="edgeLabels" checked>	
		&nbsp;
		<i class="fa fa-question-circle helpIcon hidden-xs" 
			data-toggle="popover" title="" 
			data-content="Mouse wheel zooms, Q/E keys to rotate. Click on nodes to select and view details." 
			data-original-title="Workflow Graph">
		</i>
		<!-- 
		<input id="inverted" name="inverted" type="hidden" value="${inverted}"/>
		 -->
		<input id="startingNode" name="startingNode" type="hidden" value="${startingNode}"/>
		<input id="inverted" name="inverted" type="hidden" value="true"/>
		<input id="filterNode" name="filterNode" type="hidden" value="${filterNode}"/>
	</div>	

	<div id="graph2Info"></div>
	<div id="graph2" class="noSelect"></div>
	
	<script src="${baseURI}/scripts/sigmajs2/sigma.min.js"></script>
	<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.layout.forceAtlas2.min.js"></script>
	<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.renderers.edgeLabels.min.js"></script>
	<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.renderers.customShapes.min.js"></script>
</t:main>