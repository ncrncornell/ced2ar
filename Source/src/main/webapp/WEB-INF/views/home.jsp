<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:main>
	<a href="${baseURI}/search" class="btn btn-primary btn-lg btn-block active" role="button">Search The Repository</a>
	<a href="${baseURI}/codebooks" class="btn btn-primary btn-lg btn-block active" role="button">Browse Codebooks</a>
</t:main>