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
		<c:set var="subTitl" value="Merge Process" />
	</c:if>
	<title>${subTitl} - CED2AR</title>
	<%-- Applied based on whether or not print argument was given --%>
	<c:choose>
		<%--TODO: add back minified css --%>
		<c:when test="${restricted}">
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/bootstrap/bootstrap.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/bootstrap/bootstrap-theme.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/main.css" />
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/fonts.css" />
			<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />
			<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/main.css" />
		</c:otherwise>
	</c:choose>
	
	<%--Applies all page specified CSS --%>
	<c:forEach var="c" items="${fn:split(css,' ')}">
		<c:if test="${not empty c}">
			<link rel="stylesheet" type="text/css"
				href="${baseURI}/styles/${c}.css" />
		</c:if>
	</c:forEach>
	
	<%--IE doesn't like loading Font Awesome --%>
	<c:choose>
		<c:when test="${fn:contains(header['User-Agent'],'MSIE') || fn:contains(header['User-Agent'],'Trident')}">
			<link rel="stylesheet" type="text/css" href="${baseURI}/font-awesome/css/font-awesome.min.css" />
		</c:when>
		<c:when test="${restricted}">
			<link rel="stylesheet" type="text/css" href="${baseURI}/font-awesome/css/font-awesome.min.css" />
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" />
		</c:otherwise>
	</c:choose>
	
	<script src="//ajax.googleapis.com/ajax/libs/webfont/1.4.7/webfont.js"></script>
	<script>
		  WebFont.load({
		    google: {
		      families: ['Roboto:400,700,400italic']
		    },
		    timeout:5000
		  });
	</script>	
	<link rel="shortcut icon" href="${baseURI}/images/favicon.png" />

</head>
	<body>
		<div id="main" class="container mergeMain">	
			<jsp:doBody />		
		</div>
		<c:choose>
			<c:when test="${restricted}">
				<script type="text/javascript"
					src="${baseURI}/scripts/jquery/jquery-2.10.min.js"></script>
				<script type="text/javascript"
					src="${baseURI}/scripts/bootstrap/bootstrap.min.js"></script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript"
					src="//ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js"></script>
				<script type="text/javascript"
					src="//netdna.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
			</c:otherwise>
		</c:choose>
		<c:forEach var="j" items="${fn:split(js,' ')}">
			<c:if test="${not empty j}">
				<script type="text/javascript" src="${baseURI}/scripts/${j}.js"></script>
			</c:if>
		</c:forEach>
	</body>
</html>