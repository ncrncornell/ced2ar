<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<!-- All the /codeBook/docDscr elements that were in codebook.xsl  -->
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
					<xsl:for-each select="codeBook/docDscr/citation/prodStmt/producer">
						<span itemprop="publisher">
							<xsl:value-of select="current()" />
							<xsl:if test="count(/codeBook/docDscr/citation/prodStmt/producer) gt 1">
								<xsl:if test="position() lt count(/codeBook/docDscr/citation/prodStmt/producer) -1">
								,
								</xsl:if>
								<xsl:if test="position() eq count(/codeBook/docDscr/citation/prodStmt/producer) -1">
									,&#160;and&#160;
								</xsl:if>
							</xsl:if>
						</span>
					</xsl:for-each>
				</p>
				<div class="lb2" />
			</xsl:if>
		</div>

		<xsl:if
			test="codeBook/docDscr/citation/biblCit != ''">
			<p class="staticHeader">Citation</p>
		</xsl:if>
		<xsl:if test="codeBook/docDscr/citation/biblCit != ''">
			<div class="value2">
				<em>Please cite this codebook as:</em>
				<br />
				<xsl:copy-of select="codeBook/docDscr/citation/biblCit/node()" />
				<xsl:copy-of
					select="codeBook/docDscr/citation/biblCit/node()/ExtLink/node()" />
			</div>
		</xsl:if>

		<xsl:if test="codeBook/docDscr/docSrc/biblCit  != ''">
			<p>
				This documentation derived from:
				<br />
				<xsl:copy-of select="codeBook/docDscr/docSrc/biblCit/node()" />
			</p>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>