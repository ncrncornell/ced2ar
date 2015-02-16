<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>joint.min prov</c:set>
<t:main>
	<div id="graph"></div>
	<script type="text/javascript"
		src="${baseURI}/scripts/jointjs/joint.min.js"></script>
	<script type="text/javascript"
		src="${baseURI}/scripts/jointjs/joint.ced2ar.js"></script>
	<script type="text/javascript"
		src="${baseURI}/scripts/jointjs/joint.layout.DirectedGraph.js"></script>
	<script type="text/javascript" src="${baseURI}/scripts/prov.js"></script>
</t:main>