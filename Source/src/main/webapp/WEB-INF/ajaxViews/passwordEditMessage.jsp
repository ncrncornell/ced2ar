<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<c:when test="${empty messages}">
		<div class='configSuccess'>Password changed successfully</div>
	</c:when>
	<c:otherwise>
		<div class='configError'>
			<p>
				<c:if test="${messages.invalidOriginal eq 'invalidOriginal' }">
					Invalid current password <br />
				</c:if>
				<c:if
					test="${messages.newAndConfirmMismatch eq 'newAndConfirmMismatch' }">
					New and confirm passoword's do not match <br />
				</c:if>
				<c:if test="${messages.invalidPassword eq 'invalidPassword' }">
					Invalid BaseX Password or BaseX URL	<br />
				</c:if>
				<c:if test="${messages.missingBaseXUrl eq 'missingBaseXUrl' }">
					BaseX URL is required.	<br />
				</c:if>
				<c:if
					test="${messages.missingAdminPassword eq 'missingAdminPassword' }">
					Admin Password is Required.	<br />
				</c:if>
				<c:if
					test="${messages.missingReaderPassword eq 'missingReaderPassword' }">
					Reader Password is Required.	<br />
				</c:if>
				<c:if
					test="${messages.missingWriterPassword eq 'missingWriterPassword' }">
					Writer Password is Required.	<br />
				</c:if>
				<c:if test="${not empty messages.basexDBNotChanged}">
					Invalid ${messages.basexDBNotChanged} 	<br />
				</c:if>

				<c:if test="${not empty messages.invalidBaseXURL}">
					BaseX DB URL is invalid or Unreachable.<br />
				</c:if>
				<c:if test="${not empty messages.baseXDBUrlNotChanged}">
					You are using current BaseX DB URL.<br />
				</c:if>


			</p>
		</div>
	</c:otherwise>
</c:choose>