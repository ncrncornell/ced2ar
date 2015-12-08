<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="js" scope='request'>provEdit3 prov2c chosen/chosen.proto.min chosen/chosen.jquery.min</c:set>
<c:set var="css" scope='request'>provEdit edit prov2 chosen/chosen.min</c:set>
<t:main>
	<div class="row">
		<h2>Workflow Replication</h2>
		<div class="col-xs-6">
			<%--TODO: Write editing for existing node. Show all relationships, allow to change, or delete --%>
			<%--TODO: Write upload button --%>
			<p>
				<i class="fa fa-exclamation-triangle"></i>
				It is strongly recommended that you use a path to a Git or SVN repository for the URIs.
				For example, use a Github URL such as: 
				<a href="https://github.com/ncrncornell/ced2ar/blob/master/README.md">
					https://github.com/ncrncornell/ced2ar/blob/master/README.md
				</a>
				
			</p>
			<div>
				<form id="workflowAdd" method="POST" action="/" class="lb2">
					<fieldset>
						<legend>1. Input file</legend>		
						<div>
							<input type="radio" name="inputType" value="existing">Select existing node
						</div>
						<div>
							<input type="radio" name="inputType" value="new">Create new node
						</div>
						<div id="inputExisting">
							<select id="inputSelector"></select>
						</div>					
						<div id="inputNew">
							<span class="input-group">
								<span class="input-group-addon">
									<label for="inputID">Input ID</label>
								</span>
								<input id="inputID" class="form-control" name="inputID" type="text" placeholder="Enter an ID"/>	
							</span>
							<span class="inputErrorMsg"></span>
							
							<span class="input-group">
								<span class="input-group-addon">
									<label for="inputLabel">Input Label</label>
								</span>
								<input id="inputLabel" class="form-control" name="inputLabel" type="text" placeholder="Enter a label"/>
							</span>
							<span class="inputErrorMsg"></span>
							<span class="input-group">
								<span class="input-group-addon">
									<label for="inputURI">Input URI</label>
								</span>
								<input id="inputURI" class="form-control uri" name="inputURI" type="text" placeholder="Enter a URI"/>		
							</span>
						</div>
						<span class="inputErrorMsg"></span>
			        </fieldset>
					<fieldset>
						<legend>2. Program File</legend>		
						<span class="input-group">
							<span class="input-group-addon">
								<label for="progID">Program ID</label>
							</span>
							<input id="progID" class="form-control" name="progID" type="text" placeholder="Enter an ID"/>
						</span>
						<span class="inputErrorMsg"></span>
						<span class="input-group">
							<span class="input-group-addon">
								<label for="progLabel">Program Label</label>
							</span>
							<input id="progLabel" class="form-control" name="progLabel" type="text" placeholder="Enter a label"/>
						</span>
						<span class="inputErrorMsg"></span>
						<span class="input-group">
							<span class="input-group-addon">
								<label for="progURI">Program URI</label>
							</span>
							<input id="progURI" class="form-control uri" name="progURI" type="text" placeholder="Enter a URI"/>	
						</span>
						<span class="inputErrorMsg"></span>
			        </fieldset>
			        <fieldset>
						<legend>3. Output File</legend>		
						<span class="input-group">
							<span class="input-group-addon">
								<label for="outputID">Input ID</label>
							</span>
							<input id="outputID" class="form-control" name="outputID" type="text" placeholder="Enter an ID"/>
						</span>
						<span class="inputErrorMsg"></span>
						<span class="input-group">
							<span class="input-group-addon">
								<label for="outputLabel">Input Label</label>
							</span>
							<input id="outputLabel" class="form-control" name="outputLabel" type="text" placeholder="Enter a label"/>
						</span>
						<span class="inputErrorMsg"></span>
						<span class="input-group">
							<span class="input-group-addon">
								<label for="outputURI">Input URI</label>
							</span>
							<input id="outputURI" class="form-control uri" name="outputURI" type="text" placeholder="Enter a URI"/>
						</span>
						<span class="inputErrorMsg"></span>
			        </fieldset>
			        <fieldset>
						<legend>4. Metadata documentation</legend>	
						<select name="codebook">
							<option value="">None</option>
							<c:forEach var="codebook" items="${codebooks}">			
								<option value="${codebook.value[0]}${codebook.value[1]}">
								 ${codebook.value[4]} 
								</option>>
							</c:forEach>
						</select>
						<p>
							Linking to metadata documentation is optional at this time, but strongly recommended if possible.
						</p>
			        </fieldset>
			        <fieldset>
						<legend>5. Submit Form</legend>	
						<input type="submit" class="btn" value="Submit" />
						<span id="provError"></span>
						<span id="provSuccess"></span>
			        </fieldset>
			     </form>
			</div>
		</div>
		<div id="provSmallGrid" class="col-xs-6">
			<div id="provSmall">
				<div id="provHeaderControls">
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
	</div>
</t:main>