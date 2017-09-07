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
	<a href="downloadProperties" class="printButton" title="Download properties">
	  <i class="fa fa-download"></i>
	</a>
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

			<%-- UI Navbar customization --%>
			<tr>
				<td>UI Navbar Tabs</td>
			</tr>
			<tr>
				<td align="right" >
					<c:if
						test="${propertiesForm.propertiesMap['uiNavBarBrowseCodebook'] eq  'true' }">
						<c:set var="checkedBarBrowseCodebook" value="checked" />
					</c:if> <label for="propertiesMap['uiNavBarBrowseCodebook']">Codebook</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavBarBrowseCodebook']" ${checkedBarBrowseCodebook}>

					<input type="text" name="propertiesMap['uiNavBarBrowseCodebookLabel']"
						value="${propertiesForm.propertiesMap['uiNavBarBrowseCodebookLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavBarBrowseCodebookLabel]" />
				</td>
			</tr>
			<tr>
				<td align="right" ><c:if
						test="${propertiesForm.propertiesMap['uiNavBarBrowseStudy'] eq  'true' }">
						<c:set var="checkedBarBrowseStudy" value="checked" />
					</c:if> <label for="propertiesMap['uiNavBarBrowseStudy']">Study</label>
				</td>
				<td> <input type="checkbox"
						name="propertiesMap['uiNavBarBrowseStudy']" ${checkedBarBrowseStudy}>

					<input type="text" name="propertiesMap['uiNavBarBrowseStudyLabel']"
						value="${propertiesForm.propertiesMap['uiNavBarBrowseStudyLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavBarBrowseStudyLabel]" />
				</td>
			</tr>

			<%-- UI Tab customization (DDI tabs) --%>
			<tr>
				<td>UI Study Page Tabs</td>
			</tr>
			<tr>
				<td align="right" >
					<c:if
						test="${propertiesForm.propertiesMap['uiNavTabDoc'] eq  'true' }">
						<c:set var="checkedTabDoc" value="checked" />
					</c:if> <label for="propertiesMap['uiNavTabDoc']">Doc</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavTabDoc']" ${checkedTabDoc}>

					<input type="text" name="propertiesMap['uiNavTabDocLabel']"
						value="${propertiesForm.propertiesMap['uiNavTabDocLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavTabDocLabel]" />
				</td>
			</tr>
			<tr>
				<td align="right" >
					<c:if
						test="${propertiesForm.propertiesMap['uiNavTabStdy'] eq  'true' }">
						<c:set var="checkedTabStdy" value="checked" />
					</c:if> <label for="propertiesMap['uiNavTabStdy']">Study</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavTabStdy']" ${checkedTabStdy} disabled=true>

					<input type="text" name="propertiesMap['uiNavTabStdyLabel']"
						value="${propertiesForm.propertiesMap['uiNavTabStdyLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavTabStdyLabel]" />
				</td>
			</tr>
			<tr>
				<td align="right" >
					<c:if
						test="${propertiesForm.propertiesMap['uiNavTabFile'] eq  'true' }">
						<c:set var="checkedTabFile" value="checked" />
					</c:if> <label for="propertiesMap['uiNavTabFile']">File</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavTabFile']" ${checkedTabFile}>

					<input type="text" name="propertiesMap['uiNavTabFileLabel']"
						value="${propertiesForm.propertiesMap['uiNavTabFileLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavTabFileLabel]" />
				</td>
			</tr>
			<tr>
				<td align="right" >
					<c:if
						test="${propertiesForm.propertiesMap['uiNavTabData'] eq  'true' }">
						<c:set var="checkedTabData" value="checked" />
					</c:if> <label for="propertiesMap['uiNavTabData']">Data</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavTabData']" ${checkedTabData}>

					<input type="text" name="propertiesMap['uiNavTabDataLabel']"
						value="${propertiesForm.propertiesMap['uiNavTabDataLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavTabDataLabel]" />
				</td>
			</tr>
			<tr>
				<td align="right" > <c:if
						test="${propertiesForm.propertiesMap['uiNavTabOtherMat'] eq  'true' }">
						<c:set var="checkedTabOtherMat" value="checked" />
					</c:if> <label for="propertiesMap['uiNavTabOtherMat']">OtherMat</label>
				</td>
				<td>
					<input type="checkbox" name="propertiesMap['uiNavTabOtherMat']" ${checkedTabOtherMat}>

					<input type="text" name="propertiesMap['uiNavTabOtherMatLabel']"
						value="${propertiesForm.propertiesMap['uiNavTabOtherMatLabel']}"> <form:errors
						class='configError' path="propertiesMap[uiNavTabOtherMatLabel]" />
				</td>
			</tr>

			<%-- Service URL/Endpoints --%>
			<tr>
				<td>Services</td>
			</tr>
			<tr>
				<td> <c:if
						test="${propertiesForm.propertiesMap['data2ddiSvc'] eq  'true' }">
						<c:set var="checkedData2ddiSvc" value="checked" />
					</c:if> <input type="checkbox" name="propertiesMap['data2ddiSvc']" ${checkedData2ddiSvc}>
					<label for="propertiesMap['data2ddiSvc']">data2ddi</label>
				</td>
				<td>
					<label for="propertiesMap['data2ddiUrl']">URL</label>
					<input type="text" name="propertiesMap['data2ddiUrl']"
						value="${propertiesForm.propertiesMap['data2ddiUrl']}"> <form:errors
						class='configError' path="propertiesMap[data2ddiUrl]" />
				</td>
			</tr>


			<tr>
				<td><input type="submit" class="btn" value='Update Properties'></td>		
			</tr>
		</table>
	</form:form>
</t:main>