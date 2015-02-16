<%@ tag description="Error Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags"%>
<!DOCTYPE HTML>
<c:set var="baseURI" scope="session">${pageContext.request.contextPath}</c:set>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<c:if test="${empty subTitl}">
	<c:set var="subTitl" value="Error" />
</c:if>
<title>${subTitl} - CED2AR</title>
<link rel="stylesheet" type="text/css"
	href="${baseURI}/styles/error.css" />
<script src="//ajax.googleapis.com/ajax/libs/webfont/1.4.7/webfont.js"></script>
<script>
		  WebFont.load({
		    google: {
		      families: ['Roboto:400,700,400italic']
		    },
		    timeout:5000
		  });
		</script>
</head>
<body>
	<div id="main" class="container">
		<jsp:doBody />
	</div>
</body>
</html>