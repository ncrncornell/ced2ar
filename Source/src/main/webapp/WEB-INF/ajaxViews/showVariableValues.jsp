<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div id="loadCoverInner"
	class="passwordSplash col-xs-offset-2 col-sm-offset-3 col-md-offset-4 col-xs-6 col-sm-5 col-md-4">
	<a id="closeButton" href="#" title="Close Window">×</a>
	
	<h2>Variable Values: ${codeFormat} Code</h2>
	<form:form method="post" id="showVaribleValues" modelAttribute="data">
		<table>
			<tr>
				<td>
					 <textarea rows="25" cols="70">
						${data}
					</textarea> 
				</td>
			</tr>
		</table>
	</form:form>
</div>