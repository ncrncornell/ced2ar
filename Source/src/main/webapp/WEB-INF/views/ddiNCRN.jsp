<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<div class="infoText">
		<h2>DDI 2.5.1 NCRN</h2>
		<p class="lb2">
			DDI 2.5.1 NCRN was derived from DDI 2.5.1 <br /> Minor modifications
			were made to add the access attribute to the child elements of a
			variable.
		</p>
		<p class="lb">
			<a
				href="http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN/schemas/fielddoc.html"
				target="_blank"> Full Documentation </a><br /> <a
				href="http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN/schemas/codebook.xsd"
				target="_blank"> Complete Schema </a>
		</p>
		<em>Changes</em>
		<ul>
			<li>Added access attribute to catStatType</li>
			<li>Added access attribute to catgryType</li>
			<li>Added access attribute to notesType</li>
			<li>Added access attribute to rangeType</li>
			<li>Added access attribute to sumStatType</li>
			<li>Added access attribute to valrngType</li>
			<li>Added access attribute to lablType</li>
		</ul>
	</div>
</t:main>