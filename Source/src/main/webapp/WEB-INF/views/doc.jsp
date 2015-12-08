<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<div class="infoText">
		<h2>Documentation</h2>
		<div class="infoFloat">
			<h4>User Guides</h4>
			<p>
				<a href="docs/api">API Documentation</a> <br /> <a href="docs/faq">FAQ</a><br />
				<a href="docs/ddi-ncrn">DDI 2.5.1 + NCRN</a><br />
				<a href="https://docs.google.com/spreadsheets/d/1b1BaxDzlBdueYaFVIMK5dAlnVoWgBGTOI4eNHjCfdwU" 
				target="_blank">Current DDI Profile</a>
			</p>
		</div>
		<div class="infoFloat">
			<h4>External Resources</h4>
			<p>
				<a href="https://confluence.cornell.edu/display/ncrn/CED2AR+User's+Guide"
				target="_blank">User's Guide</a><br />
			</p>
			<p>
				<a href="https://github.com/ncrncornell"
					target="_blank">NCRN GitHub</a>
			</p>
			<p>
				<a href="http://www.ddialliance.org/Specification/DDI-Codebook/2.5/"
					target="_blank">DDI 2.5 Schema</a>
			</p>
			<p>
				<a href="https://cornell.qualtrics.com/SE/?SID=SV_7a1Wl3aNdVvfkQR"
					target="_blank">Survey</a>
			</p>
			<p>
				<a href="http://www.nsf.gov/awardsearch/showAward?AWD_ID=1131848"
					target="_blank">NSF Grant Information</a>
			</p>
			<p>
				<a href="http://www.ncrn.cornell.edu/" target="_blank">Cornell NCRN Site</a><br />
			</p>
			<p>
				<a href="http://www.ncrn.info/" target="_blank">Main NCRN Site</a><br />
			</p>
		</div>
	</div>
</t:main>