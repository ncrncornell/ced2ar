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
<div id="loadCoverInner" class="editControlB ${lciw}">
	<form id="editForm" action="${handle}/edit" method="POST">
		<a id="editDiscard" href="../codebooks/${handle}" class="closeWindow" title="Discard Changes">×</a>
		<h2>${title}</h2>	
		<a href="#" title="Save changes" id="editSave"
			class="editControl3 editIcon" onclick="return false;"> <i
			class="fa fa-floppy-o"></i>
		</a> 
		<c:choose>
			<c:when test="${type.equals('attr')}"></c:when>
			<c:when test="${type.equals('accs')}">
				<select name="newValue" class="newAccs">
					<option value="">undefined</option>
					<c:forEach var="level" items="${accessLevels}">
						<c:if test="${not empty level}">
							<option value="${level}"
								<c:if test="${level eq curAccs}">selected="selected"</c:if>>
								${level}</option>
						</c:if>
					</c:forEach>
				</select>
			</c:when>
			<c:when test="${type.equals('elem')}">		
				<c:choose>
					<c:when test="${editorType.equals('0')}">
						<script src="${baseURI}/scripts/tinymce/tinymce.min.js"></script>
						<script type="text/javascript">
							$("#editSave").css("display","none");
							tinymce.init({		
							    selector: "textarea",
							    menubar : false,
							    content_css : "${baseURI}/styles/tinymce.css",
							    oninit : "setPlainText",
							    plugins: "paste save link code",
							    toolbar: "save | undo redo | link | bullist | code",
							    save_onsavecallback: function(){
							    	$("textarea [name=newText]").html(this.getContent());
									$("#editSave").click();
							    }
							});
						</script>
						<c:set var="newTxtHeight" value="${((fn:length(curVal)/80)*1.1)+2}" />
						<c:if test="${newTxtHeight lt 6}">
							<c:set var="newTxtHeight" value="6" />
						</c:if>
						<textarea name="newValue" style="height:${newTxtHeight}em">${curVal}</textarea>
					</c:when>
					<c:otherwise>
						<input type="text" name="newValue" class="newTxtPlain" value="${curVal}" />
					</c:otherwise>
				</c:choose>		
			</c:when>
		</c:choose>
		<c:if test="${editorType.equals('0')}">
			<p class="editDoc">
				This field supports ASCII math
				See <a href='${baseURI}/docs/faq#q3' target='_blank'>FAQ</a> for details. <br />	
			</p>
		</c:if>
		<input type="hidden" name="field" value="${field}" /> 
		<input type="hidden" name="index" value="${index}" /> 
		<input type="hidden" name="append" value="${append}" /> 
	</form>
</div>
