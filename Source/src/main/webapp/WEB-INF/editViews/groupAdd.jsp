<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>edit.min</c:set>
<c:set var="js" scope='request'>vargroup</c:set>
<t:main>
	<h2>Variable Group</h2>
	<p class="lb">Add new variable group to ${title}</p>
	<form id="createGroup" action="${baseURI}/edit/codebooks/${baseHandle}/v/${version}/groups" method="POST">
		<div class="input-group margin-bottom-sm lb2">
			<span class="input-group-addon">Group Name</span>
			<input class="form-control requiredInput" type="text" placeholder="" name="name">
		</div>
		<div class="input-group lb2">
			<span class="input-group-addon">Label</span>
			<input class="form-control requiredInput" type="text" placeholder="" name="label">
		</div>
		<p>Description</p>
		<textarea name="desc"></textarea>
		<div>
			<button type="submit" class="btn">
				<i class="fa fa-plus"></i> Add
			</button>
		</div>
	</form>
</t:main>