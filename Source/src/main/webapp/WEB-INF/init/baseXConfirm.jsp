<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>initConfig</c:set>
<c:set var="css" scope='request'>initConfig</c:set>
<t:error>
	<h2>CED2AR Setup Wizard</h2>
	<form:form action="${flowExecutionUrl}" method='post' modelAttribute="configProperties">
		<form:errors path="*" class = "configError"/>
		<table class='configTable'>		
			<tr>
				<td>BaseX DB Url:</td>
				<td>
					<input type = "text" id = "baseXDB" name = "baseXDB" value ='${configProperties.baseXDB}'>
					
				</td>
			</tr>
			<tr>
				<td colspan='2'><input type="submit" name="_eventId_prev" class="btn" value='Previous'> <input type="submit" name="_eventId_next" class="btn" value='Next'></td>
			</tr>
		</table>
	</form:form>
</t:error>