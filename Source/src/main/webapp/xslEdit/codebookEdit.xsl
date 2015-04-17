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
			<xsl:copy-of select="cdr:schemaDoc($schemaDoc)" />
		</p>
	</xsl:function>
	
	<!-- Schema doc link-->
	<xsl:function name="cdr:schemaDoc">
		<xsl:param name="elementName"/>
		<xsl:if test="$elementName ne ''">
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/<xsl:value-of select="$elementName"/></xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</xsl:if>
	</xsl:function>
	
	<!-- Edit and delete buttons -->
	<xsl:function name="cdr:fieldControls">
		<xsl:param name="fieldName"/>
	    <xsl:param name="index"/>
	    <xsl:param name="delete"/>	
	    <xsl:param name="append"/>	
	    <span class="editButtonControls">
		    <a title="Edit field" class="editIcon2">
		    	<xsl:attribute name="href">edit?f=<xsl:value-of select="$fieldName"/>&amp;i=<xsl:value-of select="$index"/>&amp;a=<xsl:value-of select="$append"/></xsl:attribute>
		    	<i class="fa fa-pencil"></i>
			</a>
			<xsl:if test="$delete">
				<a title="Delete field" class="editIcon2">
					<xsl:attribute name="href">delete?f=<xsl:value-of select="$fieldName"/>&amp;i=<xsl:value-of select="$index"/></xsl:attribute>
					<i class="fa fa-trash"></i>
				</a>
			</xsl:if>
		</span>
	</xsl:function>
	
	<!-- Edit and delete buttons for multi fields -->
	<xsl:function name="cdr:fieldControlsMulti">
		<xsl:param name="fieldName"/>
	    <xsl:param name="index"/>
	    <xsl:param name="delete"/>	
	    <xsl:param name="append"/>	
	    <span class="editButtonControls">
		    <a title="Edit field" class="editIcon2">
		    	<xsl:attribute name="href">editMulti?f=<xsl:value-of select="$fieldName"/>&amp;i=<xsl:value-of select="$index"/>&amp;a=<xsl:value-of select="$append"/></xsl:attribute>
		    	<i class="fa fa-pencil"></i>
			</a>
			<xsl:if test="$delete">
				<a title="Delete field" class="editIcon2">
					<xsl:attribute name="href">delete?f=<xsl:value-of select="$fieldName"/>&amp;i=<xsl:value-of select="$index"/></xsl:attribute>
					<i class="fa fa-trash"></i>
				</a>
			</xsl:if>
		</span>
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
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'true')" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'rich'">
				<xsl:copy-of select="$value" />
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'div'">
				<div class="value2"><xsl:copy-of select="$value" /></div>
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'span'">
				<span class="valueInline"><xsl:copy-of select="$value" /></span>
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'p'">
				<p class="value2"><xsl:copy-of select="$value" /></p>
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
			</xsl:when>
			<xsl:when test="$wrapper eq 'li'">
				<li class="valueInline">
					<xsl:copy-of select="$value" />
					<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
				</li>
			</xsl:when>
			<xsl:when test="$wrapper eq 'url'">
				<a>
					<xsl:attribute name="href"><xsl:value-of select="$value" /></xsl:attribute>
					<xsl:value-of select="$value" />
					<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
				</a>
			</xsl:when>
			<xsl:when test="$wrapper eq 'multi'">
				<xsl:value-of select="$value" />
				<xsl:copy-of select="cdr:fieldControlsMulti($fieldName,$index,$delete,'false')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value" />
				<xsl:copy-of select="cdr:fieldControls($fieldName,$index,$delete,'false')" />
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
			<xsl:choose>
				<xsl:when test="not($value)">
					<xsl:copy-of select="cdr:schemaDoc($schemaElement)"></xsl:copy-of>
					<div class="value2">
						<xsl:variable name="label">Add <xsl:value-of select="$title"/></xsl:variable>
						<xsl:copy-of select="cdr:fieldAdd($fieldName,$label,'Add Element',1)"></xsl:copy-of>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="cdr:fieldControls($fieldName,'1',false(),'false')"/>
					<xsl:copy-of select="cdr:schemaDoc($schemaElement)"></xsl:copy-of>
					<div class="value2"><xsl:copy-of select="$value"></xsl:copy-of></div>
				</xsl:otherwise>
			</xsl:choose>
		</p>	
  	</xsl:function>
  	
  	<!-- Button to add new plural field -->
  	<xsl:function name="cdr:fieldAdd">
  		<xsl:param name="fieldName"/>
  		<xsl:param name="label"/>
  		<xsl:param name="title"/>
	    <xsl:param name="index"/>
		<a>
			<xsl:attribute name="class">	
				editIcon2	
				<xsl:if test="$label ne ''"> editIconText</xsl:if>
			</xsl:attribute>	
			<xsl:attribute name="title"><xsl:value-of select="$title"/></xsl:attribute>
			<xsl:attribute name="href">edit?f=<xsl:value-of select="$fieldName"/>&amp;i=<xsl:value-of select="$index"/>&amp;a=true</xsl:attribute>
			<i class="fa fa-plus"></i><em><xsl:value-of select="$label" /></em>
		</a>
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
					<xsl:copy-of select="cdr:schemaDoc('version')"/>
				</p>
				<p id="DocumentDate" class="value4">
					Document Date:
					<xsl:value-of select="codeBook/docDscr/citation/prodStmt/prodDate" />
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=version</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/prodDate</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>	
				<p id="DocumentProducer" class="value4">
					Codebook prepared by:
					<xsl:for-each select="codeBook/docDscr/citation/prodStmt/producer">
						<xsl:copy-of select="cdr:field(current(),'multi','docProducer',position(),false())" />
					</xsl:for-each>
					<xsl:copy-of select="cdr:fieldAdd('docProducer','','Add producer',count(codeBook/docDscr/citation/prodStmt/producer)+1)" />
					<xsl:copy-of select="cdr:schemaDoc('producer')" />
				</p>
				<p id="Study Producer" class="value4">
					Data prepared by:
					<xsl:for-each select="codeBook/stdyDscr/citation/prodStmt/producer">
						<xsl:copy-of select="cdr:field(current(),'multi','stdyProducer',position(),false())" />
					</xsl:for-each>
					<xsl:copy-of select="cdr:fieldAdd('stdyProducer','','Add producer',count(codeBook/stdyDscr/citation/prodStmt/producer)+1)" />
					<xsl:copy-of select="cdr:schemaDoc('producer')" />
				</p>	
			</div>
		
			<!-- Data Distributed by: -->
			<p class="staticHeader2" id="Distributor">
				Data Distributed by:
				<xsl:copy-of select="cdr:schemaDoc('distrbtr')" />
			</p>
			<xsl:for-each select="/codeBook/stdyDscr/citation/distStmt/distrbtr">
				<p class="value2">
					<xsl:copy-of select="cdr:field(current(),'','distrbtr',position(),true())" />
					<xsl:choose>
						<xsl:when test="current()/@URI ne ''">
							<br />
							<xsl:copy-of select="cdr:field(current()/@URI,'url','distrbtrURL',position(),false())" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="cdr:fieldAdd('distrbtrURL','Add Distributor URL','Add URL',position())" />
						</xsl:otherwise>		
					</xsl:choose>
				
				</p>				
			</xsl:for-each>
			<div class="value2">
				<xsl:copy-of select="cdr:fieldAdd('distrbtr','Add Distributor','Add Distributor',count(codeBook/stdyDscr/citation/prodStmt/producer)+1)" />
			</div>
		</div>
		<!-- Citation -->
		<p class="staticHeader lb3">
   			Citation
   			<xsl:copy-of select="cdr:schemaDoc('biblCit')" />
   		</p>
   		<div class="valueInline value2">
   			<em id="CodebookCitation">Please cite this codebook as:</em><br />
			<xsl:copy-of select="cdr:field(/codeBook/docDscr/citation/biblCit/node(),'rich','docCit',1,false())" />
		</div>
		<div class="valueInline value2">
   			<em id="DataCitation">Please cite this dataset as:</em><br />
			<xsl:copy-of select="cdr:field(/codeBook/stdyDscr/citation/biblCit/node(),'rich','stdyCit',1,false())" />
		</div>
		
		<!-- Abstract -->
   		<xsl:copy-of select="cdr:field2('Abstract',/codeBook/stdyDscr/stdyInfo/abstract/node(),'abstract','abstract','0')" />

		<!-- Datasets TODO: make editable-->
		<p class="staticHeader">
			Datasets
			<xsl:copy-of select="cdr:schemaDoc('fileDscr')" />
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
				</p>
			</xsl:for-each>
			<xsl:if test="count(codeBook/fileDscr) eq 0">
				<p>Sorry, no file information is avalible for this codebook.</p>
			</xsl:if>
		</div>
		
		<!-- Terms of Use-->
		<p class="staticHeader lb2">
			Terms of Use
			<xsl:copy-of select="cdr:schemaDoc('dataAccs')" />
		</p>
			
		<!-- Access Levels-->
		<p id="accessLevels" class="staticHeader hs3">
   			Access Levels <xsl:copy-of select="cdr:schemaDoc('restrctn')" />		
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
			<xsl:copy-of select="cdr:fieldAdd('accessRstrID','Add Access Level','Add Access Level',count(codeBook/stdyDscr/dataAccs)+1)" />
		</div>
		
		<!-- Access Restrictions (Default)-->
   		<xsl:copy-of select="cdr:field2('Access Restrictions (Default)',codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn/node(),'accessRstr','restrctn','1')" />
		
		<!-- Access Requirements -->
   		<xsl:copy-of select="cdr:field2('Access Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/confDec,'confDec','confDec','1')" />
		
		<!-- Access Conditions -->
   		<xsl:copy-of select="cdr:field2('Access Conditions',codeBook/stdyDscr/dataAccs[1]/useStmt/conditions/node(),'accessCond','conditions','1')" />
		
		<!-- Access Permission Requirements  -->
		<xsl:copy-of select="cdr:field2('Access Permission Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm/node(),'accessPermReq','specPerm','1')" />
		
		<!-- Citation Requirements  -->
		<xsl:copy-of select="cdr:field2('Citation Requirements',codeBook/stdyDscr/dataAccs[1]/useStmt/citReq/node(),'citReq','citReq','1')" />
		
		<!-- Disclaimer -->
		<xsl:copy-of select="cdr:field2('Disclaimer',codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer/node(),'disclaimer','disclaimer','1')" />
		
		<!-- Contact -->
		<xsl:copy-of select="cdr:field2('Contact',codeBook/stdyDscr/dataAccs[1]/useStmt/contact/node(),'contact','contact','1')" />

	<!--Additional Info-->	
	<p class="staticHeader lb2">
		Additional Information	
	</p>	
	
	<!-- Methodology -->
	<xsl:copy-of select="cdr:field2('Methodology',/codeBook/stdyDscr/method/dataColl/collMode/node(),'method','method','1')" />
	
	<!-- Sources -->
	<xsl:copy-of select="cdr:fieldTitle('Data Sources','dataSrc','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/method/dataColl/sources/dataSrc">
				<xsl:copy-of select="cdr:field(current()/node(),'li','relMat',position(),true())" />
			</xsl:for-each>
		</ol>
		<xsl:copy-of select="cdr:fieldAdd('sources','Add Source','Add field',count(codeBook/stdyDscr/method/dataColl/sources/dataSrc)+1)" />
	</div>	
	
	<!-- Related Material -->
	<xsl:copy-of select="cdr:fieldTitle('Related Material','relMat','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relMat">
				<xsl:copy-of select="cdr:field(current()/node(),'li','relMat',position(),true())" />
			</xsl:for-each>
		</ol>
		<xsl:copy-of select="cdr:fieldAdd('relMat','Add Material','Add field',count(codeBook/stdyDscr/othrStdyMat/relMat)+1)" />
	</div>	
	
	<!-- Related Publications -->
	<xsl:copy-of select="cdr:fieldTitle('Related Publications','relPubl','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relPubl">
				<xsl:copy-of select="cdr:field(current()/node(),'li','relPubl',position(),true())" />
			</xsl:for-each>
		</ol>
		<xsl:copy-of select="cdr:fieldAdd('relPubl','Add Publication','Add field',count(codeBook/stdyDscr/othrStdyMat/relPubl)+1)" />
	</div>	
	
	<!-- Related Studies -->
	<xsl:copy-of select="cdr:fieldTitle('Related Studies','relStdy','1')" />
	<div class="value2">
		<ol>
			<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relStdy">
				<xsl:copy-of select="cdr:field(current()/node(),'li','relStdy',position(),true())" />
			</xsl:for-each>
		</ol>
		<xsl:copy-of select="cdr:fieldAdd('relStdy','Add Study','Add field',count(codeBook/stdyDscr/othrStdyMat/relStdy)+1)" />
	</div>	
	
	<!-- Document Derivation -->
	<div class="valueInline" id="BibliographicCitation">
	<p>This document was derived from:&#160;</p>
		<xsl:copy-of select="cdr:field(codeBook/docDscr/docSrc/biblCit/node(),'rich','docSrcBib',1,false())" />
		<xsl:copy-of select="cdr:schemaDoc('biblCit')" />&#160;
	</div>
		
	</xsl:template>
</xsl:stylesheet>