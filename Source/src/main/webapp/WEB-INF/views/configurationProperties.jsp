<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>configurationProperties</c:set>
<c:set var="css" scope='request'>config</c:set>
<t:main>
	<h2>Configuration Properties</h2>
	<h4>Current BaseX url</h4>
	<em>${propertiesForm.propertiesMap['baseXDB']}</em>
	<form:form action="config" method="post" id="properties"
		modelAttribute="propertiesForm">
		<input type='hidden' name="propertiesMap['baseXDB']"
			value="${propertiesForm.propertiesMap['baseXDB']}">
		<div class="lb">
			<c:if
				test="${propertiesForm.propertiesMap['baseXAllowChangePasswords'] eq true }">
				<a class="editPasswordCell btn" href="editPassword"
					title="Change Password"> Change Passwords </a>
			</c:if>
			<a class="editPasswordCell btn" href="changeBaseXDB"
				title="Change BaseX Database"> Change BaseX Database </a>
		</div>
		<table class='configTable'>
			<h4>Other Properties</h4>
			<tr>
				<td>Editing API Application Name</td>
				<td><input type="text" name="propertiesMap['eAPI']"
					value="${propertiesForm.propertiesMap['eAPI']}"> <form:errors
						class='configError' path="propertiesMap[eAPI]" /></td>
			</tr>




			<!-- START Bug Report Service   -->

			<tr>
				<td>Report Bugs</td>
				<td>
					<c:choose>
						<c:when test="${propertiesForm.propertiesMap['bugReportEnable'] eq  'true' }">
							<c:set var="enableBugReport" value="checked" />
						</c:when>
						<c:otherwise>
							<c:set var="disbledBugReport" value="checked" />
						</c:otherwise>
					</c:choose> 
					
					<label for="propertiesMap['bugReportEnable']">Enable</label> 
					<input class="bugReportOn" type="radio"	 id="enableRadio" name="propertiesMap['bugReportEnable']" value ="true" ${enableBugReport} >
					 
					 <label for="propertiesMap['bugReportEnable']">Disable</label> 
					 <input class="bugReportOff"  type="radio" id="enableRadio" name="propertiesMap['bugReportEnable']" value = "false" ${disbledBugReport}>
				</td>
			</tr>
			
				<tr class="bugReportField">
					<td>Email to Report Bugs</td>
					<td><input type="text" name="propertiesMap['bugReportEmail']"
						value="${propertiesForm.propertiesMap['bugReportEmail']}">
						<form:errors class='configError'
							path="propertiesMap[bugReportEmail]" /></td>
				</tr>
				<tr class="bugReportField">
					<td >Bug Reporting Service Email</td>
					<td><input type="text" name="propertiesMap['bugReportSender']"
						value="${propertiesForm.propertiesMap['bugReportSender']}">
						<form:errors class='configError'
							path="propertiesMap[bugReportSender]" /></td>
	
				</tr>
				<tr class="bugReportField">
					<td  >Bug Reporting Password</td>
					<td><a class="editPasswordCell" href="editEmailPassword"
						title="Change BugReporter Password"> <input type="button"
							class="btn" value='Change Password' />
					</a> </td>
				</tr>
				
					<!-- 
					<input type="hidden" name="propertiesMap['bugReportEmail']" value="${propertiesForm.propertiesMap['bugReportEmail']}">
					<input type="hidden" name="propertiesMap['bugReportSender']" value="${propertiesForm.propertiesMap['bugReportSender']}">
					 -->
			<!-- END Bug Report Service   -->












			<tr>
				<td>HTTP Timeout</td>
				<td><input type="text" name="propertiesMap['timeout']"
					value="${propertiesForm.propertiesMap['timeout']}"> <form:errors
						class='configError' path="propertiesMap[timeout]" /></td>
			</tr>
			<tr>
				<td>Network Mode</td>
				<td><select name="propertiesMap['restricted']">
						<c:choose>
							<c:when
								test="${propertiesForm.propertiesMap['restricted'] eq  'true' }">
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
				<td>Development Features</td>
				<td><c:if
						test="${propertiesForm.propertiesMap['devFeatureProv'] eq  'true' }">
						<c:set var="checkedProv" value="checked" />
					</c:if> <label for="propertiesMap['devFeatureProv']">Provenance
						Graph</label> <input type="checkbox"
					name="propertiesMap['devFeatureProv']" ${checkedProv}> 

					<c:if
						test="${propertiesForm.propertiesMap['devFeatureGoogleAnalytics'] eq  'true' }">
						<c:set var="checkedGA" value="checked" />
					</c:if> <label for="propertiesMap['devFeatureGoogleAnalytics']">Google
						Analytics</label> <input type="checkbox"
					name="propertiesMap['devFeatureGoogleAnalytics']" ${checkedGA}>
				</td>
			</tr>
			<tr>
				<td><input type="submit" class="btn" value='Update Properties'>
				</td>
			</tr>
		</table>
	</form:form>
</t:main>