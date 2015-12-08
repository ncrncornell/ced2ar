<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>merge/merge</c:set>
<c:set var="js" scope='request'>diff/diff_match_patch merge/varDiff</c:set>
<t:main>
	<!-- 
	<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/${var}" class="printButton"
	title="Back to reading mode" aria-label="Back to reading mode">
		<i class="fa fa-arrow-left"></i> Back to reading mode
	</a> -->
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
			</div>
	</div>
</t:main>