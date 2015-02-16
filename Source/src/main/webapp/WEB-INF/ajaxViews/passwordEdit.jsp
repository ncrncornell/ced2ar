<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div id="loadCoverInner"
	class="passwordSplash col-xs-offset-2 col-sm-offset-3 col-md-offset-4 col-xs-6 col-sm-5 col-md-4">
	<a id="closeButton" href="#" title="Close Window">×</a>
	<h2>Change Password</h2>
	<form:form method="post" id="updPassword"
		modelAttribute="passwordChangeForm">
		<table>
			<%-- BaseX passwords --%>
			<c:choose>
				<c:when test="${passwordChangeForm.emailPasswordChange eq false}">
					<tr>
						<td>Select User Id</td>
						<td><select id="baseXDBUserId" name="baseXDBUserId">
								<option value="admin">admin</option>
								<option value="reader">reader</option>
								<option value="writer">writer</option>
						</select></td>
					</tr>
				</c:when>
				<%-- Bugreport password --%>
				<c:otherwise>
					<input type='hidden' id="baseXDBUserId" name="baseXDBUserId"
						value="">
					<input type='hidden' id="baseXDBUrl" name="baseXDBUrl" value="">
				</c:otherwise>
			</c:choose>
			<tr>
				<td>Current Password</td>
				<td><input type='password' id='currentPassword'
					name='currentPassword'></td>
			</tr>
			<tr>
				<td>New Password</td>
				<td><input type='password' id='newPassword' name='newPassword'></td>
			</tr>
			<tr>
				<td>Confirm Password</td>
				<td><input type='password' id='confirmPassword'
					name='confirmPassword'></td>
			</tr>
			<tr>
				<td></td>
				<td class="editPasswordCell"><input id='changePasswordButton'
					type="button" class="btn" value='Update Password' /></td>
			</tr>
			<tr>
				<td></td>
				<td id="changePwdMsg"></td>
			</tr>
		</table>
	</form:form>
</div>