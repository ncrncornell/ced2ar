<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>initConfig</c:set>
<c:set var="css" scope='request'>initConfig</c:set>
<t:error>
	<h2>CED2AR Setup Wizard - Upload Saved Config</h2>
	<form:form  enctype="multipart/form-data" modelAttribute="configProperties">
	<form:errors path="*" class = "configError"/>
		<table class='configTable'>
			<tr>
				<td><input id="multipartFileUpload" name="multipartFileUpload" type="file" value="" /></td>
			</tr>
			<tr>
				<td>
				    <input type="submit" class="btn" name="_eventId_back"  value="Back">
				    <input class="btn" name="_eventId_doUploadSavedConfig" type="submit" id="Upload" value="Next">
				</td>		
			</tr>
		</table>
	</form:form>
</t:error>