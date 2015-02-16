<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="js" scope='request'>toggle</c:set>
<c:set var="css" scope='request'>compare</c:set>
<t:main>
	<a href="?print=y" class="printButton" target="_blank"
		title="View print version"> <i class="fa fa-print"></i>
	</a>
	<h2>Comparison Overview</h2>
	<table id="varComp" class="lb2">
		<tr class="varCompHeader">
			<td>Name</td>
			<td>Codebook</td>
			<td>Label</td>
			<td>Description</td>
		</tr>
		<c:forEach items='${requestScope}' var='i'>
			<c:if test='${fn:startsWith(i.key,"var")}'>
				<tr>
					<c:if test="${not empty i.value}">${i.value}</c:if>
				</tr>
			</c:if>
		</c:forEach>
	</table>
	<h2>Values</h2>
	<c:choose>
		<c:when test="${fn:length(values) gt 0}">
			<table id="varComp2">
				<tr>
					<td>Values</td>
					<c:forEach items='${headers}' var='h'>
						<td class="valueHeader"><a
							href="${baseURI}/codebooks/${h[0]}/vars/${h[2]}">${h[2]}</a><br />
							${h[1]}<br /> <em>(${h[3]} total)</em></td>
					</c:forEach>
				</tr>
				<c:forEach items='${values}' var='v'>
					<tr>
						<td>${v.key}</td>
						<c:forEach items='${headers}' var='row' varStatus="vi">
							<c:choose>
								<c:when test="${fn:contains(v.value,vi.index)}">
									<td class="valueYes"><i class="fa fa-check"></i></td>
								</c:when>
								<c:otherwise>
									<td class="valueNo"><i class="fa fa-times"></i></td>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<p>The selected variables do not have values</p>
		</c:otherwise>
	</c:choose>
</t:main>