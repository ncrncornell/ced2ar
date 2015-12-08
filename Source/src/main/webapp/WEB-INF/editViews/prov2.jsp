<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="js" scope='request'>provEdit2</c:set>
<c:set var="css" scope='request'>provEdit edit</c:set>
<t:main>
	<h2>Workflow Editing - New Entry</h2>
	<%--TODO: Write editing for existing node. Show all relationships, allow to change, or delete --%>
	<%--TODO: Write upload button --%>
	<div>
		<form id="provFileAdd" method="POST" action="/" class="lb2">
			<fieldset>
				<legend>1.Input Location</legend>
				<div class="lb3">
					<input type="radio" name="fileLocSelect" value="online">Online
					<i class="fa fa-question-circle helpIcon" data-toggle="popover" 
					title="" data-original-title="Online Input (prefered)" 
					data-content="A URL that points directly to the targeted location."></i>
				</div>
				<div class="lb2">
					<input type="radio" name="fileLocSelect" value="offline">Offline
					<i class="fa fa-question-circle helpIcon" data-toggle="popover" 
					title="" data-original-title="Offline Input" 
					data-content="Select a file from your local computer. Note this does not upload the file."></i>
				</div>							
				<div id="fileOnline" class="input-group lb2 hidden">
					<span class="input-group-addon">
						<label for="objURL">Input URL</label>
					</span>
					<input id="objURL" class="form-control uri" name="objURL" type="text" placeholder="Enter a URL">		
				</div>
				<div id="fileOffline" class="hidden">
					<button class="btn fileUploadBtn">
						<i class="fa fa-file"></i> Select New File
					</button>   
		            <input class="hidden fileUploadHidden" type="file" name="fileOffline">
		            <input class="fileUploadDisplay requiredInput" name="offlineDisplay" placeholder="No File Selected" type="text" value="" disabled>		           
		        </div>
		        <input type="hidden" name="objLocal">
	        </fieldset>
			<fieldSet>
				<legend>
					2. File Type					
				</legend>
				<select name="provObjClass">			
					<option value="">--Select Type--</option>
					<option value="meta">Archive</option>
					<option value="ddi">Data File</option>	
					<option value="ddi">DDI Metadata</option>
					<option value="doc">Documentation File</option>	
					<option value="meta">Other Structured Metadata</option>	
					<option value="other">Other</option>	
					<option value="script">Scripting Code</option>
					<option value="stat">Statistical Code</option>						
				</select>
				<i class="fa fa-question-circle helpIcon" data-toggle="popover" 
					title="" data-original-title="File Type" 
					data-content="Choose a catagory that best describes what are targeting">
				</i>
			</fieldSet>
			<fieldSet>
				<legend>3. Details</legend>
				<div class="input-group lb2">
					<span class="input-group-addon">
						<label for="objLabel">
							Input Label
							<i class="fa fa-question-circle helpIcon helpIconB" data-toggle="popover" 
							title="" data-original-title="Label (required)" 
							data-content="A short title describing the input"></i>
						</label>
					</span>
					<input id="objLabel" class="form-control" name="objLabel" type="text" placeholder="Enter a Label">		
				</div>
			</fieldSet>
			<button id="fileAddButton" type="submit" class="btn disabled">
	            <i class="fa fa-plus largeIcon"></i> Add
	        </button>
	        <input type="hidden" name="duplicateOverride" value="">
	        <div id="duplicatePrompt" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
					<h4 class="modal-title" id="mySmallModalLabel">Warning</h4>
					</div>
					<div id="duplicateMessage" class="modal-body"></div>
						<div class="modal-footer">
							<button id="duplicateOverride" type="button" class="btn btn-default">
								Submit
							</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						</div>
					</div>
				</div>
			</div>
        </form> 
		<div>
			<p id="provErrors"></p>
			<p id="provStatus"></p>
		</div>
	</div>
</t:main>