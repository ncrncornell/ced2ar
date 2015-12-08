<%@ tag description="InitConfig Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags"%>

<c:set var="baseURI" scope="session">${pageContext.request.contextPath}</c:set>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Init Config - CED2AR</title>
<link rel="stylesheet" type="text/css" href="${baseURI}/styles/initConfig.css" />
<script type="text/javascript" src="${baseURI}/scripts/jquery/jquery-2.10.min.js"></script>
<script src="${baseURI}/scripts/initConfig.js"></script>
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
	
	<div id="main" class="container" >
		<jsp:doBody />
	</div>
	
</body>
</html>