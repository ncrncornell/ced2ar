<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>edit</c:set>
<c:set var="js" scope='request'>handlebars/handlebars typehead/typeahead.bundle autoComplete</c:set>
<t:main>
	<h2>Autocomplete Test</h2>
	<div id="addVar">
	  <input class="typeahead" type="text" placeholder="Select a variable">
	</div>	
</t:main>