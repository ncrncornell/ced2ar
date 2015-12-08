<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="css" scope='request'>merge/merge</c:set>
<c:set var="js" scope='request'>diff/diff_match_patch merge/mergeVars2</c:set>
<t:merge>
	<h2>${mergeProperties.getCurrentVar()}</h2>	
		<div id="details" class="row">		
			<div class="col-xs-6">
				<h2>Crowdsourced Documentation</h2>
				<div id="remoteDetails">
					<p>${mergeProperties.getRemoteSnippet()}</p>
				</div>
			</div>
			<div class="col-xs-6">
				<h2>Official Documentation</h2>
				<div id="localDetails" class="lb">
					<p>${mergeProperties.getLocalSnippet()}</p>
				</div>
				<div id="mergeButtons">
					<button id="saveMerge" 
					class="btn lb2"><i class="fa fa-floppy-o fa-lg"></i> Make Changes</button>
					<form:form action="${flowExecutionUrl}" method='post'>
						<input type="hidden" name="handle" id="baseHandle" 
						value ="${mergeProperties.getBaseHandle()}"/>
						<input type="hidden" name="handle" id="version" 
						value ="${mergeProperties.getVersion()}"/>
						<input type="hidden" name="varName" id="varName" 
						value ="${mergeProperties.getCurrentVar()}"/>
						<button type="submit" name="_eventId_doMerge" type="submit" class="btn">
							<i class="fa fa-arrow-right"></i> Continue/Skip
						</button>
					</form:form>	
				</div>
				<p id="mergeFeedback"></p>
			</div>
		</div>	
</t:merge>