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
	<form:form action="${flowExecutionUrl}" method='post'>
		<table>
			<tr>
				<td>Do you have a previously saved config file for this install ?</td>
			</tr>
		</table>
		<input name="_eventId_uploadSavedConfig"  type="submit" class="btn" value='Yes'>
		<input name="_eventId_doCheckBaseXUsingDeployedConfig"  type="submit" class="btn" value='No'>		
		
	</form:form>
</t:init>