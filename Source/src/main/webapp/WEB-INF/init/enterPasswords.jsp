<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>initConfig</c:set>
<c:set var="css" scope='request'>initConfig</c:set>
<t:init>
	<h2>CED2AR Setup Wizard</h2>
	<form:form action="${flowExecutionUrl}" method='post' modelAttribute="configProperties">

		<form:errors path="*" class = "configError"/>
		<table>		
			<tr>
				<th>BaseX URL</th>
				<td>${configProperties.baseXDB}</td>
			</tr>
			<c:if test="${configProperties.baseXReaderHash eq ''}">
				<tr>
					<th>Reader Password</th>
					<td><input  type='password' id='newReaderPassword' name='newReaderPassword' value ='${configProperties.newReaderPassword}'></td>
				</tr>
			</c:if>
			<c:if test="${configProperties.baseXWriterHash eq ''}">
				<tr>
					<th>Writer Password</th>
					<td><input  type='password' id='newWriterPassword' name='newWriterPassword' value ='${configProperties.newWriterPassword}'></td>
				</tr>
			</c:if>
			<c:if test="${configProperties.baseXAdminHash eq ''}">
				<tr>
					<th>Admin Password</th>
					<td><input  type='password' id='newAdminPassword' name='newAdminPassword' value ='${configProperties.newAdminPassword}'></td>
				</tr>
			</c:if>	
		</table>
		<input type="submit" class="btn" name="_eventId_prev"  value="Prev">
	 	<input type="submit" class="btn" name="_eventId_next"  value="Next">
	</form:form>
</t:init>