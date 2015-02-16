<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
	version="2.0">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<h2>
			Variable Groups -
			<xsl:value-of select="codeBook/titl" />
		</h2>
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<xsl:if test="count(codeBook/varGrp) > 0">
			<xsl:for-each select="codeBook/varGrp">
				<p class="lb3">
					<a>
						<xsl:attribute name="href"><xsl:value-of
							select="@ID" />/</xsl:attribute>
						<xsl:value-of select="@name" />
					</a>
				</p>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(codeBook/varGrp) eq 0">
			<p>
				<em>Codebook does not contain variable groups.</em>
			</p>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>