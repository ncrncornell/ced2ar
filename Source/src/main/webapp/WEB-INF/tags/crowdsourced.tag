<%@ tag description="Crowdsourcing tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:if test="${not empty crowdsourceSwitch}">
	<div class="lb2 crowdSourcedMsg">
		<c:choose>
			<c:when test="${crowdsourceSwitch eq 'master'}">
				<p>
					<em>
						<i class="fa fa-university fa-2x"></i>
						You are viewing the official metadata. View crowdsourced 
						<a href="${remoteServerURL}">contributions</a>.
					</em>
				</p>
			</c:when>
			<c:when test="${crowdsourceSwitch eq 'wiki'}">
				<p>
					<em>
						<i class="fa fa-users fa-2x"></i>
						You are viewing crowdsourced metadata. View the 
						<a href="${remoteServerURL}">official version</a>
						<c:if test="${not empty crowdsourceCompareURL}">
							or <a href="${crowdsourceCompareURL}">compare the changes</a>
						</c:if>.
					</em>
				</p>
			</c:when>
		</c:choose>
	</div>				
</c:if>