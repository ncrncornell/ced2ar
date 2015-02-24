<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="css" scope='request'>edit.min</c:set>
<c:set var="js" scope='request'>fileUpload</c:set>
<t:main>
	<div id="uploadCodebook">
		<h2 class="lb3">Modify a Codebook</h2>		
		<div class="nav nav-tabs">
			<ul class="nav tabs">
				<li class="active"><a href="#t1" data-toggle="tab" title="Add a new codebook"><i class="fa fa-plus largeIcon"></i> Add</a></li>	
				<li><a href="#t2" data-toggle="tab" title="Modify an existing codebook"><i class="fa fa-pencil-square-o largeIcon"></i> Update</a></li>
				<li><a href="#t3" data-toggle="tab" title="Delete an existing codebook"><i class="fa fa-trash-o largeIcon"></i> Delete</a></li>
				<li><a href="#t4" data-toggle="tab" title="Change settings"><i class="fa fa-wrench largeIcon"></i> Settings</a></li>
				<c:if test="${git}">
					<li><a href="#t5" data-toggle="tab" title="Git"><i class="fa fa-git largeIcon"></i> Versions</a></li>
				</c:if>
			</ul>
			<div class="tab-content">		  
				<div class="tab-pane active" id="t1">
					<h4>Add New Codebook</h4>
					<sf:form id="addForm" method="post" action="${baseURI}/edit/add/codebook" enctype="multipart/form-data">		
						<div class="uploadFileForm">
		  					<button class="btn fileUploadBtn"><i class="fa fa-file"></i> Select New File</button>   
				            <input class="hidden fileUploadHidden" type="file" name="file">
				            <input class="fileUploadDisplay requiredInput" placeholder="No File Selected" type="text" disabled>
			          	</div>
						<span class="input-group">
			  				<span class="input-group-addon">
			  					<label for="handle">
			  						Base Handle
			  						<i class="fa fa-question-circle helpIcon helpIconB" 
			  						data-toggle="popover" title="" 
			  						data-original-title="Base Handle (required)"
			  						data-content="A handle is a unique alphanumeric identifier for a group of codebooks. 
			  						For example, a collections of codebooks on SIPP Sythenic Beta might have the handle of 'ssb'" 
			  						></i>
			  					</label>
			  				</span>
			 				<input class="form-control requiredInput" type="text" name="handle">
						</span>		
						<span class="input-group">
			  				<span class="input-group-addon">
				  				<label for="label">
				  					Label
				  					<i class="fa fa-question-circle helpIcon helpIconB" 
			  						data-toggle="popover" title="" 
			  						data-original-title="Label (required)"
			  						data-content="A shorthand name to describe the handle that is human readable - ie SSB. This should be unique." 
			  						></i>
				  				</label>
			  				</span>
			 				<input class="form-control requiredInput" type="text" name="label">		 				
						</span>		
						<span class="input-group">
			  				<span class="input-group-addon">
				  				<label for="handle">
				  					Version
			  						<i class="fa fa-question-circle helpIcon helpIconB" 
			  						data-toggle="popover" title="" 
			  						data-original-title="Version (required)"
			  						data-content="The version can be any alphanumeric identifier such as a year or release number" 
			  						></i>
				  				</label>
			  				</span>
			 				<input class="form-control requiredInput" type="text" name="version">
						</span>		
						<button type="submit" class="btn">
			                <i class="fa fa-upload largeIcon"></i> Add
			            </button>
					</sf:form>
		  		</div>
		  		<div class="tab-pane" id="t2">
			  		<h4>Update Existing Codebook</h4>
			  		<sf:form id="updateForm" method="post" action="${baseURI}/edit/update/codebook" enctype="multipart/form-data">			
		  				<div class="uploadFileForm">
		  					<button class="btn fileUploadBtn"><i class="fa fa-file"></i> Select New File</button>   
				            <input class="hidden fileUploadHidden" type="file" name="file">
				            <input class="fileUploadDisplay requiredInput" placeholder="No File Selected" type="text" disabled>
			          	</div>
			          	<select name="handle" class="requiredInput">
							<option value="">--Select Codebook to Modify--</option>
							<c:forEach var="codebook" items="${codebooks}">					
									<option value="${codebook.value[0]}.${codebook.value[1]}">${codebook.value[2]} - ${codebook.value[1]}</option>					
							</c:forEach>			
						</select>
						<button type="submit" class="btn">
			                <i class="fa fa-upload largeIcon"></i> Update
			            </button>
					</sf:form>
		  		</div>
		  		<div class="tab-pane" id="t3">
			  		<h4>Delete Codebook</h4>
			  		<sf:form id="deleteForm" method="post" action="${baseURI}/edit/delete/codebook" enctype="multipart/form-data">			
			          	<select name="handle" class="requiredInput">
							<option value="">--Select Codebook to Delete--</option>
							<c:forEach var="codebook" items="${codebooks}">					
									<option value="${codebook.value[0]}.${codebook.value[1]}">${codebook.value[2]} - ${codebook.value[1]}</option>					
							</c:forEach>			
						</select>
						<button type="submit" class="btn">
			                <i class="fa fa-trash-o largeIcon"></i> Delete
			            </button>
					</sf:form>
		  		</div>
		  		<div class="tab-pane" id="t4">
		  			<h4>Set Default Codebook</h4>
		  			${indexSettings}
		  			<h4>Generate PDFs</h4>
		  			<p class="lb2">
		  				Generates new PDF copies of codebooks. 
		  				This might take longer for several codebooks or large codebooks
		  			</p>
		  			<a href="#t4" id="newPDFs" class="footerButton"><i class="fa fa-file-pdf-o"></i>Generate now</a>
		  		</div>
		  		<c:if test="${git}">
		  			<div class="tab-pane" id="t5">
			  			<h4>Version Control</h4>
						<p>Coming soon</p>
			  		</div>
		  		</c:if>
			</div>
		</div>
	</div>	
</t:main>