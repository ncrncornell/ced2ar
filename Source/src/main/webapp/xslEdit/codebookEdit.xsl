<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<div class="lb2">
			<p class="value4">
				Last update to metadata:
				<xsl:variable name='handle'>
					<xsl:value-of select="/codeBook/@handle" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="/codeBook/docDscr/citation/verStmt/version/@date != ''">
						<xsl:value-of select="/codeBook/docDscr/citation/verStmt/version/@date" />
					</xsl:when>
					<xsl:otherwise>
						unavailable
					</xsl:otherwise>
				</xsl:choose>
				<a title="Schema Documentation" class="schemaDocLink baseURIa">
					<xsl:attribute name="href">/schema/doc/version</xsl:attribute>
					<i class="fa fa-info-circle"></i>
				</a>
			</p>
			<xsl:choose>
				<xsl:when test="/codeBook/docDscr/citation/prodStmt/prodDate != ''">
					<p class="value4">
						Document Date:
						<xsl:value-of select="/codeBook/docDscr/citation/prodStmt/prodDate" />
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=version</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/prodDate</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:when test="not(/codeBook/docDscr/citation/prodStmt/prodDate)">
					<p class="value4">
						Document Date:
						<a title="Add document date" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=version&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
								<em>Add document date</em>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/prodDate</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p class="value4">
						Document Date:
						<em>No value entered</em>&#160;
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=version</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/prodDate</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="/codeBook/docDscr/citation/prodStmt/producer != ''">
					<p class="value4">
						Codebook prepared by:
						<xsl:for-each select="/codeBook/docDscr/citation/prodStmt/producer">
							<xsl:value-of select="current()" />
							<a title="Edit field" class="editIcon2">
								<xsl:attribute name="href">edit?f=docProducer&amp;i=<xsl:value-of
									select="position()" /></xsl:attribute>
								<i class="fa fa-pencil"></i>
							</a>
						</xsl:for-each>
						<a title="Add New Producer" class="editIcon2">
							<xsl:attribute name="href">edit?f=docProducer&amp;i=<xsl:value-of
								select="position()" />&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:when test="not(/codeBook/docDscr/citation/prodStmt/producer)">
					<p class="value4">
						Codebook prepared by:
						<em>No value entered</em>&#160;
						<a title="Add field" class="editIcon2">
							<xsl:attribute name="href">edit?f=docProducer&amp;a=true&amp;i=1</xsl:attribute>
							<i class="fa fa-plus"></i>
						</a>template
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p class="value4">
						Codebook prepared by:
						<em>No value entered</em>&#160;
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=docProducer</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="/codeBook/stdyDscr/citation/prodStmt/producer != ''">
					<p class="value4">
						Data prepared by:
						<xsl:for-each select="/codeBook/stdyDscr/citation/prodStmt/producer">
							<xsl:value-of select="current()" />
							<a title="Edit field" class="editIcon2">
								<xsl:attribute name="href">edit?f=stdyProducer&amp;i=<xsl:value-of
									select="position()" /></xsl:attribute>
								<i class="fa fa-pencil"></i>
							</a>
						</xsl:for-each>
						<a title="Add New Producer" class="editIcon2">
							<xsl:attribute name="href">edit?f=stdyProducer&amp;i=<xsl:value-of
								select="position()" />&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:when test="not(codeBook/stdyDscr/citation/prodStmt/producer)">
					<p class="value4">
						Data prepared by:
						<em>No value entered</em>&#160;
						<a title="Add Producer" class="editIcon2">
							<xsl:attribute name="href">edit?f=stdyProducer&amp;i=<xsl:value-of
								select="position()" />&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p class="value4">
						Data prepared by:
						<em>No value entered</em>&#160;
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=stdyProducer&amp;i=1</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/producer</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
				</xsl:otherwise>
			</xsl:choose>
			<div class="lb" />
			<xsl:choose>
				<xsl:when test="/codeBook/stdyDscr/citation/distStmt/distrbtr != ''">
					<p class="staticHeader2">
						Data Distributed by:
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/distrbtr</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<xsl:for-each select="/codeBook/stdyDscr/citation/distStmt/distrbtr">
						<p class="value2">
							<xsl:value-of select="current()" />
							<a title="Edit field" class="editIcon2">
								<xsl:attribute name="href">edit?f=distrbtr&amp;i=<xsl:value-of
									select="position()" /></xsl:attribute>
								<i class="fa fa-pencil"></i>
							</a>

							<xsl:choose>
								<xsl:when test="not(current()/@URI)">
									<a title="Add Distributor URL" class="editIcon2 editIconText">
										<xsl:attribute name="href">edit?f=distrbtrURL&amp;i=<xsl:value-of
											select="position()" />&amp;a=true</xsl:attribute>
										<i class="fa fa-plus"></i>
										<em>Add distributor URL</em>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<br />
									<a>
										<xsl:attribute name="href"><xsl:value-of
											select="current()/@URI" /></xsl:attribute>
										<xsl:value-of select="current()/@URI" />
									</a>
									<a title="Edit field" class="editIcon2">
										<xsl:attribute name="href">edit?f=distrbtrURL&amp;i=<xsl:value-of
											select="position()" /></xsl:attribute>
										<i class="fa fa-pencil"></i>
									</a>
								</xsl:otherwise>
							</xsl:choose>
						</p>
					</xsl:for-each>
					<div class="value2">
						<a title="Add New Distributor" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=distrbtr&amp;i=<xsl:value-of
								select="position()" />&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
							<em>Add new distributor</em>
						</a>
					</div>
				</xsl:when>
				<xsl:when test="not(/codeBook/stdyDscr/citation/distStmt/distrbtr)">
					<p class="staticHeader2">
						Data Distributed by:
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/distrbtr</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<p class="value2">
						<a title="Add Distributor" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=distrbtr&amp;i=<xsl:value-of
								select="position()" />&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
							<em>Add distributor</em>
						</a>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p class="staticHeader2">
						Data Distributed by:
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/distrbtr</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<p class="value2">
						<em>No value entered</em>&#160;
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=distrbtr&amp;i=<xsl:value-of
								select="position()" /></xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<p class="staticHeader">
			Citation
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/biblCit</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>
		<xsl:choose>
			<xsl:when test="/codeBook/docDscr/citation/biblCit != ''">
				<div class="value2">
					<em>Please cite this codebook as:</em>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=docCit</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<br />
					<xsl:copy-of select="/codeBook/docDscr/citation/biblCit/node()" />
					<xsl:if test="/codeBook/docDscr/citation/biblCit/ExtLink != ''">
						<xsl:value-of select="/codeBook/docDscr/citation/biblCit/ExtLink" />
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=docCitURL</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</xsl:if>
					<xsl:copy-of select="/codeBook/docDscr/citation/biblCit/ExtLink/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/docDscr/citation/biblCit)">
				<p class="value2">
					<em>Please cite this codebook as:</em>
					<br />
					<a title="Add field" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=docCit&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add citation</em>
					</a>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p class="value2">
					<em>Please cite this codebook as:</em>
					<br />
					<em>No value entered</em>&#160;
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=docCit</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</p>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/citation/biblCit != ''">
				<div class="value2">
					<em>Please cite this dataset as:</em>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=stdyCit</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<br />
					<xsl:copy-of select="/codeBook/stdyDscr/citation/biblCit/node()" />
					<xsl:if test="/codeBook/stdyDscr/citation/biblCit/ExtLink != ''">
						<xsl:copy-of select="/codeBook/stdyDscr/citation/biblCit/ExtLink" />
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=stdyCitURL</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/citation/biblCit)">
				<p class="value2">
					<em>Please cite this dataset as:</em>
					<br />
					<a title="Add field" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=stdyCit&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add citation</em>
					</a>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p class="value2">
					<em>Please cite this dataset as:</em>
					<br />
					<em>No value entered</em>&#160;
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=stdyCit</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</p>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/stdyInfo/abstract != ''">
				<p class="staticHeader">
					Abstract
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=abstract</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/abstract</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/stdyInfo/abstract/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/stdyInfo/abstract)">
				<p class="staticHeader">
					Abstract
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/abstract</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
					<div class="value2">

						<a title="Add field" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=abstract&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
							<em>Add abstract</em>
						</a>
					</div>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader">
					Abstract
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=abstract</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/abstract</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<p class="staticHeader">
			Datasets
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/fileDscr</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
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
							(No hyperlink available)
						</xsl:otherwise>
					</xsl:choose>
					&#160;(
					<xsl:value-of select="fileTxt/fileType" />
					)
				</p>
			</xsl:for-each>
			<xsl:if test="count(/codeBook/fileDscr) eq 0">
				<p>Sorry, no file information is avalible for this codebook.</p>
			</xsl:if>
		</div>
		<p class="staticHeader lb2 tcs">
			Terms of Use
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/dataAccs</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>
		<p id="accessLevels" class="staticHeader hs3">
			Access Levels
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/restrctn</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>			
		<div class="value2">
			<p>
				<em>undefined</em>
			</p>
			<p class="lb2">
				Elements flagged with undefined have not yet been reviewed for release
			</p>
			<xsl:for-each select="codeBook/stdyDscr/dataAccs[@ID]">
				<div class="lb2">
				<xsl:choose>
						<xsl:when test="current()/useStmt/restrctn/node() != ''">
							<p>
								<em><xsl:value-of select="current()/@ID" /></em>
							</p>
							<span class="valueInline">
								<xsl:copy-of select="current()/useStmt/restrctn/node()" />			
							</span>		
							<a title="Edit field" class="editIcon2">
								<xsl:attribute name="href">edit?f=accessRstr&amp;i=<xsl:value-of select="position()+1 "/></xsl:attribute>
								<i class="fa fa-pencil"></i>
							</a>			
						</xsl:when>
					<xsl:otherwise>
					<p>
						<em><xsl:value-of select="current()/@ID" /></em><br /><em>No description given</em>
						<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessRstr&amp;i=<xsl:value-of select="position() "/>&amp;a=true</xsl:attribute>
						<i class="fa fa-pencil"></i>
						</a>
					</p>	
					</xsl:otherwise>
				</xsl:choose>
				</div>
			</xsl:for-each>
		</div>
		<div class="value2">
			<a title="Add Access Level" class="editIcon2 editIconText">
				<xsl:attribute name="href">edit?f=accessRstrID&amp;i=<xsl:value-of select="count(/codeBook/stdyDscr/dataAccs)+1"/>&amp;a=true</xsl:attribute>
				<i class="fa fa-plus"></i>
				<em>Add Access Level</em>
			</a>
		</div>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn != ''">
				<p class="staticHeader hs3">
					Access Restrictions (Default)
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessRstr&amp;i=1</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/restrctn</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn/node()" />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Access Restrictions (Default)
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/restrctn</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Restrictions" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=accessRstr&amp;a=true&amp;i=1</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add restrictions</em>
					</a>
				</div>
			</xsl:otherwise>
		</xsl:choose>
			<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec != ''">
				<p class="staticHeader hs3">
					Access Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=confDec</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/confDec</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/node()" />
					<br />
					<xsl:variable name='useStmtURI'
						select='/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/@URI'></xsl:variable>
					<xsl:if test="$useStmtURI != ''">
						<em>Additional information: </em>
						<a>
							<xsl:attribute name="href"><xsl:value-of
								select="$useStmtURI" /></xsl:attribute>
							<xsl:value-of select="$useStmtURI" />
						</a>
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=confDecURL</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec)">
				<p class="staticHeader hs3">
					Access Requirements
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/confDec</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Requirements" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=confDec&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add requirements</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Access Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=confDec</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/confDec</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/conditions != ''">
				<p class="staticHeader hs3">
					Access Conditions
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessCond</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/conditions</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of
						select="/codeBook/stdyDscr/dataAccs[1]/useStmt/conditions/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/conditions)">
				<p class="staticHeader hs3">
					Access Conditions
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/conditions</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">

					<a title="Add field" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=accessCond&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add conditions</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Access Conditions
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessCond</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/conditions</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm != ''">
				<p class="staticHeader hs3">
					Access Permission Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessPermReq</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/specPerm</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm)">
				<p class="staticHeader hs3">
					Access Permission Requirements
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/specPerm</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">

					<a title="Add Permission Requirements" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=accessPermReq&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add permission requirements</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Access Permission Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=accessPermReq</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/specPerm</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/citReq != ''">
				<p class="staticHeader hs3">
					Citation Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=citReq</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/citReq</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/citReq/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/citReq)">
				<p class="staticHeader hs3">
					Citation Requirements
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/citReq</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Citation Requirements" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=citReq&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add citation requirements</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Citation Requirements
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=citReq</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/citReq</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer != ''">
				<p class="staticHeader hs3">
					Disclaimer
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=disclaimer</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/disclaimer</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer)">
				<p class="staticHeader hs3">
					Disclaimer
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/disclaimer</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">

					<a title="Add Disclaimer" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=disclaimer&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add disclaimer</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Disclaimer
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=disclaimer</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/disclaimer</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/stdyDscr/dataAccs[1]/useStmt/contact != ''">
				<p class="staticHeader hs3">
					Contact
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=contact</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/contact</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					For questions regarding this data collection, please contact:
					<xsl:copy-of select="/codeBook/stdyDscr/dataAccs[1]/useStmt/contact/node()" />
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/dataAccs[1]/useStmt/contact)">
				<p class="staticHeader hs3">
					Contact
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/contact</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Contact" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=contact&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add contact</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Contact
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=contact</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/contact</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:variable name='relMat'>
			<xsl:value-of select="/codeBook/stdyDscr/othrStdyMat/relMat" />
		</xsl:variable>
		<xsl:variable name='relPubl'>
			<xsl:value-of select="/codeBook/stdyDscr/othrStdyMat/relPubl" />
		</xsl:variable>
		<xsl:variable name='relStdy'>
			<xsl:value-of select="/codeBook/stdyDscr/othrStdyMat/relStdy" />
		</xsl:variable>
		<xsl:variable name='collMode'>
			<xsl:value-of select="/codeBook/stdyDscr/method/dataColl/collMode" />
		</xsl:variable>
		<xsl:variable name='dataSrc'>
			<xsl:value-of select="/codeBook/stdyDscr/method/dataColl/sources/dataSrc" />
		</xsl:variable>

		<p class="staticHeader lb2">Additional Information</p>
		<div class="value2">
			<xsl:choose>
				<xsl:when test="$collMode  != ''">
					<p class="staticHeader lb3">
						Methodology
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=method</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/method</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<div class="tab lb">
						<xsl:copy-of select="/codeBook/stdyDscr/method/dataColl/collMode/node()" />
					</div>
				</xsl:when>
				<xsl:when test="not(/codeBook/stdyDscr/method/dataColl/collMode)">
					<p class="staticHeader">
						Methodology
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/method</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<div class="value2">
						<a title="Add Methodology" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=method&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
							<em>Add methodology</em>
						</a>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<p class="staticHeader lb3">
						Methodology
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=method</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/method</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<p class="tab lb2">
						<em>No value entered</em>
					</p>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$dataSrc != ''">
					<p class="staticHeader">
						Sources
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/dataSrc</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<ol class="value2">
						<xsl:for-each select="/codeBook/stdyDscr/method/dataColl/sources/dataSrc">
							<li>
								<span class="valueInline">
									<xsl:copy-of select="current()/node()" />
								</span>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">edit?f=sources&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
								<a title="Delete field" class="editIcon2">
									<xsl:attribute name="href">delete?f=sources&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-trash"></i>
								</a>
							</li>
						</xsl:for-each>						
					</ol>
					<a title="Add New Source" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=sources&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add new source</em>
					</a>
				</xsl:when>
				<xsl:when test="not(/codeBook/stdyDscr/method/dataColl/sources/dataSrc)">
					<p class="staticHeader">
						Sources
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/dataSrc</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<div class="value2">
						<a title="Add Sources" class="editIcon2 editIconText">
							<xsl:attribute name="href">edit?f=sources&amp;a=true</xsl:attribute>
							<i class="fa fa-plus"></i>
							<em>Add sources</em>
						</a>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<p class="staticHeader">
						Sources
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=method&amp;i=1</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
						<a title="Schema Documentation" class="schemaDocLink baseURIa">
							<xsl:attribute name="href">/schema/doc/dataSrc</xsl:attribute>
							<i class="fa fa-info-circle"></i>
						</a>
					</p>
					<em class="value2">No value entered</em>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:choose>
			<xsl:when test="$relMat  != ''">
				<p class="staticHeader hs3">
					Related Material
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relMat</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<ol>
						<xsl:for-each select="/codeBook/stdyDscr/othrStdyMat/relMat">
							<li>
								<span class="valueInline">
									<xsl:copy-of select="current()/node()" />
								</span>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">edit?f=relMat&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
								<a title="Delete field" class="editIcon2">
									<xsl:attribute name="href">delete?f=relMat&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-trash"></i>
								</a>
							</li>
						</xsl:for-each>						
					</ol>
					<a title="Add Related Material" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relMat&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related material</em>
					</a>
					
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/othrStdyMat/relMat)">
				<p class="staticHeader hs3">
					Related Material
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relMat</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Related Materials" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relMat&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related materials</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Related Material
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relMat</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<p>
						<em>No value entered</em>&#160;
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">edit?f=relMat&amp;i=1</xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</p>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$relPubl  != ''">
				<p class="staticHeader hs3">
					Related Publications
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relPubl</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<ol>
						<xsl:for-each select="/codeBook/stdyDscr/othrStdyMat/relPubl">
							<li>
								<span class="valueInline">
									<xsl:copy-of select="current()/node()" />
								</span>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">edit?f=relPubl&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
								<a title="Delete field" class="editIcon2">
									<xsl:attribute name="href">delete?f=relPubl&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-trash"></i>
								</a>
							</li>
						</xsl:for-each>						
					</ol>
					<a title="Add Related Publication" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relPubl&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related publication</em>
					</a>
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/othrStdyMat/relPubl)">
				<p class="staticHeader hs3">
					Related Publications
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relPubl</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Related Publication" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relPubl&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related publication</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Related Publications
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relPubl</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>&#160;
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=relPubl&amp;i=1</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$relStdy  != ''">
				<p class="staticHeader hs3">
					Related Studies
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relStdy</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<ol>
						<xsl:for-each select="/codeBook/stdyDscr/othrStdyMat/relStdy">
							<li>
								<span class="valueInline">
									<xsl:copy-of select="current()/node()" />
								</span>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">edit?f=relStdy&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
								<a title="Delete field" class="editIcon2">
									<xsl:attribute name="href">delete?f=relStdy&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-trash"></i>
								</a>
							</li>
						</xsl:for-each>						
					</ol>
					<a title="Add Related Studies" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relStdy&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related studies</em>
					</a>
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/stdyDscr/othrStdyMat/relStdy)">
				<p class="staticHeader hs3">
					Related Studies
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relStdy</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<a title="Add Related Study" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=relStdy&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add related study</em>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p class="staticHeader hs3">
					Related Studies
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/relStdy</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
				<div class="value2">
					<em>No value entered</em>&#160;
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=relStdy&amp;i=1</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="/codeBook/docDscr/docSrc/biblCit  != ''">
				<div>
					This documentation derived from:
					<span class="valueInline">
						<xsl:copy-of select="/codeBook/docDscr/docSrc/biblCit/node()" />
					</span>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=docSrcBib&amp;i=<xsl:value-of
							select="position()" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/biblCit</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</div>
			</xsl:when>
			<xsl:when test="not(/codeBook/docDscr/docSrc/biblCit)">
				<p>
					This documentation derived from:
					<a title="Add Derivation" class="editIcon2 editIconText">
						<xsl:attribute name="href">edit?f=docSrcBib&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
						<em>Add derivation</em>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/biblCit</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p>
					This documentation derived from:
					<em>No value entered</em>&#160;
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">edit?f=docSrcBib&amp;i=<xsl:value-of
							select="position()" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/biblCit</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>