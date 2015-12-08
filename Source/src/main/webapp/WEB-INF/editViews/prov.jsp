<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="js" scope='request'>provEdit</c:set>
<c:set var="css" scope='request'>provEdit edit</c:set>
<t:main>
	<h2>Workflow Editing - New Relationship</h2>
	<div id="provEdit">
		<form id="provForm" method="POST" class="lb2" action="/">
			<input type="hidden" name="psObject" id="psObject" value="${psObject}" >
			<input type="hidden" name="psSubject" id="psSubject" value="${psSubject}" >
			<fieldset>
				<legend>1. Select an object</legend>
				<table>
					<tr>
						<td>
							<input type="radio" name="objSelect" value="existingNode">	
							Select existing node
						</td>
					</tr>
					<tr>
						<td>
							<input type="radio" name="objSelect" value="newNode">	
							Create new node
						</td>	
					</tr>
					<tr>
						<td>
							<span id="provObjExisting" class="hidden">
								<select name="provObjExisting">
									<c:forEach var="node" items="${nodeList}" varStatus="i">
										 <option value="${node['id']}#${node['nodeType']}">${node['label']} (${node['id']})</option>
									</c:forEach>
								</select>
							</span>
						</td>
					</tr>
					<tr>	
						<td>
							<span id="provObjNew" class="hidden">
								<select name="provObjClass">			
										<option value="-1">--Select Class--</option>
										<option value="0">Data Object</option>	
										<option value="1">Program</option>	
										<option value="2">Project</option>				
								</select>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="objID">Object ID</label>
									</span>
									<input id="objID" class="form-control" name="objID" type="text" placeholder="Enter an ID"/>	
								</span>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="objLabel">Object Label</label>
									</span>
									<input id="objLabel" class="form-control" name="objLabel" type="text" placeholder="Enter a label"/>
								</span>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="objURI">Object URI</label>
									</span>
									<input id="objURI" class="form-control uri" name="objURI" type="text" placeholder="Enter a URI"/>		
								</span>
							</span>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>2. Select a subject</legend>
				<table>
					<tr>
						<td>
							<input type="radio" name="subSelect" value="existingNode"> Select existing node
						</td>
					</tr>
					<tr>
						<td>
							<input type="radio" name="subSelect" value="newNode"> Create new node
						</td>
					</tr>
					<tr>
						<td>
							<span id="provSubExisting" class="hidden">
								<select name="provSubExisting">
									<c:forEach var="node" items="${nodeList}" varStatus="i">
										 <option value="${node['id']}#${node['nodeType']}">${node['label']} (${node['id']})</option>
									</c:forEach>
								</select>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span id="provSubNew" class="hidden">
								<select name="provSubClass">			
										<option value="-1">--Select Class--</option>
										<option value="0">Data Object</option>	
										<option value="1">Program</option>	
										<option value="2">Project</option>				
								</select>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="subID">Subject ID</label>
									</span>
									<input id="subID" class="form-control" name="subID" type="text" placeholder="Enter an ID"/>	
								</span>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="subLabel">Subject Label</label>
									</span>
									<input id="subLabel" class="form-control" name="subLabel" type="text" placeholder="Enter a label"/>
								</span>
								<span class="input-group">
									<span class="input-group-addon">
										<label for="subURI">Subject URI</label>
									</span>
									<input id="subURI" class="form-control uri" name="subURI" type="text" placeholder="Enter a URI"/>		
								</span>
							</span>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset id="provEditPred" class="lb2 fieldSetI"> 
				<legend>3. Select a predicate</legend>
					<input type="hidden" value='${predList}' />
					<select disabled class="disabled" name="provPred"></select>
	        </fieldset>
	        <fieldset class="fieldSetI">  	        	
					<legend>4. Review and Add</legend>
					<p class="lb2">
						<span class="provPrev" id="provPreviewObj"></span>
						<span class="provPrev" id="provPreviewPred"></span>
						<span class="provPrev" id="provPreviewSub"></span>
						<button disabled id="provAddButton" type="submit" class="btn disabled">
				            <i class="fa fa-plus largeIcon"></i> Add
				        </button>
					</p>
					<p id="provErrors"></p>
					<p id="provStatus"></p>
	        </fieldset>
        </form>
	</div>
</t:main>