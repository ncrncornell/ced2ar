<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>provEdit edit prov2 chosen/chosen.min</c:set>
<c:set var="js" scope='request'>workflow/workflowAdd chosen/chosen.proto.min chosen/chosen.jquery.min</c:set>
<t:main>
	<h2>Replicate a Workflow</h2>
		<div>
			<p>
				<i class="fa fa-exclamation-triangle"></i>
				It is strongly recommended that you use a path to a Git or SVN repository for the URIs.
				For example, use a Github URL such as: 
				<a href="https://github.com/ncrncornell/ced2ar/blob/master/README.md">
					https://github.com/ncrncornell/ced2ar/blob/master/README.md
				</a>
			</p>
			<form id="workflowAdd" method="POST" action="/" class="lb2">
				<fieldset>
					<legend>1. Input file</legend>		
					<div>
						<input type="radio" name="inputType" value="existing">Select existing
					</div>
					<div id="inputExisting">
						<select id="inputSelector">
							<c:forEach begin="0" end="${inputs.length()-1}" varStatus="loop">
								<c:set var="i" value="${inputs.get(loop.index).get('row')}"/>
								<%--i.get(0) is the id --%>
								<option value="${i.get(1)}">${i.get(1)}</option>
							</c:forEach>
						</select>
					</div>	
					<div>
						<input type="radio" name="inputType" value="new">Create new
					</div>			
					<div id="inputNew">
						<span class="input-group">
							<span class="input-group-addon">
								<label for="inputName">Name</label>
							</span>
							<input id="inputName" class="form-control" name="inputName" type="text" placeholder="Enter a unique name"/>	
						</span>
						<span class="inputErrorMsg"></span>
						<span class="input-group">
							<span class="input-group-addon">
								<label for="inputURI">Input URI</label>
							</span>
							<input id="inputURI" class="form-control uri" name="inputURI" type="text" placeholder="Enter a URI"/>		
						</span>
						<span>
							Provider
							<select id="providerSelector">
								<c:forEach begin="0" end="${providers.length()-1}" varStatus="loop">
									<c:set var="p" value="${providers.get(loop.index).get('row')}"/>
									<option value="${p.get(1)}">${p.get(1)}</option>
								</c:forEach>
							</select>
						</span>
					</div>
					<span class="inputErrorMsg"></span>
		        </fieldset>
				<fieldset>
					<legend>2. Program File</legend>		
					<span class="input-group">
						<span class="input-group-addon">
							<label for="progName">Name</label>
						</span>
						<input id="progName" class="form-control" name="progName" type="text" placeholder="Enter a unique name"/>
					</span>
					<span class="inputErrorMsg"></span>
					<span class="input-group">
						<span class="input-group-addon">
							<label for="progURI">Program URI</label>
						</span>
						<input id="progURI" class="form-control uri" name="progURI" type="text" placeholder="Enter a URI"/>	
					</span>
					<span class="inputErrorMsg"></span>
		        </fieldset>
		        <fieldset>
					<legend>3. Output File</legend>		
					<span class="input-group">
						<span class="input-group-addon">
							<label for="outputName">Name</label>
						</span>
						<input id="outputName" class="form-control" name="outputName" type="text" placeholder="Enter a unique name"/>
					</span>
					<span class="inputErrorMsg"></span>					
					<span class="input-group">
						<span class="input-group-addon">
							<label for="outputURI">Input URI</label>
						</span>
						<input id="outputURI" class="form-control uri" name="outputURI" type="text" placeholder="Enter a URI"/>
					</span>
					<span class="inputErrorMsg"></span>
		        </fieldset>
		        <fieldset>
					<legend>4. Submit Form</legend>	
					<input type="submit" class="btn" value="Submit" />
					<span id="edgeError" class="success"></span>
					<span id="edgeSuccess" class="failure"></span>
		        </fieldset>
		     </form>
		</div>
</t:main>