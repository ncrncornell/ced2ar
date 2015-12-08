<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>initConfig</c:set>
<c:set var="css" scope='request'>initConfig</c:set>
<t:init>
	<h2>CED2AR Setup Wizard - Configure your Instance</h2>
	<form:form action="${flowExecutionUrl}" method='post' modelAttribute="configProperties">
		<form:errors path="*" class = "configError"/>
		<table >
			<tr>
				<th>Initialization Done Before</th>
				<td>${configProperties.configInitialized}</td>
			</tr>
			<tr>
				<th>Report Bugs </th>
				<td>
					<c:choose>
						<c:when test="${configProperties.bugReportEnable eq  'true' }">
							<c:set var="enableBugReport" value="checked" />
						</c:when>
						<c:otherwise>
							<c:set var="disbledBugReport" value="checked" />
						</c:otherwise>
					</c:choose> 
					
					<label >Enable</label> 
					<input class="bugReportOn" type="radio"	 id="enableRadio" name="bugReportEnable" value ="true" ${enableBugReport} >
					 
					 <label >Disable</label> 
					 <input class="bugReportOff"  type="radio" id="enableRadio" name="bugReportEnable" value = "false" ${disbledBugReport}>
				</td>
			</tr>
			<tr class="bugReportField">
				<th>Email to Report Bugs</th>
				<td><input type="text" name="bugReportEmail" value="${configProperties.bugReportEmail}">
			</tr>
			
			<tr class="bugReportField">
				<th>Bug Report Sender Email</th>
				<td><input type="text" name="bugReportSender" value="${configProperties.bugReportSender}">
			</tr>
			
			<tr class="bugReportField">
				<th>Bug Reporting Password</th>
				<td><input type="password" name="bugReportPwd" value="${configProperties.bugReportPwd}">
			</tr>
			
			<tr class="bugReportField">
				<th>Confirm Bug Reporting Password</th>
				<td><input type="password" name="confirmBugReportPwd" value="${configProperties.confirmBugReportPwd}">
			</tr>

			<tr>
				<th>HTTP Timeout</th>
				<td><input type="text" name="timeout" value="${configProperties.timeout}"></td>
			</tr>
			<tr>
				<th>Network Mode</th>
				<td><select name="restricted">
						<c:choose>
							<c:when
								test="${configProperties.restricted eq  'true' }">
								<option value='false'>Unrestricted</option>
								<option value='true' selected>Restricted</option>
							</c:when>
							<c:otherwise>
								<option value='false' selected>Unrestricted</option>
								<option value='true'>Restricted</option>
							</c:otherwise>
						</c:choose>
				</select></td>
			</tr>
			<tr>
				<th>Development Features</th>
				<td>
					<c:if test="${configProperties.devFeatureProv eq  'true' }">
						<c:set var="checkedProv" value="checked" />
					</c:if> 
					Provenance  <input type="checkbox" name="devFeatureProv" ${checkedProv}> 
				</td>
			</tr>
		</table>
		<input type="submit" class="btn" name="_eventId_prev"  value="Prev">
	 	<input type="submit" class="btn" name="_eventId_next"  value="Next">

	</form:form>
</t:init>