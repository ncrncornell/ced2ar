<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:set var="css" scope='request'>edit</c:set>
<c:set var="js" scope='request'>workflow/upload</c:set>
<t:main>
	<%-- TODO: limit form length --%>
	<h2>Convert a Dataset</h2>
	<p>
		<em>Current Requirements</em>
	</p>
	<ul class="lb2 bullets">
		<li>Stata .dti files (version 8-14) and SPSS .sav file are accepted at this time</li>
		<li>Please limit total file size to 75mb</li>
	</ul>
	<p>
		See this github repository on conversion instructions for R, SAS and other formats:
		<a href="https://github.com/ncrncornell/DatasetConversions">https://github.com/ncrncornell/DatasetConversions</a>	
	</p>
	<sf:form id="addForm" method="post" action="${baseURI}/edit/data" enctype="multipart/form-data">		
		<div class="uploadFileForm">
			<button class="btn fileUploadBtn"><i class="fa fa-file"></i> Select New File</button>   
            <input class="hidden fileUploadHidden" type="file" name="file">
            <input class="fileUploadDisplay requiredInput" placeholder="No File Selected" type="text" disabled>
         	</div>
		<span class="input-group">
 				<span class="input-group-addon">
 					<label for="handle">
 						Base Handle <i class="fa fa-question-circle helpIcon helpIconB" 
 						data-toggle="popover" title="" 
 						data-original-title="Base Handle (required)"
 						data-content="A handle is a unique alphanumeric identifier for a group of codebooks. 
 						For example, a collections of codebooks on SIPP Sythenic Beta might have the handle of 'ssb'"></i>
 					</label>
 				</span>
				<input class="form-control requiredInput" type="text" name="handle">
		</span>		
		<%-- 
		<span class="input-group">
 				<span class="input-group-addon">
  				<label for="label">
  					Label <i class="fa fa-question-circle helpIcon helpIconB" 
 						data-toggle="popover" title="" 
 						data-original-title="Label (required)"
 						data-content="A shorthand name to describe the handle that is human readable - ie SSB. This should be unique."></i>
  				</label>
 				</span>
				<input class="form-control requiredInput" type="text" name="label">		 				
		</span>	
		--%>	
		<span class="input-group">
 				<span class="input-group-addon">
  				<label for="handle">
  					Version <i class="fa fa-question-circle helpIcon helpIconB" 
					data-toggle="popover" title="" 
					data-original-title="Version (required)"
					data-content="The version can be any alphanumeric identifier such as a year or release number" ></i>
  				</label>
 				</span>
				<input class="form-control requiredInput" type="text" name="version">
		</span>		
		<button type="submit" class="btn">
               <i class="fa fa-upload largeIcon"></i> Add
        </button>
	</sf:form>
</t:main>