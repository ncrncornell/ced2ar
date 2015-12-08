<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>provEdit edit prov2 chosen/chosen.min</c:set>
<c:set var="js" scope='request'>workflow/addNode chosen/chosen.proto.min chosen/chosen.jquery.min</c:set>
<t:main>
	<h2>Add a Node</h2>
	<div>
		<form id="addNode" method="POST" action="/" class="lb2">
			<fieldset>
				<span>
					<label for="name">Type</label>
				</span>
				<select name="type">			
					<option value="Dataset">Dataset</option>
					<option value="Program">Program</option>
				</select>
				<span class="input-group lb2">
					<span class="input-group-addon">
						<label for="name">Name</label>
					</span>
					<input class="form-control" name="name" type="text" placeholder="Enter a unique name"/>	
				</span>
				<span class="inputErrorMsg"></span>
				<span class="input-group lb2">
					<span class="input-group-addon">
						<label for="uri">Input URI</label>
					</span>
					<input class="form-control uri" name="uri" type="text" placeholder="Enter a URI"/>		
				</span>
				<input type="submit" class="btn" value="Submit" />
				
				<%--TODO: CSS for this page --%>
				<span id="workflowError"></span>
				<span id="workflowSuccess"></span>
			</fieldset>	
		</form>    
	</div>
</t:main>