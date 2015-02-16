<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<div class="lb2">
			<p class="value4">
				Last update to metadata:
				<xsl:variable name='handle'>
					<xsl:value-of select="/codeBook/@handle" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="/codeBook/docDscr/citation/verStmt/version/@date != ''">
						<span itemprop="dateModified">
							<xsl:value-of select="/codeBook/docDscr/citation/verStmt/version/@date" />
						</span>
					</xsl:when>
					<xsl:otherwise>
						unavailable
					</xsl:otherwise>
				</xsl:choose>
			</p>
			<xsl:if test="codeBook/docDscr/citation/prodStmt/prodDate != ''">
				<p class="value4">
					Document Date:
					<xsl:value-of select="codeBook/docDscr/citation/prodStmt/prodDate" />
				</p>
			</xsl:if>
			<div class="lb2" />
			<xsl:if test="codeBook/docDscr/citation/prodStmt/producer != ''">
				<p class="value4">
					Codebook prepared by:
					<span itemprop="publisher">
						<xsl:value-of select="codeBook/docDscr/citation/prodStmt/producer" />
					</span>
				</p>
				<div class="lb2" />
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/citation/prodStmt/producer != ''">
				<p class="value4">
					Data prepared by:
					<xsl:for-each select="codeBook/stdyDscr/citation/prodStmt/producer">
						<span itemprop="author">
							<xsl:value-of select="current()" />
							<xsl:if
								test="count(/codeBook/stdyDscr/citation/prodStmt/producer) gt 1">
								<xsl:if
									test="position() lt count(/codeBook/stdyDscr/citation/prodStmt/producer) -1">
									,&#160;
								</xsl:if>
								<xsl:if
									test="position() eq count(/codeBook/stdyDscr/citation/prodStmt/producer) -1">
									,&#160;and&#160;
								</xsl:if>
							</xsl:if>
						</span>
					</xsl:for-each>
				</p>
			</xsl:if>
			<div class="lb" />
			<xsl:if test="codeBook/stdyDscr/citation/distStmt/distrbtr != ''">
				<p class="staticHeader2">Data Distributed by:</p>
				<xsl:for-each select="codeBook/stdyDscr/citation/distStmt/distrbtr">
					<p class="value2">
						<xsl:value-of select="current()" />
						<br />
						<a>
							<xsl:attribute name="href"><xsl:value-of
								select="current()/@URI" /></xsl:attribute>
							<xsl:value-of select="current()/@URI" />
						</a>
					</p>
				</xsl:for-each>
			</xsl:if>
		</div>
		<xsl:variable name='DbiblCit'>
			<xsl:value-of select="codeBook/docDscr/citation/biblCit" />
		</xsl:variable>
		<xsl:variable name='SbiblCit'>
			<xsl:value-of select="codeBook/stdyDscr/citation/biblCit" />
		</xsl:variable>
		<xsl:if test="$DbiblCit != '' or $SbiblCit !=''">
			<p class="staticHeader">Citation</p>
		</xsl:if>
		<xsl:if test="$DbiblCit != ''">
			<p class="value2">
				<em>Please cite this codebook as:</em>
				<br />
				<xsl:value-of select="/$DbiblCit" />
				<xsl:copy-of select="/$DbiblCit/ExtLink/node()" />
			</p>
		</xsl:if>
		<xsl:if test="$SbiblCit != ''">
			<p class="value2">
				<em>Please cite this dataset as:</em>
				<br />
				<span itemprop="citation">
					<xsl:value-of select="$SbiblCit" />
				</span>
			</p>
		</xsl:if>
		<xsl:if test="codeBook/stdyDscr/stdyInfo/abstract != ''">
			<p class="staticHeader">Abstract</p>
			<xsl:variable name='abstract'>
				<span itemprop="description">
					<xsl:copy-of select="codeBook/stdyDscr/stdyInfo/abstract/node()" />
				</span>
			</xsl:variable>
			<span class="hidden printRemove" itemprop="about"><xsl:value-of select="$abstract" /></span>
			<p class="value2 ">
				<xsl:choose>
					<xsl:when test="string-length($abstract) > 400">
						<xsl:value-of select="substring($abstract,0,400)" />
						<span class="truncTxt">
							<xsl:value-of select="substring($abstract,400,string-length($abstract))" />
						</span>
						<span class="truncExp"> ... more</span>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$abstract" />
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		<div class="printLB"></div>
		<p class="toggleHeader">Datasets</p>
		<div class="toggleContent">
			<div class="toggleText">
				<xsl:for-each select="codeBook/fileDscr">
					<p>
						<xsl:value-of select="@ID" />
						&#160;
						<xsl:choose>
							<xsl:when test="not(contains(@URI,'http')) and @URI != ''">
								<xsl:value-of select="fileTxt/fileName" />
								(Incomplete URL provided)
							</xsl:when>
							<xsl:when test="string-length(@URI) gt 0 ">
								<a itemprop="distribution">
									<xsl:attribute name="href"><xsl:value-of
										select="@URI" /></xsl:attribute>
									<xsl:value-of select="fileTxt/fileName" />
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="fileTxt/fileName" />
								(No hyperlink available)
							</xsl:otherwise>
						</xsl:choose>
						&#160;(
						<xsl:value-of select="fileTxt/fileType" />
						)
					</p>
				</xsl:for-each>
				<xsl:if test="count(codeBook/fileDscr) eq 0">
					<p>Sorry, no file information is avalible for this codebook.</p>
				</xsl:if>
			</div>
		</div>
		<div class="printLB"></div>
		<p class="toggleHeader lb2 tcs">Terms of Use</p>
		<div class="toggleContent">
			<xsl:if test="not(codeBook/stdyDscr/dataAccs[1]/*[normalize-space()])">
				<p>No documentation avalible for terms of use</p>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs/@ID">
				<p id="accessLevels" class="toggleHeader hs2">Access Levels</p>
				<div class="toggleContent">
					<div class="toggleText">
							<!--  
							<p>
								<em>undefined</em>
							</p>
							<p class="lb2">
								Elements flagged with undefined have not yet been reviewed for release
							</p>
							-->
						<xsl:for-each select="codeBook/stdyDscr/dataAccs[@ID]">
							<p>
								<em><xsl:value-of select="current()/@ID" /></em>
							</p>
							<div class="lb2">
								<xsl:choose>
									<xsl:when test="current()/useStmt/restrctn/node() != ''">
										<xsl:copy-of select="current()/useStmt/restrctn/node()" />
									</xsl:when>
									<xsl:otherwise><em>No description given</em></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn != ''">
				<p class="toggleHeader hs2">Access Restrictions (Default)</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of select="codeBook/stdyDscr/dataAccs[1]/useStmt/restrctn/node()" />
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/confDec != ''">
				<p class="toggleHeader hs2">Access Requirements</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of select="codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/node()" />
						<br />
						<xsl:variable name='useStmtURI'
							select='codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/@URI'></xsl:variable>
						<xsl:if test="$useStmtURI != ''">
							<em>Additional information: </em>
							<a>
								<xsl:attribute name="href"><xsl:value-of
									select="$useStmtURI" /></xsl:attribute>
								<xsl:value-of select="$useStmtURI" />
							</a>
						</xsl:if>
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/conditions != ''">
				<p class="toggleHeader hs2">Access Conditions</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of
							select="codeBook/stdyDscr/dataAccs[1]/useStmt/conditions/node()" />
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm != ''">
				<p class="toggleHeader hs2">Access Permission Requirements</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of select="codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm/node()" />
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/citReq != ''">
				<p class="toggleHeader hs2">Citation Requirements</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of select="codeBook/stdyDscr/dataAccs[1]/useStmt/citReq/node()" />
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer != ''">
				<p class="toggleHeader hs2">Disclaimer</p>
				<div class="toggleContent">
					<div class="toggleText">
						<xsl:copy-of
							select="codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer/node()" />
					</div>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/dataAccs[1]/useStmt/contact != ''">
				<p class="toggleHeader hs2">Contact</p>
				<div class="toggleContent">
					<div class="toggleText">
						For questions regarding this data collection, please contact:
						<xsl:copy-of select="codeBook/stdyDscr/dataAccs[1]/useStmt/contact/node()" />
					</div>
				</div>
			</xsl:if>
		</div>
		<xsl:variable name='relMat'>
			<xsl:value-of select="codeBook/stdyDscr/othrStdyMat/relMat" />
		</xsl:variable>
		<xsl:variable name='relPubl'>
			<xsl:value-of select="codeBook/stdyDscr/othrStdyMat/relPubl" />
		</xsl:variable>
		<xsl:variable name='relStdy'>
			<xsl:value-of select="codeBook/stdyDscr/othrStdyMat/relStdy" />
		</xsl:variable>
		<xsl:variable name='collMode'>
			<xsl:value-of select="codeBook/stdyDscr/method/dataColl/collMode" />
		</xsl:variable>
		<xsl:variable name='dataSrc'>
			<xsl:value-of select="codeBook/stdyDscr/method/dataColl/dataSrc" />
		</xsl:variable>
		<xsl:if
			test="$relMat != '' or $relPubl !='' or $relStdy !='' or $collMode  != '' or $dataSrc != ''">
			<p class="toggleHeader">Additional Information</p>
			<div class="toggleContent value2">
				<xsl:if test="$collMode  != '' or $dataSrc != ''">
					<p class="toggleHeader">Methodology</p>
					<div class="toggleContent">
						<xsl:if test="codeBook/stdyDscr/method/dataColl/collMode  != ''">
							<div class="toggleText2 lb2">
								<xsl:copy-of select="codeBook/stdyDscr/method/dataColl/collMode/node()" />
							</div>
						</xsl:if>
						<xsl:if test="codeBook/stdyDscr/method/dataColl/sources/dataSrc  != ''">
							<div class="toggleText2">
								<p>Sources</p>
								<ol>
									<xsl:for-each
										select="codeBook/stdyDscr/method/dataColl/sources/dataSrc">
										<li>
											<xsl:copy-of select="current()" />
										</li>
									</xsl:for-each>
								</ol>
							</div>
						</xsl:if>
					</div>
				</xsl:if>
				<xsl:if test="$relMat  != ''">
					<p class="toggleHeader">Related Material</p>
					<div class="toggleContent">
						<div class="toggleText2">
							<ol>
								<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relMat">
									<li>
										<xsl:copy-of select="current()" />
									</li>
								</xsl:for-each>
							</ol>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="$relPubl  != ''">
					<p class="toggleHeader">Related Publications</p>
					<div class="toggleContent">
						<div class="toggleText2">
							<ol>
								<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relPubl">
									<li>
										<xsl:copy-of select="current()" />
									</li>
								</xsl:for-each>
							</ol>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="$relStdy  != ''">
					<p class="toggleHeader">Related Studies</p>
					<div class="toggleContent">
						<div class="toggleText2">
							<ol>
								<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relStdy">
									<li>
										<xsl:copy-of select="current()" />
									</li>
								</xsl:for-each>
							</ol>
						</div>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
		<xsl:if test="codeBook/docDscr/docSrc/biblCit  != ''">
			<p>
				This documentation derived from:
				<br />
				<xsl:value-of select="codeBook/docDscr/docSrc/biblCit" />
			</p>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>