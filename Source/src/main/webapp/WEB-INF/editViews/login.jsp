<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="baseURI" scope="request">${pageContext.request.contextPath}</c:set>
<t:error>
	<h1>Login to Continue</h1>
	<p>Please choose an authentication method</p>
	<c:if test="${config.googleOauth2Supported eq true}">
		<form class="loginForm" name="googleLoginForm" method="post" action="./google_oauth2_login">
		   <button id="googleBtn" type="submit">
		      <span class="icon"></span>
		      <span class="buttonText">Google</span>
		   </button>
		</form>
	</c:if>
	<c:if test="${config.orcidOauth2Supported eq true}">
		<form class="loginForm" name="orcidLoginForm" method="post" action="./orcid_oauth2_login">
		   <button id="orcidBtn" type="submit">
		      <span class="buttonText">ORCID</span>
		   </button>
		</form>
	</c:if>	
</t:error>