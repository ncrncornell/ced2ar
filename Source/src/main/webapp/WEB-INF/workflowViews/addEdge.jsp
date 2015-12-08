<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>workflow/workflow chosen/chosen.min provEdit</c:set>
<c:set var="js" scope='request'>workflow/addEdge chosen/chosen.proto.min chosen/chosen.jquery.min</c:set>
<t:main>
	<h2>Add an Edge</h2>
	<div>
		<form id="addEdge" method="POST" action="/" class="lb2">
			<fieldset>
				<legend>1. Select Source Node</legend>		
				<select name="source">			
					<option></option>	
				</select>
			</fieldset>	
			<fieldset>
				<legend>2. Select Target Node</legend>
				<select name="target">			
					<option></option>
				</select>
			</fieldset>	
			<fieldset>
				<legend>3. Select Edge Type</legend>
				<select name="type"></select>
				<p id="typeError" class="failure"></p>
			</fieldset>	
			<fieldset>
				<legend>4. Approve Edge</legend>
				<p id="triplePreview"></p>
				<input type="submit" class="btn" value="Add" />
				<p id="edgeError"></p>
				<p id="edgeSuccess"></p>
			</fieldset>	
		</form>    
	</div>
</t:main>