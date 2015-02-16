<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="baseURI" scope="request">${pageContext.request.contextPath}</c:set>
<t:error>
	<h1>Login to Continue</h1>
	<p>Please choose an authentication method</p>
	<c:url var="openIDLoginUrl" value="/login/openid" />
	<form name="googleLoginForm" method="post" action="./google_oauth2_login">
	   <button id="googleBtn" type="submit">
	      <span class="icon"></span>
	      <span class="buttonText">Google</span>
	   </button>
	</form>
</t:error>