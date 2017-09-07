<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="css" scope='request'>edit</c:set>
<c:set var="js" scope='request'>fileUpload inlineLoad</c:set>
<t:main>
	<div id="uploadCodebook">
		<h2 class="lb3">Manage codebooks</h2>		
		<div id="tabSections">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active"><a href="#t1" aria-controls="t1" role="tab" data-toggle="tab" title="Upload a new codebook"><i class="fa fa-upload largeIcon"></i> Upload</a></li>	
				<li role="presentation"><a href="#t2" aria-controls="t2" role="tab" data-toggle="tab" title="Modify an existing codebook"><i class="fa fa-pencil-square-o largeIcon"></i> Update</a></li>
				<li role="presentation"><a href="#t3" aria-controls="t3" role="tab" data-toggle="tab" title="Delete an existing codebook"><i class="fa fa-trash-o largeIcon"></i> Delete</a></li>
				<li role="presentation"><a href="#t4" aria-controls="t4" role="tab" data-toggle="tab" title="Change settings"><i class="fa fa-wrench largeIcon"></i> Settings</a></li>
				<c:if test="${git}">
					<li role="presentation"><a href="#t5" aria-controls="t5" role="tab" data-toggle="tab" title="Git"><i class="fa fa-git largeIcon"></i> Versions</a></li>
				</c:if>
				<c:if test="${data2ddiSvc eq  'true' }">
					<li role="presentation"><a href="#t6" aria-controls="t6" role="tab" data-toggle="tab" title="Create a new codebook"><i class="fa fa-plus largeIcon"></i> Create</a></li>
				</c:if>
			</ul>
			<div class="tab-content">		  
				<div class="tab-pane active" role="tabpanel" id="t1">
					<h4>Upload DDI-C codebook</h4>
					<sf:form id="addForm" method="post" action="${baseURI}/edit/add/codebook" enctype="multipart/form-data">		
						<div class="uploadFileForm">
		  					<button class="btn fileUploadBtn">
		  						<i class="fa fa-file"></i>
		  						Select File
			  						<i class="fa fa-question-circle helpIcon helpIconB"
			  							data-toggle="popover" title=""
			  							data-original-title="File (required)"
			  							data-content="A file containing a DDI-C codebook and the schema it will be validated against."
 			  						></i>
		  					</button>
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
			                <i class="fa fa-upload largeIcon"></i> Upload
			            </button>
					</sf:form>
		  		</div>
		  		<div class="tab-pane" role="tabpanel" id="t2">
			  		<h4>Update Existing Codebook</h4>
			  		<sf:form id="updateForm" method="post" action="${baseURI}/edit/update/codebook" enctype="multipart/form-data">			
		  				<div class="uploadFileForm">
		  					<button class="btn fileUploadBtn"><i class="fa fa-file"></i> Select New File</button>   
				            <input class="hidden fileUploadHidden" type="file" name="file">
				            <input class="fileUploadDisplay requiredInput" placeholder="No File Selected" type="text" disabled>
			          	</div>
			          	<select name="handle" class="requiredInput btn">
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
		  		<div class="tab-pane" role="tabpanel" id="t3">
			  		<h4>Delete Codebook</h4>
			  		<sf:form id="deleteForm" method="post" action="${baseURI}/edit/delete/codebook" enctype="multipart/form-data">			
			          	<select name="handle" class="requiredInput btn">
							<option value="">--Select Codebook to Delete--</option>
							<c:forEach var="codebook" items="${codebooks}">					
									<option value="${codebook.value[0]}.${codebook.value[1]}">${codebook.value[2]} - ${codebook.value[1]}</option>					
							</c:forEach>			
						</select>
						<button type="submit" class="btn inlineLoad">
			                <i class="fa fa-trash-o largeIcon"></i> Delete
			            </button>
					</sf:form>
		  		</div>
		  		<div class="tab-pane" role="tabpanel" id="t4">
		  			<h4>Set Default Codebook</h4>
		  			${indexSettings}
		  			<!-- TODO: The JS listener to trigger this was removed   -->
		  			<!-- Send AJAX get to /edit/codebooks/generatepdf -->
		  			<!-- 
		  			<h4>Generate PDFs</h4>
		  			<p class="lb2">
		  				Generates new PDF copies of codebooks. 
		  				This might take longer for several codebooks or large codebooks
		  			</p>
		  			<a href="#t4" id="newPDFs" class="footerButton"><i class="fa fa-file-pdf-o"></i>Generate now</a>
		  			 -->
		  		</div>
		  		<c:if test="${git}">
		  			<div class="tab-pane" role="tabpanel" id="t5">
			  			<h4>Version Control</h4>
						<p>
							<a title="Version control status page" href="${baseURI}/edit/gitStatus">Status Page</a>
						</p>
						<p>
							<a title="Version control merge process" href="${baseURI}/mergeFlow">Merge Crowdsourced Edits</a>
						</p>
			  		</div>
		  		</c:if>

		  		<div class="tab-pane" role="tabpanel" id="t6">
		  			<%--
		  				CDR-208 - This content came from: Source/src/main/webapp/WEB-INF/workflowViews/convert.jsp
		  			 --%>
			  		<h4>Create a basic DDI-C codebook from an existing dataset.</h4>
			  		<div class="bg-warning">
			  			<p class="text-center"><i class="fa fa-warning largeIcon"></i> Experimental</p>
			  		</div>
			  		<br>
			  		<p>
			  			<em>Current Requirements</em>
			  		</p>
			  		<ul class="lb2 bullets">
			  			<li>Stata .dta files (version 8-14) and SPSS .sav file are accepted at this time</li>
			  			<li>Please limit total file size to 75mb</li>
			  		</ul>
			  		<p>
			  			See this github repository on conversion instructions for R, SAS and other formats:
			  			<a href="https://github.com/ncrncornell/DatasetConversions">https://github.com/ncrncornell/DatasetConversions</a>	
			  		</p>

					<sf:form id="createForm" method="post" action="${baseURI}/edit/data" enctype="multipart/form-data">
						<div class="createForm">
							<br>
							<h5>Existing dataset (source):</h5>

							<div class="radio">
								<label> <input type="radio" name="optionsRadios"
									id="optionsRadios1" value="option1" checked> File
									&mdash; A file containing: Stata (.dta) OR SPSS (.sav) data.
								</label>
								<div class="row">
									<div class="col-sm-1"></div>
									<div class="col-sm-11">
										<button class="btn fileUploadBtn">
											<i class="fa fa-file"></i> Select File <i
												class="fa fa-question-circle helpIcon helpIconB"
												data-toggle="popover" title=""
												data-original-title="File (required)"
												data-content="A file containing: Stata (.dta) OR SPSS (.sav) data."></i>
										</button>
										<input class="hidden fileUploadHidden" type="file" name="file">
										<input class="fileUploadDisplay requiredInput"
											placeholder="No File Selected" type="text" disabled>
									</div>
								</div>
							</div>
							<div class="radio disabled">
								<label> <input type="radio" name="optionsRadios"
									id="optionsRadios2" value="option2" disabled>
									Repository &mdash; Via SWORD2 interface &mdash; non-functional,
									disabled
								</label>
								<div class="row">
									<div class="col-sm-1"></div>
									<div class="col-sm-11">
										<button class="btn fileUploadBtn" disabled>
											<i class="fa fa-cloud"></i> Select Repository <i
												class="fa fa-question-circle helpIcon helpIconB"
												data-toggle="popover" title=""
												data-original-title="Repository (required)"
												data-content="A Repository web service..."></i>
										</button>
										<input class="fileUploadDisplay"
											placeholder="No Repository Selected" type="text" disabled>
									</div>
								</div>
							</div>

							<br>
							<h5>DDI-C codebook (target):</h5>

							<span class="input-group"> <span class="input-group-addon">
									<label for="handle"> Base Handle <i
										class="fa fa-question-circle helpIcon helpIconB"
										data-toggle="popover" title=""
										data-original-title="Base Handle (required)"
										data-content="A handle is a unique alphanumeric identifier for a group of codebooks. 
 											For example, a collections of codebooks on SIPP Sythenic Beta might have the handle of 'ssb'"></i>
									</label>
								</span>
								<input class="form-control requiredInput" type="text" name="handle">
							</span>

							<span class="input-group"> <span class="input-group-addon">
									<label for="handle"> Version <i
										class="fa fa-question-circle helpIcon helpIconB"
										data-toggle="popover" title=""
										data-original-title="Version (required)"
										data-content="The version can be any alphanumeric identifier such as a year or release number"></i>
									</label>
								</span>
								<input class="form-control requiredInput" type="text" name="version">
							</span>

							<button type="submit" class="btn">
								<i class="fa fa-plus largeIcon"></i> Create
							</button>

						</div>				<%-- End: createForm div --%>
					</sf:form>
		  		</div>
			</div>
		</div>
	</div>	
</t:main>