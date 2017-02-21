<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:if test="${not print}">
	<c:set var="js" scope='request'>toggle hints/popups hints/crowdsourceWarning</c:set>
</c:if>
<t:main>
	<%-- 
		Displays a codebook broken out into tabs by DDI complex types (/docDscr, /stdyDscr, /fileDscr, /dataDscr, /otherMat)
		Since the search is by Study title, the Study tab content is displayed when the page is opened.
		
		The DDI tab labels (Doc, Study, File, Data, Other Material) can be configured in the configuration file.
		Most DDI tabs can be disabled.  (The Study tab cannot be disabled.)  This is also set in the configuration file.
		
		Currently, the Other Material tab does not display any /otherMat element data.  
		You can disable the tab if you want.  (See above.)
		You can add DDI content by changing the otherMat.xsl (stylesheet)
	 --%>

	<c:if test="${not empty newVersion}">
		<c:set var="newHandle" scope="page">${baseHandle}${newVersion}</c:set>
		<em> <i class="fa fa-exclamation-triangle"></i> This codebook is
			outdated. Please use <a href="${baseURI}/codebooks/${baseHandle}/v/${newVersion}">
				${codebooks[newHandle][4]} </a>
		</em>
	</c:if>
	<c:if test="${not empty codebook}">			
			<c:if test="${not empty pdf}">
				<a href="${pdf}" class="printButton" target="_blank"
					title="Download as PDF" aria-label="Download as PDF" download="${codebookTitl}.pdf"> <i
					class="fa fa-file-pdf-o"></i>
				</a>
			</c:if>
			<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/exportToSTATA"
				class="printButton3b printRemove" title="Download Stata variable values" aria-label="Download Stata variable values">Stata</a>
			<a href="${baseURI}/codebooks/${baseHandle}/v/${version}/vars/exportToSAS"
				class="printButton3b printRemove" title="Download SAS variable values" aria-label="Download SAS variable values">SAS</a>
			<a href="${baseURI}/rest/codebooks/${handle}"<%--?type=gitNotes --%>
				class="printButton" target="_blank" title="Download raw XML" aria-label="Raw XML"
				download="${subTitl}.xml"> <i class="fa fa-code"></i>
			</a>
			<a href="?print=y" class="printButton" target="_blank"
				title="View print layout" aria-label="View print layout"> <i class="fa fa-print"></i>
			</a>
<%-- TODO: cs Add back in when working: editing
		<c:if test="${editing}">
			<< cs Removed choose block >>
		</c:if>
--%>

<div>

  <%-- Nav tabs --%>
  <ul class="nav nav-tabs" role="tablist">
    <c:if test="${uiNavTabDoc eq  'true' }">
    	<li role="presentation"><a href="#docDscr" aria-controls="docDscr" role="tab" data-toggle="tab">${uiNavTabDocLabel}</a></li>
    </c:if>
    <%-- The study tab should NOT be disabled.  This tab displays the selected study --%>
    <c:if test="${uiNavTabStdy eq  'true' }">
    	<li role="presentation" class="active"><a href="#stdyDscr" aria-controls="stdyDscr" role="tab" data-toggle="tab">${uiNavTabStdyLabel}</a></li>
    </c:if>
    <c:if test="${uiNavTabFile eq  'true' }">
    	<li role="presentation"><a href="#fileDscr" aria-controls="fileDscr" role="tab" data-toggle="tab">${uiNavTabFileLabel}</a></li>
    </c:if>
    <c:if test="${uiNavTabData eq  'true' }">
    	<li role="presentation"><a href="#dataDscr" aria-controls="dataDscr" role="tab" data-toggle="tab">${uiNavTabDataLabel}</a></li>
    </c:if>
    <c:if test="${uiNavTabOtherMat eq  'true' }">
    	<li role="presentation"><a href="#otherMat" aria-controls="otherMat" role="tab" data-toggle="tab">${uiNavTabOtherMatLabel}</a></li>
    </c:if>
  </ul>

  <%-- Tab panes --%>
  <div class="tab-content">

    <%-- Contains docDscr content. Uses: doc.xsl --%>
    <div role="tabpanel" class="tab-pane" id="docDscr">
    	<c:if test="${uiNavTabDoc eq  'true' }">
			<div id="details" itemscope itemtype="http://schema.org/Dataset">
				<c:if test="${not empty timeStamp}">
					<p><em>This document was generated: ${timeStamp}</em></p>
				</c:if>
				<h2 itemprop="name">${codebookTitl}</h2>
				${codebookDocDscr}
			</div>
    	</c:if>
    </div>

    <%-- Contains stdyDscr content. Uses: study.xsl --%>
    <div role="tabpanel" class="tab-pane active" id="stdyDscr">
    	<c:if test="${uiNavTabStdy eq  'true' }">
			<div id="details" itemscope itemtype="http://schema.org/Dataset">
				<c:if test="${not empty timeStamp}">
					<p><em>This document was generated: ${timeStamp}</em></p>
				</c:if>
				${codebookStdyDscr}
			</div>
    	</c:if>
    </div>

    <%-- Contains fileDscr content. Uses: file.xsl --%>
    <div role="tabpanel" class="tab-pane" id="fileDscr">
    	<c:if test="${uiNavTabFile eq  'true' }">
			<div id="details" itemscope itemtype="http://schema.org/Dataset">
				<c:if test="${not empty timeStamp}">
					<p><em>This document was generated: ${timeStamp}</em></p>
				</c:if>
				${codebookFileDscr}
			</div>
    	</c:if>
    </div>

    <%-- Contains only a link.  NO dataDscr content or stylesheet (.xsl).
     dataDscr	- Only a variable COUNT is returned by getTitlePage()
     			  	  Use the View Variables link in the Data tab to make it work like the current codebook page.
     --%>
    <div role="tabpanel" class="tab-pane" id="dataDscr">			  
		<c:if test="${uiNavTabData eq  'true' }">
			<div id="details" itemscope itemtype="http://schema.org/Dataset">
				<h4></h4>
				<p class="value4">
					<c:choose>
						<c:when test="${count gt 0}">
							<a class="printRemove" href="${baseURI}/landing?c=${handle}">View Variables</a>
							<em>(${count} variables)</em>
						</c:when>
						<c:otherwise>
							<em>This codebook does not have variables.</em>
						</c:otherwise>
					</c:choose>
				</p>
			</div>
    	</c:if>
    </div>

    <%-- COULD contain the otherMat content. Uses: otherMat.xsl  Currently, ced2ar does not display any /otherMat element data. --%>
    <div role="tabpanel" class="tab-pane" id="otherMat">
    	<c:if test="${uiNavTabOtherMat eq  'true' }">
			<div id="details" itemscope itemtype="http://schema.org/Dataset">
				<c:if test="${not empty timeStamp}">
					<p><em>This document was generated: ${timeStamp}</em></p>
				</c:if>
				${codebookOtherMat}
			</div>
    	</c:if>
    </div>
    
  </div>

</div>


	</c:if>
</t:main>