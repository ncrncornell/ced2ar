<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<div class="lb2">
			<em>
				Last update to metadata:
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
			</em>	
		</div>
		
		<!-- Abstract -->
		<xsl:if test="codeBook/stdyDscr/stdyInfo/abstract != ''">
			<div class="lb">
				<h3>Abstract</h3>
				<xsl:copy-of select="codeBook/stdyDscr/stdyInfo/abstract/node()" />
			</div>
		</xsl:if>
		
		<!-- File info -->
		<div class="lb">
			<h3>File Information</h3>
			<xsl:for-each select="codeBook/fileDscr">
				<p>
					<xsl:value-of select="@ID" />
					&#160;
					URI: <xsl:value-of select="@URI" />
					&#160;(<xsl:value-of select="fileTxt/fileType" />)
				</p>
			</xsl:for-each>
			<xsl:if test="count(codeBook/fileDscr) eq 0">
				<p>Sorry, no file information is avalible for this codebook.</p>
			</xsl:if>
		</div>
		
		<!-- Methodology -->
		<div class="lb">
			<xsl:if test="codeBook/stdyDscr/method/dataColl/collMode  != ''">
				<div class="lb2">
					<xsl:copy-of select="codeBook/stdyDscr/method/dataColl/collMode/node()" />
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/method/dataColl/sources/dataSrc  != ''">
					<div>
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
		
		<!-- Other Materials -->
		<div class="lb">
			<xsl:if test="codeBook/stdyDscr/othrStdyMat/relMat != ''">
				<div class="lb2">
					<h3>Related Materials</h3>
					<xsl:copy-of select="codeBook/stdyDscr/othrStdyMat/relMat/node()"></xsl:copy-of>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/othrStdyMat/relPubl != ''">
				<div class="lb2">
					<h3>Related Publications</h3>
					<xsl:copy-of select="codeBook/stdyDscr/othrStdyMat/relPubl/node()"></xsl:copy-of>
				</div>
			</xsl:if>
			<xsl:if test="codeBook/stdyDscr/othrStdyMat/relStdy != ''">
				<div class="lb2">
					<h3>Related Studies</h3>
					<xsl:copy-of select="codeBook/stdyDscr/othrStdyMat/relStdy/node()"></xsl:copy-of>
				</div>
			</xsl:if>
		</div>
		<!-- Variables -->
		<h3>Variables</h3>
			<xsl:for-each select="codeBook/dataDscr/var">
				<div class="lb2">
						<p>
							<em>Name:&#160;</em><xsl:value-of select="@name"></xsl:value-of>
						</p>
						<xsl:if test="labl != ''">
							<p><em>Label:</em>&#160;<xsl:value-of select="labl"></xsl:value-of></p>
						</xsl:if>
						<xsl:if test="varFormat/@type != ''">
							<p><em>Type:</em>&#160;<xsl:value-of select="varFormat/@type"></xsl:value-of></p>
						</xsl:if>
						<xsl:if test="txt != ''">
							<p><em>Description:</em>&#160;<xsl:value-of select="txt"></xsl:value-of></p>
						</xsl:if>	
						<xsl:if test="count(notes) gt 0">
							<p><em>Notes:</em> </p>
							<div>			
								<xsl:for-each select="notes">
								<p class="lb2"> 
									#<xsl:value-of select="position()" />: <xsl:value-of select="current()" />
								</p>
								</xsl:for-each>
							</div>
						</xsl:if>		
				</div>			
			</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>