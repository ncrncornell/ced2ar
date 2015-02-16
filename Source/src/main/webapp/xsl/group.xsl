<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
	version="2.0">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<h2>
			<xsl:value-of select="codeBook/dataDscr/varGrp/@name" />
		</h2>
		<p class="lb2">
			<xsl:value-of select="codeBook/dataDscr/varGrp/txt" />
		</p>
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<xsl:for-each select="codeBook/dataDscr/var">
			<p class="lb3">
				<a>
					<xsl:attribute name="href">../../vars/<xsl:value-of
						select="@name" /></xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</p>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>