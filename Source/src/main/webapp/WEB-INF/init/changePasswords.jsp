<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="css" scope='request'>initConfig</c:set>
<t:init>
	<h2>CED2AR Setup Wizard - Change Password</h2>

	<form:form action="${flowExecutionUrl}" method='post' modelAttribute="configProperties">

		<form:errors path="*" class = "configError"/>
		
		<input  type='hidden' id='keepReaderPassword' name='keepReaderPassword' value ='${configProperties.keepReaderPassword}'>
		<input  type='hidden' id='keepWriterPassword' name='keepWriterPassword' value ='${configProperties.keepWriterPassword}'>
		<input  type='hidden' id='keepAdminPassword' name='keepAdminPassword' value ='${configProperties.keepAdminPassword}'>
		<table class="configTable">		
			<tr>
				<th>BaseX URL</th>
				<td>${configProperties.baseXDB}</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<th></th>
				<td>New Password</td>
				<td>Confirm Password</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
				<th>Reader Password</th>
				<c:choose>
					<c:when test = "${configProperties.keepReaderPassword eq 'true'}">
						<td><input disabled type='password' id='newReaderPassword' name='newReaderPassword' ></td>
						<td><input disabled type='password' id='confirmReaderPassword' name='confirmReaderPassword' ></td>
						<td><input disabled type="button" id='randomizeReaderPassword' class="btn randomizeReaderPassword" value="Randomize Reader Password"></td>
						<td><input type="button" id='changeReaderPassword' class="btn changeReaderPassword" value="Change Reader Password"></td>
					</c:when>
					<c:otherwise>
						<td><input type='password' id='newReaderPassword' name='newReaderPassword' value='${configProperties.newReaderPassword}'></td>
						<td><input type='password' id='confirmReaderPassword' name='confirmReaderPassword' value='${configProperties.confirmReaderPassword}'></td>
						<td><input type="button" id='randomizeReaderPassword' class="btn randomizeReaderPassword" value="Randomize Reader Password"></td>
						<td><input type="button" id='changeReaderPassword' class="btn changeReaderPassword" value="Keep Reader Password"></td>
						
					</c:otherwise>
				</c:choose>
				
			</tr>

			<tr>
				<th>Writer Password</th>
				<c:choose>
					<c:when test = "${configProperties.keepWriterPassword eq 'true'}">
						<td><input disabled type='password' id='newWriterPassword' name='newWriterPassword' ></td>
						<td><input disabled type='password' id='confirmWriterPassword' name='confirmWriterPassword'></td>
						<td><input disabled type="button" id="randomizeWriterPassword" class="btn randomizeWriterPassword" value="Randomize Writer Password"></td>
						<td><input type="button" id='changeWriterPassword' class="btn changeWriterPassword" value="Change Writer Password"></td>
					</c:when>
					<c:otherwise>
						<td><input  type='password' id='newWriterPassword' name='newWriterPassword' value='${configProperties.newWriterPassword}'></td>
						<td><input type='password' id='confirmWriterPassword' name='confirmWriterPassword' value='${configProperties.confirmWriterPassword}'></td>
						<td><input type="button" id="randomizeWriterPassword" class="btn randomizeWriterPassword" value="Randomize Writer Password"></td>
						<td><input type="button" id='changeWriterPassword' class="btn changeWriterPassword" value="Change Writer Password"></td>
					</c:otherwise>
				</c:choose>
			</tr>

			<tr>
				<th>Admin Password</th>
				<c:choose>
					<c:when test = "${configProperties.keepAdminPassword eq 'true'}">
						<td><input disabled type='password' id='newAdminPassword' name='newAdminPassword'></td>
						<td><input disabled type='password' id='confirmAdminPassword' name='confirmAdminPassword'></td>
						<td><input disabled type="button" id="randomizeAdminPassword" class="btn randomizeAdminPassword" value="Randomize Admin Password"></td>
						<td><input type="button" id='changeAdminPassword' class="btn changeAdminPassword" value="Change Admin Password"></td>
					</c:when>
					<c:otherwise>
						<td><input type='password' id='newAdminPassword' name='newAdminPassword' value='${configProperties.newAdminPassword}'></td>
						<td><input type='password' id='confirmAdminPassword' name='confirmAdminPassword' value='${configProperties.confirmAdminPassword}'></td>
						<td><input type="button" id="randomizeAdminPassword" class="btn randomizeAdminPassword" value="Randomize Admin Password"></td>
						<td><input type="button" id='changeAdminPassword' class="btn changeAdminPassword" value="Change Admin Password"></td>
					</c:otherwise>
				</c:choose>
			</tr>
		</table>
		
		<input type="submit" class="btn" name="_eventId_prev"  value="Prev">
	 	<input type="submit" class="btn" name="_eventId_next"  value="Next">
		
	</form:form>

</t:init>