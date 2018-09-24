<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="css" scope='request'>edit</c:set>
<c:set var="js" scope='request'>fileUpload inlineLoad</c:set>
<t:main>
	<div class="infoText">
		<h2>Generate PDF Files</h2>
		Clicking the button below will create PDF files for all the codebooks in the database.
	</div>
	<p>
	<form id="generatePDFsForm" method="post" action="${baseURI}/edit/generatepdfs" enctype="multipart/form-data">
		<div>
			<br>
			<button type="submit" class="btn">Generate PDFs</button>
		</div>
	</form>
</t:main>