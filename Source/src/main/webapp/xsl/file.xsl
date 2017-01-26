<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<!-- All the /codeBook/fileDscr elements that were in codebook.xsl  -->
		<p class="toggleHeader tcs">Datasets</p>
		<div class="toggleContent">
			<div class="toggleText">
				<xsl:for-each select="codeBook/fileDscr">
					<p>
						<xsl:value-of select="fileTxt/fileName" />
						&#160;
						<xsl:choose>
							<xsl:when
								test="(not(contains(@URI,'http')) and not(contains(@URI,'ftp:'))) and @URI != ''">
								<xsl:value-of select="fileTxt/fileName" />
								(Incomplete URL provided -
								<xsl:value-of select="@URI" />
								)
							</xsl:when>
							<xsl:when test="string-length(@URI) gt 0 ">
								<a itemprop="distribution" class="iLinkR">
									<xsl:attribute name="target">_blank</xsl:attribute>
									<xsl:attribute name="href"><xsl:value-of
										select="@URI" /></xsl:attribute>
									<xsl:value-of select="@URI" />
									<i class="fa fa-external-link"></i>
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
				<xsl:if test="count(codeBook/fileDscr) eq 0">
					<p>Sorry, no file information is available for this codebook.</p>
				</xsl:if>
			</div>
		</div>
		<div class="printLB"></div>
	</xsl:template>
</xsl:stylesheet>