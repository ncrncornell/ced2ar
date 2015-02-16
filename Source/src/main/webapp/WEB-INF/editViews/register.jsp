<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="js" scope='request'>register</c:set>
<c:set var="css" scope='request'>edit.min</c:set>
<t:main>
	<div id="userComments" class="toggleContent">
		<h2>Register with CED<sup>2</sup>AR</h2>
		<p class="lb">
			<i class="fa fa-exclamation-circle largeIcon"></i>
			Registration requires that you use an email address associated with a google account.
			This can be off the google.com domain, however it must be compatible with standard google authentication.
		</p>
		<form id="createAccount" action="${baseURI}/register" method="POST">
			<div class="input-group margin-bottom-sm lb2">
				<span class="input-group-addon">First Name</span>
				<input class="form-control requiredInput" type="text" placeholder="First Name" name="fName">
			</div>
			<div class="input-group lb2">
				<span class="input-group-addon">Last Name</span>
				<input class="form-control requiredInput" type="text" placeholder="Last Name" name="lName">
			</div>
			<div class="input-group lb2">
				<span class="input-group-addon">Email</span>
				<input class="form-control requiredInput" type="text" placeholder="Google Account" name="email">
			</div>
			<div class="input-group lb2">
				<span class="input-group-addon">Affiliation</span>
				<input class="form-control requiredInput" type="text" placeholder="University, Employeer, Group, etc." name="org">
			</div>
			<span class="hidden">
				<label for="hp">Do not enter anything here</label> 
				<input type="text" name="hp" autocomplete="off" />
			</span>
			<button type="submit" class="btn">
				<i class="fa fa-sign-in"></i> Register
			</button>
		</form>
	</div>
</t:main>