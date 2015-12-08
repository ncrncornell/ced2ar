<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'></c:set>
<c:set var="js" scope='request'>toggle</c:set>
<t:main>
	<div id="details">
		<h2>Codebook Score</h2>	
		<div class="lb2">
			<h3>Variables</h3>	
			<c:choose>
				<c:when test="${varTotalScore gt 0}">
					<div class="lb2">
						<p>
							<fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" 
							value="${lablScore * 100}" />% of variables have labels
						</p>
						<c:if test="${lablScore gt 0 && lablScore lt 1}">
							<span class="truncPre printRemove">
								<em>Variables without labels</em>
							</span>
							<span class="truncFull hidden">
							<div>
								<p>
									<em>Variables without labels</em>
								</p>
								<c:forEach items="${lablMissing}" var="var">
									-&nbsp;<a href="${baseURI}/edit/codebooks/${basehandle}/v/${version}/vars/${var}">${var}</a>
								</c:forEach>
							</div>
							</span>
							<span class="truncExp"> ... more</span>
						</c:if>
					</div>
					<div class="lb2">
						<p>
							<fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" 
							value="${txtScore * 100}" />% of variables have significant full descriptions
						</p>
						<c:if test="${txtScore gt 0 && txtScore lt 1}">
							<span class="truncPre printRemove">
								<em>Variables without significant full descriptions</em>
							</span>
							<span class="truncFull hidden">
							<div>
								<p>
									<em>Variables without significant full descriptions</em>
								</p>
								<c:forEach items="${txtMissing}" var="var">
									<a href="${baseURI}/edit/codebooks/${basehandle}/v/${version}/vars/${var.key}">${var.key} (${var.value} chars)</a>
									<br />
								</c:forEach>
							</div>
							</span>
							<span class="truncExp"> ... more</span>
						</c:if>
					</div>
					<div class="lb2">
						<p>
							<fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" 
							value="${catScore * 100}" />% of variables have values
						</p>
						<c:if test="${catScore gt 0 && catScore lt 1}">
							<span class="truncPre printRemove">
								<em>Variables without values</em>
							</span>
							<span class="truncFull hidden">
							<div>
								<p>
									<em>Variables without values</em>
								</p>
								<c:forEach items="${catMissing}" var="var">
									-&nbsp;<a href="${baseURI}/edit/codebooks/${basehandle}/v/${version}/vars/${var}">${var}</a>
								</c:forEach>
							</div>
							</span>
							<span class="truncExp"> ... more</span>
						</c:if>
					</div>
					<div class="lb2">
						<p>
							<fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" 
							value="${sumStatScore * 100}" />% of variables have summary statistics
						</p>
						<c:if test="${sumStatScore gt 0 && sumStatScore lt 1}">
							<span class="truncPre printRemove">
								<em>Variables without summary statistics</em>
							</span>
							<span class="truncFull hidden">
							<div>
								<p>
									<em>Variables without summary statistics</em>
								</p>
								<c:forEach items="${sumStatMissing}" var="var">
									-&nbsp;<a href="${baseURI}/edit/codebooks/${basehandle}/v/${version}/vars/${var}">${var}</a>
								</c:forEach>
							</div>
							</span>
							<span class="truncExp"> ... more</span>
						</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<p>No variables in this codebook</p>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="lb2">
			<h3>Title Page</h3>
			<c:forEach items="${titlePageScores}" var="score">
				<c:if test="${score.value eq '0'}">
					<p>
						Missing 
						<a href="${baseURI}/edit/codebooks/${basehandle}/v/${version}/#${fn:replace(score.key,' ','')}">${fn:toLowerCase(score.key)}</a>
					</p>						
				</c:if>
			</c:forEach>
		</div>
		<div>
			<h3>Overall Score</h3>
			<p>
				<fmt:formatNumber type="number"
				minFractionDigits="1" maxFractionDigits="1" value="${overallScore * 100}" />%
			</p>
		</div>
	</div>
</t:main>