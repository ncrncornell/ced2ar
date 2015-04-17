<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:choose>
	<c:when test="${type.equals('accs')}">
		<c:set var="lciw" value="col-xs-offset-2 col-sm-offset-3 col-md-offset-4 col-xs-6 col-sm-5 col-md-4" />
	</c:when>
	<c:when test="${editorType.equals('0')}">
		<c:set var="lciw" value="col-sm-offset-1 col-md-offset-2 col-xs-12 col-sm-10 col-md-8" />
	</c:when>
	<c:otherwise>
		<c:set var="lciw" value="col-xs-offset-1 col-sm-offset-2 col-md-offset-3 col-xs-10 col-sm-8 col-md-6" />
	</c:otherwise>
</c:choose>
<div id="loadCoverInner" class="editControlB no Select ${lciw}">
	<form id="editForm" action="${handle}/editMulti" method="POST">
		<a id="editDiscard" href="#" class="closeWindow" title="Discard Changes">×</a>
		
		<h2 class="capitalize">${title}</h2>	
		<a href="#" title="Save changes" id="editSave"
			class="editIcon3" onclick="return false;"> <i
			class="fa fa-floppy-o"></i>
		</a> 
		<c:forEach var="value" items="${values}" varStatus="i">
			<div class="lb2">
				<span class="input-group">
					<span class="input-group-addon">
						<label for="newValue">${labels[i.index]}</label>
					</span>
					<input type="text" name="newValue" class="newTxtPlain2" value="${value}" />
				</span>
			</div>
		</c:forEach>
		<input type="hidden" name="field" value="${field}" /> 
		<input type="hidden" name="append" value="${append}" /> 
	</form>
</div>
