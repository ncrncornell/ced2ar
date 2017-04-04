<%@ tag description="Content Wrapping Tag" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE HTML>
<c:set var="baseURI" scope="session">${pageContext.request.contextPath}</c:set>
<html lang="en">
<head>	
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%--This is the rich snippet as seen by search engines. It is sometimes specific for a particular page --%>
	<c:choose>
		<c:when test="${empty metaDesc}">
			<meta name="description" 
				content="A repository for metadata on surveys, administrative microdata, and other statistical data, built by Cornell University">
		</c:when>
		<c:otherwise>
			<meta name="description" content="${metaDesc}">
		</c:otherwise>
	</c:choose>
	<meta name="keywords" content="CED2AR,Cornell,DDI,Metadata${metaKeywords}">
	<meta name="author" content="CISER">
	<meta name="robots" content="index, follow">
	<c:if test="${print}">
		<c:set var="subTitl">${subTitl} - Print View</c:set>
	</c:if>
	<c:if test="${fn:trim(subTitl) ne ''}">
		<c:set var="subTitl">${subTitl} -&nbsp;</c:set>
	</c:if>
	<title>${subTitl}CED2AR</title>
	<%-- Applied based on whether or not print argument was given --%>
	<c:choose>
		<%--TODO: add back minified css --%>
		<c:when test="${print}">
			<link rel="stylesheet" type="text/css"  href="${baseURI}/styles/fonts.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/main.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/print.css" />
		</c:when>
		<c:when test="${not print && restricted}">
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/bootstrap/bootstrap.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/bootstrap/bootstrap-theme.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/main.css" />
			<link rel="stylesheet" type="text/css" media="print" href="${baseURI}/styles/print.css" />
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/fonts.css" />
			<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />
			<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" />
			<link rel="stylesheet" type="text/css" href="${baseURI}/styles/main.css" />
			<link rel="stylesheet" type="text/css" media="print" href="${baseURI}/styles/print.css" />
		</c:otherwise>
	</c:choose>
	
	<%-- Optional for projector --%>
	<%-- <link rel="stylesheet" type="text/css" href="${baseURI}/styles/projector.css" />	--%>
	
	<%--Applies all page specified CSS --%>
	<c:forEach var="c" items="${fn:split(css,' ')}">
		<c:if test="${not empty c}">
			<link rel="stylesheet" type="text/css"
				href="${baseURI}/styles/${c}.css" />
		</c:if>
	</c:forEach>
	
	<%--IE doesn't like loading Font Awesome --%>
	<c:choose>
		<c:when test="${fn:contains(header['User-Agent'],'MSIE') || fn:contains(header['User-Agent'],'Trident')}">
			<%-- <link rel="stylesheet" type="text/css" href="${baseURI}/font-awesome/css/font-awesome.min.css" /> --%>
			<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" rel="stylesheet">
			 <%--<link rel="stylesheet" type="text/css" href="${baseURI}/font-awesome/css/font-awesome-ie.css" />--%>
		</c:when>
		<c:when test="${restricted}">
			<link rel="stylesheet" type="text/css" href="${baseURI}/font-awesome/css/font-awesome.min.css" />
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" />
		</c:otherwise>
	</c:choose>
	<link rel="shortcut icon" href="${baseURI}/images/favicon.png" />
