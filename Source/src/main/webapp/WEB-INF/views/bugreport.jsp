<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>bugreport</c:set>
<t:main>
	<div id="userComments" class="toggleContent">
		<h2>Bug Report</h2>
		<form id="bugReport" action="${baseURI}/bugreport" method="POST">
			<div class="form-group">
				<label for="bugType">Bug Type</label> <label for="bugType"
					class="asterisk"> *</label> <br /> <select name="bugType"
					class="form-control">
					<option value="Coding">Website functionality</option>
					<option value="Data">Data Error</option>
					<option value="Suggestion">Project Suggestion</option>
					<option value="Other">Other</option>
				</select>
			</div>
			<div class="form-group">
				<label for="bugDescription">Bug Description</label> <label
					for="bugDescription" class="asterisk"> *</label>
				<textarea class="form-control" name="bugDescription"
					id="bugDescription" maxlength="300" rows="4"
					placeholder="Describe in detail the problem you encountered"></textarea>
			</div>
			<div class="form-group">
				<label for="reproductionSteps">Steps to Reproduce</label> <label
					for="reproductionSteps" class="asterisk"> *</label>
				<textarea class="form-control" name="reproductionSteps"
					id="reproductionSteps" maxlength="300" rows="4"
					placeholder="Describe the exact conditions that led to the problem"></textarea>
			</div>
			<div class="input-group margin-bottom-sm">
				<span class="input-group-addon"><i class="fa fa-user fa-fw"></i></span>
				<input class="form-control" type="text" placeholder="Your Name"
					name="yourName" id="yourName">
			</div>
			<br />
			<div class="input-group margin-bottom-sm lb2">
				<span class="input-group-addon"><i
					class="fa fa-envelope fa-fw"></i></span> <input class="form-control"
					type="text" placeholder="Your Email" name="yourEmail"
					id="yourEmail">
			</div>
			<span class="hidden"> <input type="hidden" name="lastPage"
				value="${lastPage}"> <input type="hidden" id="screenHeight"
				name="screenHeight"> <input type=hidden id="screenWidth"
				name="screenWidth"> <label for="reportHP">Do not
					enter anything here</label> <input type="text" name="reportHP"
				id="reportHP" autocomplete="off" />
			</span> <input type="submit" class="btn" value="Submit" />
		</form>
	</div>
</t:main>