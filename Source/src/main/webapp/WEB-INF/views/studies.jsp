<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<%-- Displays a list of (codebook) studies.
		 The data on this page cannot be edited.

		 If you uncomment the two sections below, you can also display the name of the codebook in the table.
	 --%>
	<div class="infoText">
  		<h2>Codebook Studies</h2>
	</div>
	<div class="infoFloat">
		<c:choose>
			<c:when test="${not empty sessionScope.studies}">
				<table style="width:80%;" >
<%--
					<tr>
  						<th>Codebook</th>
						<th>Study Title</th>
					</tr>
--%>
					<c:forEach var="study" items="${studies}">
						<tr>
<%--
							<td><a href="${baseURI}/codebooks/${study.value[0]}/v/${study.value[1]}"
									>${study.value[2]} 
								</a></td>
--%>
							<td><a href="${baseURI}/codebooks/${study.value[0]}/v/${study.value[1]}/study"
									>${study.value[3]} 
								</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<p>No Codebook Studies found.</p>
			</c:otherwise>
		</c:choose>
	</div>
</t:main>