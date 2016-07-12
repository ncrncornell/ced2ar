<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div id="loadCoverInner" class="editControlB col-xs-offset-1 col-sm-offset-2 col-md-offset-3 col-xs-10 col-sm-8 col-md-6">
	<form id="editForm" action="edit" method="POST">
		<h2>${title}</h2>
		<a href="edit" title="Save changes" id="editSave"
			class="editControl3 editIcon" onclick="return false;"> <i
			class="fa fa-floppy-o"></i>
		</a>
		<a href="../" title="Discard Changes"
			id="editDiscard" class="editControl3 editIcon"> 
			<i class="fa fa-trash-o"></i>
		</a>
		<c:set var="newTxtHeight" value="${((fn:length(curVal)/120))}" />
		<c:if test="${newTxtHeight lt 3}">
			<c:set var="newTxtHeight" value="3" />
		</c:if>
		<script src="${baseURI}/scripts/tinymce/tinymce.min.js"></script>
		<script type="text/javascript">
			$("#editSave").css("display","none");
			
			tinymce.init({		
			    selector: "textarea",
			    menubar : false,
			    content_css : "${baseURI}/styles/tinymce.css",
			    oninit : "setPlainText",
			    browser_spellcheck : true,
			    plugins: "paste save link code",
			    target_list: [{title: 'None', value: ''}],
			    paste_as_text: true,
			    toolbar: "save | undo redo | link | bullist | italic | code | git",
			    valid_elements :"a[href|target=_blank],strong/b,em,div,br,p,li,ul",
			    save_onsavecallback: function(){
			    	$("textarea [name=newText]").html(this.getContent());
					$("#editSave").click();
			    }
			});
		</script>
		<textarea name="newValue" style="height:${newTxtHeight}em">${curVal}</textarea>
		<c:if test="${not empty editDoc}">
			<p class="editDoc">${editDoc}</p>
		</c:if>
		<input type="hidden" name="field" value="${field}" /> 
		<input type="hidden" name="append" value="${append}" /> 
		<div class="hidden">
			<input type="hidden" name="curVal" value='${curVal}' />
		</div>
	</form>
</div>
