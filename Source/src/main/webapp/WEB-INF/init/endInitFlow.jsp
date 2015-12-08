<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="js" scope='request'>initConfig</c:set>
<c:set var="css" scope='request'>initConfig</c:set>
<t:error>
	<h2>CED2AR Setup Wizard - Successfully Updated Configuration</h2>
	<form:form action="${flowExecutionUrl}" method='post' modelAttribute="configProperties">
		<form:errors path="*" class = "configError"/>
	<a href="downloadProperties" class="printButton" title="Download properties">
	  <i class="fa fa-download"></i>
	</a>
		<c:redirect url="/"/>		
	</form:form>
</t:error>

