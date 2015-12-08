<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>prov2 workflow/workflow</c:set>
<c:set var="js" scope='request'>workflow/prov2g workflow/workflowSearch</c:set>
<t:main>
	<div class="row">
		<div class="col-xs-2">
			<h2>Legend</h2>
			<p class="lb2">
				<svg width="40" height="40">
					<rect width="40" height="40" stroke="rgba(42,100,150,1)" 
					stroke-width="8" fill="rgba(42,100,150,.9)" />
				</svg>
				Squares represent datasets. 
				These include any source of information,including derivations, 
				summary statistics, and tabulations.
			</p>
			<p class="lb2">
				<svg width="48" height="48">
					 <g transform="translate(24 2) rotate(45)">
						<rect width="30" height="30" stroke="rgba(42,100,150,1)" 
						stroke-width="4" fill="rgba(42,100,150,.9)" />
					 </g>
				</svg>
				Diamonds represent programs. 
				Programs would be any code, application or even process that can produce or use datasets.
			</p>
			<p class="lb2">
				<svg width="44" height="44">
					<circle cx="22" cy="22" r="20" stroke="rgba(42,100,150,1)" stroke-width="4" fill="rgba(42,100,150,.9)" />
				</svg>
				Circles represent providers.
				Providers are the host of datasets, such as institutions, organizations or groups.
			</p>
		</div>
		<div class="col-xs-8">
			<div id="provHeaderControls">
				<button id="provCluster" title="Hold to cluster" class="btn hidden">Cluster</button>		
				<label for="edgeLabels" class="hidden">Edge Labels</label>
				<input class="hidden" type="checkbox" id="edgeLabelToggle" name="edgeLabels" checked>	
				
				<button id="provRotateCCW" class="btn" title="Rotate Counter-Clockwise"><i class="fa fa-undo"></i></button>
				<button id="provRotateCW" class="btn" title="Rotate Clockwise"><i class="fa fa-repeat"></i></button>
				<button id="provShuffle" title="Reset with root nodes" class="btn"><%--<i class="fa fa-refresh"></i>--%>Rebuild</button>
				
				<a href="${baseURI}/edit/workflow/add" class="btn" 
				title="Add new node"><i class="fa fa-plus"></i>  Add Node</a>
				
				<a href="${baseURI}/edit/workflow/edge" class="btn" 
				title="Add new edge"><i class="fa fa-plus"></i>  Add Edge</a>
				
				&nbsp;
				<i class="fa fa-question-circle helpIcon hidden-xs" 
					data-toggle="popover" title="" 
					data-content="Mouse wheel zooms. Click on nodes to select and view details." 
					data-original-title="Workflow Graph">
				</i>
				
				<%--
				<a href="${baseURI}/edit/workflow/add-chain" class="provButtonF" 
				title="Add new replication workflow"><i class="fa fa-plus"></i></a>
				 --%>
				 
				<input id="filterNode" name="filterNode" type="hidden" value="${filterNode}"/>
				<input id="startingNode" name="startingNode" type="hidden" value="${startingNode}"/>
				
			</div>	
		
			<div id="graph2Info"></div>
			<div id="graph2" class="noSelect"></div>
			
			<script src="${baseURI}/scripts/sigmajs2/sigma.min.js"></script>
			<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.layout.forceAtlas2.min.js"></script>
			<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.renderers.edgeLabels.min.js"></script>
			<script src="${baseURI}/scripts/sigmajs2/plugins/sigma.renderers.customShapes.min.js"></script>
		</div>
		<div class="col-xs-2">
			<h2>Search</h2>
			<input id="workflowSearch" type="text" placeholder="Enter a name">
			<table id="workflowResults"></table>
		</div>
	</div>
</t:main>