<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div id="loadCoverInner" class="passwordSplash col-xs-offset-2 col-sm-offset-3 col-md-offset-4 col-xs-6 col-sm-5 col-md-4">
	<a id="closeButton" href="#" title="Close Window">×</a>
	<h2>Change BaseX Database</h2>
	<form:form method="post" id="baseXDBForm" modelAttribute="baseXDBForm">
		<table>
			<tr>
				<td>BaseX DB URL</td>
				<td><input type='text' id='baseXDBUrl' name='baseXDBUrl'></td>
			</tr>
			<tr>
				<td>Admin Password</td>
				<td><input type='password' id='adminPassword'
					name='adminPassword'></td>
			</tr>
			<tr>
				<td>Reader Password</td>
				<td><input type='password' id='readerPassword'
					name='readerPassword'></td>
			</tr>
			<tr>
				<td>Writer Password</td>
				<td><input type='password' id='writerPassword'
					name='writerPassword'></td>
			</tr>
			<tr>
				<td></td>
				<td class="editPasswordCell"><input id='changeBaseXDBButton'
					type="button" class="btn" value='Change Database' /></td>
			</tr>
			<tr>
				<td></td>
				<td id="changeBaseXDBMsg"></td>
			</tr>
		</table>
	</form:form>
</div>