<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:cdr="http://www2.ncrn.cornell.edu/ced2ar-web"
	exclude-result-prefixes="cdr">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes"/>
<!-- Functions -->		
	<!-- Field title -->
	<xsl:function name="cdr:fieldTitle">
		<xsl:param name="title"/>
		<xsl:param name="schemaDoc"/>
		<xsl:param name="size"/>
		<p>
			<xsl:attribute name="id">
				<xsl:value-of select="translate($title, ' ','')"/> 
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$size eq '1'">staticHeader hs3</xsl:when>
					<xsl:otherwise>staticHeader</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:value-of select="$title" />
		</p>
	</xsl:function>
	
	<!-- Field values -->
	<xsl:function name="cdr:field">
	    <xsl:param name="value"/>
	    <xsl:param name="wrapper"/>
	    <xsl:param name="fieldName"/>
	    <xsl:param name="index"/>
	    <xsl:param name="delete"/>
		<xsl:choose>
			<xsl:when test="not($value)">
				<em>No description given</em>
			</xsl:when>
			<xsl:when test="$wrapper eq 'rich'">
				<xsl:copy-of select="$value" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'div'">
				<div class="value2"><xsl:copy-of select="$value" /></div>
			</xsl:when>
			<xsl:when test="$wrapper eq 'span'">
				<span class="valueInline"><xsl:copy-of select="$value" /></span>
			</xsl:when>
			<xsl:when test="$wrapper eq 'p'">
				<p class="value2"><xsl:copy-of select="$value" /></p>
			</xsl:when>
			<xsl:when test="$wrapper eq 'li'">
				<li class="valueInline">
					<xsl:copy-of select="$value" />
				</li>
			</xsl:when>
			<xsl:when test="$wrapper eq 'url'">
				<a>
					<xsl:attribute name="href"><xsl:value-of select="$value" /></xsl:attribute>
					<xsl:value-of select="$value" />
				</a>
			</xsl:when>
			<xsl:when test="$wrapper eq 'blank'">
			</xsl:when>
			<xsl:when test="$wrapper eq 'multi'">
				<xsl:value-of select="$value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value" />
			</xsl:otherwise>
		</xsl:choose>
  	</xsl:function>
  	
  	<!-- Field with title and controls -->
	<xsl:function name="cdr:field2">
		<xsl:param name="title"/>
	    <xsl:param name="value"/>
	    <xsl:param name="fieldName"/>
		<xsl:param name="schemaElement"/>
		<xsl:param name="size"/>
		<p>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$size eq '1'">staticHeader hs3</xsl:when>
					<xsl:otherwise>staticHeader</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="translate($title, ' ','')"/> 
			</xsl:attribute>
			<xsl:value-of select="$title"/>
			<div class="value2"><xsl:copy-of select="$value"></xsl:copy-of></div>
		</p>	
  	</xsl:function>

   	<!-- Main template -->
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="/codeBook/@handle" />
		</xsl:variable>
		
		<!-- Header information -->
		<div class="lb2">
			<div class="lb">
				<p class="value4">
					Last update to metadata:	
					<xsl:choose>
						<xsl:when test="/codeBook/docDscr/citation/verStmt/version/@date != ''">
							<xsl:value-of select="/codeBook/docDscr/citation/verStmt/version/@date" />
						</xsl:when>
						<xsl:otherwise>
							<em>not available</em>
						</xsl:otherwise>
					</xsl:choose>
				</p>
				
				<!-- Merge starts here -->
				<p id="DocumentDate" class="value4">
					Document Date:
					<span class="value merge mergeEprodDate">
						<span class="mergeDisplay">
							<xsl:value-of select="codeBook/docDscr/citation/prodStmt/prodDate" />
						</span>
						<span class="mergeOriginal hidden">
							<xsl:value-of select="codeBook/docDscr/citation/prodStmt/prodDate" />
						</span>
					</span>
				
				</p>	
				<p id="DocumentProducer" class="value4">
					Codebook prepared by:
					<xsl:for-each select="codeBook/docDscr/citation/prodStmt/producer">
						<span>
							<xsl:attribute name="class">value merge mergeEdocProducer<xsl:value-of select="position()"/></xsl:attribute>
							<span class="mergeDisplay">
								<xsl:copy-of select="cdr:field(current(),'multi','docProducer',position(),false())" />
							</span>
							<span class="mergeOriginal hidden">
								<xsl:copy-of select="cdr:field(current(),'multi','docProducer',position(),false())" />
							</span>
						</span>
					</xsl:for-each>
				</p>
				<p id="Study Producer" class="value4">
					Data prepared by:
					<xsl:for-each select="codeBook/stdyDscr/citation/prodStmt/producer">
						<span>
							<xsl:attribute name="class">value merge mergeEstdyProducer<xsl:value-of select="position()"/></xsl:attribute>
							<span class="mergeDisplay">
								<xsl:copy-of select="cdr:field(current(),'multi','stdyProducer',position(),false())" />
							</span>
							<span class="mergeOriginal hidden">
								<xsl:copy-of select="cdr:field(current(),'multi','stdyProducer',position(),false())" />
							</span>
						</span>
					</xsl:for-each>
				</p>	
			</div>
		
			<!-- Data Distributed by: -->
			<p class="staticHeader2" id="Distributor">
				Data Distributed by:
			</p>
			<xsl:for-each select="/codeBook/stdyDscr/citation/distStmt/distrbtr">
				<p class="value2">
					<span>
						<xsl:attribute name="class">merge distrbtr<xsl:value-of select="position()"/></xsl:attribute>
						<span class="mergeDisplay">
							<xsl:copy-of select="cdr:field(current(),'','distrbtr',position(),true())" />
						</span>
						<span class="mergeOriginal hidden">
							<xsl:copy-of select="cdr:field(current(),'','distrbtr',position(),true())" />	
						</span>
					</span>
					<br />
					<span>
						<xsl:attribute name="class">merge distrbtrURL<xsl:value-of select="position()"/></xsl:attribute>
						<span class="mergeDisplay">
							<xsl:copy-of select="cdr:field(current()/@URI,'url','distrbtrURL',position(),false())" />
						</span>
						<span class="mergeOriginal hidden">
							<xsl:copy-of select="cdr:field(current()/@URI,'url','distrbtrURL',position(),false())" />
						</span>
					</span>
				</p>				
			</xsl:for-each>
		</div>
		<!-- Citations -->
		<p class="staticHeader lb3">
   			Citation
   		</p>
   		<div class="valueInline value2">
   			<em id="CodebookCitation">Please cite this codebook as:</em><br />
			<span class="value merge mergeEdocCit">
				<span class="mergeDisplay">
					<xsl:copy-of select="cdr:field(/codeBook/docDscr/citation/biblCit/node(),'rich','docCit',1,false())" />
					
				</span>
				<span class="mergeOriginal hidden">
					<xsl:copy-of select="cdr:field(/codeBook/docDscr/citation/biblCit/node(),'rich','docCit',1,false())" />
				</span>
			</span>
		</div>
		<div class="valueInline value2">
   			<em id="DataCitation">Please cite this dataset as:</em><br />
			<span class="value merge mergeEstdyCit">
				<span class="mergeDisplay">
					<xsl:copy-of select="cdr:field(/codeBook/stdyDscr/citation/biblCit/node(),'rich','stdyCit',1,false())" />
				</span>
				<span class="mergeOriginal hidden">
					<xsl:copy-of select="cdr:field(/codeBook/stdyDscr/citation/biblCit/node(),'rich','stdyCit',1,false())" />
				</span>
			</span>
		</div>
		
		<!-- Abstract -->
		<p class="staticHeader mergeTitle" id="Abstract">Abstract</p>
		<span class="value2 merge mergeEabstract">
			<span class="value mergeDisplay">
				<xsl:copy-of select="cdr:field(/codeBook/stdyDscr/stdyInfo/abstract/node(),'rich','abstract',1,false())" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field(/codeBook/stdyDscr/stdyInfo/abstract/node(),'rich','abstract',1,false())" />
			</span>
		</span>
		
		<!-- Making datasets non-mergable -->
		<!--
		<p class="staticHeader">
			Datasets
		</p>
		<div class="value2">
			<xsl:for-each select="codeBook/fileDscr">
				<p>
					<xsl:value-of select="fileTxt/fileName" />&#160;
					<xsl:choose>
						<xsl:when test="(not(contains(@URI,'http')) and not(contains(@URI,'ftp:'))) and @URI != ''">
							<xsl:value-of select="fileTxt/fileName" />
							(Incomplete URL provided - <xsl:value-of select="@URI" />)
						</xsl:when>
						<xsl:when test="string-length(@URI) gt 0 ">
							<a itemprop="distribution" class="iLinkR">
								<xsl:attribute name="target">_blank</xsl:attribute>
								<xsl:attribute name="href"><xsl:value-of
									select="@URI" /></xsl:attribute>
								<xsl:value-of select="@URI" /><i class="fa fa-external-link"></i>
							</a>
						</xsl:when>
						<xsl:otherwise>
							(No hyperlink available)hs3
						</xsl:otherwise>
					</xsl:choose>
					&#160;(<xsl:value-of select="fileTxt/fileType" />)
					<xsl:copy-of select="cdr:field(current()/@URI,'blank','fileDscrURL',position(),false())" />
				</p>
				
			</xsl:for-each>
			<xsl:if test="count(codeBook/fileDscr) eq 0">
				<p>Sorry, no file information is available for this codebook.</p>
			</xsl:if>
		</div>
		-->
		
		<!-- Terms of Use-->
		<p class="staticHeader lb2">
			Terms of Use
		</p>
			
		<!-- Access Levels-->
		<!-- Making accesslevels non-mergable -->
		<!-- 
		<p id="accessLevels" class="staticHeader hs3">
		</p>
		<div class="value2">
			<p><em>undefined</em></p>
			<p class="lb2">
				Elements flagged with undefined have not yet been reviewed for release
			</p>
			<xsl:for-each select="codeBook/stdyDscr/dataAccs[@ID]">
				<div class="lb2">
					<p><em><xsl:value-of select="current()/@ID" /></em></p>
					<xsl:copy-of select="cdr:field(current()/useStmt/restrctn/node(),'span','accessRstr',position(),false())" />
				</div>	
			</xsl:for-each>
		</div>
		-->
		
		<!-- Access Restrictions (Default)-->
		<span class="value merge mergeEaccessRstr">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Access Restrictions (Default)',codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn/node(),'accessRstr','restrctn','1')" />			
			</span>
			<span class="mergeOriginal hidden">
				 <xsl:copy-of select="cdr:field2('Access Restrictions (Default)',codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn/node(),'accessRstr','restrctn','1')" />
			</span>
		</span>
			
		<!-- Access Requirements -->
		<span class="value merge mergeEconfDec">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Access Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/confDec,'confDec','confDec','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Access Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/confDec,'confDec','confDec','1')" />
			</span>
		</span>	
		
		<!-- Access Conditions -->
		<span class="value merge mergeEaccessCond">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Access Conditions',codeBook/stdyDscr/dataAccs[1]/useStmt/conditions/node(),'accessCond','conditions','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Access Conditions',codeBook/stdyDscr/dataAccs[1]/useStmt/conditions/node(),'accessCond','conditions','1')" />
			</span>
		</span>
		
		<!-- Access Permission Requirements  -->
		<span class="value merge mergeEaccessPermReq">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Access Permission Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm/node(),'accessPermReq','specPerm','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Access Permission Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm/node(),'accessPermReq','specPerm','1')" />
			</span>
		</span>
		
		<!-- Citation Requirements  -->
		<span class="value merge mergeEcitReq">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Citation Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/citReq/node(),'citReq','citReq','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Citation Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/citReq/node(),'citReq','citReq','1')" />
			</span>
		</span>
		
		<!-- Disclaimer -->
		<span class="value merge mergeEdisclaimer">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Disclaimer',codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer/node(),'disclaimer','disclaimer','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Disclaimer',codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer/node(),'disclaimer','disclaimer','1')" />		
			</span>
		</span>
		
		<!-- Contact -->
		<span class="value merge mergeEcontact">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field2('Contact',codeBook/stdyDscr/dataAccs[1]/useStmt/contact/node(),'contact','contact','1')" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field2('Contact',codeBook/stdyDscr/dataAccs[1]/useStmt/contact/node(),'contact','contact','1')" />
			</span>
		</span>
		
	<!--Additional Info-->	
	<p class="staticHeader lb2">
		Additional Information	
	</p>	
	
	<!-- Methodology -->
	<span class="value merge mergeEmethod">
		<span class="mergeDisplay">
			<xsl:copy-of select="cdr:field2('Methodology',/codeBook/stdyDscr/method/dataColl/collMode/node(),'method','method','1')" />
		</span>
		<span class="mergeOriginal hidden">
			<xsl:copy-of select="cdr:field2('Methodology',/codeBook/stdyDscr/method/dataColl/collMode/node(),'method','method','1')" />
		</span>
	</span>
	
	<!-- Sources -->
	<xsl:copy-of select="cdr:fieldTitle('Data Sources','dataSrc','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/method/dataColl/sources/dataSrc">
				<span>
					<xsl:attribute name="class">value merge mergeEdataSrc<xsl:value-of select="position()" /></xsl:attribute> 
					<span class="mergeDisplay">
						<xsl:copy-of select="cdr:field(current()/node(),'li','dataSrc',position(),true())" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:copy-of select="cdr:field(current()/node(),'li','dataSrc',position(),true())" />
					</span>
				</span>		
			</xsl:for-each>
		</ol>
	</div>	
	
	<!-- Related Material -->
	<xsl:copy-of select="cdr:fieldTitle('Related Material','relMat','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relMat">
				<span>
					<xsl:attribute name="class">value merge mergeErelMat<xsl:value-of select="position()" /></xsl:attribute> 
					<span class="mergeDisplay">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relMat',position(),true())" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relMat',position(),true())" />
					</span>
				</span>		
			</xsl:for-each>
		</ol>
	</div>	
	
	<!-- Related Publications -->
	<xsl:copy-of select="cdr:fieldTitle('Related Publications','relPubl','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relPubl">
				<span>
					<xsl:attribute name="class">value merge mergeErelPubl<xsl:value-of select="position()" /></xsl:attribute> 
					<span class="mergeDisplay">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relPubl',position(),true())" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relPubl',position(),true())" />
					</span>
				</span>		
			</xsl:for-each>
		</ol>
	</div>	
	
	<!-- Related Studies -->
	<xsl:copy-of select="cdr:fieldTitle('Related Studies','relStdy','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relStdy">
				<span>
					<xsl:attribute name="class">value merge mergeErelStdy<xsl:value-of select="position()" /></xsl:attribute> 
					<span class="mergeDisplay">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relStdy',position(),true())" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:copy-of select="cdr:field(current()/node(),'li','relPubl',position(),true())" />
					</span>
				</span>		
			</xsl:for-each>
		</ol>
	</div>	
	
	<!-- Document Derivation -->
	<div class="valueInline" id="BibliographicCitation">
	<p>This document was derived from:&#160;</p>
		<span class="value merge mergeEdocSrcBib">
			<span class="mergeDisplay">
				<xsl:copy-of select="cdr:field(codeBook/docDscr/docSrc/biblCit/node(),'rich','docSrcBib',1,false())" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="cdr:field(codeBook/docDscr/docSrc/biblCit/node(),'rich','docSrcBib',1,false())" />
			</span>
		</span>
	</div>
		
	</xsl:template>
</xsl:stylesheet>