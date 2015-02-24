<%@ page contentType="text/html" pageEncoding="UTF-8"%>editControl4
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div id="loadCoverInner"
	class="editControlB col-xs-offset-2 col-sm-offset-3 col-md-offset-4 col-xs-6 col-sm-5 col-md-4">
	<form id="editForm" action="${handle}/edit" method="POST">
		<h4>
			Are you sure you want to delete this
			'${fn:toLowerCase(title)}' field?
		</h4>
		<p>	
			<a href="../codebooks/${handle}" title="Cancel Changes"
			id="editDiscard" class="delIcon"> 
				Cancel
			</a> 
			<a href="#" title="Delete Field" id="editSave"
			class="delete delIcon2" onclick="return false;"> 
				Delete
			</a>
			<input type="hidden" name="field" value="${field}" /> 
			<input type="hidden" name="index" value="${index}" /> 
		</p>
	</form>
</div>
