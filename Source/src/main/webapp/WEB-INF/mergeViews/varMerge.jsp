<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>merge/merge</c:set>
<c:set var="js" scope='request'>diff/diff_match_patch merge/mergeVars</c:set>
<t:main>
	<div id="details" class="row">		
			<div class="col-xs-6">
				<h2>Remote</h2>
				<div id="remoteDetails">
					${remote}
				</div>
			</div>
			<div class="col-xs-6">
				<h2>Current</h2>
				<div id="localDetails">
					${local}
				</div>
				<div id="mergeButtons">
					<button id="saveMerge" class="btn"><i class="fa fa-floppy-o fa-lg"></i> Save and Continue</button>
					<input name="_eventId_doMerge" type="submit" class="hidden" value='Merge'>
				</div>
			<p id="mergeFeedback"></p>
			</div>
	</div>
</t:main>