</head>
<body>
	<span id="meta">
		<c:if test="${not empty sessionScope.filter}"><c:set var="meta_cbb" scope="session" value="true" /></c:if>
		<span id="meta_codebook">${meta_cbb}</span>
		<span id="meta_uri">${baseURI}</span>
		<span id="meta_username">${userName}</span>
		<span id="meta_user">${userEmail}</span>
		<input type="hidden" id="meta_demo" value="${demoMode}" />
	</span>
	<div id="load"></div>
	<div id="wrapper" class="container-fluid">
		<%--Chooses banner color based off server location --%>
		<c:choose>
			<c:when test="${fn:contains(pageContext.request.serverName,'wiki-census.ncrn.cornell.edu')}">
				<c:set var="tagLine" scope="application">Community Development Server (Crowdsourced)</c:set>
				<c:set var="hC" scope="application">hC0</c:set>
			</c:when>
			<c:when test="${fn:contains(pageContext.request.serverName,'wiki.ncrn.cornell.edu')}">
				<c:set var="demoMode" scope="application" value="true" />
				<c:set var="tagLine" scope="application">Community Development Server (Beta)</c:set>
				<c:set var="hC" scope="application">hC0</c:set>
			</c:when>
			<c:when test="${fn:contains(pageContext.request.serverName,'demo.ncrn.cornell.edu')}">
				<c:set var="demoMode" scope="application" value="true" />
				<c:set var="tagLine" scope="application">Demo Mode</c:set>
				<c:set var="hC" scope="application">hC3</c:set>
			</c:when>
			<c:when test="${fn:contains(pageContext.request.serverName,'dev.ncrn.cornell.edu')}">
				<c:set var="tagLine" scope="application">Development Server</c:set>
				<c:set var="hC" scope="application">hC0</c:set>
			</c:when>
			<c:when test="${fn:contains(pageContext.request.serverName,'www2.ncrn.cornell.edu')}">
				<c:set var="tagLine" scope="application">Official Server</c:set>
				<c:set var="hC" scope="application">hC0</c:set>
			</c:when>
			<c:when test="${fn:contains(pageContext.request.serverName,'cornell.edu')}">
				<c:set var="tagLine" scope="application"></c:set>
				<c:set var="hC" scope="application">hC0</c:set>
			</c:when>
			<c:when test="${pageContext.request.serverName == 'localhost'}">
				<c:set var="hC" scope="application">hC1</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="hC" scope="application">hC2</c:set>
			</c:otherwise>
		</c:choose>
		<div id="header" class="${hC}">
			<div id="subHeader">
				<a href='${baseURI}/search'><img src='${baseURI}/images/logo.png' alt='CED2AR' height="42"
					width="200" /></a>
				<div class="row">
					<div class="hidden-xs col-sm-8">
						<p>
							<c:if test="${not empty tagLine}">${tagLine} - </c:if>
							The Comprehensive Extensible Data Documentation and Access Repository
							<c:if test="${restricted}">(Restricted Network Mode)</c:if>
							<c:if test="${hC eq 'hC1'}">(Local Access)</c:if>
						</p>
					</div>
					<div id="loginStatus" class="col-xs-12 col-sm-4">
						<p>
							<c:if test="${not empty userEmail}">
								Logged in as 
								${userName}
								${userEmail}
							</c:if>
						</p>
					</div>
				</div>
			</div>
		</div>
		<t:nav />

		<%--This block of logic decides whether or not to make the main div
			 fill the entire page, or center itself with sidebars  --%>
		<c:choose>
			<c:when test="${pageWidth eq 1}">
				<c:set var="mainWidth" scope="request" value="col-xs-12" />
				<c:set var="sideWidth" scope="request" value="hidden" />
				<c:set var="sideWidthR" scope="request" value="hidden" />
			</c:when>
			<c:otherwise>
				<c:set var="mainWidth" scope="request"
					value="col-sm-12 col-md-10 col-lg-8 " />
				<c:set var="sideWidth" scope="request"
					value="hidden-sm hidden-xs col-md-1 col-lg-2" />
				<c:set var="sideWidthR" scope="request"
					value="hidden-sm hidden-xs col-md-1 col-lg-2" />
			</c:otherwise>
		</c:choose>
		<div id="mainRow" class="row">
			<div id="info">
				<c:if test="${not empty error}">
					<div class="alert alert-danger fade in">
						<button type="button" class="close" data-dismiss="alert">×</button>
						ERROR: ${error}
					</div>
					<c:remove var="error" />
				</c:if>
				<c:if test="${not empty info_splash}">
					<div class="alert alert-success fade in">
						<button type="button" class="close" data-dismiss="alert">×</button>
						${info_splash}
					</div>
					<c:remove var="info_splash" />
				</c:if>
				<c:if test="${not empty error2}">
					<c:set scope="session" var="error">${error2}</c:set>
					<c:remove var="error2" />
				</c:if>
				<c:if test="${not empty info_splash2}">
					<c:set scope="session" var="info_splash">${info_splash2}</c:set>
					<c:remove var="info_splash2" />
				</c:if>
			</div>		
			<div id="sidebar" class="${sideWidth}">
				<div id="filter">
					<div id="filterCodebook"></div>
					<div id="filterCompare"></div>
				</div>
			</div>
			<div id="filterHide"></div>
			<div id="main" class="${mainWidth}">
				<div class="row crumbJar xs-hidden">
					<div id="crumbs" class="col-lg-12">
						<%--TODO: Add Unofficial/official switch --%>
						<t:crowdsourced />
						<c:if test="${not empty crumbs}">
							<ul class="breadcrumb">
								<li><a href="${baseURI}">CED2AR</a><span class="divider"></span></li>
								<c:if test="${fn:length(crumbs) gt 1 }">
									<c:forEach var="crumb" items="${crumbs}"
										end="${fn:length(crumbs)-2}">
										<li><a href="${baseURI}/${crumb[1]}">${crumb[0]}</a><span
											class="divider"></span></li>
									</c:forEach>
								</c:if>
								<li class="active">${crumbs[ fn:length(crumbs)- 1][0]}</li>
							</ul>
						</c:if>
					</div>
				</div>
				<jsp:doBody />
			</div>
			<div id="sidebarR" class="${sideWidthR}"></div>
		</div>
	</div>
	<div id="footer" class="hidden-xs container-fluid">
		<div class="row">
			<div class="col-sm-12">
				<p class="lb3">
					<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">
								<img alt="Creative Commons License" height="15" width="80"  src="${baseURI}/images/cc.png" />
					</a>
					<br />
					&copy; 2012-2016, <span itemscope itemtype="http://schema.org/WebSite">
						<span itemprop="copyrightHolder" > Cornell Institute for 
						Social and Economic Research </span></span>
				</p>
				<p>
					Please report bugs on <a href="https://github.com/ncrncornell/ced2ar/issues">GitHub</a>.
				</p>
				<a href="https://cornell.qualtrics.com/SE/?SID=SV_7a1Wl3aNdVvfkQR" class="footerButton" target="_blank"><i
					class="fa fa-users"></i>User Survey</a>
				<span id='contactUs'>
					<a class='footerButton emailContact'><i class='fa fa-envelope'></i>Email us</a>
				</span> 		
				<a href="${baseURI}/about#legal" class="footerButton"><i
					class="fa fa-gavel"></i>Copyright Information</a>
				<a href="https://github.com/ncrncornell" class="footerButton"><i
					class="fa fa-github"></i>NCRN GitHub</a>
				
			</div>
		</div>
	</div>
	<%-- Loads mathjax if needed --%>
	<c:if test="${hasMath}">
		<script type="text/x-mathjax-config">
			MathJax.Hub.Config({messageStyle: "none"});
		</script>
		<c:choose>
			<c:when test="${restricted}">
				<script type="text/javascript"
					src="${baseURI}/mathjax/MathJax.js?config=AM_HTMLorMML-full"></script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript"
					src="//cdn.mathjax.org/mathjax/2.3-latest/MathJax.js?config=AM_HTMLorMML-full"></script>
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:choose>
		<c:when test="${restricted}">
			<script type="text/javascript"
				src="${baseURI}/scripts/jquery/jquery-2.10.min.js"></script>
			<script type="text/javascript"
				src="${baseURI}/scripts/bootstrap/bootstrap.min.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript"
				src="//ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js"></script>
			<script type="text/javascript"
				src="//netdna.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
			<!--  
			<script type="text/javascript" src="${baseURI}/scripts/survey.js"></script>
			-->
		</c:otherwise>
	</c:choose>
	<c:forEach var="j" items="${fn:split(js,' ')}">
		<c:if test="${not empty j}">
			<script type="text/javascript" src="${baseURI}/scripts/${j}.js"></script>
		</c:if>
	</c:forEach>
	<%-- Auto complete JS, needs jquery--%>
	<c:if test="${varAutoComplete}">
		<script type="text/javascript">	
				var varList = new Bloodhound({
					  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name','label'),
					  queryTokenizer: Bloodhound.tokenizers.whitespace,
					  prefetch: "${baseURI}/data/codebooks/${handle}/vars"//can also use remote
				}); 	
				loadVarAC(varList);
		</script>
	</c:if>
	<%--Keeping this inline to reduce load times --%>
	<script type="text/javascript">
			//Popovers for inline help
			$('.helpIcon').popover({ trigger: 'hover'});
			
			// Email obfuscator script 2.1 by Tim Williams, University of Arizona
			// Random encryption key feature by Andrew Moulden, Site Engineering Ltd
			// This code is freeware provided these four comment lines remain intact
			// A wizard to generate this code is at http://www.jottings.com/obfuscator/
			{ 
			  coded = "eGKrAl-KGBC-j@e9l0Gjj.GKt"
			  key = "c7hxAjRVqUXPYi0wIr8vNZtCdeTmWyH6fogESk53OalbBzLF4KGn1Q2JpM9usD"
			  shift=coded.length;
			  link="";
			  for (var i=0; i<coded.length; i++) {
			    if (key.indexOf(coded.charAt(i))==-1) {
			      ltr = coded.charAt(i);
			      link += (ltr);
			    }
			    else {     
			      ltr = (key.indexOf(coded.charAt(i))-shift+key.length) % key.length;
			      link += (key.charAt(ltr));
			    }
			  }
			  $(".emailContact").attr("href", "mailto:"+link);	
			  $(".emailContact2").html(link);		
			  $(".emailContact2").attr("href", "mailto:"+link);		
			  
			  //Resizes side bar if screenwidth is 1200px to 1400px
			  //
			  var w = $(window).width();
			  <%-- TODO: Move this into LESS compiled bootstrap later--%>
			  if(w > 1200 && w < 1450 && !$("#main").hasClass("col-xs-12")){
				  $("#main").removeClass("col-lg-8");	
				  $("#main").addClass("col-lg-10");
				  $("#sidebar").removeClass("col-lg-2");
				  $("#sidebar").addClass("col-lg-1");
				  $("#sidebarR").removeClass("col-lg-2");
				  $("#sidebarR").addClass("col-lg-1");
			  }
			}	
	</script>
	<c:if test="${analytics}">
	 	<%--Add your Google Analytics tracking code here--%>
		<script>
			  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
			  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
			  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
			  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
			 
			  ga('create', 'UA-47380966-1', 'cornell.edu');
			  ga('send', 'pageview');
		</script>
	</c:if>
</body>
</html>